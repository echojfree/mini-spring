package com.minispring.beans.exception;

/**
 * Bean 异常基类
 * <p>
 * 所有与 Bean 相关的异常都继承自此类
 * 这是一个运行时异常（RuntimeException），不需要强制捕获
 *
 * @author mini-spring
 */
public class BeansException extends RuntimeException {

    /**
     * 构造函数
     *
     * @param message 异常信息
     */
    public BeansException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message 异常信息
     * @param cause   原因异常
     */
    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }

}
