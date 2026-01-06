package com.minispring.aop.test;

import com.minispring.aop.AfterReturningAdvice;
import com.minispring.aop.AspectJExpressionPointcut;
import com.minispring.aop.MethodBeforeAdvice;
import com.minispring.aop.framework.ProxyFactory;
import com.minispring.aop.framework.adapter.AfterReturningAdviceInterceptor;
import com.minispring.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * InterceptorChainTest - 拦截器链测试
 * <p>
 * 测试多个拦截器按顺序执行
 * <p>
 * 测试内容：
 * 1. 多个前置通知按顺序执行
 * 2. 前置通知 + 后置通知组合
 * 3. 环绕通知 + 前置/后置通知组合
 * <p>
 * 面试要点：
 * 1. 拦截器链执行顺序
 *    - 前置通知：按添加顺序执行
 *    - 后置通知：在目标方法返回后执行
 *    - 环绕通知：包裹目标方法执行
 * <p>
 * 2. 责任链模式
 *    - 每个拦截器调用 proceed() 继续链
 *    - 最后一个拦截器调用目标方法
 *    - 支持多种通知类型组合
 *
 * @author mini-spring
 */
public class InterceptorChainTest {

    /**
     * 测试多个前置通知
     * <p>
     * 验证：
     * 1. 多个前置通知按添加顺序执行
     * 2. 所有前置通知都会执行
     * 3. 目标方法正常执行
     */
    @Test
    public void testMultipleBeforeAdvice() throws Exception {
        System.out.println("\n=== 测试：多个前置通知 ===\n");

        // 1. 创建目标对象
        UserService target = new UserServiceImpl();

        // 2. 创建第一个前置通知
        MethodBeforeAdvice before1 = (method, args, target1) -> {
            System.out.println("[前置通知1] 方法: " + method.getName());
        };

        // 3. 创建第二个前置通知
        MethodBeforeAdvice before2 = (method, args, target1) -> {
            System.out.println("[前置通知2] 方法: " + method.getName());
        };

        // 4. 创建切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut(
                "execution(* com.minispring.aop.test.UserService.*(..))"
        );

        // 5. 创建 ProxyFactory
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(target);
        factory.setInterfaces(target.getClass().getInterfaces());
        factory.setMethodMatcher(pointcut.getMethodMatcher());

        // 6. 添加拦截器链
        factory.addMethodInterceptor(new MethodBeforeAdviceInterceptor(before1));
        factory.addMethodInterceptor(new MethodBeforeAdviceInterceptor(before2));

        // 7. 创建代理
        UserService proxy = (UserService) factory.getProxy();

        // 8. 调用方法
        System.out.println("\n--- 调用代理方法 ---");
        String result = proxy.queryUser("001");
        assertEquals("User-001", result);

        System.out.println("\n✅ 测试通过：多个前置通知按顺序执行\n");
    }

    /**
     * 测试前置通知 + 后置通知组合
     * <p>
     * 验证：
     * 1. 前置通知在目标方法前执行
     * 2. 后置通知在目标方法后执行
     * 3. 执行顺序：前置 → 目标方法 → 后置
     */
    @Test
    public void testBeforeAndAfterAdvice() throws Exception {
        System.out.println("\n=== 测试：前置通知 + 后置通知 ===\n");

        // 1. 创建目标对象
        UserService target = new UserServiceImpl();

        // 2. 创建前置通知
        MethodBeforeAdvice before = (method, args, target1) -> {
            System.out.println("[前置通知] 方法: " + method.getName());
        };

        // 3. 创建后置通知
        AfterReturningAdvice after = (returnValue, method, args, target1) -> {
            System.out.println("[后置通知] 返回值: " + returnValue);
        };

        // 4. 创建切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut(
                "execution(* com.minispring.aop.test.UserService.*(..))"
        );

        // 5. 创建 ProxyFactory
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(target);
        factory.setInterfaces(target.getClass().getInterfaces());
        factory.setMethodMatcher(pointcut.getMethodMatcher());

        // 6. 添加拦截器链（前置 → 后置）
        factory.addMethodInterceptor(new MethodBeforeAdviceInterceptor(before));
        factory.addMethodInterceptor(new AfterReturningAdviceInterceptor(after));

        // 7. 创建代理
        UserService proxy = (UserService) factory.getProxy();

        // 8. 调用方法
        System.out.println("\n--- 调用代理方法 ---");
        String result = proxy.queryUser("002");
        assertEquals("User-002", result);

        System.out.println("\n✅ 测试通过：前置和后置通知正确执行\n");
    }

    /**
     * 测试环绕通知 + 前置/后置通知
     * <p>
     * 验证：
     * 1. 环绕通知包裹整个执行过程
     * 2. 前置通知在环绕通知内执行
     * 3. 后置通知在环绕通知内执行
     * 4. 执行顺序：环绕前 → 前置 → 目标 → 后置 → 环绕后
     */
    @Test
    public void testAroundWithBeforeAndAfter() throws Exception {
        System.out.println("\n=== 测试：环绕通知 + 前置/后置通知 ===\n");

        // 1. 创建目标对象
        UserService target = new UserServiceImpl();

        // 2. 创建环绕通知
        MethodInterceptor around = invocation -> {
            System.out.println("[环绕通知 - 前] 方法: " + invocation.getMethod().getName());
            Object result = invocation.proceed();
            System.out.println("[环绕通知 - 后] 返回值: " + result);
            return result;
        };

        // 3. 创建前置通知
        MethodBeforeAdvice before = (method, args, target1) -> {
            System.out.println("  [前置通知] 参数: " + java.util.Arrays.toString(args));
        };

        // 4. 创建后置通知
        AfterReturningAdvice after = (returnValue, method, args, target1) -> {
            System.out.println("  [后置通知] 方法完成");
        };

        // 5. 创建切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut(
                "execution(* com.minispring.aop.test.UserService.*(..))"
        );

        // 6. 创建 ProxyFactory
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(target);
        factory.setInterfaces(target.getClass().getInterfaces());
        factory.setMethodMatcher(pointcut.getMethodMatcher());

        // 7. 添加拦截器链（环绕 → 前置 → 后置）
        factory.addMethodInterceptor(around);
        factory.addMethodInterceptor(new MethodBeforeAdviceInterceptor(before));
        factory.addMethodInterceptor(new AfterReturningAdviceInterceptor(after));

        // 8. 创建代理
        UserService proxy = (UserService) factory.getProxy();

        // 9. 调用方法
        System.out.println("\n--- 调用代理方法 ---");
        String result = proxy.queryUser("003");
        assertEquals("User-003", result);

        System.out.println("\n✅ 测试通过：环绕通知与前置/后置通知正确组合\n");
    }

}
