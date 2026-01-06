package com.minispring.aop.aspectj.annotation;

import com.minispring.aop.Advisor;
import com.minispring.aop.aspectj.AspectJAnnotationParser;
import com.minispring.aop.framework.autoproxy.AbstractAutoProxyCreator;
import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.BeanFactory;
import com.minispring.beans.factory.BeanFactoryAware;
import com.minispring.beans.factory.config.BeanPostProcessor;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AnnotationAwareAspectJAutoProxyCreator - 注解式 AOP 自动代理创建器
 * <p>
 * 扫描 @Aspect 注解的类,解析为 Advisor,并自动创建代理
 * <p>
 * 设计模式:模板方法模式、工厂模式
 * <p>
 * 面试要点:
 * 1. 注解式 AOP 自动代理原理
 *    - 继承 AbstractAutoProxyCreator
 *    - 扫描所有 Bean,查找 @Aspect 注解
 *    - 解析切面为 Advisor
 *    - 匹配目标 Bean,创建代理
 * <p>
 * 2. 与 DefaultAdvisorAutoProxyCreator 的区别
 *    - DefaultAdvisorAutoProxyCreator: 查找容器中的 Advisor Bean
 *    - AnnotationAwareAspectJAutoProxyCreator: 解析 @Aspect 注解生成 Advisor
 *    - 本实现支持两种方式共存
 * <p>
 * 3. 执行时机
 *    - 作为 BeanPostProcessor,在 Bean 初始化后执行
 *    - postProcessAfterInitialization 中创建代理
 *    - 切面 Bean 本身不会被代理
 *
 * @author mini-spring
 */
public class AnnotationAwareAspectJAutoProxyCreator extends AbstractAutoProxyCreator implements BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;

    /**
     * 缓存已解析的 Advisor
     */
    private List<Advisor> cachedAdvisors;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof DefaultListableBeanFactory)) {
            throw new BeansException("AnnotationAwareAspectJAutoProxyCreator requires a DefaultListableBeanFactory");
        }
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    protected List<Advisor> getAdvisors(Object bean, String beanName) throws BeansException {
        if (cachedAdvisors == null) {
            cachedAdvisors = findAdvisors();
        }
        return cachedAdvisors;
    }

    /**
     * 查找所有 Advisor
     * <p>
     * 包括:
     * 1. 容器中注册的 Advisor Bean
     * 2. @Aspect 注解的类解析出的 Advisor
     *
     * @return Advisor 列表
     */
    private List<Advisor> findAdvisors() throws BeansException {
        List<Advisor> advisors = new ArrayList<>();

        // 1. 查找容器中的 Advisor Bean
        Map<String, Advisor> advisorBeans = beanFactory.getBeansOfType(Advisor.class);
        if (advisorBeans != null) {
            advisors.addAll(advisorBeans.values());
        }

        // 2. 扫描 @Aspect 注解的类
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            try {
                Class<?> beanClass = beanFactory.getBeanDefinition(beanName).getBeanClass();
                if (beanClass != null && beanClass.isAnnotationPresent(Aspect.class)) {
                    // 解析切面
                    List<Advisor> aspectAdvisors = AspectJAnnotationParser.parseAspect(beanClass);
                    advisors.addAll(aspectAdvisors);
                }
            } catch (Exception e) {
                // 忽略解析错误
            }
        }

        return advisors;
    }

    @Override
    protected boolean isInfrastructureClass(Class<?> beanClass) {
        // 切面类本身不需要代理
        return super.isInfrastructureClass(beanClass) || beanClass.isAnnotationPresent(Aspect.class);
    }

}
