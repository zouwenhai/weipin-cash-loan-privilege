package nirvana.cash.loan.privilege.fegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "weipin-cash-loan-api", fallback = HystrixCashLoanApi.class)
public interface FeginCashLoanApi {

    @RequestMapping(value = "/api/product/getAllProductList", method = RequestMethod.GET)
    NewResponseUtil getAllProductList();

    @GetMapping(value = "/api/product/realNo")
    NewResponseUtil realNo(Long id);


}
