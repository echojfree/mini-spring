package com.minispring.tx.annotation;

import com.minispring.tx.TransactionDefinition;

import java.lang.annotation.*;

/**
 * Transactional - 声明式事务注解
 * <p>
 * 标记在类或方法上,表示该方法需要事务支持
 * <p>
 * 面试要点:
 * 1. @Transactional 原理
 *    - AOP 实现,通过拦截器在方法前后管理事务
 *    - TransactionInterceptor 拦截方法调用
 *    - 方法执行前开启事务,执行后提交,异常时回滚
 * <p>
 * 2. @Transactional 失效场景(高频考点)
 *    - 非 public 方法
 *    - 同类调用(绕过代理)
 *    - 异常被捕获
 *    - 数据库不支持事务(如 MyISAM)
 *    - 未启用事务管理
 * <p>
 * 3. 传播行为配置
 *    - propagation: 设置传播行为
 *    - isolation: 设置隔离级别
 *    - timeout: 设置超时时间
 *    - readOnly: 设置只读标记
 *    - rollbackFor: 指定回滚异常
 * <p>
 * 4. 异常回滚规则
 *    - RuntimeException: 默认回滚
 *    - CheckedException: 默认不回滚
 *    - rollbackFor: 指定回滚的异常类型
 *    - noRollbackFor: 指定不回滚的异常类型
 *
 * @author mini-spring
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transactional {

    /**
     * 传播行为
     * 默认 REQUIRED
     */
    int propagation() default TransactionDefinition.PROPAGATION_REQUIRED;

    /**
     * 隔离级别
     * 默认使用数据库默认隔离级别
     */
    int isolation() default TransactionDefinition.ISOLATION_DEFAULT;

    /**
     * 超时时间(秒)
     * 默认不限制
     */
    int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

    /**
     * 是否只读
     * 默认 false
     */
    boolean readOnly() default false;

    /**
     * 需要回滚的异常类型
     * 默认 RuntimeException
     */
    Class<? extends Throwable>[] rollbackFor() default {};

    /**
     * 不需要回滚的异常类型
     */
    Class<? extends Throwable>[] noRollbackFor() default {};

}
