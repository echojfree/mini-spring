package com.minispring.web.servlet.mvc.method.annotation;

import com.minispring.web.bind.annotation.PathVariable;
import com.minispring.web.bind.annotation.RequestBody;
import com.minispring.web.bind.annotation.RequestParam;
import com.minispring.web.bind.annotation.ResponseBody;
import com.minispring.web.servlet.HandlerAdapter;
import com.minispring.web.servlet.ModelAndView;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RequestMappingHandlerAdapter - @RequestMapping 处理器适配器
 * <p>
 * 适配 HandlerMethod,负责参数解析和返回值处理
 * <p>
 * 面试要点:
 * 1. 参数解析流程
 *    - 遍历方法参数
 *    - 根据参数类型和注解选择解析器
 *    - 解析请求数据绑定到参数
 * <p>
 * 2. 参数解析器类型
 *    - @RequestParam: 请求参数
 *    - @PathVariable: 路径变量
 *    - @RequestBody: 请求体
 *    - HttpServletRequest/Response: Servlet API
 * <p>
 * 3. 返回值处理
 *    - @ResponseBody: 直接写入响应体
 *    - ModelAndView: 视图渲染
 *    - String: 视图名称
 *    - void: 通过 response 直接输出
 * <p>
 * 4. 内容协商
 *    - 根据 Accept 头选择合适的 MessageConverter
 *    - JSON, XML, HTML 等格式
 *
 * @author mini-spring
 */
public class RequestMappingHandlerAdapter implements HandlerAdapter {

    /**
     * JSON 序列化工具
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 1. 解析方法参数
        Object[] args = resolveArguments(handlerMethod, request, response);

        // 2. 调用处理器方法
        Method method = handlerMethod.getMethod();
        Object bean = handlerMethod.getBean();
        Object returnValue = method.invoke(bean, args);

        // 3. 处理返回值
        return handleReturnValue(returnValue, handlerMethod, request, response);
    }

    /**
     * 解析方法参数
     */
    private Object[] resolveArguments(HandlerMethod handlerMethod, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        Method method = handlerMethod.getMethod();
        Parameter[] parameters = method.getParameters();
        List<Object> args = new ArrayList<>();
        Map<String, String> pathVariables = handlerMethod.getPathVariables();

        for (Parameter parameter : parameters) {
            Class<?> paramType = parameter.getType();

            // HttpServletRequest
            if (paramType == HttpServletRequest.class) {
                args.add(request);
            }
            // HttpServletResponse
            else if (paramType == HttpServletResponse.class) {
                args.add(response);
            }
            // @PathVariable
            else if (parameter.isAnnotationPresent(PathVariable.class)) {
                PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
                String varName = pathVariable.value();
                if (varName.isEmpty()) {
                    varName = parameter.getName();
                }

                String varValue = pathVariables.get(varName);

                // 处理必需参数
                if (varValue == null && pathVariable.required()) {
                    throw new IllegalArgumentException("Required path variable '" + varName + "' is not present");
                }

                // 类型转换
                Object convertedValue = convertValue(varValue, paramType);
                args.add(convertedValue);
            }
            // @RequestBody
            else if (parameter.isAnnotationPresent(RequestBody.class)) {
                RequestBody requestBody = parameter.getAnnotation(RequestBody.class);

                // 读取请求体
                StringBuilder body = new StringBuilder();
                try (BufferedReader reader = request.getReader()) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        body.append(line);
                    }
                }

                String bodyContent = body.toString();

                // 处理必需参数
                if ((bodyContent == null || bodyContent.isEmpty()) && requestBody.required()) {
                    throw new IllegalArgumentException("Required request body is missing");
                }

                // JSON 反序列化
                Object bodyObject = objectMapper.readValue(bodyContent, paramType);
                args.add(bodyObject);
            }
            // @RequestParam
            else if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                String paramName = requestParam.value();
                if (paramName.isEmpty()) {
                    paramName = parameter.getName();
                }

                String paramValue = request.getParameter(paramName);

                // 处理必需参数
                if (paramValue == null && requestParam.required()) {
                    if (!requestParam.defaultValue().isEmpty()) {
                        paramValue = requestParam.defaultValue();
                    } else {
                        throw new IllegalArgumentException("Required parameter '" + paramName + "' is not present");
                    }
                }

                // 类型转换
                Object convertedValue = convertValue(paramValue, paramType);
                args.add(convertedValue);
            }
            // 其他类型暂不支持
            else {
                throw new UnsupportedOperationException("Parameter type not supported: " + paramType);
            }
        }

        return args.toArray();
    }

    /**
     * 简单的类型转换
     */
    private Object convertValue(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        }

        throw new UnsupportedOperationException("Type conversion not supported: " + targetType);
    }

    /**
     * 处理返回值
     */
    private ModelAndView handleReturnValue(Object returnValue, HandlerMethod handlerMethod,
                                            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Method method = handlerMethod.getMethod();

        // @ResponseBody 注解处理
        boolean hasResponseBody = method.isAnnotationPresent(ResponseBody.class) ||
                method.getDeclaringClass().isAnnotationPresent(ResponseBody.class);

        if (hasResponseBody) {
            // 直接写入响应体
            writeResponse(returnValue, response);
            return null;
        }

        // ModelAndView
        if (returnValue instanceof ModelAndView) {
            return (ModelAndView) returnValue;
        }

        // String 视图名称
        if (returnValue instanceof String) {
            return new ModelAndView((String) returnValue);
        }

        // void
        if (returnValue == null || method.getReturnType() == void.class) {
            return null;
        }

        throw new UnsupportedOperationException("Return type not supported: " + returnValue.getClass());
    }

    /**
     * 写入响应
     */
    private void writeResponse(Object value, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");

        String json;
        if (value instanceof String) {
            json = (String) value;
        } else {
            json = objectMapper.writeValueAsString(value);
        }

        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }

}
