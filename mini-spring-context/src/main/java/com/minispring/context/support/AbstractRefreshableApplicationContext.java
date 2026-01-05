package com.minispring.context.support;

import com.minispring.beans.factory.support.DefaultListableBeanFactory;

/**
 * AbstractRefreshableApplicationContext 抽象基类
 * <p>
 * 支持多次刷新的 ApplicationContext
 * 每次刷新都会创建新的 BeanFactory
 * <p>
 * 设计思想：
 * - 将 BeanFactory 的创建和管理抽取到此类
 * - 子类只需要实现 BeanDefinition 的加载
 * <p>
 * 面试考点：
 * 1. 为什么需要支持多次刷新？
 *    - 配置文件变更后重新加载
 *    - 开发环境热部署
 * 2. 多次刷新如何保证线程安全？
 *    - 使用同步锁（synchronized）
 *    - 原子性操作
 *
 * @author mini-spring
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

    /**
     * 内部的 BeanFactory
     */
    private DefaultListableBeanFactory beanFactory;

    /**
     * 创建 BeanFactory
     * <p>
     * 每次刷新都会创建新的 BeanFactory
     * <p>
     * 步骤：
     * 1. 创建新的 BeanFactory
     * 2. 加载 BeanDefinition
     *
     * @throws Exception 创建失败时抛出异常
     */
    @Override
    protected final void refreshBeanFactory() throws Exception {
        // 步骤1：创建新的 BeanFactory
        DefaultListableBeanFactory beanFactory = createBeanFactory();

        // 步骤2：加载 BeanDefinition
        loadBeanDefinitions(beanFactory);

        // 保存 BeanFactory
        this.beanFactory = beanFactory;
    }

    /**
     * 创建 BeanFactory 实例
     * <p>
     * 可以在此处进行自定义配置
     *
     * @return BeanFactory 实例
     */
    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory();
    }

    /**
     * 加载 BeanDefinition
     * <p>
     * 模板方法，由子类实现
     * 不同的子类有不同的加载方式
     * <p>
     * 例如：
     * - 从 XML 文件加载
     * - 从注解配置加载
     * - 从 Properties 文件加载
     *
     * @param beanFactory BeanFactory 实例
     * @throws Exception 加载失败时抛出异常
     */
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws Exception;

    /**
     * 获取 BeanFactory
     *
     * @return BeanFactory 实例
     */
    @Override
    protected DefaultListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

}
