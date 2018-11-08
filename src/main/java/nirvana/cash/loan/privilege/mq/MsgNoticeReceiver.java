package nirvana.cash.loan.privilege.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.enums.MsgChannelEnum;
import nirvana.cash.loan.privilege.common.util.DateUtil;
import nirvana.cash.loan.privilege.common.util.EmaiUtil;
import nirvana.cash.loan.privilege.common.util.ListUtil;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.MsgConfigDetailVo;
import nirvana.cash.loan.privilege.mq.facade.MqMsgNoticFacade;
import nirvana.cash.loan.privilege.service.MessageConfigService;
import nirvana.cash.loan.privilege.service.MsgListService;
import nirvana.cash.loan.privilege.service.UserService;
import nirvana.cash.loan.privilege.service.base.RedisService;
import nirvana.cash.loan.privilege.websocket.facade.WebSocketMsgNoticeFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @RabbitListener(containerFactory = "myContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = "${rabbitmq.queue.auth_msg_notice}", durable = "true"),
                    exchange = @Exchange(value = "${rabbitmq.exchange.auth_msg_notice}", type = ExchangeTypes.TOPIC, durable = "true"),
                    key = "${rabbitmq.routingkey.auth_msg_notice}"))
    @RabbitHandler
    public void receive(String msg) {
        log.info("消息中心|接收消息推送:msg={}", msg);
        LocalTime now = LocalTime.now();
        MqMsgNoticFacade facade = JSON.parseObject(msg, MqMsgNoticFacade.class);
        //消息通知模块
        Integer msgModule = null;
        //消息内容
        String content = null;
        //消息唯一ID
        String uuid = null;
        //查询消息模块对应配置
        MessageConfig msgConfig = messageConfigService.findMessageConfigByMsgModule(msgModule,60*5L);
        if (msgConfig == null || StringUtils.isBlank(msgConfig.getMsgContent())) {
            log.info("未设置消息通知发送规则,不处理此消息.");
            return;
        }
        List<MsgConfigDetailVo> configDetailVoList = JSON.parseArray(msgConfig.getMsgContent(), MsgConfigDetailVo.class);
        if (ListUtil.isEmpty(configDetailVoList)) {
            log.info("未设置消息通知发送规则,不处理此消息.");
            return;
        }
        //1:获取全部消息发送目标对象|并保存通知消息
        Set<Long> userIdSet = new HashSet<>();
        for (MsgConfigDetailVo item : configDetailVoList) {
            Set<String> tmpSet = new HashSet<>(Arrays.asList(item.getMsgTarget().trim().split(",")));
            userIdSet = tmpSet.stream().map(t -> Long.valueOf(t)).collect(Collectors.toSet());
        }
        int i = 0;
        userIdSet.forEach(t -> {
            MsgList msgList = new MsgList();
            msgList.setUserId(t);
            msgList.setUuid(uuid + "-" + (i + 1));
            msgList.setMsgModule(msgModule);
            msgList.setContent(content);
            msgListService.saveMsg(msgList);
        });

        //2:获取站内信通知发送目标对象|并保存至redis中
        //站内信通知
        MsgConfigDetailVo wesocketVo = configDetailVoList.stream()
                .filter(t -> MsgChannelEnum.channel_web_socket.getValue() == t.getMsgChannel())
                .findAny().orElse(null);
        if (wesocketVo != null) {
            Set<String> tmpSet = new HashSet<>(Arrays.asList(wesocketVo.getMsgTarget().trim().split(",")));
            userIdSet = tmpSet.stream().map(t -> Long.valueOf(t)).collect(Collectors.toSet());
            userIdSet.forEach(t -> {
                WebSocketMsgNoticeFacade websocketMsg = new WebSocketMsgNoticeFacade();
                websocketMsg.setUuid(uuid);
                websocketMsg.setUserId(t);
                websocketMsg.setMsg(content);
                redisService.putSet(RedisKeyContant.YOFISHDK_MSG_NOTICE_PREFIX + t, new String[]{JSON.toJSONString(websocketMsg)});
            });
        }

        //3:获取邮件通知目标对象|并发送邮件 TODO
        MsgConfigDetailVo emailVo = configDetailVoList.stream()
                .filter(t -> MsgChannelEnum.channel_email.getValue() == t.getMsgChannel())
                .findAny().orElse(null);
        if (emailVo != null && DateUtil.isTimeSpecifiedInTimeBucket(now, emailVo.getStartTime(), emailVo.getEndTime())) {
            Set<String> tmpSet = new HashSet<>(Arrays.asList(emailVo.getMsgTarget().trim().split(",")));
            userIdSet = tmpSet.stream().map(t -> Long.valueOf(t)).collect(Collectors.toSet());
            List<User> userList = userService.findByIds(userIdSet);
            List<String> toAddresList = userList.stream().filter(t -> StringUtils.isNotBlank(t.getEmail()))
                    .map(t -> t.getEmail())
                    .collect(Collectors.toList());
            String title = "消息中心|您好！“放款审核”环节有新订单需要您关注！";
            emaiUtil.sendEmail(fromAddress, toAddresList, title, content);
        }


    }


}
