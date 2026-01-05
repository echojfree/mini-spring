package com.minispring.context;

/**
 * ConfigurableApplicationContext 接口
 * <p>
 * 可配置的 ApplicationContext 接口
 * 提供了配置和生命周期管理的能力
 * <p>
 * 这个接口主要用于：
 * 1. 容器的刷新和关闭操作
 * 2. 配置容器的行为
 * 3. 获取内部的 BeanFactory
 * <p>
 * 设计思想：
 * - 接口隔离原则：将配置功能从 ApplicationContext 中分离
 * - 用户使用 ApplicationContext，框架内部使用 ConfigurableApplicationContext
 * <p>
 * 面试考点：
 * 1. 为什么需要 ConfigurableApplicationContext？
 *    - 隔离配置操作，用户只能使用，不能配置
 *    - 提供框架内部使用的配置接口
 * 2. ApplicationContext 和 ConfigurableApplicationContext 的关系？
 *    - ConfigurableApplicationContext 继承 ApplicationContext
 *    - 增加了配置和管理功能
 *
 * @author mini-spring
 */
public interface ConfigurableApplicationContext extends ApplicationContext {

    /**
     * 刷新容器
     * <p>
     * 从 ApplicationContext 继承的方法
     * 负责容器的初始化和 Bean 的预加载
     *
     * @throws Exception 刷新失败时抛出异常
     */
    @Override
    void refresh() throws Exception;

    /**
     * 关闭容器
     * <p>
     * 从 ApplicationContext 继承的方法
     * 优雅地关闭容器，释放所有资源
     */
    @Override
    void close();

    /**
     * 注册关闭钩子
     * <p>
     * 从 ApplicationContext 继承的方法
     * 向 JVM 注册关闭钩子
     */
    @Override
    void registerShutdownHook();

}
