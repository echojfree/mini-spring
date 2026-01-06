package com.minispring.aop;

import java.lang.reflect.Method;

/**
 * AfterReturningAdvice - 后置返回通知
 * <p>
 * 在目标方法正常返回后执行
 * <p>
 * 使用场景：
 * 1. 记录方法返回值
 * 2. 缓存方法结果
 * 3. 结果验证
 * <p>
 * 与 MethodBeforeAdvice 区别：
 * 1. MethodBeforeAdvice：方法执行前
 * 2. AfterReturningAdvice：方法正常返回后（不包括异常情况）
 * <p>
 * 面试要点：
 * 1. 后置通知只在方法正常返回时执行
 * 2. 如果方法抛出异常，后置通知不会执行
 * 3. 后置通知可以访问方法返回值，但不能修改
 *
 * @author mini-spring
 */
public interface AfterReturningAdvice extends Advice {

    /**
     * 后置返回通知方法
     *
     * @param returnValue 方法返回值
     * @param method      被调用的方法
     * @param args        方法参数
     * @param target      目标对象
     * @throws Throwable 异常
     */
    void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable;

}
