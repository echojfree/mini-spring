package com.minispring.aop.framework.autoproxy;

import com.minispring.aop.Advisor;
import com.minispring.aop.ClassFilter;
import com.minispring.aop.Pointcut;
import com.minispring.aop.PointcutAdvisor;
import com.minispring.aop.framework.AdvisedSupport;
import com.minispring.aop.framework.ProxyFactory;
import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.BeanFactory;
import com.minispring.beans.factory.BeanFactoryAware;
import com.minispring.beans.factory.config.BeanPostProcessor;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * AbstractAutoProxyCreator - AOP 自动代理创建器基类
 * <p>
 * 核心功能：
 * 1. 实现 BeanPostProcessor 接口，在 Bean 初始化后创建代理
 * 2. 查找匹配的 Advisor
 * 3. 为目标 Bean 创建代理对象
 * <p>
 * 设计模式：
 * 1. 模板方法模式：定义代理创建流程，子类实现具体查找逻辑
 * 2. 策略模式：通过 ProxyFactory 自动选择代理方式
 * <p>
 * 面试要点：
 * 1. AOP 如何与 IoC 集成
 *    - 通过 BeanPostProcessor 在 Bean 初始化后创建代理
 *    - postProcessAfterInitialization 是代理创建时机
 * <p>
 * 2. 代理创建流程
 *    - 获取所有 Advisor
 *    - 判断 Advisor 是否匹配当前 Bean
 *    - 如果匹配，创建代理对象并返回
 *    - 如果不匹配，返回原始 Bean
 * <p>
 * 3. 匹配逻辑
 *    - ClassFilter 匹配：类级别过滤
 *    - MethodMatcher 匹配：方法级别过滤
 *    - 只要有一个方法匹配，就创建代理
 *
 * @author mini-spring
 */
public abstract class AbstractAutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    protected BeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 初始化前不做处理
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 避免对 Advisor 类型的 Bean 进行代理
        if (isInfrastructureClass(bean.getClass())) {
            return bean;
        }

        // 获取所有匹配的 Advisor
        List<Advisor> advisors = getAdvisors(bean, beanName);
        if (advisors == null || advisors.isEmpty()) {
            // 没有匹配的 Advisor，返回原始 Bean
            return bean;
        }

        // 创建代理对象
        return createProxy(bean, beanName, advisors);
    }

    /**
     * 判断是否是基础设施类（Advisor、Advice 等）
     */
    protected boolean isInfrastructureClass(Class<?> beanClass) {
        return Advisor.class.isAssignableFrom(beanClass)
                || MethodInterceptor.class.isAssignableFrom(beanClass);
    }

    /**
     * 获取所有匹配的 Advisor
     * 模板方法，由子类实现具体查找逻辑
     */
    protected abstract List<Advisor> getAdvisors(Object bean, String beanName);

    /**
     * 判断 Advisor 是否匹配目标 Bean
     */
    protected boolean isAdvisorMatched(Advisor advisor, Class<?> targetClass) {
        if (!(advisor instanceof PointcutAdvisor)) {
            // 不是 PointcutAdvisor，默认匹配
            return true;
        }

        PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
        Pointcut pointcut = pointcutAdvisor.getPointcut();

        // 1. ClassFilter 匹配
        ClassFilter classFilter = pointcut.getClassFilter();
        if (!classFilter.matches(targetClass)) {
            return false;
        }

        // 2. MethodMatcher 匹配：检查目标类的所有方法
        // 只要有一个方法匹配，就认为 Advisor 匹配
        try {
            java.lang.reflect.Method[] methods = targetClass.getDeclaredMethods();
            for (java.lang.reflect.Method method : methods) {
                if (pointcut.getMethodMatcher().matches(method, targetClass)) {
                    return true;
                }
            }
        } catch (Exception e) {
            // 忽略异常，返回 false
            return false;
        }

        return false;
    }

    /**
     * 创建代理对象
     */
    protected Object createProxy(Object bean, String beanName, List<Advisor> advisors) {
        // 创建 ProxyFactory
        ProxyFactory proxyFactory = new ProxyFactory();

        // 设置目标对象
        proxyFactory.setTarget(bean);

        // 设置接口（如果有）
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        if (interfaces.length > 0) {
            proxyFactory.setInterfaces(interfaces);
        }

        // 设置 Advisor（目前只支持单个 Advisor，后续可扩展为多个）
        if (!advisors.isEmpty()) {
            Advisor advisor = advisors.get(0);
            if (advisor instanceof PointcutAdvisor) {
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
                proxyFactory.setMethodMatcher(pointcutAdvisor.getPointcut().getMethodMatcher());
                proxyFactory.setMethodInterceptor((MethodInterceptor) pointcutAdvisor.getAdvice());
            }
        }

        // 创建代理
        return proxyFactory.getProxy();
    }

}
