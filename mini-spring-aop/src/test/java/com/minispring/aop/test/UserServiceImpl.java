package com.minispring.aop.test;

/**
 * UserServiceImpl - 用户服务实现
 * <p>
 * 用于测试 JDK 动态代理和 CGLIB 代理
 *
 * @author mini-spring
 */
public class UserServiceImpl implements UserService {

    @Override
    public String queryUser(String userId) {
        System.out.println("查询用户: " + userId);
        return "User-" + userId;
    }

    @Override
    public boolean register(String username) {
        System.out.println("注册用户: " + username);
        return true;
    }

}
