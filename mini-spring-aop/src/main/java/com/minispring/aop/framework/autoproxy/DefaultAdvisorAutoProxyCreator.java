package com.minispring.aop.framework.autoproxy;

import com.minispring.aop.Advisor;
import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.BeanFactory;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DefaultAdvisorAutoProxyCreator - 默认 Advisor 自动代理创建器
 * <p>
 * 核心功能：
 * 1. 查找容器中所有 Advisor 类型的 Bean
 * 2. 判断哪些 Advisor 匹配当前 Bean
 * 3. 为匹配的 Bean 创建代理对象
 * <p>
 * 使用场景：
 * 1. 将 DefaultAdvisorAutoProxyCreator 注册为 BeanPostProcessor
 * 2. 容器中定义多个 Advisor（切面）
 * 3. 容器启动时自动为匹配的 Bean 创建代理
 * <p>
 * 工作流程：
 * 1. 容器启动，注册 BeanPostProcessor
 * 2. Bean 初始化后，postProcessAfterInitialization 被调用
 * 3. 查找所有 Advisor
 * 4. 判断是否匹配当前 Bean
 * 5. 如果匹配，创建代理对象
 * 6. 返回代理对象（或原始 Bean）
 * <p>
 * 面试要点：
 * 1. Spring AOP 自动代理原理
 *    - 通过 BeanPostProcessor 机制实现
 *    - 在 Bean 初始化后创建代理
 *    - 对用户透明，无需手动创建代理
 * <p>
 * 2. Advisor 查找机制
 *    - 通过 BeanFactory.getBeansOfType() 查找所有 Advisor
 *    - 遍历所有 Advisor 判断是否匹配
 *    - 使用 Pointcut 进行匹配
 * <p>
 * 3. 代理创建时机
 *    - Bean 实例化：createBeanInstance()
 *    - 属性填充：populateBean()
 *    - 初始化前：applyBeanPostProcessorsBeforeInitialization()
 *    - 初始化：invokeInitMethods()
 *    - 初始化后：applyBeanPostProcessorsAfterInitialization() ← 这里创建代理
 *
 * @author mini-spring
 */
public class DefaultAdvisorAutoProxyCreator extends AbstractAutoProxyCreator {

    @Override
    protected List<Advisor> getAdvisors(Object bean, String beanName) {
        BeanFactory beanFactory = getBeanFactory();
        if (!(beanFactory instanceof DefaultListableBeanFactory)) {
            return new ArrayList<>();
        }

        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) beanFactory;

        // 查找容器中所有 Advisor 类型的 Bean
        Map<String, Advisor> advisorBeans;
        try {
            advisorBeans = listableBeanFactory.getBeansOfType(Advisor.class);
        } catch (BeansException e) {
            return new ArrayList<>();
        }

        if (advisorBeans == null || advisorBeans.isEmpty()) {
            return new ArrayList<>();
        }

        // 过滤出匹配当前 Bean 的 Advisor
        List<Advisor> matchedAdvisors = new ArrayList<>();
        for (Advisor advisor : advisorBeans.values()) {
            if (isAdvisorMatched(advisor, bean.getClass())) {
                matchedAdvisors.add(advisor);
            }
        }

        return matchedAdvisors;
    }

}
