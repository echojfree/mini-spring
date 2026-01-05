package com.minispring.beans.factory;

/**
 * Aware 标记接口
 * <p>
 * 这是所有 Aware 接口的顶层标记接口
 * 实现此接口的子接口可以让 Bean 感知容器的各种信息
 * <p>
 * Aware 接口家族：
 * - BeanNameAware：感知 Bean 的名称
 * - BeanFactoryAware：感知 BeanFactory
 * - ApplicationContextAware：感知 ApplicationContext（后续实现）
 * - BeanClassLoaderAware：感知 ClassLoader（后续实现）
 * <p>
 * 设计模式：
 * - 标记接口模式：用于标识一类特殊的接口
 * - 回调模式：容器在特定时机回调 Bean 的方法
 * <p>
 * 面试考点：
 * 1. Aware 接口的作用是什么？
 *    - 让 Bean 能够获取容器的相关信息
 *    - 实现与容器的交互和通信
 * 2. Aware 接口的调用时机？
 *    - 在属性注入之后、InitializingBean 之前
 * 3. 为什么需要 Aware 接口？
 *    - 某些特殊的 Bean 需要与容器交互
 *    - 例如：根据 Bean 名称做不同处理、直接使用 BeanFactory 等
 * 4. 使用 Aware 接口的注意事项？
 *    - 会增加代码与 Spring 的耦合
 *    - 应该谨慎使用，只在必要时使用
 *
 * @author mini-spring
 */
public interface Aware {
    // 标记接口，不需要任何方法
}
