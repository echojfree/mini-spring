package com.minispring.beans.factory;

/**
 * BeanNameAware 接口
 * <p>
 * 实现此接口的 Bean 能够感知自己在容器中的名称
 * <p>
 * 使用场景：
 * - Bean 需要知道自己的名称用于日志记录
 * - 根据 Bean 名称做不同的处理逻辑
 * - 在初始化时需要使用自己的名称
 * <p>
 * 调用时机：
 * 在属性注入之后、InitializingBean.afterPropertiesSet() 之前调用
 * <p>
 * 面试考点：
 * 1. BeanNameAware 的调用时机？
 *    - 属性注入完成后
 *    - 在 InitializingBean 之前
 * 2. 为什么需要 BeanNameAware？
 *    - 有些场景下 Bean 需要知道自己的名称
 *    - 例如：日志记录、监控、动态行为等
 * 3. BeanNameAware vs @Value 注入？
 *    - BeanNameAware：接口方式，代码耦合但类型安全
 *    - @Value：注解方式，解耦但需要依赖注解处理
 *
 * @author mini-spring
 */
public interface BeanNameAware extends Aware {

    /**
     * 设置 Bean 的名称
     * <p>
     * 容器在创建 Bean 时会自动调用此方法
     * 将 Bean 在容器中注册的名称传递给 Bean
     * <p>
     * 调用时机：
     * 1. 实例化完成
     * 2. 属性注入完成
     * 3. 调用 setBeanName() ← 当前位置
     * 4. 调用 InitializingBean.afterPropertiesSet()
     * 5. 调用 init-method
     *
     * @param beanName Bean 在容器中的名称
     */
    void setBeanName(String beanName);

}
