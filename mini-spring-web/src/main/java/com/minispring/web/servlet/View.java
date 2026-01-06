package com.minispring.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * View - 视图接口
 * <p>
 * 负责渲染视图,生成响应内容
 * <p>
 * 面试要点:
 * 1. 视图的作用
 *    - 渲染模型数据
 *    - 生成响应内容
 *    - 解耦业务逻辑和展示
 * <p>
 * 2. 常见实现
 *    - JstlView: JSP 视图
 *    - FreeMarkerView: FreeMarker 模板
 *    - ThymeleafView: Thymeleaf 模板
 *    - JsonView: JSON 响应
 * <p>
 * 3. 视图渲染流程
 *    - 设置响应类型
 *    - 合并模型数据
 *    - 生成响应内容
 *
 * @author mini-spring
 */
public interface View {

    /**
     * 渲染视图
     *
     * @param model    模型数据
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @throws Exception 渲染异常
     */
    void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception;

    /**
     * 获取内容类型
     */
    default String getContentType() {
        return "text/html;charset=UTF-8";
    }

}
