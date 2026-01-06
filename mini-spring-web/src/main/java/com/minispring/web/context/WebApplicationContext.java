package com.minispring.web.context;

import com.minispring.context.ApplicationContext;

import javax.servlet.ServletContext;

/**
 * WebApplicationContext - Web 应用上下文接口
 * <p>
 * Spring Web 应用的根上下文
 * <p>
 * 面试要点:
 * 1. WebApplicationContext 特性
 *    - 继承 ApplicationContext
 *    - 提供 ServletContext 访问
 *    - 支持 Web 环境的 Bean
 * <p>
 * 2. 作用域扩展
 *    - request 作用域
 *    - session 作用域
 *    - application 作用域
 * <p>
 * 3. 与 ApplicationContext 的关系
 *    - ApplicationContext: 通用容器
 *    - WebApplicationContext: Web 专用容器
 *    - 层次性结构: Root Context + Servlet Context
 *
 * @author mini-spring
 */
public interface WebApplicationContext extends ApplicationContext {

    /**
     * ServletContext 属性名
     */
    String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE =
            WebApplicationContext.class.getName() + ".ROOT";

    /**
     * 获取 ServletContext
     */
    ServletContext getServletContext();

}
