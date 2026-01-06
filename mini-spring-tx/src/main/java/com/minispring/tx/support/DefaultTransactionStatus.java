package com.minispring.tx.support;

import com.minispring.tx.TransactionStatus;

/**
 * DefaultTransactionStatus - 默认事务状态实现
 * <p>
 * 保存事务状态信息
 *
 * @author mini-spring
 */
public class DefaultTransactionStatus implements TransactionStatus {

    /**
     * 事务对象
     */
    private final Object transaction;

    /**
     * 是否是新事务
     */
    private final boolean newTransaction;

    /**
     * 是否只回滚
     */
    private boolean rollbackOnly = false;

    /**
     * 是否已完成
     */
    private boolean completed = false;

    public DefaultTransactionStatus(Object transaction, boolean newTransaction) {
        this.transaction = transaction;
        this.newTransaction = newTransaction;
    }

    @Override
    public boolean isNewTransaction() {
        return newTransaction;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void setRollbackOnly() {
        this.rollbackOnly = true;
    }

    @Override
    public boolean isRollbackOnly() {
        return rollbackOnly;
    }

    /**
     * 设置已完成
     */
    public void setCompleted() {
        this.completed = true;
    }

    /**
     * 获取事务对象
     */
    public Object getTransaction() {
        return transaction;
    }

}
