package nirvana.cash.loan.privilege.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.enums.MsgModuleEnum;
import nirvana.cash.loan.privilege.common.enums.OrderStatusEnum;
import nirvana.cash.loan.privilege.common.util.MsgModuleUtil;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.mq.facade.MessageFacade;
import nirvana.cash.loan.privilege.mq.facade.OrderStatusChangeFacade;
import nirvana.cash.loan.privilege.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
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
        //消息通知模块
        OrderStatusEnum orderStatusEnum = OrderStatusEnum.getEnum(facade.getOrderStatus());
        MsgModuleEnum msgModuleEnum = MsgModuleUtil.transOrderStatus2MsgModule(orderStatusEnum);
        if (msgModuleEnum == null) {
            log.info("未设置该消息通知模块,不处理此消息:uuid={}", facade.getUuid());
            return;
        }
        MessageFacade messageFacade = new MessageFacade();
        BeanUtils.copyProperties(facade, messageFacade);
        messageFacade.setMessageModule(msgModuleEnum.getCode());

        User user = null;
        String userName = facade.getOrderUser();
        if (StringUtils.isNotBlank(userName)) {
            user = userService.findByName(userName);
        }
        if (user != null) {
            List userIds = new ArrayList();
            userIds.add(user.getUserId());
            messageFacade.setUserIds(userIds);
        }
        rabbitTemplate.convertAndSend(mcExchange, mcRoutingKey, JSONObject.toJSONString(messageFacade));
    }

}
