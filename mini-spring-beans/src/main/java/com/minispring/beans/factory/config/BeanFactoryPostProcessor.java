package com.minispring.beans.factory.config;

import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;

/**
 * BeanFactoryPostProcessor 接口
 * <p>
 * BeanFactory 的后置处理器，用于在 Bean 实例化之前修改 BeanDefinition
 * <p>
 * 核心功能（面试重点）:
 * 1. 作用时机：Bean 定义加载完成后、Bean 实例化之前
 * 2. 作用对象：BeanDefinition（Bean 的元数据）
 * 3. 典型应用：
 *    - PropertyPlaceholderConfigurer（占位符替换）
 *    - PropertyOverrideConfigurer（属性覆盖）
 *    - CustomScopeConfigurer（自定义作用域）
 * <p>
 * 与 BeanPostProcessor 的区别（面试高频）：
 * 1. BeanFactoryPostProcessor：
 *    - 作用时机：Bean 实例化之前
 *    - 作用对象：BeanDefinition（元数据）
 *    - 目的：修改 Bean 定义
 * 2. BeanPostProcessor：
 *    - 作用时机：Bean 实例化之后
 *    - 作用对象：Bean 实例
 *    - 目的：修改 Bean 实例
 * <p>
 * 调用时机（在 refresh() 中）：
 * 1. obtainFreshBeanFactory()          → 创建 BeanFactory，加载 BeanDefinition
 * 2. invokeBeanFactoryPostProcessors() → 调用 BeanFactoryPostProcessor ⬅️ 在这里
 * 3. registerBeanPostProcessors()      → 注册 BeanPostProcessor
 * 4. finishBeanFactoryInitialization() → 实例化 Bean
 * <p>
 * 设计模式：
 * - 策略模式：不同的后置处理器实现不同的处理策略
 * - 模板方法模式：定义处理流程，具体处理逻辑由实现类决定
 * <p>
 * 典型应用场景：
 * 1. 动态修改 Bean 属性（如数据库连接信息）
 * 2. 替换占位符（${database.url}）
 * 3. 注册自定义作用域
 * 4. 动态添加或删除 BeanDefinition
 *
 * @author mini-spring
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

    /**
     * 后置处理 BeanFactory
     * <p>
     * 在所有 BeanDefinition 加载完成后、Bean 实例化之前调用
     * 可以修改 BeanDefinition 的元数据
     * <p>
     * 注意事项（面试考点）：
     * 1. 不要在此方法中实例化 Bean
     *    - 会导致 Bean 过早实例化
     *    - 破坏 Spring 容器的初始化流程
     * 2. 只应该修改 BeanDefinition
     *    - 添加/删除/修改 BeanDefinition
     *    - 修改 Bean 的属性值、作用域等
     * 3. 可以通过 BeanFactory 获取其他 BeanDefinition
     *    - 但不要调用 getBean() 方法
     * <p>
     * 典型实现示例：
     * <pre>
     * public void postProcessBeanFactory(DefaultListableBeanFactory beanFactory) {
     *     // 1. 获取所有 BeanDefinition 名称
     *     String[] beanNames = beanFactory.getBeanDefinitionNames();
     *
     *     // 2. 遍历处理
     *     for (String beanName : beanNames) {
     *         BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
     *
     *         // 3. 修改 BeanDefinition
     *         PropertyValues pvs = bd.getPropertyValues();
     *         // 修改属性值...
     *     }
     * }
     * </pre>
     *
     * @param beanFactory BeanFactory 实例，可以通过它访问和修改 BeanDefinition
     * @throws BeansException 处理失败时抛出异常
     */
    void postProcessBeanFactory(DefaultListableBeanFactory beanFactory) throws BeansException;

}
