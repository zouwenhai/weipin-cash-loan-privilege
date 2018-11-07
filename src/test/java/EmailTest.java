import nirvana.cash.loan.privilege.common.util.EmaiUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/7.
 */
public class EmailTest extends BaseTest {

    @Autowired
    private EmaiUtil emaiUtil;

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
}
