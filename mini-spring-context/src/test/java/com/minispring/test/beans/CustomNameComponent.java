package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Component;

/**
 * 指定名称的 Component
 */
@Component("myComponent")
public class CustomNameComponent {
    public String getName() {
        return "CustomNameComponent";
    }
}
