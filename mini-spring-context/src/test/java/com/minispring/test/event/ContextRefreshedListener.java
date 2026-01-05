package com.minispring.test.event;

import com.minispring.context.ApplicationListener;
import com.minispring.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * ContextRefreshedListener - 容器刷新完成监听器
 * <p>
 * 监听容器刷新完成事件
 *
 * @author mini-spring
 */
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    // 记录收到的事件，用于测试验证
    private final List<ContextRefreshedEvent> receivedEvents = new ArrayList<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("容器刷新完成: " + event.getApplicationContext().getClass().getSimpleName());
        receivedEvents.add(event);
    }

    public List<ContextRefreshedEvent> getReceivedEvents() {
        return receivedEvents;
    }

    public void clearReceivedEvents() {
        receivedEvents.clear();
    }
}
