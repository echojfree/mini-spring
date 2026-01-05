package com.minispring.test.beans;

/**
 * DataSource 接口 - 用于测试 @Qualifier
 * <p>
 * 模拟数据源接口，有多个实现类
 */
public interface DataSource {

    /**
     * 获取数据源名称
     *
     * @return 数据源名称
     */
    String getName();

    /**
     * 获取连接字符串
     *
     * @return 连接字符串
     */
    String getConnectionString();
}
