package com.minispring.context.event;

import com.minispring.context.ApplicationContext;
import com.minispring.context.ApplicationEvent;

/**
 * ContextRefreshedEvent - 容器刷新完成事件
 * <p>
 * 当 ApplicationContext 初始化或刷新完成时发布的事件
 * <p>
 * 发布时机（面试重点）：
 * 1. 容器首次初始化完成（refresh() 方法执行完毕）
 * 2. 所有 Bean 已经创建和初始化完成
 * 3. BeanPostProcessor 已经执行完毕
 * 4. 容器完全可用
 * <p>
 * 使用场景：
 * <pre>
 * {@code
 * @Component
 * public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
 *     @Override
 *     public void onApplicationEvent(ContextRefreshedEvent event) {
 *         System.out.println("容器启动完成，开始执行初始化任务...");
 *         // 执行启动后的初始化任务
 *         // 例如：预热缓存、连接外部服务等
 *     }
 * }
 * }
 * </pre>
 * <p>
 * 面试要点：
 * 1. 与 ContextStartedEvent 的区别
 *    - ContextRefreshedEvent：容器刷新完成时发布
 *    - ContextStartedEvent：容器启动时发布（调用 start() 方法）
 * <p>
 * 2. 多次发布场景
 *    - 每次调用 refresh() 都会发布
 *    - 容器重新刷新时也会发布
 * <p>
 * 3. 典型应用场景
 *    - 缓存预热：容器启动后加载热点数据
 *    - 连接初始化：建立数据库连接池
 *    - 任务调度：启动定时任务
 *    - 健康检查：验证系统状态
 *
 * @author mini-spring
 */
public class ContextRefreshedEvent extends ApplicationEvent {

    /**
     * 构造 ContextRefreshedEvent
     *
     * @param source 事件源（ApplicationContext 实例）
     */
    public ContextRefreshedEvent(Object source) {
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
