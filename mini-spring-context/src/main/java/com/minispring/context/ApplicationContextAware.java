package com.minispring.context;

/**
 * ApplicationContextAware - 应用上下文感知接口
 * <p>
 * 实现该接口的 Bean 可以获得 ApplicationContext 的引用
 * <p>
 * 面试要点:
 * 1. Aware 接口系列
 *    - BeanNameAware: 获取 Bean 名称
 *    - BeanFactoryAware: 获取 BeanFactory
 *    - ApplicationContextAware: 获取 ApplicationContext
 * <p>
 * 2. 回调机制
 *    - 容器在初始化 Bean 时自动调用
 *    - 在 BeanPostProcessor 处理之前调用
 * <p>
 * 3. 使用场景
 *    - 需要使用容器功能
 *    - 发布事件
 *    - 获取其他 Bean
 *
 * @author mini-spring
 */
public interface ApplicationContextAware {

    /**
     * 设置 ApplicationContext
     *
     * @param applicationContext ApplicationContext 实例
     */
    void setApplicationContext(ApplicationContext applicationContext);

}
