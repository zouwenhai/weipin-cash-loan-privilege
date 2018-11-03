package nirvana.cash.loan.privilege.web.gatewayhystrix;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.util.ResResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GatewayHystrixCommandController {

    @RequestMapping("/notauth/gateway/hystrixTimeout")
    public ResResult hystrixTimeout() {
        log.error("权限管理路由转发系统|hystrixTimeout,请求超时!");
        return ResResult.error("请求超时!");
    }

    @HystrixCommand(commandKey = "gatewayHystrixCommand")
    public ResResult gatewayHystrixCommand() {
        log.error("权限管理路由转发系统|gatewayHystrixCommand,代理转发异常!");
        return ResResult.error("代理转发异常!");
    }

}