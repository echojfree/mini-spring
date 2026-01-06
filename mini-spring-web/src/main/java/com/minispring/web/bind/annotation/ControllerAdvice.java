package com.minispring.web.bind.annotation;

import java.lang.annotation.*;

/**
 * ControllerAdvice - 控制器增强注解
 * <p>
 * 标记类为全局控制器增强器
 * <p>
 * 面试要点:
 * 1. 全局异常处理
 *    - @ExceptionHandler 方法全局生效
 *    - 统一异常处理
 *    - 错误响应格式化
 * <p>
 * 2. 应用场景
 *    - 全局异常捕获
 *    - 数据绑定初始化
 *    - 模型属性添加
 * <p>
 * 3. 执行顺序
 *    - ControllerAdvice 优先级低于 Controller
 *    - 先检查 Controller 内部的 @ExceptionHandler
 *    - 再检查 ControllerAdvice 的 @ExceptionHandler
 * <p>
 * 4. 使用示例
 *    - @ControllerAdvice
 *    - public class GlobalExceptionHandler {
 *    -     @ExceptionHandler(Exception.class)
 *    -     public ModelAndView handleException(Exception ex) { ... }
 *    - }
 *
 * @author mini-spring
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ControllerAdvice {
    String value() default "";
}
