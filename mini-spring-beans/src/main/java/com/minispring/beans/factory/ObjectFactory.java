package com.minispring.beans.factory;

/**
 * ObjectFactory 对象工厂接口
 * <p>
 * 用于延迟获取对象，是三级缓存机制的核心接口
 * <p>
 * 设计思想（面试考点）：
 * 1. 延迟创建：对象不会立即创建，而是在需要时才创建
 * 2. 工厂模式：封装对象创建逻辑
 * 3. 函数式接口：可以使用 Lambda 表达式
 * <p>
 * 在循环依赖中的作用：
 * - 提前暴露一个对象工厂，而不是对象本身
 * - 在需要时才调用 getObject() 创建对象
 * - 支持 AOP 代理对象的提前暴露
 * <p>
 * 面试重点：
 * 1. 为什么需要 ObjectFactory？
 *    - 延迟对象的创建时机
 *    - 支持 AOP 代理对象的循环依赖
 *    - 提供对象创建的扩展点
 * 2. ObjectFactory 在三级缓存中的位置？
 *    - 存储在三级缓存 singletonFactories 中
 *    - 用于创建半成品对象（二级缓存）
 * 3. 与工厂模式的关系？
 *    - 工厂方法模式的应用
 *    - 将对象创建延迟到调用时
 *
 * @param <T> 对象类型
 * @author mini-spring
 */
@FunctionalInterface
public interface ObjectFactory<T> {

    /**
     * 获取对象
     * <p>
     * 创建并返回对象实例
     * 可能每次调用都返回不同的对象（原型模式）
     * 也可能每次返回同一个对象（单例模式）
     * <p>
     * 在循环依赖场景中：
     * - 第一次调用时创建半成品对象
     * - 后续调用返回同一个半成品对象
     *
     * @return 对象实例
     * @throws Exception 创建对象失败时抛出异常
     */
    T getObject() throws Exception;

}
