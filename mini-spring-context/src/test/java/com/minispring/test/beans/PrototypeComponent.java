package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Component;
import com.minispring.beans.factory.annotation.Scope;

/**
 * Prototype 作用域的 Component
 */
@Component
@Scope("prototype")
public class PrototypeComponent {
    public String getName() {
        return "PrototypeComponent";
    }
}
