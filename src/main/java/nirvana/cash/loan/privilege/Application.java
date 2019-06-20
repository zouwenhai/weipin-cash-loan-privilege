package nirvana.cash.loan.privilege;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableConfigurationProperties
@SpringCloudApplication
@EnableTransactionManagement
@MapperScan("nirvana.cash.loan.privilege.dao")
@EnableFeignClients("nirvana.cash.loan.privilege.fegin")
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logger.info("weipin-cash-loan-privilege is runing ...");
    }

}
