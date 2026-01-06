package com.minispring.web.bind.annotation;

import java.lang.annotation.*;

/**
 * Controller - 控制器注解
 * <p>
 * 标记一个类为 MVC 控制器
 *
 * @author mini-spring
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
