package nirvana.cash.loan.privilege.system.controller;

import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.common.config.FebsProperies;
import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.domain.ResponseBo;
import nirvana.cash.loan.privilege.common.util.CookieUtil;
import nirvana.cash.loan.privilege.common.util.GeneratorId;
import nirvana.cash.loan.privilege.common.util.MD5Utils;
import nirvana.cash.loan.privilege.common.util.vcode.Captcha;
import nirvana.cash.loan.privilege.common.util.vcode.GifCaptcha;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.service.MenuService;
import nirvana.cash.loan.privilege.system.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/privilige")
public class LoginController extends BaseController {
    @Autowired
    private FebsProperies febsProperies;
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;

    @RequestMapping("/login")
    public ResponseBo login(HttpServletRequest request, HttpServletResponse response, String username, String password, String code) {
        User user=null;
        try {
//            if (StringUtils.isBlank(code)) {
//                return ResponseBo.warn("验证码不能为空！");
//            }
//            String sessionCode = redisService.get("_code");
//            redisService.del("_code");
//            if (!code.toLowerCase().equals(sessionCode)) {
//                return ResponseBo.warn("验证码错误！");
//            }
            if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
                return ResponseBo.warn("用户名或密码错误！");
            }

            user = this.userService.findByName(username);
            if (user == null) {
                return ResponseBo.error("用户名或密码错误！");
            }
            if (User.STATUS_LOCK.equals(user.getStatus())) {
                ResponseBo.accountLocked("账号已被锁定,请联系管理员！");
            }
            // 密码 MD5 加密
            password = MD5Utils.encrypt(username.trim().toLowerCase(), password.trim());
            if (!password.equals(user.getPassword())) {
                return ResponseBo.error("用户名或密码错误！");
            }

            //缓存2小时，登录信息
            String jsessionid = GeneratorId.guuid();
            redisService.set(jsessionid, JSON.toJSONString(user));
            redisService.pexpire(jsessionid, 1000 * 60 * 7200L);
            //设置登录sessionId,存入cookies
            CookieUtil.setCookie(request, response, JSESSIONID, jsessionid);

            // 缓存2小时，用户权限集
            List<Menu> permissionList = menuService.findUserPermissions(username);
            String userPermissionsKey = "userPermissions-" + user.getUsername();
            redisService.set(userPermissionsKey, JSON.toJSONString(permissionList));
            redisService.pexpire(userPermissionsKey, 1000 * 60 * 7200L);
            logger.info("user menuList:{}",JSON.toJSONString(permissionList));
            //更新登录时间
            this.userService.updateLoginTime(username);

        } catch (Exception ex) {
            logger.error("执行登录操作异常:{}", ex);
            return ResponseBo.error("程序异常！");
        }
        //密码不输出至前端
        user.setPassword(null);
        return ResponseBo.ok(user);
    }

    @GetMapping(value = "/notauth/gifCode")
    public void getGifCode(HttpServletResponse response, HttpServletRequest request) {
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/gif");

            Captcha captcha = new GifCaptcha(
                    febsProperies.getValidateCode().getWidth(),
                    febsProperies.getValidateCode().getHeight(),
                    febsProperies.getValidateCode().getLength());
            captcha.out(response.getOutputStream());
            //图形验证码,缓存5min
            redisService.set("_code", captcha.text().toLowerCase());
            redisService.pexpire("_code", 1000 * 60 * 5L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
