package com.erywim.product.service.impl;

import com.erywim.product.bean.Product;
import com.erywim.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public Product getProduct(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("product-" + id);
        product.setPrice(new BigDecimal(100));
        product.setNum(100);

//        try {
//            Thread.sleep(100000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return product;
    }
}
