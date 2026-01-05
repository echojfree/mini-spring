package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Repository;

/**
 * 测试用 Repository（派生注解）
 */
@Repository
public class UserRepository {
    public String getName() {
        return "UserRepository";
    }
}
