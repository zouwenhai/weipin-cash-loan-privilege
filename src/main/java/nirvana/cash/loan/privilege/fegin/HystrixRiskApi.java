package nirvana.cash.loan.privilege.fegin;

import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.fegin.facade.UserAddApiFacade;
import nirvana.cash.loan.privilege.fegin.facade.UserUpdateApiFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by Administrator on 2018/8/3.
 */
@Component
public class HystrixRiskApi implements FeginRiskApi {
    public static final Logger logger = LoggerFactory.getLogger(HystrixRiskApi.class);

    @Override
    public NewResponseUtil addUser(@RequestBody UserAddApiFacade facade) {
        logger.error("程序进入断路器|添加风控人员失败,请求参数:{}", JSON.toJSONString(facade));
        throw new RuntimeException("请求风控服务异常|添加风控人员失败");
    }

    @Override
    public NewResponseUtil updateUser(@RequestBody UserUpdateApiFacade facade) {
        logger.error("程序进入断路器|更新催收风控失败,请求参数:{}", JSON.toJSONString(facade));
        throw new RuntimeException("请求风控服务异常|更新风控人员失败");
    }
}