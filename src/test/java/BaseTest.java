import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import nirvana.cash.loan.privilege.Application;
import nirvana.cash.loan.privilege.common.enums.MsgModuleEnum;
import nirvana.cash.loan.privilege.common.enums.OrderStatusEnum;
import nirvana.cash.loan.privilege.common.util.EmaiUtil;
import nirvana.cash.loan.privilege.common.util.MsgModuleUtil;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.mq.facade.MessageFacade;
import nirvana.cash.loan.privilege.mq.facade.OrderStatusChangeFacade;
import nirvana.cash.loan.privilege.service.MsgListService;
import nirvana.cash.loan.privilege.service.UserService;
import nirvana.cash.loan.privilege.service.base.RedisService;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * Created by Administrator on 2018/8/29.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class BaseTest {

    @Autowired
    private RedisService redisService;

    @Test
    public void test() {
        String prefix = "login_user_";
        String u1 = prefix + "1";
        String u2 = prefix + "2";
        String u3 = prefix + "3";
        redisService.put(u1, u1);
        redisService.put(u2, u2);
        redisService.put(u3, u3);
        System.err.println(redisService.get(u1,String.class));
        System.err.println(redisService.get(u2,String.class));
        System.err.println(redisService.get(u3,String.class));
    }

    @Test
    public void test2(){
        String prefix = "login_user_";
        String u1 = prefix + "1";
        String u2 = prefix + "2";
        String u3 = prefix + "3";
        System.err.println(redisService.get(u1,String.class));
        System.err.println(redisService.get(u2,String.class));
        System.err.println(redisService.get(u3,String.class));
    }

    @Test
    public void test3(){
        String prefix = "login_user_";
        String u1 = prefix + "1";
        String u2 = prefix + "2";
        String u3 = prefix + "3";
        redisService.deleteWithPattern(prefix+"*");
    }

    @Test
    public void test4(){
        String prefix = "login_user_";
        Set<String>  keys = redisService.getKeysWithPattern(prefix+"*");
        System.err.println(JSON.toJSONString(keys));
        System.err.println("done");

        Set<String> dkeys=new HashSet<>();
        dkeys.add("login_user_3");
        dkeys.add("login_user_2");
        redisService.deleteWithKeys(dkeys);
    }

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Value("${rabbitmq.exchange.message_center}")
    private String mcExchange;
    @Value("${rabbitmq.routing-key.message_center}")
    private String mcRoutingKey;


    @Test
    public void test6(){
        MessageFacade messageFacade = new MessageFacade();
        messageFacade.setOrderId("1000000000");
        messageFacade.setUuid(UUID.randomUUID().toString());
        messageFacade.setMessageModule(2);
        messageFacade.setOrderStatus(OrderStatusEnum.ClosedOrder.getValue());
        messageFacade.setDetails("xssssssssssssssssss");
        List userIds = new ArrayList();
        userIds.add(0);
        userIds.add(2);
        userIds.add(410000);
        messageFacade.setUserIds(userIds);
        amqpTemplate.convertAndSend(mcExchange,mcRoutingKey, JSONObject.toJSONString(messageFacade));
    }

    @Autowired
    private UserService userService;

    @Test
    public void test7(){

        OrderStatusChangeFacade facade =new OrderStatusChangeFacade();
        facade.setUuid(UUID.randomUUID().toString());
        facade.setOrderId("10086");
        facade.setOrderStatus(20);
        facade.setOrderUser("liuxiaowei");
        //消息通知模块
        OrderStatusEnum orderStatusEnum = OrderStatusEnum.getEnum(facade.getOrderStatus());
        MsgModuleEnum msgModuleEnum = MsgModuleUtil.transOrderStatus2MsgModule(orderStatusEnum);
        if ( msgModuleEnum== null) {
            return;
        }
        MessageFacade messageFacade = new MessageFacade();
        BeanUtils.copyProperties(facade, messageFacade);
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
        messageFacade.setMessageModule(msgModuleEnum.getCode());
        amqpTemplate.convertAndSend(mcExchange, mcRoutingKey, JSONObject.toJSONString(messageFacade));
    }

    @Autowired
    private MsgListService msgListService;

    @Test
    public void test8(){
        OrderStatusEnum a = OrderStatusEnum.SysFailed;
        int count = msgListService.selectCountByOrderIdAndStatus("1000000000", "已结清");
        System.out.println(count);
        msgListService.markAsRead("10086",a.getValue());
    }

}
