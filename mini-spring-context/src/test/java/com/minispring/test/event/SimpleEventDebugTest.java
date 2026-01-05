package com.minispring.test.event;

import com.minispring.context.support.ClassPathXmlApplicationContext;

/**
 * 简单的调试测试
 */
public class SimpleEventDebugTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== 创建容器 ===");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("event-test.xml");

        System.out.println("\n=== 获取监听器 ===");
        EmailNotificationListener emailListener = context.getBean("emailNotificationListener", EmailNotificationListener.class);
        System.out.println("邮件监听器: " + emailListener);

        System.out.println("\n=== 发布事件 ===");
        UserRegisteredEvent event = new UserRegisteredEvent("test", "张三", "zhangsan@example.com");
        context.publishEvent(event);

        System.out.println("\n=== 检查结果 ===");
        System.out.println("邮件监听器收到事件数: " + emailListener.getReceivedEvents().size());

        context.close();
    }
}
