package nirvana.cash.loan.privilege.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.CommonContants;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.util.CookieUtil;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.common.util.URLUtil;
import nirvana.cash.loan.privilege.domain.Dept;
import nirvana.cash.loan.privilege.domain.Menu;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.AuthDeptProductInfoVo;
import nirvana.cash.loan.privilege.service.DeptService;
import nirvana.cash.loan.privilege.service.base.RedisService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * Created by Administrator on 2018/7/24.
 */
@Slf4j
@Component
public class RequestCheck {

    @Autowired
    private RedisService redisService;
    @Autowired
    private DeptService deptService;

    //白名单的url,无需登录
    private static final List<String> noLoginUrls = new ArrayList<>();

    static {
        noLoginUrls.add("/privilige/notauth/gifCode");
        noLoginUrls.add("/privilige/notauth/login");
        noLoginUrls.add("/privilige/notauth/isLogin");
        noLoginUrls.add("/privilige/notauth/logout");
        noLoginUrls.add("/privilige/notauth/gateway/hystrixTimeout");
        //监控
        noLoginUrls.add("/actuator/info");
    }

    private static String websocket_url = "/privilege/notauth/webSocket/*";

    //check登录和权限
    public ResResult check(ServerHttpRequest request) {
        String url = request.getURI().getPath();
        //websocket url
        if (URLUtil.isWebsocketUrl(websocket_url, url)) {
            return ResResult.success(null);
        }
        //无需登录接口
        if (URLUtil.isEndsWith(noLoginUrls, url)) {
            return ResResult.success(null);
        }
        //check用户是否登录或登录失效
        User user = this.getLoginUser(request);
        if (user == null) {
            return ResResult.error("登录失效", ResResult.LOGIN_SESSION_TIMEOUT);
        }
        //check用户权限
        if (url.contains("notauth")) {
            //匹配路径:notauth,无需授权可访问
            return ResResult.success(user);
        }
        String userPermissions = redisService.get(RedisKeyContant.YOFISHDK_LOGIN_AUTH_PREFIX + user.getUsername(), String.class);
        if (StringUtils.isBlank(userPermissions)) {
            return ResResult.error("登录失效", ResResult.LOGIN_SESSION_TIMEOUT);
        }
        List<Menu> permissionList = JSONObject.parseArray(userPermissions, Menu.class);
        //logger.info("user menuList:{}",JSON.toJSONString(permissionList));
        long count = permissionList.stream()
                .filter(t -> StringUtils.isNotBlank(t.getPerms()))
                .filter(t -> url.endsWith(t.getPerms().trim()))
                .count();
        if (count == 0) {
            return ResResult.error("您访问的接口未经授权!", ResResult.UNAUTHORIZED_URL);
        }
        return ResResult.success(user);
    }

    public User getLoginUser(ServerHttpRequest request) {
        String jsessionid = CookieUtil.getCookieValue(request, RedisKeyContant.JSESSIONID);
        if (StringUtils.isBlank(jsessionid)) {
            return null;
        }
        String data = redisService.get(RedisKeyContant.YOFISHDK_LOGIN_USER_PREFIX + jsessionid, String.class);
        if (StringUtils.isBlank(data)) {
            return null;
        }
        return JSON.parseObject(data, User.class);
    }

    public Mono<Void> failResBody(ServerHttpResponse response, ResResult resResult) {
        log.info("权限拦截:{}", JSON.toJSONString(resResult));
        //设置headers
        HttpHeaders httpHeaders = response.getHeaders();
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        //设置response body
        String resBody = JSON.toJSONString(resResult);
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(resBody.getBytes());
        return response.writeWith(Mono.just(bodyDataBuffer));
    }


    ///获取运营团队权限信息
    public Map<String, String> findDeptAndProductAuth(User user) {
        Map resmap = new HashMap();
        String authShowIds = CommonContants.default_product_no;
        if(StringUtils.isBlank(user.getDeptId())){
            resmap.put("authShowIds", authShowIds);
            return resmap;
        }
        String[] deptIds = user.getDeptId().split(",");
        if (deptIds.length == 1) {
            Dept dept = deptService.selectByKey(user.getDeptId());
            //登录用户可以查看，管理全部产品
            if (dept.getViewRange() == 0) {
                authShowIds = "";
                resmap.put("authShowIds", authShowIds);
                return resmap;
            }
        }
        //其他，可以管理部门关联的产品
        for (String item : deptIds) {
            AuthDeptProductInfoVo vo = deptService.findAuthDeptProductInfoFromCache(user.getUserId(), Long.valueOf(item));
            if (vo != null && !CommonContants.default_product_no.equals(vo.getProductNos())) {
                authShowIds += vo.getProductNos();
            }
        }
        if (StringUtils.isNotBlank(authShowIds)) {
            Set<String> set = new HashSet<>(Arrays.asList(authShowIds.split(",")));
            authShowIds = StringUtils.join(set, ",");
        }
        resmap.put("authShowIds", authShowIds);
        return resmap;
    }
}
