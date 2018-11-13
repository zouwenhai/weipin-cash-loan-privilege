import nirvana.cash.loan.privilege.common.enums.MsgModuleEnum;
import nirvana.cash.loan.privilege.common.enums.OrderStatusEnum;
import nirvana.cash.loan.privilege.common.util.DateUtil;
import nirvana.cash.loan.privilege.common.util.EmaiUtil;
import nirvana.cash.loan.privilege.common.util.FreemarkerUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/7.
 */
public class EmailTest extends BaseTest {

    @Autowired
    private EmaiUtil emaiUtil;
    @Autowired
    FreemarkerUtil freemarkerUtil;

    @Test
    public void sendEmail() {
        String fromAddress = "liuxiaowei@yofish.com";
        List<String> toAddresList = new ArrayList<>();
        toAddresList.add("3165881299@qq.com");
        toAddresList.add("724615445@qq.com");
        String title = "消息中心|邮件发送测试1";
        String emailContent = "测试邮件，合作愉快！";
        emaiUtil.sendEmail(fromAddress, toAddresList, title, emailContent);
        System.err.println("done");
    }

    @Test
    public void sendEmailHtml(){
        String fromAddress = "liuxiaowei@yofish.com";
        List<String> toAddresList = new ArrayList<>();
        toAddresList.add("liuxiaowei@yofish.com");
        //邮件标题
        String title = MsgModuleEnum.CHECK_COLL.getName()+"模块-有新订单需要您处理";
        //邮件内容
        Map<String,String> msgmap=new HashMap();
        msgmap.put("msgModule", MsgModuleEnum.CHECK_COLL.getName());
        msgmap.put("orderId","20180929000015");
        msgmap.put("orderStatus", OrderStatusEnum.LoanFailed.getDesc());
        msgmap.put("time", DateUtil.getDateTime());
        msgmap.put("userName", "刘晓伟");
        String emailContent = freemarkerUtil.resolve("email_notice_msg.ftl",msgmap);
        //发送邮件
        emaiUtil.sendEmailHtml(fromAddress, toAddresList, title, emailContent);
        System.err.println("done");
    }
}
