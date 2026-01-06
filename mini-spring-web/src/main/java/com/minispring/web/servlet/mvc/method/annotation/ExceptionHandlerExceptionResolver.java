package com.minispring.web.servlet.mvc.method.annotation;

import com.minispring.context.ApplicationContext;
import com.minispring.context.ApplicationContextAware;
import com.minispring.web.bind.annotation.ControllerAdvice;
import com.minispring.web.bind.annotation.ExceptionHandler;
import com.minispring.web.servlet.HandlerExceptionResolver;
import com.minispring.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * ExceptionHandlerExceptionResolver - @ExceptionHandler 异常解析器
 * <p>
 * 处理 @ExceptionHandler 注解的异常处理方法
 * <p>
 * 面试要点:
 * 1. 异常处理流程
 *    - 查找异常处理方法
 *    - 按异常类型匹配
 *    - 支持异常继承匹配
 * <p>
 * 2. 查找顺序
 *    - Controller 内部的 @ExceptionHandler
 *    - @ControllerAdvice 的 @ExceptionHandler
 *    - 优先匹配最具体的异常类型
 * <p>
 * 3. 方法调用
 *    - 反射调用异常处理方法
 *    - 传入异常对象
 *    - 返回 ModelAndView
 *
 * @author mini-spring
 */
public class ExceptionHandlerExceptionResolver implements HandlerExceptionResolver, ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * Controller 内部的异常处理方法
     * Key: Controller Bean Name
     * Value: Exception Type -> Method
     */
    private final Map<String, Map<Class<? extends Throwable>, Method>> controllerExceptionHandlers = new HashMap<>();

    /**
     * 全局异常处理方法
     * Key: Exception Type
     * Value: ExceptionHandlerMethod (Bean Name + Method)
     */
    private final Map<Class<? extends Throwable>, ExceptionHandlerMethod> globalExceptionHandlers = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        // 初始化时扫描所有异常处理方法
        initExceptionHandlers();
    }

    /**
     * 扫描并注册所有异常处理方法
     */
    private void initExceptionHandlers() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Class<?> beanType = applicationContext.getType(beanName);
            if (beanType == null) {
                continue;
            }

            // 检查是否是 ControllerAdvice
            if (beanType.isAnnotationPresent(ControllerAdvice.class)) {
                // 全局异常处理器
                scanExceptionHandlerMethods(beanName, beanType, true);
            } else {
                // Controller 内部的异常处理器
                scanExceptionHandlerMethods(beanName, beanType, false);
            }
        }
    }

    /**
     * 扫描类中的 @ExceptionHandler 方法
     */
    private void scanExceptionHandlerMethods(String beanName, Class<?> beanType, boolean isGlobal) {
        Method[] methods = beanType.getDeclaredMethods();

        for (Method method : methods) {
            ExceptionHandler exceptionHandler = method.getAnnotation(ExceptionHandler.class);
            if (exceptionHandler == null) {
                continue;
            }

            Class<? extends Throwable>[] exceptionTypes = exceptionHandler.value();
            if (exceptionTypes.length == 0) {
                // 如果没有指定异常类型,从方法参数推断
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length > 0 && Throwable.class.isAssignableFrom(paramTypes[0])) {
                    exceptionTypes = new Class[]{(Class<? extends Throwable>) paramTypes[0]};
                }
            }

            for (Class<? extends Throwable> exceptionType : exceptionTypes) {
                if (isGlobal) {
                    // 全局异常处理器
                    globalExceptionHandlers.put(exceptionType, new ExceptionHandlerMethod(beanName, method));
                } else {
                    // Controller 内部异常处理器
                    controllerExceptionHandlers
                            .computeIfAbsent(beanName, k -> new HashMap<>())
                            .put(exceptionType, method);
                }

                System.out.println("Mapped exception handler for " + exceptionType.getSimpleName() +
                        " to " + method + (isGlobal ? " (global)" : ""));
            }
        }
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                          Object handler, Exception ex) {
        if (handler == null) {
            return null;
        }

        // 1. 先查找 Controller 内部的异常处理方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String beanName = handlerMethod.getBeanName();
            Map<Class<? extends Throwable>, Method> handlers = controllerExceptionHandlers.get(beanName);

            if (handlers != null) {
                Method exceptionHandlerMethod = findBestMatchingMethod(handlers, ex.getClass());
                if (exceptionHandlerMethod != null) {
                    return invokeExceptionHandler(beanName, exceptionHandlerMethod, ex);
                }
            }
        }

        // 2. 查找全局异常处理方法
        ExceptionHandlerMethod globalHandler = findBestMatchingGlobalHandler(ex.getClass());
        if (globalHandler != null) {
            return invokeExceptionHandler(globalHandler.beanName, globalHandler.method, ex);
        }

        return null;
    }

    /**
     * 查找最匹配的异常处理方法
     */
    private Method findBestMatchingMethod(Map<Class<? extends Throwable>, Method> handlers,
                                           Class<? extends Throwable> exceptionType) {
        // 1. 精确匹配
        Method method = handlers.get(exceptionType);
        if (method != null) {
            return method;
        }

        // 2. 查找父类异常
        Class<?> currentType = exceptionType.getSuperclass();
        while (currentType != null && Throwable.class.isAssignableFrom(currentType)) {
            method = handlers.get(currentType);
            if (method != null) {
                return method;
            }
            currentType = currentType.getSuperclass();
        }

        return null;
    }

    /**
     * 查找最匹配的全局异常处理方法
     */
    private ExceptionHandlerMethod findBestMatchingGlobalHandler(Class<? extends Throwable> exceptionType) {
        // 1. 精确匹配
        ExceptionHandlerMethod handler = globalExceptionHandlers.get(exceptionType);
        if (handler != null) {
            return handler;
        }

        // 2. 查找父类异常
        Class<?> currentType = exceptionType.getSuperclass();
        while (currentType != null && Throwable.class.isAssignableFrom(currentType)) {
            handler = globalExceptionHandlers.get(currentType);
            if (handler != null) {
                return handler;
            }
            currentType = currentType.getSuperclass();
        }

        return null;
    }

    /**
     * 调用异常处理方法
     */
    private ModelAndView invokeExceptionHandler(String beanName, Method method, Exception ex) {
        try {
            Object bean = applicationContext.getBean(beanName);
            Object result = method.invoke(bean, ex);

            if (result instanceof ModelAndView) {
                return (ModelAndView) result;
            } else if (result instanceof String) {
                return new ModelAndView((String) result);
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error invoking exception handler: " + e.getMessage());
            return null;
        }
    }

    /**
     * 异常处理方法包装类
     */
    private static class ExceptionHandlerMethod {
        private final String beanName;
        private final Method method;

        public ExceptionHandlerMethod(String beanName, Method method) {
            this.beanName = beanName;
            this.method = method;
        }
    }

}
