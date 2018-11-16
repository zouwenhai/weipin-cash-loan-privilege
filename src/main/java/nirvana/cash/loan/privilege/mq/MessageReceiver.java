package nirvana.cash.loan.privilege.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.enums.MsgChannelEnum;
import nirvana.cash.loan.privilege.common.enums.MsgModuleEnum;
import nirvana.cash.loan.privilege.common.enums.OrderStatusEnum;
import nirvana.cash.loan.privilege.common.util.DateUtil;
import nirvana.cash.loan.privilege.common.util.EmaiUtil;
import nirvana.cash.loan.privilege.common.util.FreemarkerUtil;
import nirvana.cash.loan.privilege.common.util.GeneratorId;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.MsgConfigDetailVo;
import nirvana.cash.loan.privilege.mq.facade.MessageFacade;
import nirvana.cash.loan.privilege.service.MessageConfigService;
import nirvana.cash.loan.privilege.service.MsgListService;
import nirvana.cash.loan.privilege.service.UserService;
import nirvana.cash.loan.privilege.service.base.RedisService;
import nirvana.cash.loan.privilege.websocket.facade.WebSocketMessageFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MQ统一消息中心
 * @author dongdong
 * @date 2018/11/6
 */
@Slf4j
@Component
public class MessageReceiver {

    @Value("${spring.mail.username}")
    private String fromAddress;
    @Value("${rabbitmq.exchange.message_center_ws}")
    private String exchange;
    @Value("${rabbitmq.routing-key.message_center_ws}")
    private String key;

    @Autowired
    private MsgListService msgListService;
    @Autowired
    private MessageConfigService messageConfigService;
    @Autowired
    private EmaiUtil emaiUtil;
    @Autowired
    public RedisService redisService;
    @Autowired
    public UserService userService;
    @Autowired
    FreemarkerUtil freemarkerUtil;
    @Autowired
    private AmqpTemplate rabbitTemplate;

    final static String template_email_notice_msg = "email_notice_msg.ftl";

