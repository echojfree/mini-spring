package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Autowired;
import com.minispring.beans.factory.annotation.Component;

/**
 * OrderService - 测试字段注入
 */
@Component
public class OrderService {

    // 字段注入：最常用的方式
    @Autowired
    private OrderRepository orderRepository;

    public String getName() {
        return "OrderService";
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public void createOrder(String order) {
        System.out.println("OrderService 创建订单: " + order);
        orderRepository.save(order);
    }
}
