package com.minispring.aop.test;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * PerformanceMonitorInterceptor - 性能监控拦截器
 * <p>
 * 环绕通知示例，用于监控方法执行时间
 *
 * @author mini-spring
 */
public class PerformanceMonitorInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long start = System.currentTimeMillis();

        System.out.println(">>> 开始执行方法: " + invocation.getMethod().getName());

        try {
            // 执行目标方法
            Object result = invocation.proceed();

            long time = System.currentTimeMillis() - start;
            System.out.println("<<< 方法执行完成, 耗时: " + time + "ms");

            return result;
        } catch (Throwable throwable) {
            long time = System.currentTimeMillis() - start;
            System.out.println("<<< 方法执行失败, 耗时: " + time + "ms, 异常: " + throwable.getMessage());
            throw throwable;
        }
    }

}
