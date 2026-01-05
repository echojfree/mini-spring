package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Component;
import com.minispring.beans.factory.annotation.Value;

/**
 * ServerConfig - 测试 @Value 注解和默认值
 */
@Component
public class ServerConfig {

    @Value("${server.port}")
    private int port;

    @Value("${server.host:0.0.0.0}")  // 带默认值
    private String host;

    @Value("${server.timeout}")
    private long timeout;

    @Value("${server.maxConnections:100}")  // 不存在的属性，使用默认值
    private int maxConnections;

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public long getTimeout() {
        return timeout;
    }

    public int getMaxConnections() {
        return maxConnections;
    }
}
