package com.minispring.aop.aspectj.annotation;

import java.lang.annotation.*;

/**
 * After - 后置通知注解
 * <p>
 * 标记一个方法为后置通知,在目标方法执行后执行(无论正常返回还是抛出异常)
 * <p>
 * 面试要点:
 * 1. 后置通知在目标方法执行后总是执行
 * 2. 无论目标方法正常返回还是抛出异常都会执行
 * 3. 类似于 finally 块,通常用于资源清理
 *
 * @author mini-spring
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface After {
    /**
     * 切点表达式
     */
    String value();
}
