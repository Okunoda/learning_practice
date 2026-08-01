package com.erywim.geteway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class CostTimeGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        URI uri = exchange.getRequest().getURI();
        ServerHttpResponse response = exchange.getResponse();

        long startTime = System.currentTimeMillis();
        Mono<Void> voidMono = chain.filter(exchange) //这里的放行是异步的，所以需要加上doFinally方法去获取耗时
                .doFinally(signalType -> {
                    long endTime = System.currentTimeMillis();
                    //打印uri 和耗时时间
                    System.out.println("uri:" + uri + " 耗时:" + (endTime - startTime));
        });
        return voidMono;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
