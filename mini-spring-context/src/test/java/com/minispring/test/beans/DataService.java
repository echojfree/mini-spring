package com.minispring.test.beans;

import com.minispring.beans.factory.annotation.Autowired;
import com.minispring.beans.factory.annotation.Component;
import com.minispring.beans.factory.annotation.Qualifier;

/**
 * DataService - 使用 @Qualifier 指定数据源
 * <p>
 * 测试 @Qualifier 字段注入
 */
@Component
public class DataService {

    // 使用 @Qualifier 指定注入 MySQL 数据源
    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    // 使用 @Qualifier 指定注入 PostgreSQL 数据源
    @Autowired
    @Qualifier("postgresDataSource")
    private DataSource postgresDataSource;

    // 不使用 @Qualifier 会导致多个候选 Bean 的歧义（这个字段用于演示问题）
    // @Autowired
    // private DataSource defaultDataSource;  // 这会抛出异常

    private DataSource setterDataSource;

    /**
     * 使用 @Qualifier 在 Setter 方法参数上
     */
    @Autowired
    public void setDataSource(@Qualifier("mysqlDataSource") DataSource dataSource) {
        this.setterDataSource = dataSource;
    }

    public DataSource getMysqlDataSource() {
        return mysqlDataSource;
    }

    public DataSource getPostgresDataSource() {
        return postgresDataSource;
    }

    public DataSource getSetterDataSource() {
        return setterDataSource;
    }

    public void connect() {
        System.out.println("Connecting to MySQL: " + mysqlDataSource.getConnectionString());
        System.out.println("Connecting to PostgreSQL: " + postgresDataSource.getConnectionString());
    }
}
