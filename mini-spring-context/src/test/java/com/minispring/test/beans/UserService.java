package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Service;

/**
 * 测试用 Service（派生注解）
 */
@Service
public class UserService {
    public String getName() {
        return "UserService";
    }
}
