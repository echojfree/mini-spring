package com.minispring.test.event;

import com.minispring.context.ApplicationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * EmailNotificationListener - 邮件通知监听器
 * <p>
 * 监听用户注册事件，发送欢迎邮件
 *
 * @author mini-spring
 */
public class EmailNotificationListener implements ApplicationListener<UserRegisteredEvent> {

    // 记录收到的事件，用于测试验证
    private final List<UserRegisteredEvent> receivedEvents = new ArrayList<>();

    @Override
    public void onApplicationEvent(UserRegisteredEvent event) {
        // 模拟发送邮件
        System.out.println("发送欢迎邮件给: " + event.getUsername() + " (" + event.getEmail() + ")");
        receivedEvents.add(event);
    }

    public List<UserRegisteredEvent> getReceivedEvents() {
        return receivedEvents;
    }

    public void clearReceivedEvents() {
        receivedEvents.clear();
    }
}
