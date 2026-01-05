package com.minispring.aop.test;

/**
 * UserService - 用户服务接口
 * <p>
 * 用于测试 JDK 动态代理
 *
 * @author mini-spring
 */
public interface UserService {

    /**
     * 查询用户
     *
     * @param userId 用户ID
     * @return 用户名
     */
    String queryUser(String userId);

    /**
     * 注册用户
     *
     * @param username 用户名
     * @return 是否成功
     */
    boolean register(String username);

}
