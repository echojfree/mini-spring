package com.minispring.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HandlerInterceptor - 处理器拦截器接口
 * <p>
 * Spring MVC 拦截器,用于请求的前置/后置处理
 * <p>
 * 面试要点:
 * 1. 拦截器 vs 过滤器
 *    - 拦截器: Spring MVC 层面,可访问 Handler
 *    - 过滤器: Servlet 层面,更底层
 *    - 拦截器可以访问 Spring 容器
 * <p>
 * 2. 三个核心方法
 *    - preHandle: 前置处理,可以终止请求
 *    - postHandle: 后置处理,在视图渲染前执行
 *    - afterCompletion: 最终处理,在视图渲染后执行
 * <p>
 * 3. 应用场景
 *    - 权限检查,登录验证
 *    - 日志记录,性能监控
 *    - 通用行为处理
 *
 * @author mini-spring
 */
public interface HandlerInterceptor {

    /**
     * 前置处理
     * <p>
     * 在 Handler 执行之前调用
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器对象
     * @return true 继续执行, false 停止执行
     * @throws Exception 处理异常
     */
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        return true;
    }

    /**
     * 后置处理
     * <p>
     * 在 Handler 执行之后,视图渲染之前调用
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器对象
     * @param mv       ModelAndView 对象
     * @throws Exception 处理异常
     */
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                            ModelAndView mv) throws Exception {
    }

    /**
     * 完成处理
     * <p>
     * 在整个请求完成之后调用,即视图渲染之后
     * <p>
     * 无论是否发生异常都会调用,用于资源清理
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器对象
     * @param ex       异常对象(如果有)
     */
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                  Exception ex) {
    }

}
