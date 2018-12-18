package nirvana.cash.loan.privilege.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.enums.MsgModuleEnum;
import nirvana.cash.loan.privilege.common.enums.OrderStatusEnum;
import nirvana.cash.loan.privilege.common.util.MsgModuleUtil;
import nirvana.cash.loan.privilege.mq.facade.MessageFacade;
import nirvana.cash.loan.privilege.mq.facade.OrderStatusChangeFacade;
import nirvana.cash.loan.privilege.mq.message.OldMessageProcessor;
import nirvana.cash.loan.privilege.service.UserService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MQ借款订单状态变更通知
 *
 * @author dongdong
 * @date 2018/11/6
 */
@Slf4j
@Component
public class OrderStatusChangeReceiver {

    @Value("${rabbitmq.exchange.message_center}")
    private String mcExchange;
    @Value("${rabbitmq.routing-key.message_center}")
    private String mcRoutingKey;

    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private OldMessageProcessor processor;

    /**
     * 接收订单状态变更，提取需要的消息，发送到消息中心的mq
     *
     * @param msg
     */
    @RabbitListener(containerFactory = "myContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = "${rabbitmq.queue.order_status_notice}", durable = "true"),
                    exchange = @Exchange(value = "${rabbitmq.exchange.order_status_notice}", type = ExchangeTypes.TOPIC),
                    key = "${rabbitmq.routing-key.order_status_notice}"),
            admin = "myRabbitAdmin"
    )
    @RabbitHandler
    public void receive(String msg) {
        log.info("接收到订单状态变更:msg={}", msg);
        OrderStatusChangeFacade facade = JSON.parseObject(msg, OrderStatusChangeFacade.class);

        OrderStatusEnum orderStatusEnum = OrderStatusEnum.getEnum(facade.getOrderStatus());
        //对旧消息进行已读处理
        processor.markAsRead(facade.getOrderId(), orderStatusEnum);

        //如果同一订单重新机审仍然失败，不再重复发送通知，而是原通知保持未读
        if (OrderStatusEnum.SysFailed == orderStatusEnum) {
            boolean sysFailedAgain = processor.isSysFailedAgain(facade.getOrderId());
            if (sysFailedAgain) {
                log.info("重新机审失败，不再次发送新消息,orderId:{}", facade.getOrderId());
                return;
            }
        }

        //消息通知模块
        MsgModuleEnum msgModuleEnum = MsgModuleUtil.transOrderStatus2MsgModule(orderStatusEnum);
        if (msgModuleEnum == null) {
            log.info("未设置该消息通知模块或订单状态变更消息队列不处理此消息:uuid={}", facade.getUuid());
            return;
        }

        //只有人工复审和待催收需要指定发个给某个具体的用户,待催收消息不在这里处理
        if (MsgModuleEnum.MANUAL_REVIEW != msgModuleEnum) {
            facade.setOrderUser(null);
        }

        MessageFacade messageFacade = new MessageFacade();
        BeanUtils.copyProperties(facade, messageFacade);
        messageFacade.setMessageModule(msgModuleEnum.getCode());
        Optional.ofNullable(facade.getOrderUser()).map(n -> userService.findByName(n)).ifPresent(u -> {
            List userIds = new ArrayList();
            userIds.add(u.getUserId());
            messageFacade.setUserIds(userIds);
        });
        rabbitTemplate.convertAndSend(mcExchange, mcRoutingKey, JSONObject.toJSONString(messageFacade));
    }

}
