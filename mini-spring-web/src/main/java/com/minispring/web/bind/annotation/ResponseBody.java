package com.minispring.web.bind.annotation;

import java.lang.annotation.*;

/**
 * ResponseBody - 响应体注解
 * <p>
 * 表示方法返回值直接作为响应体,通常返回 JSON
 *
 * @author mini-spring
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {
}
