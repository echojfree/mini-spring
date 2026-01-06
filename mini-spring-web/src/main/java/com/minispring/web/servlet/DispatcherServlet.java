package com.minispring.web.servlet;

import com.minispring.beans.factory.BeanFactory;
import com.minispring.context.ApplicationContext;
import com.minispring.web.context.WebApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DispatcherServlet - 前端控制器
 * <p>
 * Spring MVC 的核心,负责请求分发
 * <p>
 * 面试要点:
 * 1. DispatcherServlet 的作用
 *    - 统一入口,请求分发
 *    - 协调各个组件工作
 *    - 前端控制器模式
 * <p>
 * 2. 核心组件
 *    - HandlerMapping: URL 映射
 *    - HandlerAdapter: 处理器适配
 *    - ViewResolver: 视图解析
 *    - HandlerExceptionResolver: 异常处理
 * <p>
 * 3. 请求处理流程
 *    - getHandler: 获取处理器
 *    - getHandlerAdapter: 获取适配器
 *    - handle: 执行处理器
 *    - render: 渲染视图
 * <p>
 * 4. 九大组件
 *    - MultipartResolver: 文件上传
 *    - LocaleResolver: 国际化
 *    - ThemeResolver: 主题
 *    - HandlerMapping: 处理器映射
 *    - HandlerAdapter: 处理器适配器
 *    - HandlerExceptionResolver: 异常解析
 *    - RequestToViewNameTranslator: 视图名翻译
 *    - ViewResolver: 视图解析器
 *    - FlashMapManager: FlashMap 管理
 *
 * @author mini-spring
 */
public class DispatcherServlet extends HttpServlet {

    /**
     * WebApplicationContext
     */
    private WebApplicationContext webApplicationContext;

    /**
     * HandlerMapping 列表
     */
    private List<HandlerMapping> handlerMappings;

    /**
     * HandlerAdapter 列表
     */
    private List<HandlerAdapter> handlerAdapters;

    /**
     * ViewResolver 列表
     */
    private List<ViewResolver> viewResolvers;

    /**
     * HandlerExceptionResolver 列表
     */
    private List<HandlerExceptionResolver> handlerExceptionResolvers;

    @Override
    public void init() throws ServletException {
        // 初始化 WebApplicationContext
        initWebApplicationContext();

        // 初始化策略组件
        initStrategies();
    }

    /**
     * 初始化 WebApplicationContext
     */
    protected void initWebApplicationContext() {
        // 从 ServletContext 中获取 WebApplicationContext
        this.webApplicationContext = (WebApplicationContext) getServletContext()
                .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        if (this.webApplicationContext == null) {
            throw new IllegalStateException("No WebApplicationContext found: not in a web application?");
        }
    }

    /**
     * 初始化策略组件
     */
    protected void initStrategies() {
        initHandlerMappings();
        initHandlerAdapters();
        initViewResolvers();
        initHandlerExceptionResolvers();
    }

    /**
     * 初始化 HandlerMapping
     */
    private void initHandlerMappings() {
        this.handlerMappings = new ArrayList<>();

        // 从容器中获取所有 HandlerMapping
        Map<String, HandlerMapping> matchingBeans =
                webApplicationContext.getBeansOfType(HandlerMapping.class);

        if (!matchingBeans.isEmpty()) {
            this.handlerMappings.addAll(matchingBeans.values());
        }
    }

    /**
     * 初始化 HandlerAdapter
     */
    private void initHandlerAdapters() {
        this.handlerAdapters = new ArrayList<>();

        // 从容器中获取所有 HandlerAdapter
        Map<String, HandlerAdapter> matchingBeans =
                webApplicationContext.getBeansOfType(HandlerAdapter.class);

        if (!matchingBeans.isEmpty()) {
            this.handlerAdapters.addAll(matchingBeans.values());
        }
    }

    /**
     * 初始化 ViewResolver
     */
    private void initViewResolvers() {
        this.viewResolvers = new ArrayList<>();

        // 从容器中获取所有 ViewResolver
        Map<String, ViewResolver> matchingBeans =
                webApplicationContext.getBeansOfType(ViewResolver.class);

        if (!matchingBeans.isEmpty()) {
            this.viewResolvers.addAll(matchingBeans.values());
        }
    }

