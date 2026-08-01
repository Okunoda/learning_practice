package com.erywim.order.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.erywim.order.bean.Order;
import com.erywim.order.feign.ProductFeignClient;
import com.erywim.order.service.OrderService;
import com.erywim.product.bean.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private ProductFeignClient productFeignClient;

    @SentinelResource(value = "createOrder",// sentinelResource 通常标注再非controller层的方法上。
    blockHandler = "createOrderBlockHandler"//其中 blockHandler 用于处理限流、降级的保护策略方法，并且blockHandler指定的方法必须是public方法。blockHandler 是由 Sentinel 框架在方法外部、代理层直接反射调用；反射要能够访问到目标方法，必须满足 Java 访问权限检查，因此要求 public。
    ,fallback = "createOrderFallbackHandler")//fallback用于处理资源代码执行中发生异常时的策略方法，fallBack指定的方法可以是private方法。是由 Sentinel 在原方法抛出异常后，通过同一线程栈里的普通方法调用链进入；
    @Override
    public Order createOrder(Long id, Long userId) {
        Order order = new Order();
//        Product product = getProductFromRemote(id);
//        Product product = getProductFromRemoteWithLoadBalance(id);
//        Product product = getProductFromRemoteWithLoadBalanceAnnotation(id);
        Product product = productFeignClient.getProductById(id);

        order.setId(1L);
        order.setUserId(userId);
        order.setNickName("user-" + userId);
        order.setAddress("address-" + userId);
        order.setTotalAmount(product.getPrice().multiply(new BigDecimal(product.getNum())));

        order.setProducts(Lists.newArrayList(product));
        return order;
    }

    public Order createOrderBlockHandler(Long id, Long userId, BlockException e) {
        log.error("限流降级保护策略方法被调用");
        Order order = new Order();
        order.setId(1L);
        order.setUserId(userId);
        order.setNickName("未知用户");
        order.setAddress("限流保护策略方法调用");
        order.setTotalAmount(new BigDecimal(0));
        order.setProducts(Lists.newArrayList());
        return order;
    }

    private Order createOrderFallbackHandler(Long id, Long userId) {
        log.error("资源代码执行中发生异常");
        Order order = new Order();
        order.setId(1L);
        order.setUserId(userId);
        order.setNickName("未知用户");
        order.setAddress("资源代码执行中发生异常" );
        order.setTotalAmount(new BigDecimal(0));
        order.setProducts(Lists.newArrayList());
        return order;
    }

    //v2-使用 loadBalance 负载均衡 注解获取地址。注解加在restTemplate 的 Bean 上
    private Product getProductFromRemoteWithLoadBalanceAnnotation(Long id) {
        String url = "http://service-product/getProduct/" + id;
        ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class);

        Product body = response.getBody();
        return body;
    }


    //v2-使用 loadBalance 负载均衡获取地址
    private Product getProductFromRemoteWithLoadBalance(Long id) {
        ServiceInstance instance = loadBalancerClient.choose("service-product");
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/getProduct/" + id;
        ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class);
        log.info("远程调用url:{}", url);

        Product body = response.getBody();
        return body;
    }


    //v1-使用 nacos 获取远程调用地址
    private Product getProductFromRemote(Long id) {
        ServiceInstance instance = discoveryClient.getInstances("service-product").get(0);

        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/getProduct/" + id;

        ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class);

        Product body = response.getBody();
        return body;
    }
}
