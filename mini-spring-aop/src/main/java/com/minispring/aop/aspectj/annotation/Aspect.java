package com.minispring.aop.aspectj.annotation;

import java.lang.annotation.*;

/**
 * Aspect - 切面注解
 * <p>
 * 标记一个类为 AOP 切面
 * <p>
 * 面试要点:
 * 1. @Aspect 标记的类会被 AspectJAnnotationParser 解析
 * 2. 解析后会将切面中的通知方法转换为 Advisor
 * 3. 每个通知方法对应一个 Advisor
 *
 * @author mini-spring
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aspect {
    /**
     * 切点表达式(可选)
     * 如果指定,将作为该切面中所有通知的默认切点
     */
    String value() default "";
}
