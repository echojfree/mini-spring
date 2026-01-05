package com.minispring.test.event;

import com.minispring.context.support.ClassPathXmlApplicationContext;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * EventTest - 事件机制测试类
 * <p>
 * 测试 Spring 事件机制的完整功能
 * <p>
 * 测试内容：
 * 1. 自定义事件的发布和监听
 * 2. 多个监听器接收同一事件
 * 3. 容器刷新完成事件（ContextRefreshedEvent）
 * 4. 容器关闭事件（ContextClosedEvent）
 * 5. 泛型类型匹配（监听器只接收匹配类型的事件）
 * <p>
 * 面试要点：
 * 1. 事件机制的核心组件
 *    - ApplicationEvent：事件基类
 *    - ApplicationListener：监听器接口
 *    - ApplicationEventPublisher：事件发布器
 *    - ApplicationEventMulticaster：事件广播器
 * <p>
 * 2. 事件发布流程
 *    - 调用 publishEvent() 发布事件
 *    - EventMulticaster 遍历所有监听器
 *    - 通过反射匹配监听器的泛型类型
 *    - 调用匹配监听器的 onApplicationEvent()
 * <p>
 * 3. 内置事件
 *    - ContextRefreshedEvent：容器刷新完成
 *    - ContextClosedEvent：容器关闭
 *    - ContextStartedEvent：容器启动
 *    - ContextStoppedEvent：容器停止
 * <p>
 * 4. 事件机制的特点
 *    - 同步发布（默认）
 *    - 支持泛型类型匹配
 *    - 异常隔离（一个监听器异常不影响其他监听器）
 *    - 支持事件继承
 *
 * @author mini-spring
 */
public class EventTest {

    /**
     * 测试自定义事件的发布和监听
     * <p>
     * 验证：
     * 1. 监听器能够接收事件
     * 2. 事件携带的数据正确
     */
    @Test
    public void testCustomEvent() throws Exception {
        // 1. 创建容器
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("event-test.xml");

        // 2. 获取监听器
        EmailNotificationListener emailListener = context.getBean("emailNotificationListener", EmailNotificationListener.class);
        AuditLogListener auditListener = context.getBean("auditLogListener", AuditLogListener.class);

        // 3. 清空之前的事件记录（容器初始化可能触发事件）
        emailListener.clearReceivedEvents();
        auditListener.clearReceivedEvents();

        // 4. 发布事件
        UserRegisteredEvent event = new UserRegisteredEvent(this, "张三", "zhangsan@example.com");
        context.publishEvent(event);

        // 5. 验证事件被接收
        assertEquals("邮件监听器应该接收到1个事件", 1, emailListener.getReceivedEvents().size());
        assertEquals("审计监听器应该接收到1个事件", 1, auditListener.getReceivedEvents().size());

        // 6. 验证事件数据
        UserRegisteredEvent receivedByEmail = emailListener.getReceivedEvents().get(0);
        assertEquals("用户名应该匹配", "张三", receivedByEmail.getUsername());
        assertEquals("邮箱应该匹配", "zhangsan@example.com", receivedByEmail.getEmail());

        UserRegisteredEvent receivedByAudit = auditListener.getReceivedEvents().get(0);
        assertEquals("用户名应该匹配", "张三", receivedByAudit.getUsername());
        assertEquals("邮箱应该匹配", "zhangsan@example.com", receivedByAudit.getEmail());

        System.out.println("✅ 测试1通过：自定义事件的发布和监听");

        // 7. 关闭容器
        context.close();
    }

    /**
     * 测试多个监听器接收同一事件
     * <p>
     * 验证：
     * 1. 同一事件可以被多个监听器接收
     * 2. 监听器按注册顺序执行
     */
    @Test
    public void testMultipleListeners() throws Exception {
        // 1. 创建容器
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("event-test.xml");

        // 2. 获取监听器
        EmailNotificationListener emailListener = context.getBean("emailNotificationListener", EmailNotificationListener.class);
        AuditLogListener auditListener = context.getBean("auditLogListener", AuditLogListener.class);

        // 3. 清空之前的事件记录
        emailListener.clearReceivedEvents();
        auditListener.clearReceivedEvents();

        // 4. 发布多个事件
        context.publishEvent(new UserRegisteredEvent(this, "用户1", "user1@example.com"));
        context.publishEvent(new UserRegisteredEvent(this, "用户2", "user2@example.com"));
        context.publishEvent(new UserRegisteredEvent(this, "用户3", "user3@example.com"));

        // 5. 验证所有监听器都接收到了所有事件
        assertEquals("邮件监听器应该接收到3个事件", 3, emailListener.getReceivedEvents().size());
        assertEquals("审计监听器应该接收到3个事件", 3, auditListener.getReceivedEvents().size());

        // 6. 验证事件顺序
        assertEquals("用户1", emailListener.getReceivedEvents().get(0).getUsername());
        assertEquals("用户2", emailListener.getReceivedEvents().get(1).getUsername());
        assertEquals("用户3", emailListener.getReceivedEvents().get(2).getUsername());

        System.out.println("✅ 测试2通过：多个监听器接收同一事件");

        // 7. 关闭容器
        context.close();
    }

