import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.service.RedisService;
import nirvana.cash.loan.privilege.common.util.GeneratorId;
import nirvana.cash.loan.privilege.system.service.LogoutUserService;
import nirvana.cash.loan.privilege.system.service.RoleMenuServie;
import nirvana.cash.loan.privilege.system.service.UserRoleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/8/29.
 */
public class LoginTest extends BaseTest {

    @Autowired
    LogoutUserService logoutUserService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleMenuServie roleMenuService;
    @Autowired
    private RedisService redisService;

    @Test
    public void findAllLoginJsessionid() {
//        String jsessionid = "1000"+"#"+ GeneratorId.guuid();
//        redisService.putWithExpireTime(RedisKeyContant.YOFISHDK_LOGIN_USER_PREFIX+jsessionid,"XXX",60*60*2L);

        Set<String> keys = logoutUserService.findAllLoginJsessionid();
        System.err.println(JSON.toJSONString(keys));
        System.err.println("done");
    }

    @Test
    public void findJsessionidByUserId() {
        long userId = 32;
        String jsessionid = logoutUserService.findJsessionidByUserId(userId, null);
        System.err.println(jsessionid);
    }

    @Test
    public void logoutDeleteUser() {
        long userId = 32;
        logoutUserService.logoutUser(userId);
        System.err.println("done");
    }

    @Test
    public void findUserIdListByRoleId() {
        long roleId = 21;
        List<Long> list = userRoleService.findUserIdListByRoleId(roleId);
        System.err.println(JSON.toJSONString(list));
    }

    @Test
    public void findUserIdListByMenuId() {
        long menuId = 1086;
        List<Long> userIdList = roleMenuService.findUserIdListByMenuId(menuId);
        System.err.println(JSON.toJSONString(userIdList));
    }

}
