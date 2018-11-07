package nirvana.cash.loan.privilege.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.enums.MsgChannelEnum;
import nirvana.cash.loan.privilege.common.util.ListUtil;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.mq.facade.MqMsgNoticFacade;
import nirvana.cash.loan.privilege.service.MessageConfigService;
import nirvana.cash.loan.privilege.service.MsgListService;
import nirvana.cash.loan.privilege.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dongdong
 * @date 2018/11/6
 */
@Slf4j
@Component
public class MsgNoticeReceiver {

    @Autowired
    private MsgListService msgListService;
    @Autowired
    private MessageConfigService messageConfigService;
    @Autowired
    private UserService userService;

    @RabbitListener(containerFactory = "myContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = "${rabbitmq.queue.auth_msg_notice}", durable = "true"),
                    exchange = @Exchange(value = "${rabbitmq.exchange.auth_msg_notice}", type = ExchangeTypes.TOPIC,durable = "true"),
                    key = "${rabbitmq.routingkey.auth_msg_notice}"))
    @RabbitHandler
    public void receive(String msg) {
        log.info("消息中心|接收消息推送:msg={}",msg);
        MqMsgNoticFacade facade = JSON.parseObject(msg,MqMsgNoticFacade.class);
        //查询消息配置
        List<MessageConfig> msgConfigs = messageConfigService.queryMessageConfigs();
        msgConfigs = msgConfigs.stream().filter(t->t.getIsRun() == 0)
                .filter(t->t.getMsgModule() == facade.getMsgModule())
                .collect(Collectors.toList());
        //查询用户信息
        User user = userService.findByName(facade.getLoginName());
        if(ListUtil.isEmpty(msgConfigs)){
            log.info("消息配置未设置,不处理此消息.");
            return;
        }
        LocalTime now = LocalTime.now();
        //保存消息
        MsgList msgList = new MsgList();
        BeanUtils.copyProperties(facade,msgList);
        msgList.setUserId(user.getUserId());
        msgList.setCreateUser(user.getUsername());
        msgList.setUpdateUser(user.getUsername());
        ResResult resResult = msgListService.saveMsg(msgList);
        if(!ResResult.SUCCESS.equals(resResult.getCode())){
           return;
        }
        //站内信通知
        String   websocketTarget = null;
        MsgChannelEnum websocketChannel = null;

        //邮件通知
        String  emailTarget = null;
        MsgChannelEnum emailChannel = null;




    }


    /**
     * 判断时间是否在通知配置的生效时段内,用于确定是否发送消息
     *
     * @param time       指定时间点
     * @param startPoint 开始时间点时分秒字符串 eg: 07:15
     * @param endPoint   结束时间点时分秒字符串 eg: 18:05
     * @return
     */
    private boolean isTimeSpecifiedInTimeBucket(LocalTime time, String startPoint, String endPoint) {
        startPoint = StringUtils.isBlank(startPoint) ? "00:00" : startPoint;
        endPoint = StringUtils.isBlank(endPoint) ? "23:59" : endPoint;
        try {
            LocalTime start = LocalTime.parse(startPoint);
            LocalTime end = LocalTime.parse(endPoint);
            return time.isAfter(start) && time.isBefore(end);
        } catch (DateTimeParseException e) {
            log.error("消息通知配置的生效时间不规范！" + e.getMessage(), e);
        }
        return false;
    }

}
