package com.minispring.tx;

/**
 * PlatformTransactionManager - 平台事务管理器接口
 * <p>
 * Spring 事务管理的核心接口,定义了事务的基本操作
 * <p>
 * 设计模式:策略模式、模板方法模式
 * <p>
 * 面试要点:
 * 1. 事务管理器的作用
 *    - 抽象事务操作,屏蔽底层实现差异
 *    - 支持 JDBC、Hibernate、JPA 等不同技术
 *    - 提供统一的事务管理接口
 * <p>
 * 2. 核心方法
 *    - getTransaction(): 获取事务,支持事务传播
 *    - commit(): 提交事务
 *    - rollback(): 回滚事务
 * <p>
 * 3. 常见实现类
 *    - DataSourceTransactionManager: JDBC 事务管理器
 *    - HibernateTransactionManager: Hibernate 事务管理器
 *    - JpaTransactionManager: JPA 事务管理器
 * <p>
 * 4. 与 JDBC Connection 的关系
 *    - DataSourceTransactionManager 管理 JDBC Connection
 *    - Connection 绑定到当前线程(ThreadLocal)
 *    - 同一事务内共享同一个 Connection
 * <p>
 * 5. 编程式事务 vs 声明式事务
 *    - 编程式: 直接调用 TransactionManager 方法
 *    - 声明式: 使用 @Transactional 注解
 *    - 声明式底层也是调用 TransactionManager
 *
 * @author mini-spring
 */
public interface PlatformTransactionManager {

    /**
     * 获取事务
     * <p>
     * 根据事务定义获取当前事务
     * 如果当前没有事务,根据传播行为决定是否创建新事务
     * 如果当前已有事务,根据传播行为决定如何处理
     *
     * @param definition 事务定义(传播行为、隔离级别等)
     * @return 事务状态
     * @throws TransactionException 事务异常
     */
    TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException;

    /**
     * 提交事务
     * <p>
     * 只有新事务才需要提交
     * 如果设置了 rollbackOnly 标记,提交时会抛出异常
     *
     * @param status 事务状态
     * @throws TransactionException 事务异常
     */
    void commit(TransactionStatus status) throws TransactionException;

    /**
     * 回滚事务
     * <p>
     * 只有新事务才需要回滚
     * 如果不是新事务,设置 rollbackOnly 标记
     *
     * @param status 事务状态
     * @throws TransactionException 事务异常
     */
    void rollback(TransactionStatus status) throws TransactionException;

}
