package com.erywim.order.controller;

import com.erywim.order.bean.Order;
import com.erywim.order.config.OrderProperties;
import com.erywim.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RefreshScope //设置自动刷新，当配置中心内容改变时，会自动更新
@Slf4j
@RequestMapping("api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

//    @Value("${order.timeout}")
//    private String timeout;
//    @Value("${order.auto-confirm}")
//    private String autoConfirm;

    @Autowired
    private OrderProperties orderProperties;

    @GetMapping("config")
    public String getConfig() {
        return "timeout:" + orderProperties.getTimeout() + " autoConfirm:" + orderProperties.getAutoConfirm() + ":" + orderProperties.getTest();
    }

    @GetMapping("/createOrder")
    public Order createOrder(@RequestParam("id") Long id,
                             @RequestParam("userId")Long userId) {
        return orderService.createOrder(id, userId);
    }

    @GetMapping("seckill") // 在sentinel中给 orderService.createOrder 方法设置为 链路 类型的流控，限制入口为 /seckill ，也就是从createOrder接口调用这个方法不会被限制，从seckill方法就被流控
    public Order seckill() {
        return orderService.createOrder(Long.MAX_VALUE,Long.MAX_VALUE);
    }

    @GetMapping("readDb")
    public String readDb() { //读取在sentinel中被设置为 关联 类型的流控，关联 writeDb 接口，也就是当 writeDb 流量特别大的时候，会优先限制 readDb 的访问去保证 writeDb 的正常访问。
        log.info("read Db=======================================");
        return "read DB success........";
    }

    @GetMapping("writeDb")
    public String writeDb() {
        log.info("write Db=======================================");
        return "write DB success........";
    }
}
