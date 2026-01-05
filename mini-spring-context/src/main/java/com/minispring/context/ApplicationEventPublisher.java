package com.minispring.context;

/**
 * ApplicationEventPublisher - 应用事件发布器接口
 * <p>
 * 封装事件发布功能，允许对象发布事件到所有注册的监听器
 * <p>
 * 设计模式：观察者模式中的主题（Subject）
 * <p>
 * 核心功能（面试重点）：
 * 1. 事件发布：将事件传递给所有匹配的监听器
 * 2. 解耦发布者和监听器：发布者无需知道谁在监听
 * 3. ApplicationContext 实现此接口：容器本身就是事件发布器
 * <p>
 * 使用方式：
 * <pre>
 * {@code
 * // 方式1：注入 ApplicationEventPublisher
 * @Service
 * public class UserService {
 *     @Autowired
 *     private ApplicationEventPublisher publisher;
 *
 *     public void register(String username) {
 *         // 注册业务逻辑...
 *         publisher.publishEvent(new UserRegisteredEvent(this, username));
 *     }
 * }
 *
 * // 方式2：注入 ApplicationContext（它实现了 ApplicationEventPublisher）
 * @Service
 * public class OrderService {
 *     @Autowired
 *     private ApplicationContext context;
 *
 *     public void createOrder(Order order) {
 *         // 创建订单...
 *         context.publishEvent(new OrderCreatedEvent(this, order));
 *     }
 * }
 *
 * // 方式3：实现 ApplicationEventPublisherAware
 * @Service
 * public class ProductService implements ApplicationEventPublisherAware {
 *     private ApplicationEventPublisher publisher;
 *
 *     @Override
 *     public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
 *         this.publisher = publisher;
 *     }
 *
 *     public void addProduct(Product product) {
 *         // 添加产品...
 *         publisher.publishEvent(new ProductAddedEvent(this, product));
 *     }
 * }
 * }
 * </pre>
 * <p>
 * 面试要点：
 * 1. 发布机制
 *    - 事件发布是同步的（默认）
 *    - 按监听器注册顺序依次调用
 *    - 一个监听器异常不影响其他监听器
 * <p>
 * 2. 事件传播
 *    - 事件会传递给所有匹配类型的监听器
 *    - 支持事件继承：监听父类事件可收到子类事件
 *    - 不支持事件冒泡：不会传播到父容器
 * <p>
 * 3. 与消息队列对比
 *    - 事件机制：进程内通信，同步，轻量级
 *    - 消息队列：进程间通信，异步，持久化
 *    - 事件机制适合应用内部解耦
 *    - 消息队列适合分布式系统通信
 * <p>
 * 4. 事务一致性
 *    - 事件在事务内发布，但监听器可能不在同一事务
 *    - 需要考虑事务提交/回滚对事件的影响
 *    - 可使用 @TransactionalEventListener 控制事件执行时机
 * <p>
 * 5. 性能考虑
 *    - 同步发布会阻塞调用线程
 *    - 监听器数量影响发布性能
 *    - 可使用异步方式提高性能
 *
 * @author mini-spring
 */
@FunctionalInterface
public interface ApplicationEventPublisher {

    /**
     * 发布应用事件
     * <p>
     * 将事件通知给所有匹配的监听器
     * <p>
     * 工作流程：
     * 1. 接收事件对象
     * 2. 查找所有匹配类型的监听器
     * 3. 按顺序调用每个监听器的 onApplicationEvent() 方法
     * 4. 捕获并处理监听器抛出的异常
     * <p>
     * 注意事项：
     * 1. 事件发布是同步的，会阻塞调用线程
     * 2. 如果监听器执行时间长，考虑使用异步方式
     * 3. 监听器抛出的异常会被捕获，不会影响其他监听器
     * 4. 事件对象应该是不可变的或线程安全的
     *
     * @param event 要发布的事件
     */
    void publishEvent(ApplicationEvent event);

}
