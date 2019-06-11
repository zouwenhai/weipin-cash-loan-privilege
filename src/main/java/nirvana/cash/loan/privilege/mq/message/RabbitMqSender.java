package nirvana.cash.loan.privilege.mq.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2018/8/1.
 */
@Slf4j
@Component
public class RabbitMqSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param exchange 交换机
     * @param routingkey  路由key
     * @param context 发送的实体对象时,实体对象implements Serializable
     * @return
     */
    public boolean send(String exchange,String routingkey, Object context) {
        boolean res = false;
        try {
            rabbitTemplate.convertAndSend(exchange,routingkey, context);
            res = true;
        } catch (Exception ex) {
            log.error(ex.toString());
        }
        return res;
    }

}
