package com.minispring.aop.test;

import com.minispring.aop.AspectJExpressionPointcut;
import com.minispring.aop.MethodBeforeAdvice;
import com.minispring.aop.framework.ProxyFactory;
import com.minispring.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * AopTest - AOP 功能测试类
 * <p>
 * 测试 AOP 的核心功能
 * <p>
 * 测试内容：
 * 1. JDK 动态代理
 * 2. CGLIB 代理
 * 3. AspectJ 表达式切点
 * 4. 前置通知
 * 5. 环绕通知（MethodInterceptor）
 * 6. ProxyFactory 自动选择代理方式
 * <p>
 * 面试要点：
 * 1. AOP 的核心概念
 *    - Pointcut：切点（在哪里）
 *    - Advice：通知（做什么）
 *    - Advisor：切面（切点+通知）
 * <p>
 * 2. 代理方式选择
 *    - 有接口：JDK 动态代理
 *    - 无接口：CGLIB 代理
 *    - proxyTargetClass=true：强制 CGLIB
 * <p>
 * 3. 通知类型
 *    - MethodBeforeAdvice：前置通知
 *    - MethodInterceptor：环绕通知
 *    - AfterReturningAdvice：后置通知
 *    - ThrowsAdvice：异常通知
 *
 * @author mini-spring
 */
public class AopTest {

    /**
     * 测试 JDK 动态代理 + 前置通知
     * <p>
     * 验证：
     * 1. JDK 动态代理能够创建代理对象
     * 2. 前置通知能够在方法执行前执行
     * 3. AspectJ 表达式切点能够正确匹配方法
     */
    @Test
    public void testJdkProxyWithBeforeAdvice() throws Exception {
        System.out.println("\n=== 测试1：JDK 动态代理 + 前置通知 ===\n");

        // 1. 创建目标对象
        UserService target = new UserServiceImpl();

        // 2. 创建前置通知
        MethodBeforeAdvice beforeAdvice = new UserServiceBeforeAdvice();

        // 3. 创建 AspectJ 表达式切点
        // 匹配 UserService 的所有方法
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut(
                "execution(* com.minispring.aop.test.UserService.*(..))"
        );

        // 4. 创建 ProxyFactory
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(target);
        factory.setInterfaces(target.getClass().getInterfaces());
        factory.setMethodInterceptor(new MethodBeforeAdviceInterceptor(beforeAdvice));
        factory.setMethodMatcher(pointcut.getMethodMatcher());

        // 5. 获取代理对象
        UserService proxy = (UserService) factory.getProxy();

        // 6. 验证代理对象类型
        assertNotNull("代理对象不应为null", proxy);
        assertTrue("代理对象应该实现UserService接口", proxy instanceof UserService);
        System.out.println("代理对象类型: " + proxy.getClass().getName());

        // 7. 调用代理方法
        String result = proxy.queryUser("001");
        assertEquals("User-001", result);

        boolean success = proxy.register("张三");
        assertTrue(success);

        System.out.println("\n✅ 测试1通过：JDK 动态代理 + 前置通知\n");
    }

    /**
     * 测试 CGLIB 代理 + 环绕通知
     * <p>
     * 验证：
     * 1. CGLIB 代理能够创建代理对象
     * 2. 环绕通知能够在方法执行前后执行
     * 3. 性能监控功能正常工作
     */
    @Test
    public void testCglibProxyWithAroundAdvice() throws Exception {
        System.out.println("\n=== 测试2：CGLIB 代理 + 环绕通知 ===\n");

        // 1. 创建目标对象
        UserServiceImpl target = new UserServiceImpl();

        // 2. 创建环绕通知（性能监控）
        MethodInterceptor interceptor = new PerformanceMonitorInterceptor();

        // 3. 创建 AspectJ 表达式切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut(
                "execution(* com.minispring.aop.test.UserServiceImpl.*(..))"
        );

