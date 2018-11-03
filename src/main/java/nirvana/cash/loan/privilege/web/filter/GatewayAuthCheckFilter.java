package nirvana.cash.loan.privilege.web.filter;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.common.util.URLUtil;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.web.RequestCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;


/**
 * 网关代理,权限(包括登录)校验
 */
@Slf4j
@Component
public class GatewayAuthCheckFilter extends AbstractGatewayFilterFactory<Object> {

    @Autowired
    private RequestCheck requestCheck;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            //check登录和权限
            ResResult checkResResult = requestCheck.check(request);
            if (!ResResult.SUCCESS.equals(checkResResult.getCode())) {
                return requestCheck.failResBody(response, checkResResult);
            }
            //添加请求头信息，执行继续
            User user = (User) checkResResult.getData();
            ServerHttpRequest host = null;
            host = exchange.getRequest()
                    .mutate()
                    .header("loginName", user.getUsername())
                    .header("userName", URLUtil.encode(user.getName(), "utf-8"))
                    .build();
            ServerWebExchange build = exchange.mutate().request(host).build();
            return chain.filter(build);
        };
    }


}