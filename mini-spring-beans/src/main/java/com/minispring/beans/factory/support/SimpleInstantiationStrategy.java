package com.minispring.beans.factory.support;

import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.config.BeanDefinition;

/**
 * 简单实例化策略：使用 JDK 反射创建 Bean 实例
 * <p>
 * 实现原理：
 * 使用 Class.newInstance() 方法通过反射创建对象
 * <p>
 * 优点：
 * - 实现简单
 * - 不需要额外依赖
 * - 适用于大多数场景
 * <p>
 * 限制：
 * - 只能调用无参构造器
 * - 构造器必须是 public 的
 * - 不能是抽象类
 * <p>
 * 面试考点：
 * 1. Class.newInstance() 的工作原理
 * 2. 反射创建对象的性能问题
 * 3. 为什么需要无参构造器？
 * 4. JDK 反射 vs CGLIB 的区别
 *
 * @author mini-spring
 */
public class SimpleInstantiationStrategy implements InstantiationStrategy {

    /**
     * 使用 JDK 反射实例化 Bean
     * <p>
     * 实现步骤：
     * 1. 获取 Bean 的 Class 对象
     * 2. 调用 Class.newInstance() 创建实例
     * 3. 处理可能的异常
     *
     * @param beanDefinition Bean 定义
     * @return Bean 实例
     * @throws BeansException 实例化失败
     */
    @Override
    public Object instantiate(BeanDefinition beanDefinition) throws BeansException {
        // 获取 Bean 的 Class 对象
        Class<?> beanClass = beanDefinition.getBeanClass();

        try {
            // 使用反射创建实例
            // 面试考点：newInstance() 内部实现
            // 1. 调用无参构造器
            // 2. 构造器必须可访问（public）
            // 3. 不能是抽象类、接口
            return beanClass.newInstance();

        } catch (InstantiationException e) {
            // 实例化异常：抽象类、接口、数组类等
            throw new BeansException(
                    "实例化 Bean 失败: " + beanClass.getName() +
                            "，原因：该类可能是抽象类、接口或数组类，或者没有无参构造函数", e);

        } catch (IllegalAccessException e) {
            // 访问权限异常：构造器不是 public 的
            throw new BeansException(
                    "实例化 Bean 失败: " + beanClass.getName() +
                            "，原因：无参构造函数不是 public 的", e);

        } catch (Exception e) {
            // 其他异常
            throw new BeansException("实例化 Bean 失败: " + beanClass.getName(), e);
        }
    }

}