    /**
     * 初始化 HandlerExceptionResolver
     */
    private void initHandlerExceptionResolvers() {
        this.handlerExceptionResolvers = new ArrayList<>();

        // 从容器中获取所有 HandlerExceptionResolver
        Map<String, HandlerExceptionResolver> matchingBeans =
                webApplicationContext.getBeansOfType(HandlerExceptionResolver.class);

        if (!matchingBeans.isEmpty()) {
            this.handlerExceptionResolvers.addAll(matchingBeans.values());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            doDispatch(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            doDispatch(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            doDispatch(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            doDispatch(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * 请求分发处理
     * <p>
     * 核心流程:
     * 1. 获取处理器执行链
     * 2. 获取处理器适配器
     * 3. 执行拦截器 preHandle
     * 4. 执行处理器
     * 5. 执行拦截器 postHandle
     * 6. 渲染视图
     * 7. 执行拦截器 afterCompletion
     */
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        HandlerExecutionChain mappedHandler = null;
        Exception dispatchException = null;
        ModelAndView mv = null;

        try {
            // 1. 获取处理器执行链
            mappedHandler = getHandler(request);
            if (mappedHandler == null) {
                noHandlerFound(request, response);
                return;
            }

            // 2. 获取处理器适配器
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

            // 3. 执行拦截器的 preHandle
            if (!mappedHandler.applyPreHandle(request, response)) {
                return;
            }

            // 4. 执行处理器
            mv = ha.handle(request, response, mappedHandler.getHandler());

            // 5. 执行拦截器的 postHandle
            mappedHandler.applyPostHandle(request, response, mv);

        } catch (Exception ex) {
            dispatchException = ex;
        }

        // 6. 处理分发结果(渲染视图或处理异常)
        processDispatchResult(request, response, mappedHandler, mv, dispatchException);
    }

    /**
     * 获取处理器执行链
     */
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        if (this.handlerMappings != null) {
            for (HandlerMapping mapping : this.handlerMappings) {
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }

    /**
     * 获取处理器适配器
     */
    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        if (this.handlerAdapters != null) {
            for (HandlerAdapter adapter : this.handlerAdapters) {
                if (adapter.supports(handler)) {
                    return adapter;
                }
            }
        }
        throw new ServletException("No adapter for handler [" + handler +
                "]: The DispatcherServlet configuration needs to include a HandlerAdapter");
    }

    /**
     * 没有找到处理器
     */
    protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * 处理分发结果
     */
    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
                                        HandlerExecutionChain mappedHandler, ModelAndView mv,
                                        Exception exception) throws Exception {
        boolean errorView = false;

        // 处理异常
        if (exception != null) {
            mv = processHandlerException(request, response, mappedHandler.getHandler(), exception);
            errorView = (mv != null);
        }

        // 渲染视图
        if (mv != null && !mv.wasCleared()) {
            render(mv, request, response);
        }

        // 执行拦截器的 afterCompletion
        if (mappedHandler != null) {
            mappedHandler.triggerAfterCompletion(request, response, exception);
        }
    }

    /**
     * 处理处理器异常
     */
    protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
                                                    Object handler, Exception ex) throws IOException {
        if (this.handlerExceptionResolvers != null) {
            for (HandlerExceptionResolver resolver : this.handlerExceptionResolvers) {
                ModelAndView exMv = resolver.resolveException(request, response, handler, ex);
                if (exMv != null) {
                    return exMv;
                }
            }
        }

        // 如果没有异常解析器处理,返回错误响应
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return null;
    }

    /**
     * 渲染视图
     */
    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // 解析视图名称
        View view = resolveViewName(mv.getViewName(), mv.getModel(), request);

        if (view == null) {
            throw new ServletException("Could not resolve view with name '" + mv.getViewName() + "'");
        }

        // 渲染视图
        view.render(mv.getModel(), request, response);
    }

    /**
     * 解析视图名称
     */
    protected View resolveViewName(String viewName, Map<String, Object> model, HttpServletRequest request)
            throws Exception {
        if (this.viewResolvers != null) {
            for (ViewResolver viewResolver : this.viewResolvers) {
                View view = viewResolver.resolveViewName(viewName);
                if (view != null) {
                    return view;
                }
            }
        }
        return null;
    }

}
