package nirvana.cash.loan.privilege.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.service.RedisService;
import nirvana.cash.loan.privilege.common.util.CookieUtil;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2018/7/24.
 */
@Component
public class RequestCheck {
    private static final Logger logger = LoggerFactory.getLogger(RequestCheck.class);

    @Autowired
    private RedisService redisService;

    //check登录和权限
    public ResResult check(HttpServletRequest request) {
        //1:check用户是否登录或登录失效
        String jsessionid = CookieUtil.getCookieValue(request, RedisKeyContant.JSESSIONID);
        if (jsessionid == null || jsessionid.trim().length() == 0) {
            return ResResult.error("您未进行登录操作或登录超时!",ResResult.LOGIN_SESSION_TIMEOUT);
        }
        String data = redisService.get(RedisKeyContant.YOFISHDK_LOGIN_USER_PREFIX+jsessionid,String.class);
        if (StringUtils.isBlank(data)) {
            return ResResult.error("您未进行登录操作或登录超时!",ResResult.LOGIN_SESSION_TIMEOUT);
        }

        String url = request.getRequestURL().toString();
        User user = JSON.parseObject(data, User.class);

        //2:check用户权限
        if(url.contains("notauth")){
            //匹配路径:notauth,无需授权可访问
            return ResResult.success(user);
        }

        String userPermissions = redisService.get(RedisKeyContant.YOFISHDK_LOGIN_AUTH_PREFIX + user.getUsername(),String.class);
        if (StringUtils.isBlank(userPermissions)) {
            return ResResult.error("您访问的接口未经授权或登录超时!",ResResult.LOGIN_SESSION_TIMEOUT);
        }
        List<Menu> permissionList = JSONObject.parseArray(userPermissions, Menu.class);
        //logger.info("user menuList:{}",JSON.toJSONString(permissionList));
        boolean priviligeFlag = false;
        for (Menu menu : permissionList) {
            if (StringUtils.isBlank(menu.getPerms())) {
                continue;
            }
            priviligeFlag = url.endsWith(menu.getPerms().trim());
            if (priviligeFlag) {
                break;
            }
        }
        if (!priviligeFlag) {
            return ResResult.error("您访问的接口未经授权!",ResResult.UNAUTHORIZED_URL);
        }
        return ResResult.success(user);
    }

    public String printArray(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
