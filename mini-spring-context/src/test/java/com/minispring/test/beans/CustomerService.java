package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Autowired;
import com.minispring.beans.factory.annotation.Component;

/**
 * CustomerService - 测试可选依赖（required=false）
 */
@Component
public class CustomerService {

    // 可选依赖：如果找不到 Bean 也不报错
    @Autowired(required = false)
    private NonExistentRepository nonExistentRepository;

    @Autowired
    private UserRepository userRepository;

    public String getName() {
        return "CustomerService";
    }

    public NonExistentRepository getNonExistentRepository() {
        return nonExistentRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
