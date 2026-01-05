package com.minispring.context;

import java.util.EventListener;

/**
 * ApplicationListener - 应用事件监听器接口
 * <p>
 * 监听和处理 ApplicationEvent 事件的接口
 * <p>
 * 设计模式：观察者模式中的观察者
 * <p>
 * 核心功能（面试重点）：
 * 1. 事件监听：接收特定类型的事件
 * 2. 事件处理：在 onApplicationEvent() 方法中处理事件
 * 3. 泛型支持：使用泛型指定监听的事件类型
 * <p>
 * 使用方式：
 * <pre>
 * {@code
 * // 方式1：实现接口
 * @Component
 * public class UserEventListener implements ApplicationListener<UserRegisteredEvent> {
 *     @Override
 *     public void onApplicationEvent(UserRegisteredEvent event) {
 *         System.out.println("用户注册: " + event.getUsername());
 *     }
 * }
 *
 * // 方式2：使用 @EventListener 注解（Spring 4.2+）
 * @Component
 * public class UserService {
 *     @EventListener
 *     public void handleUserRegistered(UserRegisteredEvent event) {
 *         System.out.println("用户注册: " + event.getUsername());
 *     }
 * }
 * }
 * </pre>
 * <p>
 * 面试要点：
 * 1. 监听器的注册方式
 *    - 实现 ApplicationListener 接口，容器自动扫描
 *    - 手动调用 addApplicationListener() 注册
 *    - 使用 @EventListener 注解（需要额外处理器）
 * <p>
 * 2. 事件类型匹配
 *    - 通过泛型参数确定监听的事件类型
 *    - 使用反射获取泛型类型信息
 *    - 支持事件继承关系（监听父类事件会收到子类事件）
 * <p>
 * 3. 监听器执行顺序
 *    - 默认按注册顺序执行
 *    - 可使用 @Order 注解指定优先级
 *    - 数字越小，优先级越高
 * <p>
 * 4. 异常处理
 *    - 监听器抛出异常不会影响其他监听器
 *    - 可使用 ErrorHandler 统一处理异常
 *    - 建议在监听器内部捕获异常
 * <p>
 * 5. 同步 vs 异步
 *    - 默认同步执行：阻塞事件发布者
 *    - 异步执行：使用 @Async 或自定义线程池
 *    - 异步执行需要考虑线程安全和事务传播
 * <p>
 * 6. 应用场景
 *    - 解耦业务逻辑：主业务和附加业务分离
 *    - 日志审计：记录关键操作
 *    - 缓存更新：数据变更后刷新缓存
 *    - 消息通知：发送邮件、短信等
 *    - 统计分析：收集业务数据
 *
 * @param <E> 监听的事件类型
 * @author mini-spring
 */
@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

    /**
     * 处理应用事件
     * <p>
     * 当匹配的事件发布时，此方法会被调用
     * <p>
     * 注意事项：
     * 1. 方法应该尽快执行，避免阻塞事件发布者
     * 2. 异常应该在方法内部处理，避免影响其他监听器
     * 3. 如果需要长时间处理，考虑使用异步方式
     * 4. 注意事务传播：事件在事务内发布，监听器可能不在同一事务中
     *
     * @param event 应用事件对象
     */
    void onApplicationEvent(E event);

}
