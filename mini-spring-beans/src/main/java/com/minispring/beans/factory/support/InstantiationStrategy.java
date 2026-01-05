package com.minispring.beans.factory.support;

import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.config.BeanDefinition;

/**
 * Bean 实例化策略接口
 * <p>
 * 职责：定义 Bean 的实例化方式
 * <p>
 * 设计模式：策略模式（Strategy Pattern）
 * 面试考点：
 * 1. 为什么需要实例化策略？
 *    - 不同的场景需要不同的实例化方式
 *    - JDK 反射：简单、直接，但性能稍低
 *    - CGLIB：可以代理没有接口的类，支持方法拦截
 * 2. 策略模式的优势？
 *    - 算法可以自由切换
 *    - 避免使用多重条件判断
 *    - 扩展性好，易于添加新的实例化策略
 * 3. Spring 中 Bean 的实例化方式有哪些？
 *    - 构造器实例化
 *    - 静态工厂方法
 *    - 实例工厂方法
 *    - FactoryBean
 *
 * @author mini-spring
 */
public interface InstantiationStrategy {

    /**
     * 实例化 Bean
     * <p>
     * 根据 BeanDefinition 创建 Bean 实例
     * 不同的实现类可以使用不同的策略：
     * - SimpleInstantiationStrategy：使用 JDK 反射
     * - CglibSubclassingInstantiationStrategy：使用 CGLIB
     *
     * @param beanDefinition Bean 定义
     * @return Bean 实例
     * @throws BeansException 实例化失败
     */
    Object instantiate(BeanDefinition beanDefinition) throws BeansException;

}
