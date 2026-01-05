package com.minispring.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性值集合：管理一个 Bean 的所有属性值
 * <p>
 * 用途：
 * 存储 Bean 的所有属性，便于批量处理
 * <p>
 * 设计模式：
 * 组合模式 - 将多个 PropertyValue 组合在一起管理
 * <p>
 * 面试考点：
 * 1. 为什么需要 PropertyValues？
 *    - 方便批量管理属性
 *    - 提供统一的操作接口
 * 2. 属性注入的顺序重要吗？
 *    - 一般不重要，但有依赖关系时需要注意
 *
 * @author mini-spring
 */
public class PropertyValues {

    /**
     * 属性值列表
     */
    private final List<PropertyValue> propertyValueList = new ArrayList<>();

    /**
     * 添加属性值
     *
     * @param propertyValue 属性值对象
     */
    public void addPropertyValue(PropertyValue propertyValue) {
        // 检查是否已存在同名属性，存在则替换
        for (int i = 0; i < propertyValueList.size(); i++) {
            PropertyValue currentPv = propertyValueList.get(i);
            if (currentPv.getName().equals(propertyValue.getName())) {
                // 替换已存在的属性值
                propertyValueList.set(i, propertyValue);
                return;
            }
        }
        // 不存在则添加
        propertyValueList.add(propertyValue);
    }

    /**
     * 添加属性值（便捷方法）
     *
     * @param name  属性名称
     * @param value 属性值
     */
    public void addPropertyValue(String name, Object value) {
        addPropertyValue(new PropertyValue(name, value));
    }

    /**
     * 获取所有属性值
     *
     * @return PropertyValue 数组
     */
    public PropertyValue[] getPropertyValues() {
        return propertyValueList.toArray(new PropertyValue[0]);
    }

    /**
     * 根据属性名称获取属性值
     *
     * @param propertyName 属性名称
     * @return PropertyValue 对象，如果不存在返回 null
     */
    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue pv : propertyValueList) {
            if (pv.getName().equals(propertyName)) {
                return pv;
            }
        }
        return null;
    }

    /**
     * 判断是否包含指定属性
     *
     * @param propertyName 属性名称
     * @return true 表示包含
     */
    public boolean contains(String propertyName) {
        return getPropertyValue(propertyName) != null;
    }

    /**
     * 判断是否为空
     *
     * @return true 表示没有任何属性
     */
    public boolean isEmpty() {
        return propertyValueList.isEmpty();
    }

    /**
     * 获取属性数量
     *
     * @return 属性数量
     */
    public int size() {
        return propertyValueList.size();
    }

}
