package com.minispring.aop.framework.adapter;

import com.minispring.aop.AfterReturningAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * AfterReturningAdviceInterceptor - 后置返回通知拦截器
 * <p>
 * 将 AfterReturningAdvice 适配为 MethodInterceptor
 * <p>
 * 设计模式：
 * 1. 适配器模式：将 AfterReturningAdvice 适配为 MethodInterceptor
 * 2. 责任链模式：作为拦截器链中的一环
 * <p>
 * 执行流程：
 * 1. 先调用 proceed() 执行目标方法
 * 2. 目标方法正常返回后，调用 afterReturning()
 * 3. 如果目标方法抛出异常，不执行 afterReturning()
 * <p>
 * 面试要点：
 * 1. 后置通知在目标方法执行后执行
 * 2. 只有方法正常返回才执行，异常情况不执行
 * 3. 可以访问返回值，但不能修改
 *
 * @author mini-spring
 */
public class AfterReturningAdviceInterceptor implements MethodInterceptor {

    private final AfterReturningAdvice advice;

    public AfterReturningAdviceInterceptor(AfterReturningAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 先执行目标方法
        Object returnValue = invocation.proceed();

        // 目标方法正常返回后，执行后置通知
        this.advice.afterReturning(returnValue, invocation.getMethod(),
                invocation.getArguments(), invocation.getThis());

        // 返回方法返回值
        return returnValue;
    }

}
