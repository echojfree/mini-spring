package com.minispring.web.servlet.mvc.method.annotation;

import com.minispring.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * HandlerMethod - 处理器方法封装
 * <p>
 * 封装 Controller Bean 和处理器方法的信息
 * <p>
 * 面试要点:
 * 1. HandlerMethod 的作用
 *    - 封装 Controller 实例和 Method
 *    - 提供方法参数信息
 *    - 提供返回值信息
 * <p>
 * 2. 与 Handler 的关系
 *    - Handler: 处理器的统称
 *    - HandlerMethod: 基于注解的处理器
 *    - 其他 Handler: Controller 接口, HttpRequestHandler 等
 * <p>
 * 3. 参数解析
 *    - 参数类型,参数注解
 *    - 参数名称(需要编译参数 -parameters)
 *    - 默认值等元信息
 *
 * @author mini-spring
 */
public class HandlerMethod {

    /**
     * Bean 名称
     */
    private final String beanName;

    /**
     * 处理器方法
     */
    private final Method method;

    /**
     * ApplicationContext
     */
    private ApplicationContext applicationContext;

    /**
     * 路径变量
     */
    private Map<String, String> pathVariables = new HashMap<>();

    public HandlerMethod(String beanName, Method method) {
        this.beanName = beanName;
        this.method = method;
    }

    /**
     * 获取 Bean 实例
     */
    public Object getBean() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext not set");
        }
        return applicationContext.getBean(beanName);
    }

    public String getBeanName() {
        return beanName;
    }

    public Method getMethod() {
        return method;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(Map<String, String> pathVariables) {
        this.pathVariables = pathVariables;
    }

    @Override
    public String toString() {
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }

}
