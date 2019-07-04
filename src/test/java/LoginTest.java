import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.service.base.RedisService;
import nirvana.cash.loan.privilege.service.LogoutUserService;
import nirvana.cash.loan.privilege.service.RoleMenuServie;
import nirvana.cash.loan.privilege.service.UserRoleService;
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
    public void deleteWithPattern(){
        redisService.deleteWithPattern(RedisKeyContant.JSESSIONID);
        redisService.deleteWithPattern(RedisKeyContant.YOFISHDK_LOGIN_USER_PREFIX);
        redisService.deleteWithPattern(RedisKeyContant.YOFISHDK_LOGIN_AUTH_PREFIX);
        redisService.deleteWithPattern(RedisKeyContant.yofishdk_msg_notice_config);
        redisService.deleteWithPattern(RedisKeyContant.yofishdk_auth_productnos_prefix);
        redisService.deleteWithPattern(RedisKeyContant.yofishdk_auth_deptname_prefix);

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
