package nirvana.cash.loan.privilege.system.controller;

import com.alibaba.fastjson.JSON;
import com.caiyi.common.security.CaiyiEncrypt;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.util.CookieUtil;
import nirvana.cash.loan.privilege.common.util.GeneratorId;
import nirvana.cash.loan.privilege.common.util.MD5Utils;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.service.LogoutUserService;
import nirvana.cash.loan.privilege.system.service.MenuService;
import nirvana.cash.loan.privilege.system.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/privilige")
public class LoginController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private LogoutUserService logoutUserService;

    //登录
    @RequestMapping("/notauth/login")
    public ResResult login(HttpServletRequest request, HttpServletResponse response, String username, String password, String code) {
        User user=null;
        String roleIds=null;
        try {
            if (StringUtils.isBlank(code)) {
                return ResResult.error("验证码不能为空！");
            }
            String sessionCode = redisService.get("_code",String.class);
            redisService.delete("_code");
            if (!code.toLowerCase().equals(sessionCode)) {
                return ResResult.error("验证码错误！");
            }
            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                return ResResult.error("用户名或密码错误！");
            }

            user = this.userService.findByName(username);
            if (user == null) {
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

            //缓存2小时，登录信息
            String jsessionid = user.getUserId()+"#"+GeneratorId.guuid();
            jsessionid = URLEncoder.encode(CaiyiEncrypt.encryptStr(jsessionid),"UTF-8");
            redisService.putWithExpireTime(jsessionid,JSON.toJSONString(user),60*60*2L);
            //redisService.put(RedisKeyContant.YOFISHDK_LOGIN_USER_PREFIX+jsessionid,JSON.toJSONString(user));
            //设置登录sessionId,存入cookies
            CookieUtil.setCookie(request, response, RedisKeyContant.JSESSIONID, jsessionid);

            // 缓存2小时，用户权限集,主要作用:“按钮显示”
            List<Menu> permissionList = menuService.findUserPermissions(username);
            String userPermissionsKey = RedisKeyContant.YOFISHDK_LOGIN_AUTH_PREFIX + user.getUsername();
            redisService.putWithExpireTime(userPermissionsKey,JSON.toJSONString(permissionList),60*60*2L);
            //redisService.put(userPermissionsKey,JSON.toJSONString(permissionList));
            //logger.info("user menuList:{}",JSON.toJSONString(permissionList));

            //更新登录时间
            this.userService.updateLoginTime(username);

        } catch (Exception ex) {
            logger.error("执行登录操作异常:{}", ex);
            return ResResult.error("程序异常！");
        }
        //密码不输出至前端
        user.setPassword(null);
        ResResult res = ResResult.success(user);
        res.getOther().put("roleIds",roleIds);
        return res;
    }

    //注销
    @RequestMapping(value = "/notauth/logout")
    public void logout(HttpServletResponse response, HttpServletRequest request) {
        try {
              User user = this.getLoginUser(request);
              if(user != null){
                  //清除Cookies缓存
                  CookieUtil.deleteCookie(request,response,RedisKeyContant.JSESSIONID);
                  logoutUserService.logoutUser(user.getUserId());
              }
        } catch (Exception e) {
            logger.error("注销失败:{}",e);
        }
    }

}
