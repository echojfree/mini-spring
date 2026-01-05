package com.minispring.test.event;

import com.minispring.context.ApplicationListener;
import com.minispring.context.event.ContextClosedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * ContextClosedListener - 容器关闭监听器
 * <p>
 * 监听容器关闭事件
 *
 * @author mini-spring
 */
public class ContextClosedListener implements ApplicationListener<ContextClosedEvent> {

    // 记录收到的事件，用于测试验证
    private final List<ContextClosedEvent> receivedEvents = new ArrayList<>();

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        System.out.println("容器即将关闭: " + event.getApplicationContext().getClass().getSimpleName());
        receivedEvents.add(event);
    }

    public List<ContextClosedEvent> getReceivedEvents() {
        return receivedEvents;
    }

    public void clearReceivedEvents() {
        receivedEvents.clear();
    }
}
