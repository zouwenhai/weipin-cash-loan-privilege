import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class V6Test extends BaseTest {

    @Autowired
    UserService userService;

    @Test
    public void userService_findAllLikeDeptId(){
        List<User> userList = userService.findAllLikeDeptId(178L);
        System.err.println(JSON.toJSONString(userList));
    }


}
