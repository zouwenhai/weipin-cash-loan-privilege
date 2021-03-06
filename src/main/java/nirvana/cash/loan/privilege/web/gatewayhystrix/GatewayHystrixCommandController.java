package nirvana.cash.loan.privilege.web.gatewayhystrix;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.CommonContants;
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
        log.error("权限管理路由转发系统|hystrixTimeout,请求超时! traceId={}",request.getHeaders().getFirst(CommonContants.gateway_trace_id));
        return ResResult.error("请求超时!");
    }

}