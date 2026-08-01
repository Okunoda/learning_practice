package com.erywim.order.fallback;

import com.erywim.order.feign.ProductFeignClient;
import com.erywim.product.bean.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


/**
 * fallback 方法，只有当远程调用失败之后才会被调用
 */
@Slf4j
@Component
public class ProductFeignClientFallback implements ProductFeignClient {

    @Override
    public Product getProductById(Long id) {
        log.info("兜底机制生效");
        Product product = Product.builder()
                .name("未知商品")
                .num(0)
                .price(new BigDecimal(0))
                .build();

        return product;
    }
}
