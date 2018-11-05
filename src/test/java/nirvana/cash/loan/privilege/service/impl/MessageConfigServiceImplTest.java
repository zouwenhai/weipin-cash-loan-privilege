package nirvana.cash.loan.privilege.service.impl;

import com.alibaba.fastjson.JSONObject;
import nirvana.cash.loan.privilege.Application;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.vo.MessageConfigVo;
import nirvana.cash.loan.privilege.service.MessageConfigService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by sunyong on 2018-11-05.
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MessageConfigServiceImplTest {

    @Autowired
    private MessageConfigService messageConfigService;

    @Test
    public void queryMessageConfigs() {
        ResResult resResult = messageConfigService.queryMessageConfigs();
        System.out.println(JSONObject.toJSONString(resResult));
    }

    @Test
    public void insertMessageConfig() {
        MessageConfigVo messageConfigVo = new MessageConfigVo();
        messageConfigVo.setMsgModule(1);
        String msgJson = "[{\"msgChannel\":\"1\",\"msgTarget\":\"213\",\"startTime\":\"03:00\"," +
                "\"endTime\":\"15:30\"},{\"msgChannel\":\"2\",\"msgTarget\":\"456\",\"startTime\":\"08:00\"," +
                "\"endTime\":\"19:30\"}]";
        messageConfigVo.setCreateUser("sun");
        messageConfigVo.setMsgJson(msgJson);
        messageConfigService.insertMessageConfig(messageConfigVo);
    }

    @Test
    public void updateMessageConfig() {
        MessageConfigVo messageConfigVo = new MessageConfigVo();
        messageConfigVo.setUpdateUser("sun123");
        messageConfigVo.setMsgModule(1);
        String msgJson = "[{\"id\":\"11\",\"msgChannel\":\"3\",\"msgTarget\":\"3\",\"startTime\":\"03:00\"," +
                "\"endTime\":\"15:30\"},{\"id\":\"12\",\"msgChannel\":\"4\",\"msgTarget\":\"4\"," +
                "\"startTime\":\"08:00\"," +
                "\"endTime\":\"19:30\"}]";
        messageConfigVo.setMsgJson(msgJson);
        messageConfigService.updateMessageConfig(messageConfigVo);
    }

    @Test
    public void delMessageConfig() {
        ResResult resResult = messageConfigService.delMessageConfig(1L);
        System.out.println(JSONObject.toJSONString(resResult));
    }
}
