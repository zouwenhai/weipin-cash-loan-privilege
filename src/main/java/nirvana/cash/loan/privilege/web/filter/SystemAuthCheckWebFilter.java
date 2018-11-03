package nirvana.cash.loan.privilege.web.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.common.util.URLUtil;
import nirvana.cash.loan.privilege.web.RequestCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统本身权限(包括登录)校验
 */
@Slf4j
@Component
public class SystemAuthCheckWebFilter implements WebFilter {

    @Autowired
    private RequestCheck requestCheck;

    private static final List<String> noLoginUrls = new ArrayList<>();
    static {
        noLoginUrls.add("/privilige/notauth/gifCode");
        noLoginUrls.add("/privilige/notauth/login");
        noLoginUrls.add("/privilige/notauth/isLogin");
        noLoginUrls.add("/privilige/notauth/gateway/hystrixTimeout");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain webFilterChain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        URI uri = request.getURI();
        String url = uri.getPath();
        //无需登录接口
        if(URLUtil.isEndsWith(noLoginUrls,url)){
            return webFilterChain.filter(exchange);
        }
        //check登录和权限
        ResResult checkResResult = requestCheck.check(request);
        if(!ResResult.SUCCESS.equals(checkResResult.getCode())){
            return requestCheck.failResBody(response,checkResResult);
        }
        return webFilterChain.filter(exchange);
    }

}
