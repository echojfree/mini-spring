package com.minispring.tx;

/**
 * TransactionDefinition - 事务定义接口
 * <p>
 * 定义事务的属性:传播行为、隔离级别、超时时间、只读标记
 * <p>
 * 面试要点:
 * 1. 事务传播行为(7种) - 高频考点
 *    - REQUIRED(默认): 如果当前有事务,加入该事务;如果没有,创建新事务
 *    - REQUIRES_NEW: 创建新事务,如果当前有事务,挂起当前事务
 *    - SUPPORTS: 如果当前有事务,加入该事务;如果没有,以非事务方式执行
 *    - NOT_SUPPORTED: 以非事务方式执行,如果当前有事务,挂起当前事务
 *    - MANDATORY: 如果当前有事务,加入该事务;如果没有,抛出异常
 *    - NEVER: 以非事务方式执行,如果当前有事务,抛出异常
 *    - NESTED: 如果当前有事务,嵌套执行;如果没有,创建新事务
 * <p>
 * 2. 事务隔离级别(4种+1个默认)
 *    - DEFAULT: 使用数据库默认隔离级别(MySQL默认 REPEATABLE_READ)
 *    - READ_UNCOMMITTED: 读未提交(脏读、不可重复读、幻读)
 *    - READ_COMMITTED: 读已提交(不可重复读、幻读)
 *    - REPEATABLE_READ: 可重复读(幻读)
 *    - SERIALIZABLE: 串行化(无并发问题,性能最差)
 * <p>
 * 3. 超时时间
 *    - 事务执行超过指定时间自动回滚
 *    - 单位:秒
 *    - 默认值: -1 (不限制)
 * <p>
 * 4. 只读标记
 *    - true: 只读事务,优化性能
 *    - false: 读写事务
 *
 * @author mini-spring
 */
public interface TransactionDefinition {

    /**
     * 传播行为:REQUIRED
     * 如果当前有事务,加入该事务;如果没有,创建新事务
     * 这是最常用的传播行为
     */
    int PROPAGATION_REQUIRED = 0;

    /**
     * 传播行为:REQUIRES_NEW
     * 创建新事务,如果当前有事务,挂起当前事务
     * 用于独立事务,不受外层事务影响
     */
    int PROPAGATION_REQUIRES_NEW = 3;

    /**
     * 传播行为:SUPPORTS
     * 如果当前有事务,加入该事务;如果没有,以非事务方式执行
     */
    int PROPAGATION_SUPPORTS = 1;

    /**
     * 传播行为:NOT_SUPPORTED
     * 以非事务方式执行,如果当前有事务,挂起当前事务
     */
    int PROPAGATION_NOT_SUPPORTED = 4;

    /**
     * 传播行为:MANDATORY
     * 如果当前有事务,加入该事务;如果没有,抛出异常
     * 强制要求在事务中执行
     */
    int PROPAGATION_MANDATORY = 2;

    /**
     * 传播行为:NEVER
     * 以非事务方式执行,如果当前有事务,抛出异常
     * 强制要求不在事务中执行
     */
    int PROPAGATION_NEVER = 5;

    /**
     * 传播行为:NESTED
     * 如果当前有事务,嵌套执行;如果没有,创建新事务
     * 嵌套事务可以独立回滚,不影响外层事务
     */
    int PROPAGATION_NESTED = 6;

    /**
     * 隔离级别:DEFAULT
     * 使用数据库默认隔离级别
     */
    int ISOLATION_DEFAULT = -1;

    /**
     * 隔离级别:READ_UNCOMMITTED
     * 读未提交,最低隔离级别
     * 问题:脏读、不可重复读、幻读
     */
    int ISOLATION_READ_UNCOMMITTED = 1;

    /**
     * 隔离级别:READ_COMMITTED
     * 读已提交,Oracle 和 SQL Server 默认隔离级别
     * 问题:不可重复读、幻读
     */
    int ISOLATION_READ_COMMITTED = 2;

    /**
     * 隔离级别:REPEATABLE_READ
     * 可重复读,MySQL 默认隔离级别
     * 问题:幻读(MySQL InnoDB 通过 MVCC 解决)
     */
    int ISOLATION_REPEATABLE_READ = 4;

    /**
     * 隔离级别:SERIALIZABLE
     * 串行化,最高隔离级别
     * 无并发问题,但性能最差
     */
    int ISOLATION_SERIALIZABLE = 8;

    /**
     * 默认超时时间:不限制
     */
    int TIMEOUT_DEFAULT = -1;

    /**
     * 获取传播行为
     *
     * @return 传播行为常量
     */
    int getPropagationBehavior();

    /**
     * 获取隔离级别
     *
     * @return 隔离级别常量
     */
    int getIsolationLevel();

    /**
     * 获取超时时间
     *
     * @return 超时时间(秒), -1 表示不限制
     */
    int getTimeout();

    /**
     * 是否只读
     *
     * @return true 表示只读事务, false 表示读写事务
     */
    boolean isReadOnly();

    /**
     * 获取事务名称
     *
     * @return 事务名称
     */
    String getName();

}
