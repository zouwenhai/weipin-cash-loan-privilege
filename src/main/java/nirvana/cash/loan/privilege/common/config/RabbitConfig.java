package nirvana.cash.loan.privilege.common.config;

import nirvana.cash.loan.privilege.common.contants.MQConstants;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author wangqiang
 * @Date 2018/8/2 14:27
 **/
@Configuration
public class RabbitConfig {


    @Bean("myContainerFactory")
    public SimpleRabbitListenerContainerFactory myContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setPrefetchCount(MQConstants.DEFAULT_PREFETCH_COUNT);
        factory.setConcurrentConsumers(MQConstants.DEFAULT_CONCURRENT);
        factory.setMaxConcurrentConsumers(MQConstants.DEFAULT_MAX_CONCURRENT);
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    /**
     *  rabbitAdmin代理类
     *  @return
     */
    @Bean("myRabbitAdmin")
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

}
