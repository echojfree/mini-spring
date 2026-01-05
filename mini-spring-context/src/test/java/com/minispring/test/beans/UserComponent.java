package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Component;

/**
 * 测试用 Component
 */
@Component
public class UserComponent {
    public String getName() {
        return "UserComponent";
    }
}
