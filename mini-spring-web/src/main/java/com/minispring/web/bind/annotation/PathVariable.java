package com.minispring.web.bind.annotation;

import java.lang.annotation.*;

/**
 * PathVariable - 路径变量注解
 * <p>
 * 绑定 URL 路径中的变量到方法参数
 * <p>
 * 面试要点:
 * 1. RESTful 风格 URL
 *    - /user/{id} → 获取用户
 *    - /order/{orderId}/item/{itemId}
 * <p>
 * 2. 路径变量解析
 *    - URL 模板匹配
 *    - 正则表达式提取
 *    - 类型转换
 * <p>
 * 3. 使用示例
 *    - @GetMapping("/user/{id}")
 *    - public User getUser(@PathVariable Long id)
 *
 * @author mini-spring
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {
    /**
     * 路径变量名称
     */
    String value() default "";

    /**
     * 是否必需
     */
    boolean required() default true;
}
