package nirvana.cash.loan.privilege.fegin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HystrixCashLoanApi implements FeginCashLoanApi {

    @Override
    public NewResponseUtil getAllProductList() {
        log.error("程序进入断路器:获取产品列表失败！");
        return new NewResponseUtil(NewResponseUtil.ERROR, null, "获取产品列表失败");
    }

    @Override
    public NewResponseUtil realNo(Long id) {
        log.error("程序进入断路器:获取通讯录手机号失败！");
        return new NewResponseUtil(NewResponseUtil.ERROR, null, "获取通讯录真实号码失败");
    }
}
