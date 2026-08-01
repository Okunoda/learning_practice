package com.erywim.order.feign;


import com.erywim.order.fallback.ProductFeignClientFallback;
import com.erywim.product.bean.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-product",path = "/api/product",
        fallback = ProductFeignClientFallback.class)//Feign 会自己连接到注册中心，并且通过负载均衡的方式去获取一个实例并发送请求
public interface ProductFeignClient {

    @GetMapping("/getProduct/{id}")
    Product getProductById(@PathVariable Long id);

}
