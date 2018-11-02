package nirvana.cash.loan.privilege;

import nirvana.cash.loan.privilege.common.config.FebsProperies;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringCloudApplication
@EnableTransactionManagement
@MapperScan("nirvana.cash.loan.privilege.*.dao")
@EnableConfigurationProperties({FebsProperies.class})
@EnableZuulProxy
@EnableFeignClients("nirvana.cash.loan.privilege.fegin")
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logger.info("yofishdk-cash-loan-privilege is runing ...");
    }

}
