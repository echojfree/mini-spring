package com.minispring.context.support;

import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.config.BeanPostProcessor;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.minispring.context.ConfigurableApplicationContext;

/**
 * AbstractApplicationContext 抽象基类
 * <p>
 * ApplicationContext 的抽象实现
 * 实现了容器的核心流程，具体的配置加载由子类实现
 * <p>
 * 设计模式：
 * - 模板方法模式：定义容器初始化的骨架，具体步骤由子类实现
 * - 门面模式：统一对外提供简单的接口
 * <p>
 * 核心方法：
 * - refresh()：容器初始化的模板方法
 * - getBeanFactory()：获取内部的 BeanFactory
 * - close()：容器关闭
 * <p>
 * 面试考点：
 * 1. ApplicationContext 的初始化流程（refresh 方法）
 * 2. 模板方法模式在 Spring 中的应用
 * 3. BeanFactory 和 ApplicationContext 的关系
 *
 * @author mini-spring
 */
public abstract class AbstractApplicationContext implements ConfigurableApplicationContext {

    /**
     * 内部的 BeanFactory
     * 实际的 Bean 管理工作委托给 BeanFactory
     */
    private DefaultListableBeanFactory beanFactory;

    /**
     * 关闭钩子线程
     */
    private Thread shutdownHook;

    /**
     * 刷新容器
     * <p>
     * 这是容器初始化的核心方法（面试重点）
     * 使用模板方法模式，定义了初始化的标准流程
     * <p>
     * 完整步骤（简化版）：
     * 1. 创建 BeanFactory
     * 2. 加载 BeanDefinition
     * 3. 注册 BeanPostProcessor
     * 4. 实例化所有单例 Bean（预加载）
     *
     * @throws Exception 刷新失败时抛出异常
     */
    @Override
    public void refresh() throws Exception {
        // 步骤1：创建 BeanFactory
        refreshBeanFactory();

        // 步骤2：获取 BeanFactory
        DefaultListableBeanFactory beanFactory = getBeanFactory();

        // 步骤3：注册 BeanPostProcessor
        // BeanPostProcessor 需要在 Bean 实例化之前注册
        registerBeanPostProcessors(beanFactory);

        // 步骤4：实例化所有单例 Bean（预加载）
        // 这是 ApplicationContext 和 BeanFactory 的重要区别
        beanFactory.preInstantiateSingletons();
    }

    /**
     * 创建 BeanFactory
     * <p>
     * 模板方法，由子类实现
     * 不同的子类有不同的创建方式
     * <p>
     * 例如：
     * - ClassPathXmlApplicationContext：从 classpath 加载 XML
     * - FileSystemXmlApplicationContext：从文件系统加载 XML
     * - AnnotationConfigApplicationContext：基于注解配置
     *
     * @throws Exception 创建失败时抛出异常
     */
    protected abstract void refreshBeanFactory() throws Exception;

    /**
     * 获取 BeanFactory
     * <p>
     * 模板方法，由子类实现
     * 返回容器内部的 BeanFactory
     *
     * @return BeanFactory 实例
     */
    protected abstract DefaultListableBeanFactory getBeanFactory();

    /**
     * 注册 BeanPostProcessor
     * <p>
     * 从 BeanFactory 中找出所有实现了 BeanPostProcessor 接口的 Bean
     * 并注册到 BeanFactory 中
     * <p>
     * 面试考点：
     * 1. BeanPostProcessor 的注册时机
     *    - 必须在普通 Bean 实例化之前注册
     *    - 在 refresh() 方法中调用
     * 2. BeanPostProcessor 本身也是 Bean
     *    - 可以在配置文件中定义
     *    - 容器会自动发现并注册
     *
     * @param beanFactory BeanFactory 实例
     */
    protected void registerBeanPostProcessors(DefaultListableBeanFactory beanFactory) {
        // 获取所有 BeanPostProcessor 类型的 Bean 名称
        String[] beanNames = beanFactory.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Object bean = null;
            try {
                bean = beanFactory.getBean(beanName);
            } catch (BeansException e) {
                // 忽略获取失败的 Bean
                continue;
            }

            // 如果是 BeanPostProcessor，则注册
            if (bean instanceof BeanPostProcessor) {
                beanFactory.addBeanPostProcessor((BeanPostProcessor) bean);
            }
        }
    }

    // ==================== BeanFactory 接口实现 ====================

    /**
     * 获取 Bean 实例
     * <p>
     * 委托给内部的 BeanFactory 处理
     *
     * @param name Bean 名称
     * @return Bean 实例
     * @throws BeansException Bean 不存在或创建失败
     */
    @Override
    public Object getBean(String name) throws BeansException {
        return getBeanFactory().getBean(name);
    }

    /**
     * 获取指定类型的 Bean 实例
     * <p>
     * 委托给内部的 BeanFactory 处理
     *
     * @param name         Bean 名称
     * @param requiredType 要求的类型
     * @param <T>          泛型类型
     * @return 指定类型的 Bean 实例
     * @throws BeansException 类型不匹配
     */
    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(name, requiredType);
    }

    // ==================== 容器生命周期管理 ====================

    /**
     * 关闭容器
     * <p>
     * 优雅地关闭容器，释放所有资源
     * <p>
     * 主要步骤：
     * 1. 销毁所有单例 Bean
     * 2. 清理资源
     * <p>
     * 面试考点：
     * - 容器关闭时的清理顺序
     * - Bean 的销毁顺序（后创建的先销毁）
     */
    @Override
    public void close() {
        // 销毁所有单例 Bean
        getBeanFactory().destroySingletons();
    }

    /**
     * 注册关闭钩子
     * <p>
     * 向 JVM 注册关闭钩子，确保容器在 JVM 关闭时也能优雅关闭
     * <p>
     * 使用场景（面试考点）：
     * 1. 应用正常退出（System.exit()）
     * 2. 应用异常终止
     * 3. 收到 kill 信号（SIGTERM）
     * 4. IDE 停止应用
     * <p>
     * 注意事项：
     * - 关闭钩子在单独的线程中执行
     * - 不要在钩子中执行长时间操作
     * - JVM 强制终止时（kill -9）钩子不会执行
     */
    @Override
    public void registerShutdownHook() {
        if (this.shutdownHook == null) {
            // 创建关闭钩子线程
            this.shutdownHook = new Thread() {
                @Override
                public void run() {
                    // 在 JVM 关闭时调用 close() 方法
                    AbstractApplicationContext.this.close();
                }
            };

            // 注册到 JVM
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }

}
