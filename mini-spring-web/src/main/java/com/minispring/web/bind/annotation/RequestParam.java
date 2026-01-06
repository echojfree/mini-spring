package com.minispring.web.bind.annotation;

import java.lang.annotation.*;

/**
 * RequestParam - 请求参数注解
 * <p>
 * 绑定请求参数到方法参数
 *
 * @author mini-spring
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    /**
     * 参数名称
     */
    String value() default "";

    /**
     * 是否必需
     */
    boolean required() default true;

    /**
     * 默认值
     */
    String defaultValue() default "";
}
