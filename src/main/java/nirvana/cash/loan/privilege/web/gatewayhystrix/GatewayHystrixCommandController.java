package nirvana.cash.loan.privilege.web.gatewayhystrix;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.util.ResResult;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/privilige")
public class GatewayHystrixCommandController {

    @RequestMapping("/notauth/gateway/hystrixTimeout")
    public ResResult hystrixTimeout(ServerHttpRequest request) {
        log.info("RemoteAddress={}",request.getRemoteAddress());
        log.info("URI={}",request.getURI());
        log.info("Path={}",request.getPath());
        log.info("Headers={}", JSON.toJSONString(request.getHeaders()));
        log.info("QueryParams={}",JSON.toJSONString(request.getQueryParams()));
        log.error("权限管理路由转发系统|hystrixTimeout,请求超时!");
        return ResResult.error("请求超时!");
    }

}