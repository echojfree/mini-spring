package com.minispring.aop.aspectj.annotation;

import java.lang.annotation.*;

/**
 * AfterThrowing - 异常通知注解
 * <p>
 * 标记一个方法为异常通知,在目标方法抛出异常后执行
 * <p>
 * 面试要点:
 * 1. 只在目标方法抛出异常时执行
 * 2. 可以访问异常对象
 * 3. 可以捕获特定类型的异常
 *
 * @author mini-spring
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AfterThrowing {
    /**
     * 切点表达式
     */
    String value();

    /**
     * 异常参数名
     * 通知方法中通过此名称访问异常对象
     */
    String throwing() default "";
}
