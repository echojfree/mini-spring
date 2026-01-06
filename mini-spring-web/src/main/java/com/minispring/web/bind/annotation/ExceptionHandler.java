package com.minispring.web.bind.annotation;

import java.lang.annotation.*;

/**
 * ExceptionHandler - 异常处理器注解
 * <p>
 * 标记方法为异常处理方法
 * <p>
 * 面试要点:
 * 1. 异常处理机制
 *    - 方法级异常处理
 *    - 类级异常处理
 *    - 全局异常处理
 * <p>
 * 2. 异常匹配
 *    - 按异常类型匹配
 *    - 支持异常继承
 *    - 优先匹配最具体的异常
 * <p>
 * 3. 返回值类型
 *    - ModelAndView: 错误页面
 *    - @ResponseBody: JSON 错误响应
 *    - String: 错误视图名
 * <p>
 * 4. 使用示例
 *    - @ExceptionHandler(Exception.class)
 *    - public ModelAndView handleException(Exception ex)
 *
 * @author mini-spring
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionHandler {
    /**
     * 要处理的异常类型
     */
    Class<? extends Throwable>[] value() default {};
}
