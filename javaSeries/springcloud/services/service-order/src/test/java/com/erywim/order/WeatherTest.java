package com.erywim.order;

import com.erywim.order.feign.WeatherFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WeatherTest {

    @Autowired
    private WeatherFeignClient weatherFeignClient;

    @Test
    public void test() {
        System.out.println(weatherFeignClient.getWeather("bbbbbbbb", "jjjjjjjjj", 1L));
    }
}
