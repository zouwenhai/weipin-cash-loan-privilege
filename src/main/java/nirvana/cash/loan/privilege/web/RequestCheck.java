package nirvana.cash.loan.privilege.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import nirvana.cash.loan.privilege.common.domain.ResponseBo;
import nirvana.cash.loan.privilege.common.service.RedisService;
import nirvana.cash.loan.privilege.common.util.CookieUtil;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Administrator on 2018/7/24.
 */
@Component
public class RequestCheck {
    private static final Logger logger = LoggerFactory.getLogger(RequestCheck.class);
    private final static String JSESSIONID = "JSESSIONID";

    @Autowired
    private RedisService redisService;

    //check登录和权限
    public ResponseBo check(HttpServletRequest request) {
        //1:check用户是否登录或登录失效
        String jsessionid = CookieUtil.getCookieValue(request, JSESSIONID);
        if (jsessionid == null || jsessionid.trim().length() == 0) {
            return ResponseBo.loginSessionTimeout("您未进行登录操作或登录超时");
        }
        if (!redisService.exists(jsessionid)) {
            return ResponseBo.loginSessionTimeout("您未进行登录操作或登录超时");
        }
        String data = redisService.get(jsessionid);
        if (StringUtils.isBlank(data)) {
            return ResponseBo.loginSessionTimeout("您未进行登录操作或登录超时");
        }
        //check用户权限
        String url = request.getRequestURL().toString();
        User user = JSON.parseObject(data, User.class);
        String userPermissions = redisService.get("userPermissions-" + user.getUsername());
        if (StringUtils.isBlank(userPermissions)) {
            return ResponseBo.unauthorizedUrl("您访问的接口未经授权！");
        }
        List<Menu> permissionList = JSONObject.parseArray(userPermissions, Menu.class);
        logger.info("user menuList:{}",JSON.toJSONString(permissionList));
        boolean priviligeFlag = false;
        for (Menu menu : permissionList) {
            priviligeFlag = url.contains(menu.getPerms());
            if (priviligeFlag) break;
        }
        if (!priviligeFlag) {
            return ResponseBo.unauthorizedUrl("您访问的接口未经授权！");
        }
        return ResponseBo.ok(user);
    }

}
