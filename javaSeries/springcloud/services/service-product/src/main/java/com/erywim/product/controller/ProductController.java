package com.erywim.product.controller;

import com.erywim.product.bean.Product;
import com.erywim.product.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/getProduct/{id}")
    public Product getProduct(@PathVariable Long id, HttpServletRequest request) {
        log.info("收到获取商品的请求，token为：{}",request.getHeader("X-Token"));

        return productService.getProduct(id);
    }
}
