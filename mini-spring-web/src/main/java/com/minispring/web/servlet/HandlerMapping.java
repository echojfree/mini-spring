package com.minispring.web.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * HandlerMapping - 处理器映射接口
 * <p>
 * 根据请求查找对应的处理器
 * <p>
 * 面试要点:
 * 1. HandlerMapping 的作用
 *    - URL 到 Handler 的映射
 *    - 请求分发的核心组件
 *    - 支持多种映射策略
 * <p>
 * 2. 常见实现
 *    - RequestMappingHandlerMapping: @RequestMapping 注解映射
 *    - BeanNameUrlHandlerMapping: Bean 名称作为 URL
 *    - SimpleUrlHandlerMapping: 配置文件映射
 * <p>
 * 3. HandlerExecutionChain
 *    - 包含 Handler 和拦截器链
 *    - 支持前置/后置处理
 *
 * @author mini-spring
 */
public interface HandlerMapping {

    /**
     * 获取请求的处理器
     *
     * @param request HTTP 请求
     * @return 处理器执行链
     * @throws Exception 查找异常
     */
    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;

}
