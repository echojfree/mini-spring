package com.minispring.web.servlet.view;

import com.minispring.web.servlet.View;
import com.minispring.web.servlet.ViewResolver;

/**
 * InternalResourceViewResolver - JSP 视图解析器
 * <p>
 * 解析视图名称为 JSP 页面路径
 * <p>
 * 面试要点:
 * 1. 视图名称解析
 *    - prefix + viewName + suffix
 *    - 例如: /WEB-INF/views/ + user/list + .jsp
 * <p>
 * 2. 配置示例
 *    - prefix: /WEB-INF/views/
 *    - suffix: .jsp
 *    - viewName: user/list -> /WEB-INF/views/user/list.jsp
 * <p>
 * 3. WEB-INF 目录
 *    - 客户端无法直接访问
 *    - 只能通过服务器内部转发
 *    - 保护 JSP 页面安全
 *
 * @author mini-spring
 */
public class InternalResourceViewResolver implements ViewResolver {

    /**
     * 视图路径前缀
     */
    private String prefix = "";

    /**
     * 视图路径后缀
     */
    private String suffix = "";

    @Override
    public View resolveViewName(String viewName) {
        String url = prefix + viewName + suffix;
        return new InternalResourceView(url);
    }

    public void setPrefix(String prefix) {
        this.prefix = (prefix != null ? prefix : "");
    }

    public void setSuffix(String suffix) {
        this.suffix = (suffix != null ? suffix : "");
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

}
