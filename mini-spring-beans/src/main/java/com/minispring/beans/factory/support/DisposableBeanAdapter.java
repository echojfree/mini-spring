package com.minispring.beans.factory.support;

import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.DisposableBean;

import java.lang.reflect.Method;

/**
 * DisposableBean 适配器
 * <p>
 * 功能：统一管理 Bean 的销毁方法
 * 支持两种销毁方式：
 * 1. 实现 DisposableBean 接口
 * 2. 配置 destroy-method
 * <p>
 * 设计模式：
 * - 适配器模式：将不同的销毁方式适配为统一接口
 * - 策略模式：根据配置选择不同的销毁策略
 * <p>
 * 面试考点：
 * 1. 为什么需要适配器？
 *    - 统一管理不同的销毁方式
 *    - 简化容器的销毁逻辑
 * 2. DisposableBean 和 destroy-method 的执行顺序
 *    - 先执行 DisposableBean.destroy()
 *    - 再执行 destroy-method 配置的方法
 * 3. 销毁方法的异常处理
 *    - 捕获异常并记录日志，不影响其他 Bean 的销毁
 *
 * @author mini-spring
 */
public class DisposableBeanAdapter implements DisposableBean {

    /**
     * Bean 实例
     */
    private final Object bean;

    /**
     * Bean 名称
     */
    private final String beanName;

    /**
     * 自定义销毁方法名称
     */
    private final String destroyMethodName;

    /**
     * 构造函数
     *
     * @param bean              Bean 实例
     * @param beanName          Bean 名称
     * @param destroyMethodName 销毁方法名称
     */
    public DisposableBeanAdapter(Object bean, String beanName, String destroyMethodName) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = destroyMethodName;
    }

    /**
     * 执行销毁方法
     * <p>
     * 执行顺序（面试考点）：
     * 1. 先调用 DisposableBean 接口的 destroy 方法
     * 2. 再调用自定义的 destroy-method
     * <p>
     * 异常处理：
     * - 捕获异常并包装为 BeansException
     * - 确保一个销毁方法失败不影响其他 Bean
     *
     * @throws Exception 销毁失败时抛出异常
     */
    @Override
    public void destroy() throws Exception {
        // 步骤1：调用 DisposableBean 接口的 destroy 方法
        if (bean instanceof DisposableBean) {
            try {
                ((DisposableBean) bean).destroy();
            } catch (Exception e) {
                throw new BeansException("DisposableBean.destroy() 调用失败: " + beanName, e);
            }
        }

        // 步骤2：调用自定义 destroy-method
        if (destroyMethodName != null && !destroyMethodName.isEmpty()) {
            // 避免重复调用（如果 destroy-method 配置的就是 "destroy"）
            if (bean instanceof DisposableBean && "destroy".equals(destroyMethodName)) {
                // 已经在步骤1中调用过了，跳过
                return;
            }

            try {
                Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
                destroyMethod.invoke(bean);
            } catch (Exception e) {
                throw new BeansException("destroy-method 调用失败: " + beanName + "." + destroyMethodName, e);
            }
        }
    }

}
