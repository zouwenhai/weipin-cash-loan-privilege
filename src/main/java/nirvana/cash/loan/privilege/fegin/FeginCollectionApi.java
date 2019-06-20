package nirvana.cash.loan.privilege.fegin;

import nirvana.cash.loan.privilege.fegin.facade.UserAddApiFacade;
import nirvana.cash.loan.privilege.fegin.facade.UserUpdateApiFacade;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 催收系统API调用
 * Created by Administrator on 2018/8/2.
 */
@FeignClient(value = "weipin-cash-loan-collection-web", fallback = HystrixCollectionApi.class)
public interface FeginCollectionApi {

    @RequestMapping(value = "/api/user/addUser", method = RequestMethod.POST, consumes = "application/json")
    NewResponseUtil addUser(@RequestBody UserAddApiFacade facade);

    @RequestMapping(value = "/api/user/updateUser", method = RequestMethod.POST, consumes = "application/json")
    NewResponseUtil updateUser(@RequestBody UserUpdateApiFacade facade);
}
