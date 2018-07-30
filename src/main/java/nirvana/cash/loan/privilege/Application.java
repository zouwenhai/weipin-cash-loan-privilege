package nirvana.cash.loan.privilege;

import nirvana.cash.loan.privilege.common.config.FebsProperies;
import nirvana.cash.loan.privilege.web.filter.RequestLogZullFilter;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringCloudApplication
@EnableTransactionManagement
@MapperScan("nirvana.cash.loan.privilege.*.dao")
@EnableConfigurationProperties({FebsProperies.class})
@EnableZuulProxy
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logger.info("yofiskdk-cash-loan-privilege is runing ...");
    }

    @Bean
    public RequestLogZullFilter requestLogZullFilter() {
        return new RequestLogZullFilter();
    }
}
