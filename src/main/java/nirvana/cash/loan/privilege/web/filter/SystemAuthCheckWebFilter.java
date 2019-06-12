package nirvana.cash.loan.privilege.web.filter;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.CommonContants;
import nirvana.cash.loan.privilege.common.util.GeneratorId;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.common.util.URLUtil;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.TbYofishdkOptionLogDto;
import nirvana.cash.loan.privilege.mq.message.RabbitMqSender;
import nirvana.cash.loan.privilege.web.RequestCheck;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.Map;

/**
 * 系统本身权限(包括登录)校验
 */
@Slf4j
@Component
public class SystemAuthCheckWebFilter implements WebFilter {

    @Autowired
    private RequestCheck requestCheck;

    @Value("${rabbitmq.exchange.collLog}")
    private String collLogExchange;

    @Value("${rabbitmq.routingkey.collLog}")
    private String collLogRoutingkey;

    @Autowired
    RabbitMqSender rabbitMqSender;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        URI uri = request.getURI();
        String traceId = GeneratorId.guuid();
        if (!uri.toString().contains("password")) {
            log.info("privilege|request traceId={},uri={}", traceId, uri);
        } else {
            log.info("privilege|request traceId={},path={}", traceId, uri.getPath());
        }
        //check登录和权限
        ResResult checkResResult = requestCheck.check(request);
        if (!ResResult.SUCCESS.equals(checkResResult.getCode())) {
            return requestCheck.failResBody(response, checkResResult);
        }
        //无需登录接口，执行继续
        if (checkResResult.getData() == null) {
            return webFilterChain.filter(exchange);
        }
        //添加请求头信息，执行继续
        User user = (User) checkResResult.getData();

        //获取运营团队权限信息
        String authDeptIds = StringUtils.isNotBlank(user.getDeptId()) ? user.getDeptId() : CommonContants.none_dept_id;
        if (user.getViewRange() == 0) {
            authDeptIds = CommonContants.all_dept_id;
        }
        //获取运营产品权限信息
        Map<String, String> deptAndProductAuth = requestCheck.findDeptAndProductAuth(user);
        String authShowIds = deptAndProductAuth.get("authShowIds");
        log.info("当前请求:traceId={},用户ID:{},部门ID:{},管理的产品showId={}", traceId, user.getUserId(), authDeptIds, authShowIds);

        ServerHttpRequest host = null;
        host = exchange.getRequest()
                .mutate()
                .header(CommonContants.gateway_trace_id, traceId)
                .header("loginName", user.getUsername())
                .header("userName", URLUtil.encode(user.getName(), "utf-8"))
                .header("authShowIds", CommonContants.all_product_no.equals(authShowIds) ? "" : authShowIds)
                .header("authDeptIds", CommonContants.all_dept_id.equals(authDeptIds) ? "" : authDeptIds)
                .build();
        ServerWebExchange build = exchange.mutate().request(host).build();


        try {

            if (uri.toString().contains("/privilige/user/updatePassword") ||
                    uri.toString().contains("/privilige/user/add") ||
                    uri.toString().contains("/privilige/user/delete") ||
                    uri.toString().contains("/privilige/user/update") ||
                    uri.toString().contains("/privilige/role/add") ||
                    uri.toString().contains("/privilige/role/update") ||
                    uri.toString().contains("/privilige/role/delete") ||
                    uri.toString().contains("/privilige/dept/add") ||
                    uri.toString().contains("/privilige/dept/update") ||
                    uri.toString().contains("/privilige/menu/add") ||
                    uri.toString().contains("/privilige/menu/update") ||
                    uri.toString().contains("/privilige/menu/delete") ||
                    uri.toString().contains("/web/collection/call/realPhone")
//                    uri.toString().contains("/privilige/user/updatePassword") ||
//                    uri.toString().contains("/privilige/user/updatePassword") ||

            ) {
                TbYofishdkOptionLogDto logDto = new TbYofishdkOptionLogDto();
                logDto.setOptionUrl(uri.toString());
                logDto.setCreateTime(new Date());
                logDto.setParams(JSONObject.toJSONString(request.getQueryParams()));
                log.info("保存请求参数1 params={}", request.getQueryParams().toString());
                log.info("保存请求参数2 params={}", JSONObject.toJSONString(request.getBody()));
                log.info("保存请求参数3 params={}", JSONObject.toJSONString(exchange.getFormData()));
                log.info("保存请求参数4 params={}", JSONObject.toJSONString(exchange.getAttributes()));



                if (StringUtils.isEmpty(user.getName())) {
                    logDto.setUsername(URLUtil.decode("用户名未获取到", "utf-8"));
                } else {
                    logDto.setUsername(URLUtil.decode(user.getName(), "utf-8"));
                }
                logDto.setOptionIp(request.getRemoteAddress().toString());
                log.info("进入日志记录阶段！！！");
                String desc = "";
                if (uri.toString().contains("/privilige/user/updatePassword")) {
                    desc = user.getName() + "修改了密码";
                } else if (uri.toString().contains("/privilige/user/add")) {
                    desc = user.getName() + "新增用户";
                } else if (uri.toString().contains("/privilige/user/delete")) {
                    desc = user.getName() + "删除用户";
                } else if (uri.toString().contains("/privilige/user/update")) {
                    desc = user.getName() + "修改用户";
                } else if (uri.toString().contains("/privilige/role/add")) {
                    desc = user.getName() + "新增角色";
                } else if (uri.toString().contains("/privilige/role/update")) {
                    desc = user.getName() + "修改角色";
                } else if (uri.toString().contains("/privilige/role/delete")) {
                    desc = user.getName() + "删除角色";
                } else if (uri.toString().contains("/privilige/dept/add")) {
                    desc = user.getName() + "新增部门";
                } else if (uri.toString().contains("/privilige/dept/update")) {
                    desc = user.getName() + "修改部门";
                } else if (uri.toString().contains("/privilige/menu/add")) {
                    desc = user.getName() + "新增菜单或按钮";
                } else if (uri.toString().contains("/privilige/menu/update")) {
                    desc = user.getName() + "更新菜单";
                } else if (uri.toString().contains("/privilige/menu/delete")) {
                    desc = user.getName() + "删除菜单";
                } else if (uri.toString().contains("/web/collection/call/realPhone")) {
                    desc = user.getName() + "获取了真实号码";
                }
                logDto.setOptionDesc(URLUtil.decode(desc, "utf-8"));
                String collLog = JSONObject.toJSONString(logDto);
                log.info("需要发送Mq的内容={}", collLog);

                rabbitMqSender.send(collLogExchange, collLogRoutingkey, collLog);
            }
        } catch (Exception e) {
            log.error("权限系统日志记录失败!!!", e);
        }

        return webFilterChain.filter(build);
    }

}
