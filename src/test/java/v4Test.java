import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/16.
 */
public class v4Test extends BaseTest {

    @Autowired
    private UserService userService;

    @Test
    public void updateUserTest(){
        User user = new User();
        user.setUserId(2L);
        user.setUsername("liuxiaowei");
        user.setName("催收人员-刘晓伟6");
        user.setEmail("893384148@qq.com");
        user.setMobile("18621264987");
        user.setStatus("1");
        user.setSsex("0");
        List<Long> roles= new ArrayList<>();
        roles.add(5L);
        roles.add(4L);
        Long loginUserId = 2L;
        String username = "liuxiaowei";
        userService.updateUser(user,roles,loginUserId,username);
        System.err.println("done");
    }
}
