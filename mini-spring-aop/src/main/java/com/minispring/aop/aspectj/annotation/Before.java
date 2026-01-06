package com.minispring.aop.aspectj.annotation;

import java.lang.annotation.*;

/**
 * Before - 前置通知注解
 * <p>
 * 标记一个方法为前置通知,在目标方法执行前执行
 * <p>
 * 面试要点:
 * 1. 前置通知在目标方法执行前执行
 * 2. 可以访问方法参数,但无法修改返回值
 * 3. 如果前置通知抛出异常,目标方法不会执行
 *
 * @author mini-spring
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Before {
    /**
     * 切点表达式
     * 例如: execution(* com.minispring.service.*.*(..))
     */
    String value();
}
