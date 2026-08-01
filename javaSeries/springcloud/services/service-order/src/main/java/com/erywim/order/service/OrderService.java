package com.erywim.order.service;

import com.erywim.order.bean.Order;

public interface OrderService {
    Order createOrder(Long id,Long userId);
}
