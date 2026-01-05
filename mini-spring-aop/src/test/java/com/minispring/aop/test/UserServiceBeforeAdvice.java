package com.minispring.aop.test;

import com.minispring.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * UserServiceBeforeAdvice - 用户服务前置通知
 * <p>
 * 在方法执行前记录日志
 *
 * @author mini-spring
 */
public class UserServiceBeforeAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("======= 前置通知 =======");
        System.out.println("方法名: " + method.getName());
        if (args != null && args.length > 0) {
            System.out.println("方法参数: " + java.util.Arrays.toString(args));
        }
        System.out.println("目标对象: " + target.getClass().getName());
        System.out.println("=======================");
    }

}
