package com.minispring.web.servlet;

import java.util.HashMap;
import java.util.Map;

/**
 * ModelAndView - 模型和视图
 * <p>
 * 封装模型数据和视图信息
 * <p>
 * 面试要点:
 * 1. ModelAndView 的作用
 *    - 封装返回给视图的数据(Model)
 *    - 指定要渲染的视图(View)
 *    - Controller 返回值类型
 * <p>
 * 2. Model 和 View 的分离
 *    - Model: 业务数据
 *    - View: 展示层
 *    - 解耦数据和展示
 * <p>
 * 3. 使用方式
 *    - new ModelAndView("viewName")
 *    - addObject("key", value)
 *    - setViewName("viewName")
 *
 * @author mini-spring
 */
public class ModelAndView {

    /**
     * 视图名称
     */
    private String viewName;

    /**
     * 模型数据
     */
    private Map<String, Object> model = new HashMap<>();

    /**
     * 是否已清除
     */
    private boolean cleared = false;

    public ModelAndView() {
    }

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public ModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }

    /**
     * 添加模型属性
     *
     * @param attributeName  属性名
     * @param attributeValue 属性值
     * @return this
     */
    public ModelAndView addObject(String attributeName, Object attributeValue) {
        this.model.put(attributeName, attributeValue);
        return this;
    }

    /**
     * 添加所有模型属性
     *
     * @param modelMap 模型Map
     * @return this
     */
    public ModelAndView addAllObjects(Map<String, ?> modelMap) {
        if (modelMap != null) {
            this.model.putAll(modelMap);
        }
        return this;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }

    /**
     * 清除 ModelAndView
     */
    public void clear() {
        this.viewName = null;
        this.model.clear();
        this.cleared = true;
    }

    /**
     * 判断是否已清除
     */
    public boolean wasCleared() {
        return this.cleared;
    }

}
