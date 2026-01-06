package com.minispring.web.bind.annotation;

import java.lang.annotation.*;

/**
 * RestController - RESTful 控制器注解
 * <p>
 * 等同于 @Controller + @ResponseBody
 *
 * @author mini-spring
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@ResponseBody
public @interface RestController {
    String value() default "";
}
