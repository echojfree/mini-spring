package com.minispring.web.bind.annotation;

import java.lang.annotation.*;

/**
 * RequestBody - 请求体注解
 * <p>
 * 将 HTTP 请求体绑定到方法参数
 * <p>
 * 面试要点:
 * 1. 请求体处理
 *    - JSON → Java 对象
 *    - XML → Java 对象
 *    - 表单数据
 * <p>
 * 2. HttpMessageConverter
 *    - 消息转换器
 *    - MappingJackson2HttpMessageConverter
 *    - 内容协商
 * <p>
 * 3. 使用示例
 *    - @PostMapping("/user")
 *    - public User createUser(@RequestBody User user)
 * <p>
 * 4. 与 @RequestParam 区别
 *    - @RequestParam: URL 参数
 *    - @RequestBody: 请求体
 *    - POST/PUT 常用 @RequestBody
 *
 * @author mini-spring
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {
    /**
     * 是否必需
     */
    boolean required() default true;
}
