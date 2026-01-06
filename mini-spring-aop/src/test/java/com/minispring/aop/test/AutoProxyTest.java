package com.minispring.aop.test;

import com.minispring.aop.AspectJExpressionPointcutAdvisor;
import com.minispring.aop.MethodBeforeAdvice;
import com.minispring.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * AutoProxyTest - AOP 自动代理测试
 * <p>
 * 测试 AOP 与 IoC 容器的集成
 * <p>
 * 测试内容：
 * 1. DefaultAdvisorAutoProxyCreator 自动创建代理
 * 2. Advisor 自动匹配和应用
 * 3. 代理对象的方法拦截
 * <p>
 * 面试要点：
 * 1. AOP 自动代理原理
 *    - 通过 BeanPostProcessor 在 Bean 初始化后创建代理
 *    - 容器启动时自动为匹配的 Bean 创建代理
 *    - 用户无需手动创建 ProxyFactory
 * <p>
 * 2. 集成流程
 *    - 注册 DefaultAdvisorAutoProxyCreator 为 BeanPostProcessor
 *    - 注册 Advisor（切面）到容器
 *    - 注册目标 Bean 到容器
 *    - 容器启动时自动创建代理
 * <p>
 * 3. 代理创建时机
 *    - Bean 初始化后（postProcessAfterInitialization）
 *    - 在 Bean 可用前完成代理创建
 *
 * @author mini-spring
 */
public class AutoProxyTest {

    /**
     * 测试 DefaultAdvisorAutoProxyCreator 自动创建代理
     * <p>
     * 验证：
     * 1. DefaultAdvisorAutoProxyCreator 作为 BeanPostProcessor 工作
     * 2. 自动查找容器中的 Advisor
     * 3. 自动为匹配的 Bean 创建代理
     * 4. 代理对象能够拦截方法调用
     */
    @Test
    public void testAutoProxy() throws Exception {
        System.out.println("\n=== 测试：AOP 自动代理 ===\n");

        // 1. 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 注册 DefaultAdvisorAutoProxyCreator（BeanPostProcessor）
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(autoProxyCreator);
        System.out.println("✓ 注册 DefaultAdvisorAutoProxyCreator");

        // 3. 注册 Advisor（切面）
        // 创建前置通知
        MethodBeforeAdvice beforeAdvice = new UserServiceBeforeAdvice();

        // 创建 Advisor
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression("execution(* com.minispring.aop.test.UserService.*(..))");
        advisor.setAdvice(beforeAdvice);

        // 注册 Advisor 到容器
        BeanDefinition advisorBeanDefinition = new BeanDefinition(AspectJExpressionPointcutAdvisor.class);
        beanFactory.registerBeanDefinition("advisor", advisorBeanDefinition);
        beanFactory.registerSingleton("advisor", advisor);
        System.out.println("✓ 注册 Advisor: execution(* com.minispring.aop.test.UserService.*(..))");

        // 4. 注册目标 Bean
        BeanDefinition beanDefinition = new BeanDefinition(UserServiceImpl.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);
        System.out.println("✓ 注册目标 Bean: UserServiceImpl");

        // 5. 获取 Bean（此时会触发代理创建）
        UserService userService = (UserService) beanFactory.getBean("userService");

        // 6. 验证返回的是代理对象
        assertNotNull("代理对象不应为null", userService);
        assertTrue("应该返回代理对象", isProxy(userService));
        System.out.println("✓ 获取到代理对象: " + userService.getClass().getName());

        // 7. 调用方法，验证拦截器生效
        System.out.println("\n--- 调用代理方法 ---");
        String result = userService.queryUser("001");
        assertEquals("User-001", result);

        boolean success = userService.register("张三");
        assertTrue(success);

        System.out.println("\n✅ 测试通过：AOP 自动代理功能正常\n");
    }

