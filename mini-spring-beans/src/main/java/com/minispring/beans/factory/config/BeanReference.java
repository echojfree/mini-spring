package com.minispring.beans.factory.config;

/**
 * Bean 引用：表示对另一个 Bean 的引用
 * <p>
 * 用途：
 * 在属性注入时，区分普通值和 Bean 引用
 * <p>
 * 使用场景：
 * 当一个 Bean 的属性是另一个 Bean 时，使用 BeanReference
 * 例如：UserService 依赖 UserDao
 * <p>
 * 面试考点：
 * 1. 为什么需要 BeanReference？
 *    - 区分普通字符串和 Bean 名称
 *    - 延迟解析：属性注入时才获取真正的 Bean
 * 2. BeanReference 和实际 Bean 的区别？
 *    - BeanReference 只是一个占位符，存储 Bean 名称
 *    - 真正的 Bean 需要通过 BeanFactory.getBean() 获取
 * 3. 依赖注入的实现原理？
 *    - 识别 BeanReference
 *    - 通过 BeanFactory 获取引用的 Bean
 *    - 使用反射设置属性值
 *
 * @author mini-spring
 */
public class BeanReference {

    /**
     * 引用的 Bean 名称
     */
    private final String beanName;

    /**
     * 构造函数
     *
     * @param beanName Bean 名称
     */
    public BeanReference(String beanName) {
        if (beanName == null || beanName.trim().isEmpty()) {
            throw new IllegalArgumentException("Bean 名称不能为空");
        }
        this.beanName = beanName;
    }

    /**
     * 获取 Bean 名称
     */
    public String getBeanName() {
        return beanName;
    }

    @Override
    public String toString() {
        return "BeanReference{beanName='" + beanName + "'}";
    }

}
