package com.erywim.order.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component //有两种使用方式，第一种是在配置文件中加入：spring.cloud.openfeign.client.config=（这个类的全类名）。第二种是将其作为一个Bean注入进容器
public class XTokenRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        log.info("XTokenRequestInterceptor processor......");
        requestTemplate.header("X-Token", UUID.randomUUID().toString());
    }
}
