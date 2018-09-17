package nirvana.cash.loan.privilege.fegin;

import nirvana.cash.loan.privilege.fegin.facade.RiskUserAddApiFacade;
import nirvana.cash.loan.privilege.fegin.facade.RiskUserUpdateApiFacade;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 风控系统API调用
 * Created by Administrator on 2018/8/2.
 */
@FeignClient(value = "yofishdk-cash-loan-api", fallback = HystrixRiskApi.class)
public interface FeginRiskApi {

    @RequestMapping(value = "/api/order/addOrderUser", method = RequestMethod.POST, consumes = "application/json")
    NewResponseUtil addOrderUser(@RequestBody RiskUserAddApiFacade facade);

    @RequestMapping(value = "/api/order/updateOrderUser", method = RequestMethod.POST, consumes = "application/json")
    NewResponseUtil updateOrderUser(@RequestBody RiskUserUpdateApiFacade facade);
}
