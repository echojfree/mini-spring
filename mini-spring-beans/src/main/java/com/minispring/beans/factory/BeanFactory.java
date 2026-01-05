package com.minispring.beans.factory;

import com.minispring.beans.exception.BeansException;

/**
 * Bean 工厂接口：IoC 容器的核心接口
 * <p>
 * 职责：提供 Bean 的获取功能
 * 这是 Spring 容器最基本的接口，定义了获取 Bean 的基本方法
 * <p>
 * 设计模式：工厂模式
 * 面试考点：
 * 1. BeanFactory 是什么？它的作用是什么？
 * 2. BeanFactory 和 ApplicationContext 的区别
 * 3. getBean 方法的工作流程
 * 4. Spring 如何管理 Bean 的生命周期
 *
 * @author mini-spring
 */
public interface BeanFactory {

    /**
     * 根据 Bean 名称获取 Bean 实例
     * <p>
     * 工作流程：
     * 1. 查找 BeanDefinition
     * 2. 如果是单例且已创建，直接返回缓存的实例
     * 3. 否则，根据 BeanDefinition 创建新实例
     * 4. 如果是单例，缓存实例
     * <p>
     * 面试考点：
     * - getBean 方法何时创建 Bean？
     * - 单例 Bean 如何保证只创建一次？
     * - 原型 Bean 每次都创建新实例吗？
     *
     * @param name Bean 的名称
     * @return Bean 实例
     * @throws BeansException 如果 Bean 不存在或创建失败
     */
    Object getBean(String name) throws BeansException;

    /**
     * 根据 Bean 名称获取指定类型的 Bean 实例
     * <p>
     * 这是一个泛型方法，避免了类型转换
     * 使用示例：
     * UserService userService = beanFactory.getBean("userService", UserService.class);
     *
     * @param name         Bean 的名称
     * @param requiredType 要求的类型
     * @param <T>          泛型类型
     * @return 指定类型的 Bean 实例
     * @throws BeansException 如果 Bean 不存在、创建失败或类型不匹配
     */
    <T> T getBean(String name, Class<T> requiredType) throws BeansException;

}
