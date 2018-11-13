import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import nirvana.cash.loan.privilege.common.enums.MsgModuleEnum;
import nirvana.cash.loan.privilege.common.util.DateUtil;
import nirvana.cash.loan.privilege.common.util.FreemarkerUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/8.
 */
public class FreemakerTest extends BaseTest {

    @Autowired
    FreemarkerUtil freemarkerUtil;

    @Test
    public void resolveTest(){
        Map<String,String> msgmap=new HashMap();
        msgmap.put("userName", "aaa");
        msgmap.put("msgModule",MsgModuleEnum.MACHINE_FAIL.getName());
        msgmap.put("orderId","20181103000126");
        msgmap.put("orderStatus","交易成功");
        msgmap.put("time", DateUtil.getDateTime());
        String res = freemarkerUtil.resolve("email_notice_msg.ftl",msgmap);
        System.err.println(res);
    }
}
