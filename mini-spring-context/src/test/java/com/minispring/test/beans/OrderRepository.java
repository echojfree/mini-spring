package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Component;

/**
 * OrderRepository - 订单数据访问层
 */
@Component
public class OrderRepository {

    public String getName() {
        return "OrderRepository";
    }

    public void save(String order) {
        System.out.println("保存订单: " + order);
    }
}
