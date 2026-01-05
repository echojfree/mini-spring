package com.minispring.beans.factory;

/**
 * 销毁 Bean 接口
 * <p>
 * Spring Bean 生命周期中的销毁接口
 * 容器关闭时，会调用此接口的 destroy 方法
 * <p>
 * 使用场景：
 * - 释放资源（关闭连接、释放文件句柄等）
 * - 关闭线程池
 * - 保存状态
 * - 清理缓存
 * <p>
 * 面试考点：
 * 1. Bean 的销毁时机：容器关闭时
 * 2. DisposableBean vs destroy-method 的区别
 *    - DisposableBean：代码耦合 Spring，但类型安全
 *    - destroy-method：解耦但需要反射调用
 * 3. 销毁顺序：
 *    - BeanPostProcessor 前置销毁处理
 *    - DisposableBean.destroy()
 *    - destroy-method 配置的方法
 *
 * @author mini-spring
 */
public interface DisposableBean {

    /**
     * Bean 销毁时的回调方法
     * <p>
     * 在容器关闭时调用此方法
     * 可以在此方法中进行：
     * - 资源释放
     * - 连接关闭
     * - 状态保存
     * - 清理工作
     * <p>
     * 面试考点：
     * - 调用时机：容器关闭时，ApplicationContext.close() 触发
     * - 异常处理：销毁方法应该捕获异常，避免影响其他 Bean 的销毁
     * - 只有单例 Bean 会执行销毁方法，原型 Bean 不会
     *
     * @throws Exception 销毁失败时抛出异常
     */
    void destroy() throws Exception;

}
