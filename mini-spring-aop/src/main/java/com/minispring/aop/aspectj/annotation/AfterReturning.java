package com.minispring.aop.aspectj.annotation;

import java.lang.annotation.*;

/**
 * AfterReturning - 后置返回通知注解
 * <p>
 * 标记一个方法为后置返回通知,在目标方法正常返回后执行
 * <p>
 * 面试要点:
 * 1. 只在目标方法正常返回时执行
 * 2. 如果目标方法抛出异常,该通知不会执行
 * 3. 可以访问返回值,但无法修改
 *
 * @author mini-spring
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AfterReturning {
    /**
     * 切点表达式
     */
    String value();

    /**
     * 返回值参数名
     * 通知方法中通过此名称访问返回值
     */
    String returning() default "";
}
