package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Component;

/**
 * MySQLDataSource - MySQL 数据源实现
 * <p>
 * 用于测试 @Qualifier 指定具体实现
 */
@Component("mysqlDataSource")
public class MySQLDataSource implements DataSource {

    @Override
    public String getName() {
        return "MySQL DataSource";
    }

    @Override
    public String getConnectionString() {
        return "jdbc:mysql://localhost:3306/test";
    }
}
