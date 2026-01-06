package com.minispring.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HandlerAdapter - 处理器适配器接口
 * <p>
 * 适配不同类型的处理器,统一调用方式
 * <p>
 * 面试要点:
 * 1. 适配器模式应用
 *    - 统一不同处理器的调用方式
 *    - 解耦 DispatcherServlet 和具体处理器
 *    - 支持多种处理器类型
 * <p>
 * 2. 常见实现
 *    - RequestMappingHandlerAdapter: @RequestMapping 注解处理器
 *    - HttpRequestHandlerAdapter: HttpRequestHandler 接口
 *    - SimpleControllerHandlerAdapter: Controller 接口
 * <p>
 * 3. 核心方法
 *    - supports: 判断是否支持该处理器
 *    - handle: 执行处理器,返回 ModelAndView
 *
 * @author mini-spring
 */
public interface HandlerAdapter {

    /**
     * 判断是否支持该处理器
     *
     * @param handler 处理器对象
     * @return true 支持, false 不支持
     */
    boolean supports(Object handler);

    /**
     * 使用给定的处理器处理请求
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器对象
     * @return ModelAndView 对象,可能为 null
     * @throws Exception 处理异常
     */
    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

}
