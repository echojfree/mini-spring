package com.minispring.beans.factory.config;

import com.minispring.beans.PropertyValues;
import com.minispring.beans.exception.BeansException;

/**
 * InstantiationAwareBeanPostProcessor 接口
 * <p>
 * Bean 实例化感知后置处理器，扩展了 BeanPostProcessor
 * 可以在 Bean 实例化前后以及属性填充前进行干预
 * <p>
 * 面试重点：
 * 1. 与 BeanPostProcessor 的区别
 *    - BeanPostProcessor：在 Bean 初始化前后调用
 *    - InstantiationAwareBeanPostProcessor：在 Bean 实例化、属性填充前后调用
 * 2. 调用时机（完整 Bean 生命周期）：
 *    a. postProcessBeforeInstantiation() ← 实例化之前
 *    b. 实例化（instantiate）
 *    c. postProcessAfterInstantiation() ← 实例化之后
 *    d. postProcessPropertyValues() ← 属性填充之前
 *    e. 属性填充（populate）
 *    f. Aware 接口回调
 *    g. postProcessBeforeInitialization() ← 初始化之前
 *    h. InitializingBean.afterPropertiesSet()
 *    i. init-method
 *    j. postProcessAfterInitialization() ← 初始化之后
 * 3. 典型应用场景
 *    - @Autowired 注解处理（postProcessPropertyValues）
 *    - AOP 代理对象创建（postProcessAfterInitialization）
 *    - 属性注入前的预处理
 *
 * @author mini-spring
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * 在 Bean 实例化之前调用
     * <p>
     * 如果此方法返回非 null 对象，则直接使用该对象，不再执行后续的实例化流程
     * 这是一个短路操作，可以用于创建代理对象
     *
     * @param beanClass Bean 类型
     * @param beanName  Bean 名称
     * @return Bean 实例，如果返回 null 则继续正常流程
     * @throws BeansException 处理失败时抛出异常
     */
    Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;

    /**
     * 在 Bean 实例化之后、属性填充之前调用
     * <p>
     * 返回 false 可以阻止后续的属性填充
     *
     * @param bean     Bean 实例
     * @param beanName Bean 名称
     * @return 是否继续属性填充，true 继续，false 停止
     * @throws BeansException 处理失败时抛出异常
     */
    boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException;

    /**
     * 在属性填充之前调用，可以修改或添加属性值
     * <p>
     * 这是 @Autowired 注解处理的关键方法
     * 在这里扫描 Bean 的字段和方法，发现 @Autowired 注解后进行依赖注入
     *
     * @param pvs      属性值集合
     * @param bean     Bean 实例
     * @param beanName Bean 名称
     * @return 处理后的属性值集合
     * @throws BeansException 处理失败时抛出异常
     */
    PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException;
}
