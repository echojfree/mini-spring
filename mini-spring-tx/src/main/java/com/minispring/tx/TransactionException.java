package com.minispring.tx;

/**
 * TransactionException - 事务异常基类
 * <p>
 * 所有事务相关异常的基类
 *
 * @author mini-spring
 */
public class TransactionException extends RuntimeException {

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

}
