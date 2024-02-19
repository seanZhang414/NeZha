package cn.com.duiba.tuia.engine.activity.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableDuibaFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * The Class Application.
 */
@ComponentScan("cn.com.duiba")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableDuibaFeignClients(basePackages = {"cn.com.duiba"})
public class Application {

    @Bean
    public AsyncRestTemplate asyncRestTemplate() {

        AsyncListenableTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setTaskExecutor(taskExecutor);
        requestFactory.setConnectTimeout(2000);//connect time out 500ms
        requestFactory.setReadTimeout(2000);// read time out 2000ms
        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(requestFactory, new RestTemplate(requestFactory));

        return asyncRestTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
