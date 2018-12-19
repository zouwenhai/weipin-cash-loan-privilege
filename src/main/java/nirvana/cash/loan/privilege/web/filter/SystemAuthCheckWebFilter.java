package nirvana.cash.loan.privilege.web.filter;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.common.util.URLUtil;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.service.DeptProductService;
import nirvana.cash.loan.privilege.web.RequestCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 系统本身权限(包括登录)校验
 */
@Slf4j
@Component
public class SystemAuthCheckWebFilter implements WebFilter {

    @Autowired
    private RequestCheck requestCheck;
    @Autowired
    private DeptProductService deptProductService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        URI uri = request.getURI();
        log.info("privilege|request uri={}",uri);
        //check登录和权限
        ResResult checkResResult = requestCheck.check(request);
        if(!ResResult.SUCCESS.equals(checkResResult.getCode())){
            return requestCheck.failResBody(response,checkResResult);
        }
        //无需登录接口，执行继续
        if(checkResResult.getData() == null){
            return webFilterChain.filter(exchange);
        }
        //添加请求头信息，执行继续
        User user = (User) checkResResult.getData();
        //产品编号
        String productNos = deptProductService.findProductNosByDeptIdFromCache(user.getDeptId());
        ServerHttpRequest host = null;
        host = exchange.getRequest()
                .mutate()
                .header("loginName", user.getUsername())
                .header("userName", URLUtil.encode(user.getName(), "utf-8"))
                .header("authShowIds",productNos)
                .header("authDeptId",user.getDeptId()!=null?user.getDeptId().toString():"0")
                .build();
        ServerWebExchange build = exchange.mutate().request(host).build();
        return webFilterChain.filter(build);
    }

}
