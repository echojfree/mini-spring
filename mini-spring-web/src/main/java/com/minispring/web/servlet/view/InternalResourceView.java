package com.minispring.web.servlet.view;

import com.minispring.web.servlet.View;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * InternalResourceView - JSP 视图
 * <p>
 * 使用 RequestDispatcher forward 到 JSP 页面
 * <p>
 * 面试要点:
 * 1. JSP 视图渲染
 *    - 使用 RequestDispatcher.forward()
 *    - 将模型数据设置为 request 属性
 *    - 服务器端转发,URL 不变
 * <p>
 * 2. forward vs redirect
 *    - forward: 服务器内部转发,一次请求
 *    - redirect: 客户端重定向,两次请求
 *    - forward 可以共享 request 数据
 *
 * @author mini-spring
 */
public class InternalResourceView implements View {

    /**
     * JSP 页面路径
     */
    private final String url;

    public InternalResourceView(String url) {
        this.url = url;
    }

    @Override
    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // 将模型数据设置为 request 属性
        if (model != null) {
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        // 转发到 JSP 页面
        RequestDispatcher dispatcher = request.getRequestDispatcher(url);
        dispatcher.forward(request, response);
    }

    public String getUrl() {
        return url;
    }

}
