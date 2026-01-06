package com.minispring.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HandlerExceptionResolver - 处理器异常解析器接口
 * <p>
 * 统一处理处理器执行过程中抛出的异常
 * <p>
 * 面试要点:
 * 1. 异常处理机制
 *    - 统一异常处理
 *    - 异常到视图的映射
 *    - 支持多种异常处理策略
 * <p>
 * 2. 常见实现
 *    - ExceptionHandlerExceptionResolver: @ExceptionHandler 注解
 *    - ResponseStatusExceptionResolver: @ResponseStatus 注解
 *    - SimpleMappingExceptionResolver: 异常类到视图的映射
 *    - DefaultHandlerExceptionResolver: Spring MVC 默认异常处理
 * <p>
 * 3. 异常处理流程
 *    - 捕获异常
 *    - 解析异常类型
 *    - 返回错误视图或错误响应
 *
 * @author mini-spring
 */
public interface HandlerExceptionResolver {

    /**
     * 解析异常
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器对象
     * @param ex       异常对象
     * @return ModelAndView 对象,如果无法处理则返回 null
     */
    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                   Object handler, Exception ex);

}
