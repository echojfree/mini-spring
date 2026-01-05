package com.minispring.beans;

/**
 * 属性值：封装单个属性的名称和值
 * <p>
 * 用途：
 * 存储 Bean 的属性信息，例如：
 * - name = "username", value = "admin"
 * - name = "userService", value = BeanReference("userService")
 * <p>
 * 面试考点：
 * 1. Spring 如何存储属性值？
 * 2. 属性值的类型有哪些？
 *    - 基本类型值（String, int 等）
 *    - Bean 引用（BeanReference）
 * 3. 属性注入的时机？
 *    - 实例化之后，初始化之前
 *
 * @author mini-spring
 */
public class PropertyValue {

    /**
     * 属性名称
     * 例如："username", "age", "userService"
     */
    private final String name;

    /**
     * 属性值
     * 可以是：
     * 1. 基本类型值：String, Integer, Boolean 等
     * 2. Bean 引用：BeanReference 对象
     * 3. 集合类型：List, Map 等（后续扩展）
     */
    private final Object value;

    /**
     * 构造函数
     *
     * @param name  属性名称
     * @param value 属性值
     */
    public PropertyValue(String name, Object value) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("属性名称不能为空");
        }
        this.name = name;
        this.value = value;
    }

    /**
     * 获取属性名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取属性值
     */
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PropertyValue{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

}
