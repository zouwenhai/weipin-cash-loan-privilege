package nirvana.cash.loan.privilege.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.domain.dto.MessageDto;
import nirvana.cash.loan.privilege.service.MessageConfigService;
import nirvana.cash.loan.privilege.service.MsgListService;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dongdong
 * @date 2018/11/6
 */
@Slf4j
//@Component
@RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${}"),
        exchange = @Exchange(value = "", type = ExchangeTypes.TOPIC),
        key = ""))
public class MessageReceiver {

    @Autowired
    private MsgListService msgListService;
    @Autowired
    private MessageConfigService messageConfigService;

    @RabbitHandler
    public void receive(MessageDto messageDto) {

        Integer msgModule = messageDto.getMsgModule();
        if (messageDto == null) {
            log.error("无效的通知模块，data:{}", messageDto);
            return;
        }
        //得到指定通知模块的消息配置
        List<MessageConfig> messageConfigs = Optional.ofNullable(messageConfigService.queryMessageConfigs()).orElseGet(() -> Collections.emptyList())
                .stream().filter(mc -> Objects.equals(msgModule, mc.getMsgModule())).collect(Collectors.toList());
        if (messageConfigs.size() == 0) {
            log.warn("没有该通知模块的消息配置", messageDto);
            return;
        }

        int runStatus = messageConfigs.get(0).getIsRun();

        //按通知渠道分组
        Map<Integer, List<MessageConfig>> group = messageConfigs.stream().filter(mc -> StringUtils.isNotBlank(mc.getMsgTarget()))
                .collect(Collectors.groupingBy(mc -> mc.getMsgChannel()));
        //需要发送站内信的用户
        List<String> userIdsNeedToSendMsg = Optional.ofNullable(group.get(1)).orElseGet(() -> Collections.emptyList()).stream()
                .map(mc -> mc.getMsgTarget()).collect(Collectors.toList());

        saveMessage(messageDto, userIdsNeedToSendMsg);

        if (runStatus == 0) {
            return;
        }
        //发送站内信
        //TODO

        //需要发送邮件的用户
        List<String> userIdsNeedToSendEmail = Optional.ofNullable(group.get(2)).orElseGet(() -> Collections.emptyList()).stream()
                .map(mc -> mc.getMsgTarget()).collect(Collectors.toList());

        LocalTime now = LocalTime.now();
        Optional.ofNullable(group.get(2)).orElseGet(() -> new ArrayList<>()).stream().peek(mc -> {
            if (isTimeSpecifiedInTimeBucket(now, mc.getStartTime(), mc.getEndTime())) {
                //发送邮件
                //TODO
            }
        }).count();

    }

    private void saveMessage(MessageDto messageDto, List<String> userIds) {
        MsgList msgList = new MsgList();
        BeanUtils.copyProperties(messageDto, msgList);
        if (!CollectionUtils.isEmpty(userIds)) {
            userIds.forEach(i -> {
                msgList.setUserId(Long.parseLong(i));
                msgListService.saveMsg(msgList);
                msgList.setId(null);
            });
        }
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
