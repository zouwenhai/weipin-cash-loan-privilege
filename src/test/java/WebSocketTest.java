import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import nirvana.cash.loan.privilege.Application;
import nirvana.cash.loan.privilege.common.util.GeneratorId;
import nirvana.cash.loan.privilege.websocket.facade.WebSocketMessageFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dongdong
 * @date 2018/11/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class WebSocketTest {

    @Autowired
    private AmqpTemplate rabbit;

    @Test
    public void test() {
        Map map = new HashMap();
        map.put("msgModule", "待催收(测试数据)");
        map.put("orderId", "20180913000008");
        map.put("orderStatus", "已逾期(测试数据)");
        map.put("time", "2018-11-14 16:58:55");
        map.put("userName", "系统管理员(勿删)");
        map.put("uuid", "634416301a774e2db729f764af4a27cc");

        WebSocketMessageFacade facade = new WebSocketMessageFacade();
        facade.setUserId(0L);
        facade.setUuid(GeneratorId.guuid());
        facade.setMsg(JSONObject.toJSONString(map));
        facade.setCount(1);
        rabbit.convertAndSend("exchange_auth_msg_notice_websocket", "routingkey_auth_msg_notice_websocket", JSON.toJSONString(facade));
    }

}