    /**
     * 测试容器刷新完成事件
     * <p>
     * 验证：
     * 1. 容器刷新完成时会自动发布 ContextRefreshedEvent
     * 2. 监听器能够接收到该事件
     */
    @Test
    public void testContextRefreshedEvent() throws Exception {
        // 1. 创建容器（会触发 refresh）
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("event-test.xml");

        // 2. 获取监听器
        ContextRefreshedListener listener = context.getBean("contextRefreshedListener", ContextRefreshedListener.class);

        // 3. 验证事件被接收
        assertEquals("应该接收到1个容器刷新完成事件", 1, listener.getReceivedEvents().size());

        // 4. 验证事件源是当前容器
        assertSame("事件源应该是当前容器", context, listener.getReceivedEvents().get(0).getApplicationContext());

        System.out.println("✅ 测试3通过：容器刷新完成事件");

        // 5. 关闭容器
        context.close();
    }

    /**
     * 测试容器关闭事件
     * <p>
     * 验证：
     * 1. 容器关闭时会自动发布 ContextClosedEvent
     * 2. 监听器能够接收到该事件
     */
    @Test
    public void testContextClosedEvent() throws Exception {
        // 1. 创建容器
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("event-test.xml");

        // 2. 获取监听器
        ContextClosedListener listener = context.getBean("contextClosedListener", ContextClosedListener.class);

        // 3. 清空之前的事件记录
        listener.clearReceivedEvents();

        // 4. 关闭容器（会触发 ContextClosedEvent）
        context.close();

        // 5. 验证事件被接收
        assertEquals("应该接收到1个容器关闭事件", 1, listener.getReceivedEvents().size());

        // 6. 验证事件源是当前容器
        assertSame("事件源应该是当前容器", context, listener.getReceivedEvents().get(0).getApplicationContext());

        System.out.println("✅ 测试4通过：容器关闭事件");
    }

    /**
     * 测试泛型类型匹配
     * <p>
     * 验证：
     * 1. 监听器只接收匹配类型的事件
     * 2. 不匹配类型的事件不会被接收
     */
    @Test
    public void testGenericTypeMatching() throws Exception {
        // 1. 创建容器
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("event-test.xml");

        // 2. 获取监听器
        EmailNotificationListener emailListener = context.getBean("emailNotificationListener", EmailNotificationListener.class);
        ContextRefreshedListener refreshedListener = context.getBean("contextRefreshedListener", ContextRefreshedListener.class);

        // 3. 清空之前的事件记录
        emailListener.clearReceivedEvents();
        refreshedListener.clearReceivedEvents();

        // 4. 发布 UserRegisteredEvent
        context.publishEvent(new UserRegisteredEvent(this, "测试用户", "test@example.com"));

        // 5. 验证 EmailNotificationListener 接收到事件
        assertEquals("邮件监听器应该接收到1个事件", 1, emailListener.getReceivedEvents().size());

        // 6. 验证 ContextRefreshedListener 没有接收到事件（类型不匹配）
        assertEquals("刷新监听器不应该接收到用户注册事件", 0, refreshedListener.getReceivedEvents().size());

        System.out.println("✅ 测试5通过：泛型类型匹配");

        // 7. 关闭容器
        context.close();
    }

    /**
     * 测试完整的事件生命周期
     * <p>
     * 验证：
     * 1. 容器刷新时触发 ContextRefreshedEvent
     * 2. 自定义事件正常工作
     * 3. 容器关闭时触发 ContextClosedEvent
     */
    @Test
    public void testCompleteEventLifecycle() throws Exception {
        // 1. 创建容器
        System.out.println("\n=== 开始完整事件生命周期测试 ===");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("event-test.xml");

        // 2. 获取所有监听器
        EmailNotificationListener emailListener = context.getBean("emailNotificationListener", EmailNotificationListener.class);
        AuditLogListener auditListener = context.getBean("auditLogListener", AuditLogListener.class);
        ContextRefreshedListener refreshedListener = context.getBean("contextRefreshedListener", ContextRefreshedListener.class);
        ContextClosedListener closedListener = context.getBean("contextClosedListener", ContextClosedListener.class);

        // 3. 验证容器刷新事件
        assertEquals("应该接收到容器刷新事件", 1, refreshedListener.getReceivedEvents().size());

        // 4. 清空自定义事件监听器的记录
        emailListener.clearReceivedEvents();
        auditListener.clearReceivedEvents();

        // 5. 发布自定义事件
        System.out.println("\n--- 发布自定义事件 ---");
        context.publishEvent(new UserRegisteredEvent(this, "完整测试用户", "complete@example.com"));

        // 6. 验证自定义事件
        assertEquals("邮件监听器应该接收到事件", 1, emailListener.getReceivedEvents().size());
        assertEquals("审计监听器应该接收到事件", 1, auditListener.getReceivedEvents().size());

        // 7. 关闭容器
        System.out.println("\n--- 关闭容器 ---");
        context.close();

        // 8. 验证容器关闭事件
        assertEquals("应该接收到容器关闭事件", 1, closedListener.getReceivedEvents().size());

        System.out.println("✅ 测试6通过：完整的事件生命周期");
        System.out.println("=== 完整事件生命周期测试结束 ===\n");
    }
}
