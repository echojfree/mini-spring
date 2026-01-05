package com.minispring.aop.framework.adapter;

import com.minispring.aop.MethodBeforeAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * MethodBeforeAdviceInterceptor - 前置通知拦截器适配器
 * <p>
 * 将 MethodBeforeAdvice 适配为 MethodInterceptor
 * 使前置通知能够在拦截器链中执行
 * <p>
 * 设计模式：适配器模式
 * <p>
 * 面试要点：
 * 1. 适配器的作用
 *    - 将不同类型的 Advice 统一为 MethodInterceptor
 *    - 使所有通知能够在同一个拦截器链中执行
 *    - 简化代理逻辑，统一处理流程
 * <p>
 * 2. 前置通知的执行流程
 *    - 1. invoke() 被调用
 *    - 2. 调用 advice.before() 执行前置逻辑
 *    - 3. 调用 invocation.proceed() 执行目标方法
 *    - 4. 返回目标方法的返回值
 * <p>
 * 3. 异常处理
 *    - 如果前置通知抛出异常，目标方法不会执行
 *    - 异常会传播给调用者
 * <p>
 * 4. 其他适配器
 *    - AfterReturningAdviceInterceptor：后置通知适配器
 *    - ThrowsAdviceInterceptor：异常通知适配器
 *    - 这些适配器的实现方式类似
 *
 * @author mini-spring
 */
public class MethodBeforeAdviceInterceptor implements MethodInterceptor {

    /**
     * 前置通知
     */
    private final MethodBeforeAdvice advice;

    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 执行前置通知
        advice.before(invocation.getMethod(), invocation.getArguments(), invocation.getThis());

        // 执行目标方法
        return invocation.proceed();
    }

}
