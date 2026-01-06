package com.minispring.tx.support;

import com.minispring.tx.TransactionDefinition;

/**
 * DefaultTransactionDefinition - 默认事务定义实现
 * <p>
 * 提供默认的事务属性配置
 *
 * @author mini-spring
 */
public class DefaultTransactionDefinition implements TransactionDefinition {

    /**
     * 传播行为,默认 REQUIRED
     */
    private int propagationBehavior = PROPAGATION_REQUIRED;

    /**
     * 隔离级别,默认使用数据库默认隔离级别
     */
    private int isolationLevel = ISOLATION_DEFAULT;

    /**
     * 超时时间,默认不限制
     */
    private int timeout = TIMEOUT_DEFAULT;

    /**
     * 是否只读,默认 false
     */
    private boolean readOnly = false;

    /**
     * 事务名称
     */
    private String name;

    public DefaultTransactionDefinition() {
    }

    public DefaultTransactionDefinition(int propagationBehavior) {
        this.propagationBehavior = propagationBehavior;
    }

    @Override
    public int getPropagationBehavior() {
        return propagationBehavior;
    }

    public void setPropagationBehavior(int propagationBehavior) {
        this.propagationBehavior = propagationBehavior;
    }

    @Override
    public int getIsolationLevel() {
        return isolationLevel;
    }

    public void setIsolationLevel(int isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
