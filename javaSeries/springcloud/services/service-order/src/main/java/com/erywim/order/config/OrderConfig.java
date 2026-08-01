package com.erywim.order.config;

import feign.Logger;
import feign.Retryer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OrderConfig {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        //rest是线程安全的，一般项目中存在一个即可
        return new RestTemplate();
    }
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean //往容器中注入一个 Retryer Bean，这个 Bean 会自动替换掉 Feign 默认的重试策略
    public Retryer retry(){
        return new Retryer.Default();
    }
}