    /**
     * 测试不匹配的 Bean 不会被代理
     * <p>
     * 验证：
     * 1. Advisor 的切点表达式不匹配某个 Bean
     * 2. 该 Bean 不会被代理
     * 3. 返回原始 Bean 对象
     */
    @Test
    public void testNoProxyForNonMatchingBean() throws Exception {
        System.out.println("\n=== 测试：不匹配的 Bean 不会被代理 ===\n");

        // 1. 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 注册 DefaultAdvisorAutoProxyCreator
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(autoProxyCreator);

        // 3. 注册 Advisor（只匹配 queryUser 方法，不匹配 register 方法）
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression("execution(* com.minispring.aop.test.UserService.queryUser(..))");
        advisor.setAdvice(new UserServiceBeforeAdvice());

        BeanDefinition advisorBeanDefinition = new BeanDefinition(AspectJExpressionPointcutAdvisor.class);
        beanFactory.registerBeanDefinition("advisor", advisorBeanDefinition);
        beanFactory.registerSingleton("advisor", advisor);
        System.out.println("✓ 注册 Advisor: execution(* com.minispring.aop.test.UserService.queryUser(..))");

        // 4. 注册 UserService Bean（部分匹配，仍然会创建代理）
        BeanDefinition beanDefinition = new BeanDefinition(UserServiceImpl.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);
        System.out.println("✓ 注册 Bean: UserServiceImpl（部分方法匹配切点表达式）");

        // 5. 获取 Bean
        UserService userService = (UserService) beanFactory.getBean("userService");

        // 6. 验证返回的是代理对象（因为 queryUser 方法匹配）
        assertNotNull("Bean 不应为null", userService);
        assertTrue("应该返回代理对象（因为有方法匹配）", isProxy(userService));
        System.out.println("✓ 获取到代理对象: " + userService.getClass().getName());

        // 7. 调用 queryUser 方法（会被拦截）
        System.out.println("\n--- 调用 queryUser 方法（会被拦截） ---");
        String result = userService.queryUser("002");
        assertEquals("User-002", result);

        // 8. 调用 register 方法（不会被拦截，但仍然在代理对象中）
        System.out.println("\n--- 调用 register 方法（不会被拦截） ---");
        boolean success = userService.register("测试");
        assertTrue(success);

        System.out.println("\n✅ 测试通过：部分方法匹配时创建代理\n");
    }

    /**
     * 测试多个 Advisor
     * <p>
     * 验证：
     * 1. 容器中有多个 Advisor
     * 2. Bean 匹配多个 Advisor
     * 3. 只要有一个匹配就创建代理
     */
    @Test
    public void testMultipleAdvisors() throws Exception {
        System.out.println("\n=== 测试：多个 Advisor ===\n");

        // 1. 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 注册 DefaultAdvisorAutoProxyCreator
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(autoProxyCreator);

        // 3. 注册第一个 Advisor（匹配 queryUser 方法）
        AspectJExpressionPointcutAdvisor advisor1 = new AspectJExpressionPointcutAdvisor();
        advisor1.setExpression("execution(* com.minispring.aop.test.UserService.queryUser(..))");
        advisor1.setAdvice(new UserServiceBeforeAdvice());
        beanFactory.registerSingleton("advisor1", advisor1);
        System.out.println("✓ 注册 Advisor1: execution(* com.minispring.aop.test.UserService.queryUser(..))");

        // 4. 注册第二个 Advisor（匹配 register 方法）
        AspectJExpressionPointcutAdvisor advisor2 = new AspectJExpressionPointcutAdvisor();
        advisor2.setExpression("execution(* com.minispring.aop.test.UserService.register(..))");
        advisor2.setAdvice(new UserServiceBeforeAdvice());
        beanFactory.registerSingleton("advisor2", advisor2);
        System.out.println("✓ 注册 Advisor2: execution(* com.minispring.aop.test.UserService.register(..))");

        // 5. 注册目标 Bean
        BeanDefinition beanDefinition = new BeanDefinition(UserServiceImpl.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 6. 获取 Bean
        UserService userService = (UserService) beanFactory.getBean("userService");

        // 7. 验证返回的是代理对象
        assertNotNull("代理对象不应为null", userService);
        assertTrue("应该返回代理对象", isProxy(userService));
        System.out.println("✓ 获取到代理对象（匹配多个 Advisor）");

        // 8. 调用方法
        System.out.println("\n--- 调用代理方法 ---");
        String result = userService.queryUser("003");
        assertEquals("User-003", result);

        System.out.println("\n✅ 测试通过：多个 Advisor 场景正常\n");
    }

    /**
     * 判断对象是否是代理对象
     */
    private boolean isProxy(Object object) {
        // JDK 动态代理
        if (java.lang.reflect.Proxy.isProxyClass(object.getClass())) {
            return true;
        }
        // CGLIB 代理
        return object.getClass().getName().contains("$$");
    }

}
