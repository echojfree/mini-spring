package com.minispring.beans.factory;

import com.minispring.beans.exception.BeansException;

/**
 * BeanFactoryAware 接口
 * <p>
 * 实现此接口的 Bean 能够获取到创建它的 BeanFactory
 * <p>
 * 使用场景：
 * - Bean 需要动态获取其他 Bean
 * - 需要访问容器的功能（如查询 Bean 是否存在）
 * - 实现工厂模式，通过容器创建其他对象
 * <p>
 * 调用时机：
 * 在 BeanNameAware 之后、InitializingBean.afterPropertiesSet() 之前调用
 * <p>
 * 面试考点：
 * 1. BeanFactoryAware 的作用？
 *    - 让 Bean 能够获取 BeanFactory 引用
 *    - 实现与容器的交互
 * 2. BeanFactoryAware 的使用场景？
 *    - 动态获取其他 Bean
 *    - 实现复杂的工厂逻辑
 *    - 需要容器级别的功能
 * 3. BeanFactoryAware vs 依赖注入？
 *    - BeanFactoryAware：主动获取，灵活但耦合
 *    - 依赖注入：被动接收，解耦但固定
 * 4. 为什么不推荐过度使用？
 *    - 增加代码与 Spring 的耦合
 *    - 违反 IoC 的设计初衷
 *    - 应优先使用依赖注入
 *
 * @author mini-spring
 */
public interface BeanFactoryAware extends Aware {

    /**
     * 设置 BeanFactory
     * <p>
     * 容器在创建 Bean 时会自动调用此方法
     * 将创建该 Bean 的 BeanFactory 传递给 Bean
     * <p>
     * 调用时机：
     * 1. 实例化完成
     * 2. 属性注入完成
     * 3. 调用 BeanNameAware.setBeanName()
     * 4. 调用 setBeanFactory() ← 当前位置
     * 5. 调用 InitializingBean.afterPropertiesSet()
     * 6. 调用 init-method
     * <p>
     * 注意事项：
     * - 保存的 BeanFactory 引用应该谨慎使用
     * - 避免在构造函数中使用 BeanFactory（此时还未注入）
     * - 注意循环依赖问题
     *
     * @param beanFactory 创建该 Bean 的 BeanFactory
     * @throws BeansException 设置失败时抛出异常
     */
    void setBeanFactory(BeanFactory beanFactory) throws BeansException;

}
