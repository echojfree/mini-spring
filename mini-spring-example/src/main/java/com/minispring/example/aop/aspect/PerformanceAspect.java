package com.minispring.example.aop.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 性能监控切面 - 记录方法执行时间
 * 演示 AOP 的环绕通知功能
 */
public class PerformanceAspect implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String methodName = invocation.getMethod().getName();
        long startTime = System.currentTimeMillis();

        System.out.println("[Performance] Method '" + methodName + "' started");

        Object result = invocation.proceed();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("[Performance] Method '" + methodName + "' finished in " + duration + "ms");

        return result;
    }
}
