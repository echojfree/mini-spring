package com.minispring.beans.factory;

/**
 * 初始化 Bean 接口
 * <p>
 * Spring Bean 生命周期中的初始化接口
 * Bean 在完成属性注入后，会调用此接口的 afterPropertiesSet 方法
 * <p>
 * 使用场景：
 * - Bean 需要在属性注入后执行初始化逻辑
 * - 建立连接（数据库、网络等）
 * - 加载配置文件
 * - 启动线程池
 * <p>
 * 面试考点：
 * 1. Bean 生命周期中的初始化阶段
 * 2. InitializingBean vs init-method 的区别
 *    - InitializingBean：代码耦合 Spring，但类型安全
 *    - init-method：解耦但需要反射调用
 * 3. 初始化的执行时机
 *    - 实例化 → 属性注入 → BeanPostProcessor 前置处理 → InitializingBean → init-method → BeanPostProcessor 后置处理
 *
 * @author mini-spring
 */
public interface InitializingBean {

    /**
     * 属性设置完成后的回调方法
     * <p>
     * 此方法在 Bean 的所有属性被设置后调用
     * 可以在此方法中进行：
     * - 属性验证
     * - 资源初始化
     * - 依赖检查
     * <p>
     * 面试考点：
     * - 调用时机：属性注入完成后，BeanPostProcessor 前置处理之后
     * - 异常处理：如果初始化失败，应该抛出异常
     *
     * @throws Exception 初始化失败时抛出异常
     */
    void afterPropertiesSet() throws Exception;

}
