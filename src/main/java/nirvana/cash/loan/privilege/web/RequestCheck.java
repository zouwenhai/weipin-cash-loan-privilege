package nirvana.cash.loan.privilege.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.CommonContants;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.exception.BizException;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.common.util.URLUtil;
import nirvana.cash.loan.privilege.domain.CacheDto;
import nirvana.cash.loan.privilege.domain.Menu;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.AuthDeptProductInfoVo;
import nirvana.cash.loan.privilege.service.AuthCacheService;
import nirvana.cash.loan.privilege.service.DeptService;
import nirvana.cash.loan.privilege.service.MenuService;
import nirvana.cash.loan.privilege.service.base.RedisService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
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
    @Autowired
    private AuthCacheService authCacheService;
    @Autowired
    private MenuService menuService;

    //白名单的url,无需登录
    private static final List<String> noLoginUrls = new ArrayList<>();

    static {
        noLoginUrls.add("/privilige/notauth/gifCode");
        noLoginUrls.add("/privilige/notauth/login");
        noLoginUrls.add("/privilige/notauth/isLogin");
        noLoginUrls.add("/privilige/notauth/logout");
        noLoginUrls.add("/privilige/notauth/gateway/hystrixTimeout");
        noLoginUrls.add("/privilige/user/findByLoginName");
        noLoginUrls.add("/privilige/user/getAuditUser");
        noLoginUrls.add("/privilige/user/getAuditUserById");
        noLoginUrls.add("/privilige/user/isDivideOrder");
        noLoginUrls.add("/privilige/user/getPageAuditUser");
        //监控
        noLoginUrls.add("/actuator/info");
        noLoginUrls.add("/actuator/health");
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
        List<Menu> permissionList = null;
        try {
            String userPermissions = redisService.get(RedisKeyContant.YOFISHDK_LOGIN_AUTH_PREFIX + user.getUsername(), String.class);
            log.info("userPermissions:{}", userPermissions);
            if (StringUtils.isBlank(userPermissions)) {
                return ResResult.error("登录失效", ResResult.LOGIN_SESSION_TIMEOUT);
            }
            permissionList = JSONObject.parseArray(userPermissions, Menu.class);
        } catch (Exception ex) {
            log.error("获取权限集发生异常:{}", ex);
            //从数据库直接获取一次
            String username = user.getUsername();
            permissionList = menuService.findUserPermissions(username);
        }
        //logger.info("user menuList:{}",JSON.toJSONString(permissionList));

        long count = permissionList.stream()
                .filter(t -> StringUtils.isNotBlank(t.getPerms()))
                .filter(t -> url.endsWith(t.getPerms().trim()))
                .count();
        if (count == 0) {
            log.info("url:{}", url);
            log.info("permissionList:{}", permissionList);
            return ResResult.error("您访问的接口未经授权!", ResResult.UNAUTHORIZED_URL);
        }
        return ResResult.success(user);
    }

    public User getLoginUser(ServerHttpRequest request) {
        String jsessionid = URLUtil.getHeader(request, RedisKeyContant.JSESSIONID);
        if (StringUtils.isBlank(jsessionid)) {
            return null;
        }
        String data = "";
        try {
            //从缓存获取登陆信息
            data = redisService.get(RedisKeyContant.YOFISHDK_LOGIN_USER_PREFIX + jsessionid, String.class);
            if (StringUtils.isBlank(data)) {
                return null;
            }
        } catch (Exception ex) {
            log.error("从缓存获取登录信息失败:{}", ex);
            //从数据库直接获取一次
            CacheDto cacheDto = authCacheService.findOne(jsessionid);
            if (cacheDto != null) {
                data = cacheDto.getValue();
            }
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


    /**
     * 获取运营团队权限信息
     * 1.登录用户未配置部门，用户不管理任何产品， authShowIds = "0"
     * 2.登录用户配置了部门，且关联的部门viewRange=0.则可以管理所有产品， authShowIds = "all"
     * 3.其他，以登录用户所关联的部门配置的产品为准。关联的产品即为登录用户可管理的产品
     *
     * @param user
     * @return
     */
    public Map<String, String> findDeptAndProductAuth(User user) {
        Map resmap = new HashMap();
        if (user.getViewRange() == 0) {
            resmap.put("authShowIds", CommonContants.all_product_no);
            return resmap;
        }
        if (StringUtils.isBlank(user.getDeptId())) {
            resmap.put("authShowIds", CommonContants.none_product_no);
            return resmap;
        }
        //用户配置了部门
        String authShowIds = "";
        String[] deptIds = user.getDeptId().split(",");
        Set<String> set = new HashSet<>();
        for (String deptId : deptIds) {
            AuthDeptProductInfoVo vo = deptService.findAuthDeptProductInfoFromCache(user.getUserId(), Long.valueOf(deptId));
            //用户可以管理所有产品，方法直接返回！
            if (CommonContants.all_product_no.equals(vo.getProductNos())) {
                resmap.put("authShowIds", CommonContants.all_product_no);
                return resmap;
            }
            //用户可以管理部分产品，汇总用户可管理的产品
            if (!CommonContants.none_product_no.equals(vo.getProductNos())) {
                authShowIds += vo.getProductNos();
                Set<String> itemSet = new HashSet<>(Arrays.asList(authShowIds.split(",")));
                set.addAll(itemSet);
            }
        }
        if (!CollectionUtils.isEmpty(set)) {
            authShowIds = StringUtils.join(set, ",");
        } else {
            authShowIds = CommonContants.none_product_no;
        }
        resmap.put("authShowIds", authShowIds);
        return resmap;
    }
}
