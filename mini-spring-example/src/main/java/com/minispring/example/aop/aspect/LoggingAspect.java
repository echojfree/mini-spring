package com.minispring.example.aop.aspect;

import com.minispring.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * 日志切面 - 方法执行前记录日志
 * 演示 AOP 的前置通知功能
 */
public class LoggingAspect implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("=== [AOP Logging] ===");
        System.out.println("Method: " + method.getName());
        System.out.println("Target: " + target.getClass().getSimpleName());
        if (args != null && args.length > 0) {
            System.out.print("Arguments: ");
            for (int i = 0; i < args.length; i++) {
                System.out.print(args[i]);
                if (i < args.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
        System.out.println("=====================");
    }
}
