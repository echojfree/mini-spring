package com.minispring.web.servlet.mvc.method.annotation;

import com.minispring.context.ApplicationContext;
import com.minispring.context.ApplicationContextAware;
import com.minispring.web.bind.annotation.RequestMapping;
import com.minispring.web.bind.annotation.RequestMethod;
import com.minispring.web.servlet.HandlerExecutionChain;
import com.minispring.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * RequestMappingHandlerMapping - @RequestMapping 注解处理器映射
 * <p>
 * 扫描所有 @RequestMapping 注解,建立 URL 到 Handler Method 的映射
 * <p>
 * 面试要点:
 * 1. 映射注册流程
 *    - 容器启动时扫描所有 Controller
 *    - 解析 @RequestMapping 注解
 *    - 建立 URL 到 Handler Method 的映射表
 * <p>
 * 2. URL 匹配规则
 *    - 精确匹配优先
 *    - 支持路径变量 {id}
 *    - 支持通配符 *
 * <p>
 * 3. Handler Method 封装
 *    - HandlerMethod: 封装 Controller 和 Method
 *    - 包含参数信息,返回值信息
 *
 * @author mini-spring
 */
public class RequestMappingHandlerMapping implements HandlerMapping, ApplicationContextAware {

    /**
     * ApplicationContext
     */
    private ApplicationContext applicationContext;

    /**
     * URL 到 HandlerMethod 的映射
     * Key: URL + HTTP Method (例如: "/user/list#GET")
     * Value: HandlerMethod
     */
    private final Map<String, HandlerMethod> handlerMethods = new HashMap<>();

    /**
     * 路径模式到 HandlerMethod 的映射 (用于路径变量)
     * Key: URL Pattern + HTTP Method (例如: "/user/{id}#GET")
     * Value: HandlerMethod
     */
    private final Map<String, HandlerMethod> patternHandlerMethods = new HashMap<>();

    /**
     * 路径匹配器缓存
     */
    private final Map<String, PathMatcher> pathMatchers = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        // 初始化时扫描所有 Handler Method
        initHandlerMethods();
    }

    /**
     * 扫描并注册所有 Handler Method
     */
    protected void initHandlerMethods() {
        // 获取容器中所有 Bean 的名称
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Class<?> beanType = applicationContext.getType(beanName);
            if (beanType == null) {
                continue;
            }

            // 检查类上是否有 @RequestMapping
            RequestMapping typeMapping = beanType.getAnnotation(RequestMapping.class);
            if (typeMapping == null) {
                continue;
            }

            // 扫描类中的所有方法
            Method[] methods = beanType.getDeclaredMethods();
            for (Method method : methods) {
                RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                if (methodMapping == null) {
                    continue;
                }

                // 注册 Handler Method
                registerHandlerMethod(beanName, method, typeMapping, methodMapping);
            }
        }
    }

    /**
     * 注册 Handler Method
     */
    protected void registerHandlerMethod(String beanName, Method method,
                                          RequestMapping typeMapping, RequestMapping methodMapping) {
        // 拼接完整的 URL 路径
        String typePath = typeMapping.value().isEmpty() ? "" : typeMapping.value();
        String methodPath = methodMapping.value().isEmpty() ? "" : methodMapping.value();
        String fullPath = combinePaths(typePath, methodPath);

        // 获取 HTTP 方法
        RequestMethod[] requestMethods = methodMapping.method();
        if (requestMethods.length == 0) {
            // 默认支持所有 HTTP 方法
            requestMethods = RequestMethod.values();
        }

        // 为每个 HTTP 方法注册映射
        for (RequestMethod requestMethod : requestMethods) {
            String mappingKey = fullPath + "#" + requestMethod.name();
            HandlerMethod handlerMethod = new HandlerMethod(beanName, method);

            // 检查是否包含路径变量
            if (fullPath.contains("{")) {
                // 包含路径变量，使用模式匹配
                patternHandlerMethods.put(mappingKey, handlerMethod);
                PathMatcher pathMatcher = new PathMatcher(fullPath);
                pathMatchers.put(mappingKey, pathMatcher);
                System.out.println("Mapped pattern \"" + fullPath + "\" onto " + method);
            } else {
                // 精确匹配
                handlerMethods.put(mappingKey, handlerMethod);
                System.out.println("Mapped \"" + fullPath + "\" onto " + method);
            }
        }
    }

    /**
     * 合并路径
     */
    private String combinePaths(String path1, String path2) {
        if (!path1.startsWith("/")) {
            path1 = "/" + path1;
        }
        if (!path2.startsWith("/")) {
            path2 = "/" + path2;
        }
        if (path1.endsWith("/")) {
            path1 = path1.substring(0, path1.length() - 1);
        }
        return path1 + path2;
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        String requestPath = request.getRequestURI();
        String contextPath = request.getContextPath();

        // 去除 context path
        if (contextPath.length() > 0) {
            requestPath = requestPath.substring(contextPath.length());
        }

        String requestMethod = request.getMethod();
        String mappingKey = requestPath + "#" + requestMethod;

        // 1. 先尝试精确匹配
        HandlerMethod handlerMethod = handlerMethods.get(mappingKey);

        if (handlerMethod != null) {
            handlerMethod.setApplicationContext(applicationContext);
            return new HandlerExecutionChain(handlerMethod);
        }

        // 2. 尝试模式匹配（路径变量）
        for (Map.Entry<String, HandlerMethod> entry : patternHandlerMethods.entrySet()) {
            String patternKey = entry.getKey();

            // 检查 HTTP 方法是否匹配
            if (!patternKey.endsWith("#" + requestMethod)) {
                continue;
            }

            PathMatcher pathMatcher = pathMatchers.get(patternKey);
            if (pathMatcher != null && pathMatcher.matches(requestPath)) {
                // 匹配成功，提取路径变量
                Map<String, String> pathVariables = pathMatcher.extractVariables(requestPath);

                HandlerMethod matchedHandler = entry.getValue();
                matchedHandler.setApplicationContext(applicationContext);
                matchedHandler.setPathVariables(pathVariables);

                return new HandlerExecutionChain(matchedHandler);
            }
        }

        return null;
    }

}
