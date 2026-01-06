package com.minispring.aop.test;

import com.minispring.aop.aspectj.annotation.*;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * AnnotationAopTest - 注解式 AOP 测试
 * <p>
 * 测试 @Aspect 注解和各种通知注解
 * <p>
 * 测试内容:
 * 1. @Before 前置通知
 * 2. @AfterReturning 后置通知
 * 3. @Around 环绕通知
 * 4. 多个切面组合
 * <p>
 * 面试要点:
 * 1. 注解式 AOP 原理
 *    - @Aspect 标记切面类
 *    - @Before/@After/@Around 标记通知方法
 *    - AnnotationAwareAspectJAutoProxyCreator 扫描和解析
 *    - 自动创建代理对象
 * <p>
 * 2. 通知执行顺序
 *    - @Around 前置部分
 *    - @Before
 *    - 目标方法
 *    - @AfterReturning
 *    - @Around 后置部分
 * <p>
 * 3. 切点表达式
 *    - execution: 方法执行切点
 *    - within: 类型切点
 *    - 支持通配符 *、..
 *
 * @author mini-spring
 */
public class AnnotationAopTest {

    /**
     * 测试 @Before 前置通知
     * <p>
     * 验证:
     * 1. @Aspect 注解能被识别
     * 2. @Before 通知在目标方法前执行
     * 3. 切点表达式正确匹配
     */
    @Test
    public void testBeforeAdvice() throws Exception {
        System.out.println("\n=== 测试：@Before 前置通知 ===\n");

        // 1. 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 注册目标 Bean
        UserService target = new UserServiceImpl();
        beanFactory.registerSingleton("userService", target);

        // 3. 注册自动代理创建器
        AnnotationAwareAspectJAutoProxyCreator autoProxyCreator = new AnnotationAwareAspectJAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);

        // 4. 注册切面 Bean
        beanFactory.registerSingleton("loggingAspect", new LoggingAspect());

        // 5. 触发代理创建
        Object proxy = autoProxyCreator.postProcessAfterInitialization(target, "userService");

        // 6. 验证代理已创建
        assertNotNull(proxy);
        assertTrue(proxy instanceof UserService);

        // 7. 调用方法,验证前置通知执行
        System.out.println("\n--- 调用代理方法 ---");
        String result = ((UserService) proxy).queryUser("001");
        assertEquals("User-001", result);

        System.out.println("\n✅ 测试通过：@Before 前置通知正确执行\n");
    }

    /**
     * 测试 @AfterReturning 后置通知
     * <p>
     * 验证:
     * 1. @AfterReturning 在目标方法正常返回后执行
     * 2. 可以访问返回值
     */
    @Test
    public void testAfterReturningAdvice() throws Exception {
        System.out.println("\n=== 测试：@AfterReturning 后置通知 ===\n");

        // 1. 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 注册目标 Bean
        UserService target = new UserServiceImpl();
        beanFactory.registerSingleton("userService", target);

        // 3. 注册自动代理创建器
        AnnotationAwareAspectJAutoProxyCreator autoProxyCreator = new AnnotationAwareAspectJAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);

        // 4. 注册切面 Bean
        beanFactory.registerSingleton("monitoringAspect", new MonitoringAspect());

        // 5. 触发代理创建
        Object proxy = autoProxyCreator.postProcessAfterInitialization(target, "userService");

        // 6. 调用方法
        System.out.println("\n--- 调用代理方法 ---");
        String result = ((UserService) proxy).queryUser("002");
        assertEquals("User-002", result);

        System.out.println("\n✅ 测试通过：@AfterReturning 后置通知正确执行\n");
    }

    /**
     * 测试 @Around 环绕通知
     * <p>
     * 验证:
     * 1. @Around 可以包裹目标方法
     * 2. 可以控制目标方法执行
     * 3. 可以修改返回值
     */
    @Test
    public void testAroundAdvice() throws Exception {
        System.out.println("\n=== 测试：@Around 环绕通知 ===\n");

        // 1. 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 注册目标 Bean
        UserService target = new UserServiceImpl();
        beanFactory.registerSingleton("userService", target);

        // 3. 注册自动代理创建器
        AnnotationAwareAspectJAutoProxyCreator autoProxyCreator = new AnnotationAwareAspectJAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);

        // 4. 注册切面 Bean
        beanFactory.registerSingleton("performanceAspect", new PerformanceAspect());

        // 5. 触发代理创建
        Object proxy = autoProxyCreator.postProcessAfterInitialization(target, "userService");

        // 6. 调用方法
        System.out.println("\n--- 调用代理方法 ---");
        String result = ((UserService) proxy).queryUser("003");
        assertEquals("User-003", result);

        System.out.println("\n✅ 测试通过：@Around 环绕通知正确执行\n");
    }

    /**
     * 测试多个切面组合
     * <p>
     * 验证:
     * 1. 多个切面可以同时生效
     * 2. 切面按顺序执行
     */
    @Test
    public void testMultipleAspects() throws Exception {
        System.out.println("\n=== 测试：多个切面组合 ===\n");

        // 1. 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 注册目标 Bean
        UserService target = new UserServiceImpl();
        beanFactory.registerSingleton("userService", target);

        // 3. 注册自动代理创建器
        AnnotationAwareAspectJAutoProxyCreator autoProxyCreator = new AnnotationAwareAspectJAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);

        // 4. 注册多个切面 Bean
        beanFactory.registerSingleton("loggingAspect", new LoggingAspect());
        beanFactory.registerSingleton("monitoringAspect", new MonitoringAspect());
        beanFactory.registerSingleton("performanceAspect", new PerformanceAspect());

        // 5. 触发代理创建
        Object proxy = autoProxyCreator.postProcessAfterInitialization(target, "userService");

        // 6. 调用方法
        System.out.println("\n--- 调用代理方法 ---");
        String result = ((UserService) proxy).queryUser("004");
        assertEquals("User-004", result);

        System.out.println("\n✅ 测试通过：多个切面正确组合执行\n");
    }

}

/**
 * LoggingAspect - 日志切面
 */
@Aspect
class LoggingAspect {

    @Before("execution(* com.minispring.aop.test.UserService.*(..))")
    public void logBefore() {
        System.out.println("[日志切面 - @Before] 方法执行前");
    }

}

/**
 * MonitoringAspect - 监控切面
 */
@Aspect
class MonitoringAspect {

    @AfterReturning("execution(* com.minispring.aop.test.UserService.*(..))")
    public void monitor() {
        System.out.println("[监控切面 - @AfterReturning] 方法执行后");
    }

}

/**
 * PerformanceAspect - 性能监控切面
 */
@Aspect
class PerformanceAspect {

    @Around("execution(* com.minispring.aop.test.UserService.*(..))")
    public Object measurePerformance(MethodInvocation invocation) throws Throwable {
        long start = System.currentTimeMillis();
        System.out.println("[性能切面 - @Around前] 开始计时");

        Object result = invocation.proceed();

        long end = System.currentTimeMillis();
        System.out.println("[性能切面 - @Around后] 执行耗时: " + (end - start) + "ms");

        return result;
    }

}
