package com.minispring.tx.datasource;

import com.minispring.tx.PlatformTransactionManager;
import com.minispring.tx.TransactionDefinition;
import com.minispring.tx.TransactionException;
import com.minispring.tx.TransactionStatus;
import com.minispring.tx.support.DefaultTransactionStatus;
import com.minispring.tx.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DataSourceTransactionManager - 数据源事务管理器
 * <p>
 * 基于 JDBC DataSource 的事务管理器实现
 * <p>
 * 设计模式:策略模式、模板方法模式
 * <p>
 * 面试要点:
 * 1. JDBC 事务管理原理
 *    - Connection.setAutoCommit(false) 开启事务
 *    - Connection.commit() 提交事务
 *    - Connection.rollback() 回滚事务
 *    - Connection.close() 关闭连接
 * <p>
 * 2. Connection 如何与线程绑定
 *    - TransactionSynchronizationManager 使用 ThreadLocal
 *    - 绑定: bindResource(dataSource, connection)
 *    - 获取: getResource(dataSource)
 *    - 解绑: unbindResource(dataSource)
 * <p>
 * 3. 事务传播行为实现
 *    - REQUIRED: 如果有绑定的 Connection,复用;否则创建新的
 *    - REQUIRES_NEW: 挂起当前事务,创建新的 Connection
 *    - 本实现仅支持 REQUIRED
 * <p>
 * 4. 事务隔离级别设置
 *    - Connection.setTransactionIsolation()
 *    - 在开启事务前设置
 * <p>
 * 5. 异常处理
 *    - RuntimeException: 自动回滚
 *    - CheckedException: 默认提交(可配置)
 *    - 手动设置 rollbackOnly: 强制回滚
 *
 * @author mini-spring
 */
public class DataSourceTransactionManager implements PlatformTransactionManager {

    /**
     * 数据源
     */
    private final DataSource dataSource;

    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        // 1. 检查当前线程是否已有事务
        ConnectionHolder holder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);

        if (holder != null) {
            // 已有事务,加入当前事务(传播行为 REQUIRED)
            return new DefaultTransactionStatus(holder, false);
        }

        // 2. 没有事务,创建新事务
        try {
            // 获取数据库连接
            Connection connection = dataSource.getConnection();

            // 创建 ConnectionHolder
            holder = new ConnectionHolder(connection);

            // 设置隔离级别
            if (definition != null && definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
                holder.setIsolationLevel(connection.getTransactionIsolation());
                connection.setTransactionIsolation(definition.getIsolationLevel());
            }

            // 开启事务
            connection.setAutoCommit(false);

            // 绑定到当前线程
            TransactionSynchronizationManager.bindResource(dataSource, holder);

            // 返回新事务状态
            return new DefaultTransactionStatus(holder, true);

        } catch (SQLException e) {
            throw new TransactionException("Could not open JDBC Connection for transaction", e);
        }
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        if (status.isCompleted()) {
            throw new IllegalStateException("Transaction is already completed");
        }

        DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
        ConnectionHolder holder = (ConnectionHolder) defStatus.getTransaction();

        // 检查是否只回滚
        if (status.isRollbackOnly()) {
            rollback(status);
            return;
        }

        // 只有新事务才需要提交
        if (status.isNewTransaction()) {
            try {
                holder.getConnection().commit();
            } catch (SQLException e) {
                throw new TransactionException("Could not commit JDBC transaction", e);
            } finally {
                cleanupAfterCompletion(holder);
                defStatus.setCompleted();
            }
        }
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        if (status.isCompleted()) {
            throw new IllegalStateException("Transaction is already completed");
        }

        DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
        ConnectionHolder holder = (ConnectionHolder) defStatus.getTransaction();

        // 只有新事务才需要回滚
        if (status.isNewTransaction()) {
            try {
                holder.getConnection().rollback();
            } catch (SQLException e) {
                throw new TransactionException("Could not rollback JDBC transaction", e);
            } finally {
                cleanupAfterCompletion(holder);
                defStatus.setCompleted();
            }
        } else {
            // 不是新事务,设置只回滚标记
            status.setRollbackOnly();
        }
    }

    /**
     * 清理事务后的资源
     *
     * @param holder Connection 持有者
     */
    private void cleanupAfterCompletion(ConnectionHolder holder) {
        // 解绑资源
        TransactionSynchronizationManager.unbindResource(dataSource);

        // 恢复隔离级别
        if (holder.getIsolationLevel() != null) {
            try {
                holder.getConnection().setTransactionIsolation(holder.getIsolationLevel());
            } catch (SQLException e) {
                // 忽略异常
            }
        }

        // 恢复自动提交
        try {
            holder.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            // 忽略异常
        }

        // 关闭连接
        try {
            holder.getConnection().close();
        } catch (SQLException e) {
            // 忽略异常
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

}
