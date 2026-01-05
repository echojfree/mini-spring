package com.minispring.context.event;

import com.minispring.context.ApplicationContext;
import com.minispring.context.ApplicationEvent;

/**
 * ContextClosedEvent - 容器关闭事件
 * <p>
 * 当 ApplicationContext 关闭时发布的事件
 * <p>
 * 发布时机（面试重点）：
 * 1. 容器关闭时（调用 close() 方法）
 * 2. 在销毁所有 Bean 之前发布
 * 3. 监听器还可以正常工作
 * <p>
 * 使用场景：
 * <pre>
 * {@code
 * @Component
 * public class ShutdownListener implements ApplicationListener<ContextClosedEvent> {
 *     @Override
 *     public void onApplicationEvent(ContextClosedEvent event) {
 *         System.out.println("容器即将关闭，执行清理任务...");
 *         // 执行关闭前的清理任务
 *         // 例如：关闭连接、保存状态等
 *     }
 * }
 * }
 * </pre>
 * <p>
 * 面试要点：
 * 1. 与 DisposableBean 的区别
 *    - ContextClosedEvent：容器级别的关闭事件
 *    - DisposableBean：单个 Bean 的销毁回调
 * <p>
 * 2. 执行顺序
 *    - 1. 发布 ContextClosedEvent
 *    - 2. 监听器处理事件
 *    - 3. 销毁所有单例 Bean
 * <p>
 * 3. 典型应用场景
 *    - 资源释放：关闭连接池、释放文件句柄
 *    - 状态保存：保存应用状态到磁盘
 *    - 通知清理：发送关闭通知
 *    - 优雅关闭：等待正在处理的请求完成
 * <p>
 * 4. 注意事项
 *    - 监听器中不要执行耗时操作
 *    - 不要依赖即将销毁的 Bean
 *    - 异常不要影响容器关闭流程
 *
 * @author mini-spring
 */
public class ContextClosedEvent extends ApplicationEvent {

    /**
     * 构造 ContextClosedEvent
     *
     * @param source 事件源（ApplicationContext 实例）
     */
    public ContextClosedEvent(Object source) {
        super(source);
    }

    /**
     * 获取 ApplicationContext
     *
     * @return ApplicationContext 实例
     */
    public ApplicationContext getApplicationContext() {
        return (ApplicationContext) getSource();
    }
}
