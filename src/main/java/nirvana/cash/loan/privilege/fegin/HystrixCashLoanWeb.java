package nirvana.cash.loan.privilege.fegin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by JinYunGang
 * on 2019/6/14 13:57
 **/
@Slf4j
@Component
public class HystrixCashLoanWeb implements FeginCashloanWeb {
    @Override
    public NewResponseUtil realNo(Long id) {
        log.error("程序进入断路器:获取通讯录手机号失败！");
        return new NewResponseUtil(NewResponseUtil.ERROR,null,"获取通讯录真实号码失败");
    }
}
