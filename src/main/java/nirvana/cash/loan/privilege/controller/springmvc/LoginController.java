package nirvana.cash.loan.privilege.controller.springmvc;

import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.util.CookieUtil;
import nirvana.cash.loan.privilege.common.util.GeneratorId;
import nirvana.cash.loan.privilege.common.util.MD5Utils;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.Menu;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.facade.LoginFacade;
import nirvana.cash.loan.privilege.service.DeptProductService;
import nirvana.cash.loan.privilege.service.MenuService;
import nirvana.cash.loan.privilege.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/privilige")
public class LoginController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private DeptProductService deptProductService;

    //登录
    @RequestMapping("/notauth/login")
    public ResResult login(ServerHttpRequest request, ServerHttpResponse response,  String username, String password,String code) {
        User user=null;
        String roleIds=null;
        String roleCodes=null;
        if (StringUtils.isBlank(code)) {
            return ResResult.error("验证码不能为空！");
        }
        String verifyId =  CookieUtil.getCookieValue(request,RedisKeyContant.YOFISHDK_LOGIN_VERIFY_CODE);
        if(StringUtils.isBlank(verifyId)){
            return ResResult.error("验证码已失效！");
        }
        response.addCookie(CookieUtil.buildCookie(RedisKeyContant.YOFISHDK_LOGIN_VERIFY_CODE,"",0));
        String sessionCode = redisService.get(verifyId,String.class);
        redisService.delete(verifyId);
        if (!code.toLowerCase().equals(sessionCode)) {
            return ResResult.error("验证码错误！");
        }
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return ResResult.error("用户名或密码错误！");
        }

        user = this.userService.findByName(username);
        if (user == null || user.getIsDelete() != 0) {
            return ResResult.error("用户名或密码错误！");
        }
        if (User.STATUS_LOCK.equals(user.getStatus())) {
            return ResResult.error("账户被锁定！",ResResult.ACCOUNT_LOCKED);
        }
        // 密码 MD5 加密
        password = MD5Utils.encrypt(username.trim().toLowerCase(), password.trim());
        if (!password.equals(user.getPassword())) {
            return ResResult.error("用户名或密码错误！");
        }

        //查询登录用户角色
        roleIds=userService.findUserRoldIds(user.getUserId().intValue());
        if(StringUtils.isNotBlank(roleIds) && roleIds.split(",").length > 0){
            roleCodes=userService.findUserRoldCodes(roleIds);
        }

        //缓存6小时，登录信息，"#"分割符在其他地方有使用到,不要替换为其他的。
        String jsessionid = user.getUserId()+"#"+GeneratorId.guuid();
        redisService.putWithExpireTime(RedisKeyContant.YOFISHDK_LOGIN_USER_PREFIX+jsessionid,JSON.toJSONString(user),60*60*6L);

        //设置登录sessionId,存入cookies
        response.addCookie(CookieUtil.buildCookie(RedisKeyContant.JSESSIONID,jsessionid));

        // 缓存6小时，用户权限集,主要作用:“按钮显示”
        List<Menu> permissionList = menuService.findUserPermissions(username);
        String userPermissionsKey = RedisKeyContant.YOFISHDK_LOGIN_AUTH_PREFIX + user.getUsername();
        redisService.putWithExpireTime(userPermissionsKey,JSON.toJSONString(permissionList),60*60*6L);

        //更新登录时间
        this.userService.updateLoginTime(username);
        //密码不输出至前端
        user.setPassword(null);
        ResResult res = ResResult.success(user);
        Map<String,Object> otherMap = res.getOther();
        otherMap.put("roleIds",roleIds);
        otherMap.put("roleCodes",roleCodes);
        return res;
    }

    //注销
    @RequestMapping(value = "/notauth/logout")
    public void logout(ServerHttpRequest request,ServerHttpResponse response) {
        String jsessionid = CookieUtil.getCookieValue(request, RedisKeyContant.JSESSIONID);
        if (StringUtils.isNotBlank(jsessionid)) {
            redisService.delete(RedisKeyContant.YOFISHDK_LOGIN_USER_PREFIX + jsessionid);
            response.addCookie(CookieUtil.buildCookie(RedisKeyContant.JSESSIONID, "", 0));
        }
    }

    //是否处于登录状态
    @RequestMapping(value = "/notauth/isLogin")
    public ResResult isLogin(ServerHttpRequest request) {
        if(requestCheck.getLoginUser(request) == null){
            return ResResult.error("未登录", ResResult.LOGIN_WHETHER);
        }
        return ResResult.success("登录中", ResResult.SUCCESS);
    }
}
