package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Component;

/**
 * PostgresDataSource - PostgreSQL 数据源实现
 * <p>
 * 用于测试 @Qualifier 指定具体实现
 */
@Component("postgresDataSource")
public class PostgresDataSource implements DataSource {

    @Override
    public String getName() {
        return "PostgreSQL DataSource";
    }

    @Override
    public String getConnectionString() {
        return "jdbc:postgresql://localhost:5432/test";
    }
}
