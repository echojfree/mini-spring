package com.minispring.web.bind.annotation;

import java.lang.annotation.*;

/**
 * RequestMapping - 请求映射注解
 * <p>
 * 映射 HTTP 请求到处理方法
 *
 * @author mini-spring
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    /**
     * URL 路径
     */
    String value() default "";

    /**
     * HTTP 方法
     */
    RequestMethod[] method() default {};
}
