package nirvana.cash.loan.privilege.service.impl;

import com.alibaba.fastjson.JSONObject;
import nirvana.cash.loan.privilege.Application;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.MessageConfig;
import nirvana.cash.loan.privilege.service.MessageConfigService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

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
        List<MessageConfig> messageConfigs = messageConfigService.queryMessageConfigs();
        System.out.println(JSONObject.toJSONString(messageConfigs));
    }

    @Test
    public void insertMessageConfig() {
        MessageConfig messageConfig = new MessageConfig();
        messageConfig.setMsgModule(1);
        String msgJson = "[{\"msgChannel\":\"1\",\"msgTarget\":\"213\",\"startTime\":\"03:00\"," +
                "\"endTime\":\"15:30\"},{\"msgChannel\":\"2\",\"msgTarget\":\"456\",\"startTime\":\"08:00\"," +
                "\"endTime\":\"19:30\"}]";
        messageConfig.setMsgContent(msgJson);
        messageConfigService.insertMessageConfig(messageConfig,"213");
    }

    @Test
    public void updateMessageConfig() {
        MessageConfig messageConfig = new MessageConfig();
        messageConfig.setId(23L);
        String msgJson = "[{\"id\":\"11\",\"msgChannel\":\"3\",\"msgTarget\":\"3\",\"startTime\":\"03:00\"," +
                "\"endTime\":\"15:30\"},{\"id\":\"12\",\"msgChannel\":\"4\",\"msgTarget\":\"4\"," +
                "\"startTime\":\"08:00\"," +
                "\"endTime\":\"19:30\"}]";
        messageConfig.setMsgContent(msgJson);
        messageConfigService.updateMessageConfig(messageConfig,"321");
    }

    @Test
    public void delMessageConfig() {
        ResResult resResult = messageConfigService.delMessageConfig(1L);
        System.out.println(JSONObject.toJSONString(resResult));
    }

    @Test
    public void getMessageConfig() {
        ResResult resResult = messageConfigService.getMessageConfig(23L);
        System.out.println(JSONObject.toJSONString(resResult));
    }

    @Test
    public void updateRun(){
        MessageConfig messageConfig = new MessageConfig();
        messageConfig.setId(23L);
        messageConfig.setIsRun(0);
        ResResult resResult = messageConfigService.updateRun(messageConfig, "111");
        System.out.println(JSONObject.toJSONString(resResult));
    }


}
