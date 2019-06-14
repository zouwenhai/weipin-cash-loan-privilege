package nirvana.cash.loan.privilege.fegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by JinYunGang
 * on 2019/6/14 11:40
 **/
@FeignClient(value = "yofishdk-cash-loan-web", fallbackFactory = HystrixCashLoanWeb.class)
public interface FeginCashloanWeb {

    @RequestMapping(value = "/web/customerInfo/realNo", method = RequestMethod.GET)
    NewResponseUtil realNo(Long id);


}
