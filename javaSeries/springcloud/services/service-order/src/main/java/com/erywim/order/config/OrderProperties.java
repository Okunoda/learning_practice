package com.erywim.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "order") // 配置文件注入的形式下，可以无需@RefreshScope就能实现自动刷新
@Data
public class OrderProperties {
    private String timeout;
    private String autoConfirm;
    private String test;
}
