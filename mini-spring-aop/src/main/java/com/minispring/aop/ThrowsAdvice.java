package com.minispring.aop;

/**
 * ThrowsAdvice - 异常通知
 * <p>
 * 在目标方法抛出异常后执行
 * <p>
 * 使用场景：
 * 1. 异常日志记录
 * 2. 异常统一处理
 * 3. 异常转换
 * 4. 事务回滚
 * <p>
 * 实现方式：
 * 标记接口，具体异常处理方法由实现类定义
 * 方法签名：void afterThrowing([Method, args, target], Throwable)
 * <p>
 * 面试要点：
 * 1. 异常通知只在方法抛出异常时执行
 * 2. 可以捕获特定类型的异常
 * 3. 不会阻止异常向上传播（除非在通知中处理）
 * 4. Spring 事务的回滚就是基于异常通知实现的
 *
 * @author mini-spring
 */
public interface ThrowsAdvice extends Advice {

    // 标记接口，具体方法由实现类定义
    // 示例方法签名：
    // void afterThrowing(Method method, Object[] args, Object target, Exception ex)
    // void afterThrowing(Exception ex)

}
