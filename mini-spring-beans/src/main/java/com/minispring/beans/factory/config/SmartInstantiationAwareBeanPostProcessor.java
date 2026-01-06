package com.minispring.beans.factory.config;

import com.minispring.beans.PropertyValues;
import com.minispring.beans.exception.BeansException;

/**
 * SmartInstantiationAwareBeanPostProcessor - 智能实例化感知后置处理器
 * <p>
 * 扩展自 InstantiationAwareBeanPostProcessor,提供更多的实例化前回调
 * <p>
 * 设计模式:策略模式、模板方法模式
 * <p>
 * 面试要点:
 * 1. 循环依赖解决
 *    - Spring 使用三级缓存解决循环依赖
 *    - 第三级缓存存储 ObjectFactory
 *    - getEarlyBeanReference() 提前暴露代理对象
 * <p>
 * 2. AOP 代理的循环依赖
 *    - 普通 Bean: 直接暴露原始对象
 *    - AOP Bean: 暴露代理对象
 *    - SmartInstantiationAwareBeanPostProcessor.getEarlyBeanReference()
 *      在循环依赖时提前创建代理
 * <p>
 * 3. 使用场景
 *    - AbstractAutoProxyCreator 实现此接口
 *    - 在 getEarlyBeanReference() 中提前创建 AOP 代理
 *    - 确保循环依赖的 Bean 注入的是代理对象
 *
 * @author mini-spring
 */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

    /**
     * 获取早期 Bean 引用
     * <p>
     * 在循环依赖场景下,通过三级缓存提前暴露 Bean
     * 如果 Bean 需要被代理,此方法返回代理对象
     * <p>
     * 执行时机:
     * 1. Bean 实例化后,属性注入前
     * 2. 放入三级缓存时
     * 3. 被其他 Bean 通过循环依赖引用时调用
     *
     * @param bean     原始 Bean 实例
     * @param beanName Bean 名称
     * @return 暴露的 Bean 引用(可能是代理对象)
     * @throws BeansException Bean 处理异常
     */
    default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override
    default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    @Override
    default PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        return pvs;
    }

}

