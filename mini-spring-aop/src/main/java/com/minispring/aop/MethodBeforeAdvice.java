package com.minispring.aop;

import java.lang.reflect.Method;

/**
 * MethodBeforeAdvice - 前置通知接口
 * <p>
 * 在目标方法执行之前执行的通知
 * <p>
 * 设计模式：策略模式、模板方法模式
 * <p>
 * 面试要点：
 * 1. 前置通知的特点
 *    - 在目标方法执行之前执行
 *    - 无法阻止目标方法的执行（除非抛出异常）
 *    - 无法修改目标方法的参数
 *    - 可以访问目标方法的参数
 * <p>
 * 2. 使用场景
 *    - 权限验证：在方法执行前检查权限
 *    - 参数校验：验证方法参数的合法性
 *    - 日志记录：记录方法调用的参数
 *    - 性能监控：记录方法开始执行的时间
 * <p>
 * 3. 异常处理
 *    - 如果前置通知抛出异常，目标方法不会执行
 *    - 异常会传播给调用者
 * <p>
 * 4. 与环绕通知的区别
 *    - 前置通知：只能在方法执行前执行，不能控制方法是否执行
 *    - 环绕通知：可以控制方法执行前后的逻辑，可以决定是否执行目标方法
 * <p>
 * 示例：
 * <pre>
 * {@code
 * public class LoggingBeforeAdvice implements MethodBeforeAdvice {
 *     @Override
 *     public void before(Method method, Object[] args, Object target) throws Throwable {
 *         System.out.println("方法执行前: " + method.getName());
 *     }
 * }
 * }
 * </pre>
 *
 * @author mini-spring
 */
@FunctionalInterface
public interface MethodBeforeAdvice extends Advice {

    /**
     * 在目标方法执行之前调用
     * <p>
     * 如果此方法抛出异常，则目标方法不会执行
     *
     * @param method 将要执行的目标方法
     * @param args   目标方法的参数数组
     * @param target 目标对象（被代理对象）
     * @throws Throwable 如果通知执行失败
     */
    void before(Method method, Object[] args, Object target) throws Throwable;

}
