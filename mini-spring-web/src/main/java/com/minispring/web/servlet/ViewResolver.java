package com.minispring.web.servlet;

/**
 * ViewResolver - 视图解析器接口
 * <p>
 * 根据视图名称解析出 View 对象
 * <p>
 * 面试要点:
 * 1. ViewResolver 的作用
 *    - 视图名称到视图对象的映射
 *    - 支持多种视图技术
 *    - 视图解析策略
 * <p>
 * 2. 常见实现
 *    - InternalResourceViewResolver: JSP 视图解析
 *    - FreeMarkerViewResolver: FreeMarker 模板
 *    - ThymeleafViewResolver: Thymeleaf 模板
 *    - ContentNegotiatingViewResolver: 内容协商
 * <p>
 * 3. 配置属性
 *    - prefix: 视图路径前缀
 *    - suffix: 视图路径后缀
 *    - order: 解析器优先级
 *
 * @author mini-spring
 */
public interface ViewResolver {

    /**
     * 根据视图名称解析视图
     *
     * @param viewName 视图名称
     * @return View 对象,如果无法解析则返回 null
     * @throws Exception 解析异常
     */
    View resolveViewName(String viewName) throws Exception;

}
