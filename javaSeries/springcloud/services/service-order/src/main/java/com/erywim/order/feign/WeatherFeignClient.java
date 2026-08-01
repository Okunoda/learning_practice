package com.erywim.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

//在FeignClient注解中指定url，则不需要在@FeignClient注解中指定value属性，此时是通过http方式进行请求，并不会进行负载均衡等操作
@FeignClient(url = "http://aliv18.data.moji.com",name = "weather-test")
public interface WeatherFeignClient {


    @PostMapping("/whapi/json/alicityweather/condition")
    String getWeather(@RequestHeader("Authorization")String authorization,
                      @RequestParam("token")String token,
                      @RequestParam("cityId")Long cityId);


}
