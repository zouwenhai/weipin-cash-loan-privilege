import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.common.util.GeneratorId;
import nirvana.cash.loan.privilege.domain.MsgList;
import nirvana.cash.loan.privilege.service.MsgListService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/11/5.
 */
public class MsgListServiceTest extends BaseTest {


    @Autowired
    private MsgListService msgListService;

    @Test
    public void findPageList() {
        MsgList msgList  = new MsgList();
        msgList.setUserId(1L);
        List<MsgList> list =  msgListService.findPageList(msgList);
        System.err.println(JSON.toJSONString(list));
    }

    @Test
    public void saveMsg(){
        MsgList msgList  = new MsgList();
        msgList.setUserId(1L);
        msgList.setUuid(GeneratorId.guuid());
        msgList.setMsgModule(1);
        msgList.setContent("测试test2");
        msgListService.saveMsg(msgList);
    }

}
