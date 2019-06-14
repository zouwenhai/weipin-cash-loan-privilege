import com.alibaba.fastjson.JSONObject;
import nirvana.cash.loan.privilege.Application;
import nirvana.cash.loan.privilege.fegin.FeginCashLoanApi;
import nirvana.cash.loan.privilege.fegin.NewResponseUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by JinYunGang
 * on 2019/6/14 14:25
 **/
@RunWith(SpringRunner.class)

@SpringBootTest(classes = Application.class)
public class MobileNoTest {

    @Autowired
    FeginCashLoanApi feginCashLoanApi;

    @Test
    public void MobileNoTest() {

        long id = 7852903;
        NewResponseUtil mobile = feginCashLoanApi.realNo(id);
        NewResponseUtil mobile1 = feginCashLoanApi.realNo(id);
    }

    public static void main(String[] args) {
        String U = "{\"id\":[\"7852943\"]}";
        String uri = "http://10.0.33.14:13002/yofishdk/cash-loan-web/web/customerInfo/realNo?id=7852943";

        String idStr = uri.toString().substring(uri.toString().indexOf("id=")+3);
//        String idStr = JSONObject.parseObject(U).getString("id");
//        String idStr = JSONObject.parseObject(U).getString("id");
//        idStr.replace(/[^0-9]/g, "")
        String idSt1r = JSONObject.parseObject(U).getString("id");
    }
    public void Test() {

    }


}
