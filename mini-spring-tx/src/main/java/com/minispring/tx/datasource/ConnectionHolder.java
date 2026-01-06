package com.minispring.tx.datasource;

import java.sql.Connection;

/**
 * ConnectionHolder - Connection 持有者
 * <p>
 * 封装 JDBC Connection,保存事务相关信息
 *
 * @author mini-spring
 */
public class ConnectionHolder {

    /**
     * JDBC Connection
     */
    private final Connection connection;

    /**
     * 原始隔离级别
     * 用于事务结束后恢复
     */
    private Integer isolationLevel;

    public ConnectionHolder(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public Integer getIsolationLevel() {
        return isolationLevel;
    }

    public void setIsolationLevel(Integer isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

}
