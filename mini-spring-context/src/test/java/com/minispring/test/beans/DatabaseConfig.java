package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Component;
import com.minispring.beans.factory.annotation.Value;

/**
 * DatabaseConfig - 测试 @Value 注解
 */
@Component
public class DatabaseConfig {

    @Value("${db.url}")
    private String url;

    @Value("${db.username}")
    private String username;

    @Value("${db.password:default_password}")  // 带默认值
    private String password;

    @Value("${db.driver}")
    private String driver;

    @Value("3306")  // 字面值
    private int port;

    @Value("true")  // boolean 字面值
    private boolean enabled;

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }

    public int getPort() {
        return port;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
