package com.erywim.service.configuration;


import com.erywim.service.IpCounterService;
import com.erywim.service.impl.IpCounterServiceImpl;
import org.springframework.context.annotation.Bean;

public class IpAutoConfiguration {
    @Bean("ipCounterService")
    public IpCounterService ipCounterService(){
        return new IpCounterServiceImpl();
    }
}
