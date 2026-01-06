package com.minispring.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * HandlerExecutionChain - 处理器执行链
 * <p>
 * 包含处理器和拦截器链
 * <p>
 * 面试要点:
 * 1. 执行链模式
 *    - 处理器 + 拦截器链
 *    - 责任链模式应用
 *    - 前置/后置处理
 * <p>
 * 2. 拦截器执行顺序
 *    - preHandle: 按注册顺序执行
 *    - postHandle: 按注册逆序执行
 *    - afterCompletion: 按注册逆序执行
 * <p>
 * 3. 拦截器短路机制
 *    - preHandle 返回 false 时停止执行
 *    - 只执行已成功执行的拦截器的 afterCompletion
 *
 * @author mini-spring
 */
public class HandlerExecutionChain {

    /**
     * 处理器对象
     */
    private final Object handler;

    /**
     * 拦截器列表
     */
    private final List<HandlerInterceptor> interceptors = new ArrayList<>();

    /**
     * 已执行拦截器的索引
     * 用于异常时正确执行 afterCompletion
     */
    private int interceptorIndex = -1;

    public HandlerExecutionChain(Object handler) {
        this.handler = handler;
    }

    /**
     * 添加拦截器
     */
    public void addInterceptor(HandlerInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    /**
     * 添加拦截器列表
     */
    public void addInterceptors(List<HandlerInterceptor> interceptors) {
        if (interceptors != null) {
            this.interceptors.addAll(interceptors);
        }
    }

    /**
     * 执行拦截器的 preHandle 方法
     *
     * @return true 继续执行, false 停止执行
     */
    public boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (int i = 0; i < interceptors.size(); i++) {
            HandlerInterceptor interceptor = interceptors.get(i);
            if (!interceptor.preHandle(request, response, handler)) {
                // preHandle 返回 false, 触发已执行拦截器的 afterCompletion
                triggerAfterCompletion(request, response, null);
                return false;
            }
            // 记录已执行的拦截器索引
            this.interceptorIndex = i;
        }
        return true;
    }

    /**
     * 执行拦截器的 postHandle 方法
     * 按注册逆序执行
     */
    public void applyPostHandle(HttpServletRequest request, HttpServletResponse response, ModelAndView mv)
            throws Exception {
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            HandlerInterceptor interceptor = interceptors.get(i);
            interceptor.postHandle(request, response, handler, mv);
        }
    }

    /**
     * 执行拦截器的 afterCompletion 方法
     * 只执行已成功执行 preHandle 的拦截器
     * 按注册逆序执行
     */
    public void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        for (int i = this.interceptorIndex; i >= 0; i--) {
            HandlerInterceptor interceptor = interceptors.get(i);
            try {
                interceptor.afterCompletion(request, response, handler, ex);
            } catch (Exception e) {
                // 记录日志,不抛出异常
                System.err.println("HandlerInterceptor.afterCompletion threw exception: " + e.getMessage());
            }
        }
    }

    public Object getHandler() {
        return handler;
    }

    public List<HandlerInterceptor> getInterceptors() {
        return interceptors;
    }
}
