package nirvana.cash.loan.privilege.mq;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.websocket.facade.WebSocketMessageFacade;
import nirvana.cash.loan.privilege.websocket.handler.WebSocketMessageHandler;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dongdong
 * @date 2018/11/9
 */
@Slf4j
@Component
public class WebSocketReceiver {

    @Autowired
    private WebSocketMessageHandler handler;

    @RabbitListener(containerFactory = "myContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = "${rabbitmq.queue.auth_msg_notice_websocket}_${eureka.instance.instance-id:}", durable = "false", autoDelete = "true"),
                    exchange = @Exchange(value = "${rabbitmq.exchange.auth_msg_notice_websocket}", type = ExchangeTypes.TOPIC),
                    key = "${rabbitmq.routingkey.auth_msg_notice_websocket}"),
            admin = "myRabbitAdmin"
    )
    @RabbitHandler
    public void receive(String message) {
        log.info("收到消息：{}", message);
        WebSocketMessageFacade msgNoticeFacade = JSONObject.parseObject(message, WebSocketMessageFacade.class);
        handler.sendMessageToClient(String.valueOf(msgNoticeFacade.getUserId()), msgNoticeFacade.getMsg());
    }

}
