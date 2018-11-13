package nirvana.cash.loan.privilege.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.enums.MsgChannelEnum;
import nirvana.cash.loan.privilege.common.enums.MsgModuleEnum;
import nirvana.cash.loan.privilege.common.enums.OrderStatusEnum;
import nirvana.cash.loan.privilege.common.util.*;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.MsgConfigDetailVo;
import nirvana.cash.loan.privilege.mq.facade.MqMsgNoticFacade;
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

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dongdong
 * @date 2018/11/6
 */
@Slf4j
@Component
public class MsgNoticeReceiver {

    @Value("${spring.mail.username}")
    private String fromAddress;
    @Value("${rabbitmq.exchange.auth_msg_notice_websocket}")
    private String exchange;
    @Value("${rabbitmq.routingkey.auth_msg_notice_websocket}")
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
                    value = @Queue(value = "${rabbitmq.queue.auth_msg_notice}", durable = "true"),
                    exchange = @Exchange(value = "${rabbitmq.exchange.auth_msg_notice}", type = ExchangeTypes.TOPIC, durable = "true"),
                    key = "${rabbitmq.routingkey.auth_msg_notice}"),
            admin = "myRabbitAdmin"
    )
    @RabbitHandler
    public void receive(String msg) {
        log.info("接收消息推送:msg={}", msg);
        MqMsgNoticFacade facade = JSON.parseObject(msg, MqMsgNoticFacade.class);
        //消息唯一ID
        String uuid = facade.getUuid();
        //消息通知模块
        OrderStatusEnum orderStatusEnum = OrderStatusEnum.getEnum(facade.getOrderStatus());
        MsgModuleEnum msgModuleEnum = MsgModuleUtil.transOrderStatus2MsgModule(orderStatusEnum);
        if (msgModuleEnum == null) {
            log.info("未设置该消息通知模块,不处理此消息:uuid={}", uuid);
            return;
        }
        Integer msgModule = msgModuleEnum.getCode();
        //查询消息模块对应配置
        MessageConfig msgConfig = messageConfigService.findMessageConfigByMsgModule(msgModule, 60 * 5L);
        if (msgConfig == null || StringUtils.isBlank(msgConfig.getMsgContent())) {
            log.info("未设置消息通知发送规则,不处理此消息:uuid={}", uuid);
            return;
        }
        List<MsgConfigDetailVo> configDetailVoList = JSON.parseArray(msgConfig.getMsgContent(), MsgConfigDetailVo.class);

        Map<String, String> msgmap = new HashMap();
        msgmap.put("uuid", uuid);
        msgmap.put("msgModule", msgModuleEnum.getName());
        msgmap.put("orderId", facade.getOrderId());
        msgmap.put("orderStatus", orderStatusEnum.getDesc());
        msgmap.put("time", DateUtil.getDateTime());

        //1:站内信通知
        configDetailVoList.stream()
                .filter(t -> MsgChannelEnum.channel_web_socket.getValue() == t.getMsgChannel())
                .forEach(t -> processWebsocketMsg(t, uuid, msgmap, msgModuleEnum));

        //2:邮件通知
        configDetailVoList.stream()
                .filter(t -> MsgChannelEnum.channel_email.getValue() == t.getMsgChannel())
                .forEach(t -> processEmailMsg(t, uuid, msgmap, msgModuleEnum));

    }


    //站内信处理
    private void processWebsocketMsg(MsgConfigDetailVo vo, String uuid, Map<String, String> msgmap, MsgModuleEnum msgModuleEnum) {
        log.info("站内信处理:uuid={}",uuid);
        Set<String> tmpSet = new HashSet<>(Arrays.asList(vo.getMsgTarget().trim().split(",")));
        Set<Long> userIdSet = tmpSet.stream().map(t -> Long.valueOf(t)).collect(Collectors.toSet());
        List<User> userList = userService.findByIds(userIdSet);
        int i = 0;
        Iterator<Long> it = userIdSet.iterator();
        while (it.hasNext()) {
            Long userId = it.next();
            try {
                String userMsgId = GeneratorId.guuid();
                userList.stream()
                        .filter(x -> x.getUserId().equals(userId))
                        .findAny()
                        .ifPresent(x->{
                            msgmap.put("userName", x.getName());
                            String content = JSON.toJSONString(msgmap);
                            //插入数据表
                            MsgList msgList = new MsgList();
                            msgList.setUserId(userId);
                            msgList.setUuid(userMsgId);
                            msgList.setMsgModule(msgModuleEnum.getCode());
                            msgList.setContent(content);
                            msgListService.saveMsg(msgList);

                            WebSocketMessageFacade msgNoticeFacade=new WebSocketMessageFacade();
                            msgNoticeFacade.setUserId(userId);
                            msgNoticeFacade.setMsg(content);
                            msgNoticeFacade.setUuid(userMsgId);
                            rabbitTemplate.convertAndSend(exchange,key, JSONObject.toJSONString(msgNoticeFacade));
                        });
            } catch (Exception ex) {
                log.error("站内信|消息接收处理失败:uuid={},userId={}", uuid, userId);
            }
        }
    }

    //邮件消息处理
    private void processEmailMsg(MsgConfigDetailVo vo, String uuid, Map<String, String> msgmap, MsgModuleEnum msgModuleEnum) {
        log.info("邮件消息处理:uuid={}",uuid);
        LocalTime now = LocalTime.now();
        if (DateUtil.isTimeSpecifiedInTimeBucket(now, vo.getStartTime(), vo.getEndTime())) {
            Set<String> tmpSet = new HashSet<>(Arrays.asList(vo.getMsgTarget().trim().split(",")));
            Set<Long> userIdSet = tmpSet.stream().map(t -> Long.valueOf(t)).collect(Collectors.toSet());
            List<User> userList = userService.findByIds(userIdSet);
            Iterator<Long> it = userIdSet.iterator();
            while (it.hasNext()) {
                Long userId = it.next();
                try {
                    userList.stream()
                            .filter(x -> x.getUserId().equals(userId))
                            .findAny()
                            .ifPresent(t -> {
                                msgmap.put("userName", t.getName());
                                String title = msgModuleEnum.getName() + "模块-有新订单需要您处理";
                                String content = freemarkerUtil.resolve(template_email_notice_msg, msgmap);
                                String toAddress = t.getEmail();
                                emaiUtil.sendEmailHtml(fromAddress, toAddress, title, content);
                            });
                } catch (Exception ex) {
                    log.error("邮件消息|消息接收处理失败:uuid={},userId={}", uuid, userId);
                }
            }
        }
    }

}
