package com.minispring.aop.test;

import com.minispring.aop.AspectJExpressionPointcut;
import com.minispring.aop.MethodBeforeAdvice;
import com.minispring.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.BeanReference;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.minispring.beans.PropertyValue;
import com.minispring.beans.PropertyValues;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * AopCircularReferenceTest - AOP 循环依赖测试
 * <p>
 * 测试 AOP 代理场景下的循环依赖解决
 * <p>
 * 测试内容:
 * 1. AOP Bean 的循环依赖
 * 2. 早期代理对象暴露
 * 3. SmartInstantiationAwareBeanPostProcessor 的使用
 * <p>
 * 面试要点:
 * 1. AOP 循环依赖的特殊性
 *    - 普通循环依赖: 暴露原始对象
 *    - AOP 循环依赖: 暴露代理对象
 *    - 需要提前创建代理
 * <p>
 * 2. 三级缓存机制
 *    - singletonObjects: 成品对象(代理对象)
 *    - earlySingletonObjects: 早期对象
 *    - singletonFactories: ObjectFactory
 * <p>
 * 3. SmartInstantiationAwareBeanPostProcessor
 *    - getEarlyBeanReference() 在三级缓存中调用
 *    - 提前创建代理对象
 *    - 确保注入的是代理对象而不是原始对象
 * <p>
 * 4. 解决流程
 *    - 1. A 实例化,放入三级缓存
 *    - 2. A 填充属性,发现依赖 B
 *    - 3. B 实例化,放入三级缓存
 *    - 4. B 填充属性,发现依赖 A
 *    - 5. 从三级缓存获取 A,调用 getEarlyBeanReference 创建 A 的代理
 *    - 6. B 注入 A 的代理对象
 *    - 7. B 初始化完成,返回给 A
 *    - 8. A 注入 B
 *    - 9. A 初始化,检测到已创建早期代理,返回缓存的代理对象
 *
 * @author mini-spring
 */
public class AopCircularReferenceTest {

    /**
     * 测试 AOP 场景下的循环依赖
     * <p>
     * 场景:
     * ServiceA 依赖 ServiceB
     * ServiceB 依赖 ServiceA
     * ServiceA 需要 AOP 代理
     * <p>
     * 验证:
     * 1. 循环依赖能够正常解决
     * 2. ServiceB 注入的是 ServiceA 的代理对象
     * 3. AOP 通知正常执行
     */
    @Test
    public void testCircularReferenceWithAop() throws Exception {
        System.out.println("\n=== 测试：AOP 场景下的循环依赖 ===\n");

        // 1. 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 注册 ServiceA (带 AOP)
        PropertyValues pvA = new PropertyValues();
        pvA.addPropertyValue(new PropertyValue("serviceB", new BeanReference("serviceB")));
        BeanDefinition serviceADef = new BeanDefinition(ServiceA.class, pvA);
        beanFactory.registerBeanDefinition("serviceA", serviceADef);

        // 3. 注册 ServiceB
        PropertyValues pvB = new PropertyValues();
        pvB.addPropertyValue(new PropertyValue("serviceA", new BeanReference("serviceA")));
        BeanDefinition serviceBDef = new BeanDefinition(ServiceB.class, pvB);
        beanFactory.registerBeanDefinition("serviceB", serviceBDef);

        // 4. 注册 Advisor (只对 ServiceA 生效)
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut(
                "execution(* com.minispring.aop.test.AopCircularReferenceTest.ServiceA.*(..))"
        );
        MethodBeforeAdvice advice = (method, args, target) -> {
            System.out.println("[AOP通知] 方法: " + method.getName());
        };

        // 使用 AspectJPointcutAdvisor 代替匿名类
        com.minispring.aop.framework.adapter.MethodBeforeAdviceInterceptor interceptor =
            new com.minispring.aop.framework.adapter.MethodBeforeAdviceInterceptor(advice);
        com.minispring.aop.aspectj.AspectJPointcutAdvisor advisor =
            new com.minispring.aop.aspectj.AspectJPointcutAdvisor(pointcut, interceptor);
        beanFactory.registerSingleton("advisor", advisor);

        // 5. 注册自动代理创建器
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(autoProxyCreator);

        // 6. 获取 Bean
        System.out.println("\n--- 获取 ServiceA ---");
        ServiceA serviceA = (ServiceA) beanFactory.getBean("serviceA");
        assertNotNull(serviceA);
        assertNotNull(serviceA.getServiceB());

        System.out.println("\n--- 获取 ServiceB ---");
        ServiceB serviceB = (ServiceB) beanFactory.getBean("serviceB");
        assertNotNull(serviceB);
        assertNotNull(serviceB.getServiceA());

        // 7. 验证循环依赖
        assertSame(serviceB, serviceA.getServiceB());
        assertSame(serviceA, serviceB.getServiceA());

        // 8. 调用方法,验证 AOP 生效
        System.out.println("\n--- 调用 ServiceA 方法 ---");
        serviceA.doSomething();

        System.out.println("\n--- 调用 ServiceB 方法 ---");
        serviceB.doSomething();

        System.out.println("\n--- 通过 ServiceB 调用 ServiceA ---");
        serviceB.callServiceA();

        System.out.println("\n✅ 测试通过：AOP 循环依赖正确解决\n");
    }

    /**
     * ServiceA - 测试服务 A
     */
    public static class ServiceA {
        private ServiceB serviceB;

        public void setServiceB(ServiceB serviceB) {
            this.serviceB = serviceB;
            System.out.println("  ServiceA.setServiceB(): " + (serviceB != null ? serviceB.getClass().getName() : "null"));
        }

        public ServiceB getServiceB() {
            return serviceB;
        }

        public void doSomething() {
            System.out.println("  ServiceA.doSomething() 执行");
        }
    }

    /**
     * ServiceB - 测试服务 B
     */
    public static class ServiceB {
        private ServiceA serviceA;

        public void setServiceA(ServiceA serviceA) {
            this.serviceA = serviceA;
            System.out.println("  ServiceB.setServiceA(): " + (serviceA != null ? serviceA.getClass().getName() : "null"));
        }

        public ServiceA getServiceA() {
            return serviceA;
        }

        public void doSomething() {
            System.out.println("  ServiceB.doSomething() 执行");
        }

        public void callServiceA() {
            System.out.println("  ServiceB 调用 ServiceA:");
            serviceA.doSomething();
        }
    }

}
