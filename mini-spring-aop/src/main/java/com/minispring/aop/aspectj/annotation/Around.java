package com.minispring.aop.aspectj.annotation;

import java.lang.annotation.*;

/**
 * Around - 环绕通知注解
 * <p>
 * 标记一个方法为环绕通知,包裹目标方法执行
 * <p>
 * 面试要点:
 * 1. 环绕通知是最强大的通知类型
 * 2. 可以控制目标方法是否执行
 * 3. 可以修改参数和返回值
 * 4. 必须调用 ProceedingJoinPoint.proceed() 来执行目标方法
 *
 * @author mini-spring
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Around {
    /**
     * 切点表达式
     */
    String value();
}