    @RabbitListener(containerFactory = "myContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = "${rabbitmq.queue.message_center}", durable = "true"),
                    exchange = @Exchange(value = "${rabbitmq.exchange.message_center}", type = ExchangeTypes.TOPIC),
                    key = "${rabbitmq.routing-key.message_center}"),
            admin = "myRabbitAdmin"
    )
    @RabbitHandler
    public void receive(String message) {

        log.info("消息中心接收到新消息:message={}", message);
        MessageFacade facade = JSON.parseObject(message, MessageFacade.class);

        //订单状态
        Integer orderStatus = facade.getOrderStatus();
        OrderStatusEnum orderStatusEnum = null;
        if (orderStatus != null) {
            orderStatusEnum = OrderStatusEnum.getEnum(facade.getOrderStatus());
        }

        //消息通知模块
        Integer msgModuleCode = facade.getMessageModule();
        MsgModuleEnum msgModuleEnum = null;
        if (msgModuleCode != null) {
            msgModuleEnum = MsgModuleEnum.getMsgModuleEnum(msgModuleCode);
        }
        if (msgModuleEnum == null) {
            log.info("没有找到对应消息通知模块,不处理此消息:message={}", message);
            return;
        }

        //查询消息模块对应配置
        MessageConfig msgConfig = messageConfigService.findMessageConfigByMsgModule(msgModuleCode, 60 * 5L);
        if (msgConfig == null || msgConfig.getIsRun() == 0) {
            log.info("该消息通知模块未设置消息通知发送规则或者未开启,不处理此消息:message={}", message);
            return;
        }

        if (StringUtils.isBlank(msgConfig.getMsgContent())) {
            log.info("该消息通知模块未设置消息通知发送规则,不处理此消息:message={}", message);
            return;
        }

        Map<String, String> messageMap = new HashMap(5);
        messageMap.put("orderStatus", orderStatusEnum != null ? orderStatusEnum.getDesc() : "订单状态未知");
        messageMap.put("uuid", facade.getUuid());
        messageMap.put("msgModule", msgModuleEnum.getName());
        messageMap.put("orderId", facade.getOrderId());
        messageMap.put("time", DateUtil.getDateTime());
        messageMap.put("details", facade.getDetails());

        List<MsgConfigDetailVo> configDetailVoList = JSON.parseArray(msgConfig.getMsgContent(), MsgConfigDetailVo.class);
        MsgModuleEnum messageModuleEnum = msgModuleEnum;
        //1:站内信通知
        configDetailVoList.stream()
                .filter(t -> MsgChannelEnum.channel_web_socket.getValue() == t.getMsgChannel())
                .forEach(t -> processWebSocketMessage(t, messageMap, messageModuleEnum, facade.getUserIds()));
        //2:邮件通知
        configDetailVoList.stream()
                .filter(t -> MsgChannelEnum.channel_email.getValue() == t.getMsgChannel())
                .forEach(t -> processEmailMessage(t, messageMap, messageModuleEnum, facade.getUserIds()));
    }

    /**
     * 站内信处理
     *
     * @param messageConfig
     * @param messageContent 消息内容
     * @param msgModuleEnum  通知模块
     * @param targetUsers    需要发送给哪些用户
     */
    private void processWebSocketMessage(MsgConfigDetailVo messageConfig, Map<String, String> messageContent, MsgModuleEnum msgModuleEnum, List<Long> targetUsers) {
        log.info("站内信处理:uuid={}", messageContent.get("uuid"));
        Set<Long> userIds = getUserIdsFromMessageConfig(messageConfig);
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        targetUsers = Optional.ofNullable(targetUsers).orElseGet(() -> Collections.emptyList())
                .stream().filter(u -> u != null).collect(Collectors.toList());

        //没有指定发送给谁，则发送给所有配置的用户
        if (CollectionUtils.isEmpty(targetUsers)) {
            Optional.ofNullable(userService.findByIds(userIds)).orElseGet(() -> Collections.emptyList()).stream().peek(u -> {
                try {
                    Long id = u.getUserId();
                    Integer unreadCount = msgListService.countUnReadMsg(id);
                    messageContent.put("userName", u.getName());
                    log.info("保存消息：{}",JSONObject.toJSONString(messageContent));
                    MsgList msgList = saveMessage(id, msgModuleEnum.getCode(), JSONObject.toJSONString(messageContent));
                    //发送到webSocket消息队列
                    sendMessageToUserClient(id, msgList, unreadCount != null ? unreadCount + 1 : 1);
                } catch (Exception e) {
                    log.error("站内信消息处理失败", e.getMessage());
                }
            }).count();
            return;
        }
        //只发送给目标用户
        targetUsers.stream().filter(id -> userIds.contains(id)).peek(id -> {
            Integer unreadCount = msgListService.countUnReadMsg(id);
            Optional.ofNullable(userService.findById(id)).ifPresent(s -> messageContent.put("userName", s.getName()));
            MsgList msgList = saveMessage(id, msgModuleEnum.getCode(), JSONObject.toJSONString(messageContent));
            //发送到webSocket消息队列
            sendMessageToUserClient(id, msgList, unreadCount != null ? unreadCount + 1 : 1);
        }).count();
        targetUsers.stream().filter(id -> !userIds.contains(id)).peek(u -> log.info("用户{}不在消息配置的用户列表中，不发送站内信", u)).count();
    }

    /**
     * 邮件消息处理
     *
     * @param messageConfig
     * @param messageContent 消息内容
     * @param msgModuleEnum  通知模块
     * @param targetUsers    需要发送给哪些用户
     */
    private void processEmailMessage(MsgConfigDetailVo messageConfig, Map<String, String> messageContent, MsgModuleEnum msgModuleEnum, List<Long> targetUsers) {
        log.info("邮件消息处理:uuid={}", messageContent.get("uuid"));

        //不在邮件通知设置的时间范围内，不发邮件
        if (!DateUtil.isTimeSpecifiedInTimeBucket(LocalTime.now(), messageConfig.getStartTime(), messageConfig.getEndTime())) {
            return;
        }
        Set<Long> userIds = getUserIdsFromMessageConfig(messageConfig);
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        targetUsers = Optional.ofNullable(targetUsers).orElseGet(() -> Collections.emptyList())
                .stream().filter(u -> u != null).collect(Collectors.toList());

        //没有指定发送给谁，则发送给所有配置的用户
        if (CollectionUtils.isEmpty(targetUsers)) {
            List<User> users = userService.findByIds(userIds);
            Optional.ofNullable(users).ifPresent(user -> {
                user.forEach(u -> sendEmail(msgModuleEnum.getName() + "模块-有新订单需要您处理", messageContent, u));
            });
            return;
        }
        //只发送给目标的用户
        targetUsers.stream().filter(id -> userIds.contains(id)).peek(id -> {
            Optional.ofNullable(userService.findById(id)).ifPresent(u ->
                    sendEmail(msgModuleEnum.getName() + "模块-有新订单需要您处理", messageContent, u));
        }).count();
        targetUsers.stream().filter(id -> !userIds.contains(id)).peek(u -> log.info("用户{}不在消息配置的用户列表中，不发送邮件", u)).count();
    }

    private Set<Long> getUserIdsFromMessageConfig(MsgConfigDetailVo msgConfig) {
        String targetUsers = msgConfig.getMsgTarget();
        if (StringUtils.isBlank(targetUsers)) {
            return null;
        }
        return new HashSet<>(Arrays.asList(targetUsers.trim().split(",")))
                .stream().map(t -> Long.valueOf(t)).collect(Collectors.toSet());
    }

    private MsgList saveMessage(Long userId, Integer messageModuleCode, String messageContent) {
        MsgList msgList = new MsgList();
        msgList.setUserId(userId);
        msgList.setUuid(GeneratorId.guuid());
        msgList.setMsgModule(messageModuleCode);
        msgList.setContent(messageContent);
        msgListService.saveMsg(msgList);
        return msgList;
    }

    private void sendMessageToUserClient(Long userId, MsgList msgList, int unreadCount) {
        WebSocketMessageFacade webSocketMessage = new WebSocketMessageFacade();
        webSocketMessage.setUserId(userId);
        webSocketMessage.setMsg(msgList.getContent());
        webSocketMessage.setUuid(msgList.getUuid());
        webSocketMessage.setCount(unreadCount);
        rabbitTemplate.convertAndSend(exchange, key, JSONObject.toJSONString(webSocketMessage));
    }

    private void sendEmail(String title, Map messageContent, User targetUser) {
        try {
            messageContent.put("userName", targetUser.getName());
            String content = freemarkerUtil.resolve(template_email_notice_msg, messageContent);
            String toAddress = targetUser.getEmail();
            boolean valid = emaiUtil.verifyEmailFormat(toAddress);
            if (valid) {
                emaiUtil.sendEmailHtml(fromAddress, toAddress, title, content);
            }
        } catch (Exception ex) {
            log.error("邮件消息|消息接收处理失败:uuid={},userId={}", messageContent.get("uuid"), targetUser.getUserId());
        }
    }

}
