package com.minispring.test.event;

import com.minispring.context.ApplicationEvent;

/**
 * UserRegisteredEvent - 用户注册事件
 * <p>
 * 自定义事件示例，用于演示事件机制的使用
 *
 * @author mini-spring
 */
public class UserRegisteredEvent extends ApplicationEvent {

    private final String username;
    private final String email;

    public UserRegisteredEvent(Object source, String username, String email) {
        super(source);
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UserRegisteredEvent{username='" + username + "', email='" + email + "'}";
    }
}
