package com.minispring.context;

import java.util.EventObject;

/**
 * ApplicationEvent - 应用事件基类
 * <p>
 * Spring 事件机制的核心类，所有应用事件都继承自此类
 * <p>
 * 设计模式：观察者模式（发布-订阅模式）
 * <p>
 * 核心概念（面试重点）：
 * 1. 事件（Event）：发生的事情，继承 ApplicationEvent
 * 2. 事件源（Source）：事件的发起者，通常是 ApplicationContext
 * 3. 事件监听器（Listener）：事件的处理者，实现 ApplicationListener
 * 4. 事件广播器（Multicaster）：负责将事件分发给所有监听器
 * <p>
 * 工作流程：
 * <pre>
 * 1. 定义事件：继承 ApplicationEvent
 * 2. 定义监听器：实现 ApplicationListener&lt;E&gt;
 * 3. 注册监听器：容器自动扫描或手动注册
 * 4. 发布事件：调用 publishEvent()
 * 5. 广播事件：EventMulticaster 通知所有监听器
 * 6. 处理事件：监听器的 onApplicationEvent() 被调用
 * </pre>
 * <p>
 * 使用场景：
 * <pre>
 * {@code
 * // 1. 自定义事件
 * public class UserRegisteredEvent extends ApplicationEvent {
 *     private String username;
 *
 *     public UserRegisteredEvent(Object source, String username) {
 *         super(source);
 *         this.username = username;
 *     }
 * }
 *
 * // 2. 事件监听器
 * @Component
 * public class EmailNotificationListener implements ApplicationListener<UserRegisteredEvent> {
 *     public void onApplicationEvent(UserRegisteredEvent event) {
 *         // 发送欢迎邮件
 *         sendEmail(event.getUsername());
 *     }
 * }
 *
 * // 3. 发布事件
 * @Service
 * public class UserService {
 *     @Autowired
 *     private ApplicationEventPublisher publisher;
 *
 *     public void register(String username) {
 *         // 注册用户...
 *         publisher.publishEvent(new UserRegisteredEvent(this, username));
 *     }
 * }
 * }
 * </pre>
 * <p>
 * 面试要点：
 * 1. Spring 事件机制的优点
 *    - 解耦：事件发布者和监听器之间松耦合
 *    - 灵活：可以动态添加/移除监听器
 *    - 扩展性：符合开闭原则，易于扩展
 * <p>
 * 2. 事件机制的实现原理
 *    - 基于观察者模式
 *    - 使用 EventMulticaster 管理监听器
 *    - 支持同步和异步事件处理
 * <p>
 * 3. Spring 内置事件
 *    - ContextRefreshedEvent：容器刷新完成
 *    - ContextStartedEvent：容器启动
 *    - ContextStoppedEvent：容器停止
 *    - ContextClosedEvent：容器关闭
 * <p>
 * 4. 事件传播机制
 *    - 默认同步传播：按注册顺序依次调用
 *    - 支持异步传播：使用 @Async 或线程池
 *    - 支持事件层次：父子事件关系
 * <p>
 * 5. 与消息队列对比
 *    - 事件机制：应用内部通信，轻量级
 *    - 消息队列：应用间通信，持久化，分布式
 *
 * @author mini-spring
 */
public abstract class ApplicationEvent extends EventObject {

    /**
     * 事件发生的时间戳
     */
    private final long timestamp;

    /**
     * 构造 ApplicationEvent
     *
     * @param source 事件源（事件的发起者）
     */
    public ApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 获取事件发生的时间戳
     *
     * @return 时间戳（毫秒）
     */
    public final long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[source=" + source + ", timestamp=" + timestamp + "]";
    }
}
