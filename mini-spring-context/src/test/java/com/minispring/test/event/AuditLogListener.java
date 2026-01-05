package com.minispring.test.event;

import com.minispring.context.ApplicationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * AuditLogListener - 审计日志监听器
 * <p>
 * 监听用户注册事件，记录审计日志
 *
 * @author mini-spring
 */
public class AuditLogListener implements ApplicationListener<UserRegisteredEvent> {

    // 记录收到的事件，用于测试验证
    private final List<UserRegisteredEvent> receivedEvents = new ArrayList<>();

    @Override
    public void onApplicationEvent(UserRegisteredEvent event) {
        // 模拟记录审计日志
        System.out.println("记录审计日志: 用户 " + event.getUsername() + " 注册成功");
        receivedEvents.add(event);
    }

    public List<UserRegisteredEvent> getReceivedEvents() {
        return receivedEvents;
    }

    public void clearReceivedEvents() {
        receivedEvents.clear();
    }
}
