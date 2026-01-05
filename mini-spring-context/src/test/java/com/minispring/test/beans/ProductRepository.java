package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Component;

/**
 * ProductRepository - 产品数据访问层
 */
@Component
public class ProductRepository {

    public String getName() {
        return "ProductRepository";
    }

    public void save(String product) {
        System.out.println("保存产品: " + product);
    }
}
