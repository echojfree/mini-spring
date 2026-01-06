package com.minispring.tx.interceptor;

import com.minispring.tx.PlatformTransactionManager;
import com.minispring.tx.TransactionDefinition;
import com.minispring.tx.TransactionStatus;
import com.minispring.tx.annotation.Transactional;
import com.minispring.tx.support.DefaultTransactionDefinition;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * TransactionInterceptor - 事务拦截器
 * <p>
 * 拦截带 @Transactional 注解的方法,管理事务生命周期
 * <p>
 * 设计模式:拦截器模式、模板方法模式
 * <p>
 * 面试要点:
 * 1. 声明式事务实现原理
 *    - AOP 拦截器实现
 *    - 方法执行前:开启事务
 *    - 方法正常返回:提交事务
 *    - 方法抛出异常:回滚事务
 * <p>
 * 2. 事务拦截器执行流程
 *    - 1. 读取 @Transactional 注解配置
 *    - 2. 创建 TransactionDefinition
 *    - 3. 开启事务:transactionManager.getTransaction()
 *    - 4. 执行目标方法:invocation.proceed()
 *    - 5. 正常返回:transactionManager.commit()
 *    - 6. 异常捕获:transactionManager.rollback()
 * <p>
 * 3. 异常回滚规则
 *    - RuntimeException: 默认回滚
 *    - Error: 默认回滚
 *    - CheckedException: 默认不回滚(可通过 rollbackFor 配置)
 * <p>
 * 4. 与 AOP 的集成
 *    - TransactionInterceptor 实现 MethodInterceptor
 *    - 通过 AOP 自动代理机制织入
 *    - 支持多个拦截器组合
 *
 * @author mini-spring
 */
public class TransactionInterceptor implements MethodInterceptor {

    /**
     * 事务管理器
     */
    private final PlatformTransactionManager transactionManager;

    public TransactionInterceptor(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 1. 获取目标方法
        Method method = invocation.getMethod();

        // 2. 读取 @Transactional 注解
        Transactional transactional = method.getAnnotation(Transactional.class);
        if (transactional == null) {
            // 如果方法上没有,检查类上是否有
            transactional = invocation.getThis().getClass().getAnnotation(Transactional.class);
        }

        if (transactional == null) {
            // 没有 @Transactional 注解,直接执行
            return invocation.proceed();
        }

        // 3. 创建事务定义
        TransactionDefinition definition = createTransactionDefinition(transactional);

        // 4. 开启事务
        TransactionStatus status = transactionManager.getTransaction(definition);

        try {
            // 5. 执行目标方法
            Object result = invocation.proceed();

            // 6. 提交事务
            transactionManager.commit(status);

            return result;

        } catch (Throwable ex) {
            // 7. 回滚事务
            completeTransactionAfterThrowing(status, ex, transactional);
            throw ex;
        }
    }

    /**
     * 创建事务定义
     *
     * @param transactional @Transactional 注解
     * @return 事务定义
     */
    private TransactionDefinition createTransactionDefinition(Transactional transactional) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(transactional.propagation());
        definition.setIsolationLevel(transactional.isolation());
        definition.setTimeout(transactional.timeout());
        definition.setReadOnly(transactional.readOnly());
        return definition;
    }

    /**
     * 异常后完成事务
     *
     * @param status        事务状态
     * @param ex            异常
     * @param transactional @Transactional 注解
     */
    private void completeTransactionAfterThrowing(TransactionStatus status, Throwable ex, Transactional transactional) {
        // 判断是否需要回滚
        if (shouldRollback(ex, transactional)) {
            transactionManager.rollback(status);
        } else {
            // 不回滚,提交事务
            transactionManager.commit(status);
        }
    }

    /**
     * 判断是否需要回滚
     *
     * @param ex            异常
     * @param transactional @Transactional 注解
     * @return true 表示需要回滚, false 表示不回滚
     */
    private boolean shouldRollback(Throwable ex, Transactional transactional) {
        // 1. 检查 noRollbackFor
        Class<? extends Throwable>[] noRollbackFor = transactional.noRollbackFor();
        for (Class<? extends Throwable> exceptionClass : noRollbackFor) {
            if (exceptionClass.isInstance(ex)) {
                return false;
            }
        }

        // 2. 检查 rollbackFor
        Class<? extends Throwable>[] rollbackFor = transactional.rollbackFor();
        if (rollbackFor.length > 0) {
            for (Class<? extends Throwable> exceptionClass : rollbackFor) {
                if (exceptionClass.isInstance(ex)) {
                    return true;
                }
            }
            return false;
        }

        // 3. 默认规则: RuntimeException 和 Error 回滚
        return (ex instanceof RuntimeException || ex instanceof Error);
    }

}
