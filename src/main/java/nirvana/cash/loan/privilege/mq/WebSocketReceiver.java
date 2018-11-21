package nirvana.cash.loan.privilege.mq;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.websocket.facade.WebSocketMessageFacade;
import nirvana.cash.loan.privilege.websocket.handler.WebSocketMessageHandler;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MQ分布式websocket
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
                    value = @Queue(value = "${rabbitmq.queue.message_center_ws}_${eureka.instance.instance-id:}",
                            durable = "true", autoDelete = "true"),
                    exchange = @Exchange(value = "${rabbitmq.exchange.message_center_ws}", type = ExchangeTypes.TOPIC),
                    key = "${rabbitmq.routing-key.message_center_ws}"),
            admin = "myRabbitAdmin"
    )
    @RabbitHandler
    public void receive(String message) {
        log.info("有消息需要推送给用户,消息：{}", message);
        WebSocketMessageFacade webSocketMessageFacade = JSONObject.parseObject(message, WebSocketMessageFacade.class);
        handler.sendMessageToClient(String.valueOf(webSocketMessageFacade.getUserId()), JSONObject.toJSONString(webSocketMessageFacade));
    }

}
