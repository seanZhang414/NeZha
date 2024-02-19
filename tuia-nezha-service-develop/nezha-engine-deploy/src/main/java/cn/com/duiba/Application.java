package cn.com.duiba;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableDuibaFeignClients;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by xuezhaoming on 16/5/13.
 */
@SpringBootApplication
@ImportResource({"classpath:/spring/spring-config.xml"})
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableDuibaFeignClients
@EnableAutoConfiguration(exclude = { DataSourceTransactionManagerAutoConfiguration.class,
        DataSourceAutoConfiguration.class})
public class Application {


    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        try {
            LOGGER.info("Start Application ...");

            SpringApplication sa=new SpringApplication(Application.class);
            sa.run(args);

            LOGGER.info("Start Application success");
        } catch (Exception e) {
            LOGGER.error("Application start error :{}", e);
            System.exit(-1);
        }
    }
}