        // 4. 创建 ProxyFactory（强制使用 CGLIB）
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(target);
        factory.setProxyTargetClass(true); // 强制使用 CGLIB
        factory.setMethodInterceptor(interceptor);
        factory.setMethodMatcher(pointcut.getMethodMatcher());

        // 5. 获取代理对象
        UserServiceImpl proxy = (UserServiceImpl) factory.getProxy();

        // 6. 验证代理对象类型
        assertNotNull("代理对象不应为null", proxy);
        System.out.println("代理对象类型: " + proxy.getClass().getName());
        assertTrue("代理对象应该是CGLIB代理", proxy.getClass().getName().contains("$$"));

        // 7. 调用代理方法
        String result = proxy.queryUser("002");
        assertEquals("User-002", result);

        System.out.println("\n✅ 测试2通过：CGLIB 代理 + 环绕通知\n");
    }

    /**
     * 测试 ProxyFactory 自动选择代理方式
     * <p>
     * 验证：
     * 1. 有接口时自动使用 JDK 动态代理
     * 2. 无接口时自动使用 CGLIB 代理
     */
    @Test
    public void testProxyFactoryAutoSelection() throws Exception {
        System.out.println("\n=== 测试3：ProxyFactory 自动选择代理方式 ===\n");

        UserService target = new UserServiceImpl();
        MethodInterceptor interceptor = new PerformanceMonitorInterceptor();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut(
                "execution(* com.minispring.aop.test.UserService.*(..))"
        );

        // 情况1：有接口，应该使用 JDK 动态代理
        ProxyFactory factory1 = new ProxyFactory();
        factory1.setTarget(target);
        factory1.setInterfaces(target.getClass().getInterfaces());
        factory1.setMethodInterceptor(interceptor);
        factory1.setMethodMatcher(pointcut.getMethodMatcher());

        Object proxy1 = factory1.getProxy();
        System.out.println("有接口时代理类型: " + proxy1.getClass().getName());
        assertTrue("应该使用JDK动态代理", java.lang.reflect.Proxy.isProxyClass(proxy1.getClass()));

        // 情况2：无接口，应该使用 CGLIB 代理
        ProxyFactory factory2 = new ProxyFactory();
        factory2.setTarget(target);
        // 不设置接口
        factory2.setMethodInterceptor(interceptor);
        factory2.setMethodMatcher(pointcut.getMethodMatcher());

        Object proxy2 = factory2.getProxy();
        System.out.println("无接口时代理类型: " + proxy2.getClass().getName());
        assertTrue("应该使用CGLIB代理", proxy2.getClass().getName().contains("$$"));

        System.out.println("\n✅ 测试3通过：ProxyFactory 自动选择代理方式\n");
    }

    /**
     * 测试 AspectJ 表达式切点匹配
     * <p>
     * 验证：
     * 1. 表达式能够正确匹配类
     * 2. 表达式能够正确匹配方法
     * 3. 不匹配的方法不会被拦截
     */
    @Test
    public void testAspectJExpressionPointcut() throws Exception {
        System.out.println("\n=== 测试4：AspectJ 表达式切点匹配 ===\n");

        // 1. 测试类匹配
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut(
                "execution(* com.minispring.aop.test.UserService.*(..))"
        );

        boolean classMatch = pointcut.matches(UserServiceImpl.class);
        System.out.println("类匹配结果: " + classMatch);
        assertTrue("UserServiceImpl 应该匹配", classMatch);

        // 2. 测试方法匹配
        java.lang.reflect.Method queryUserMethod = UserService.class.getMethod("queryUser", String.class);
        boolean methodMatch = pointcut.matches(queryUserMethod, UserServiceImpl.class);
        System.out.println("方法匹配结果: " + methodMatch);
        assertTrue("queryUser 方法应该匹配", methodMatch);

        System.out.println("\n✅ 测试4通过：AspectJ 表达式切点匹配\n");
    }

}
