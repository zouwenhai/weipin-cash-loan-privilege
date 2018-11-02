package nirvana.cash.loan.privilege.web.zuulfallback;

import com.netflix.hystrix.exception.HystrixTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * Zuul中使用fallback功能
 * 实现参考:https://www.jianshu.com/p/632f26892c44
 * Created by Administrator on 2018/8/11.
 */
@Component
public class MyFallbackProvider implements FallbackProvider {

    private static final Logger logger = LoggerFactory.getLogger(MyFallbackProvider.class);

    @Autowired
    private ZullClientHttpResponse zullClientHttpResponse;

    @Override
    public String getRoute() {
        // 表明是为哪个微服务提供回退
        // (1)*表示为所有微服务提供回退
        // (2)设定为serviceId,表示为指定微服务提供回退
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(Throwable cause) {
        logger.error("权限管理路由转发系统|zull代理转发响应异常:message={},exception={} ", cause.getMessage(), cause);
        if (cause instanceof HystrixTimeoutException) {
            return zullClientHttpResponse.response(HttpStatus.GATEWAY_TIMEOUT);
        } else {
            return this.fallbackResponse();
        }
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return zullClientHttpResponse.response(HttpStatus.BAD_GATEWAY);
    }
}

