package com.minispring.tx;

/**
 * TransactionStatus - 事务状态接口
 * <p>
 * 表示事务的状态,提供控制事务执行和查询事务状态的方法
 * <p>
 * 面试要点:
 * 1. 事务状态的作用
 *    - 查询事务状态(是否新事务、是否已完成)
 *    - 控制事务执行(设置只回滚标记)
 *    - 事务管理器通过此接口管理事务生命周期
 * <p>
 * 2. 核心方法
 *    - isNewTransaction(): 是否是新事务
 *    - isCompleted(): 事务是否已完成
 *    - setRollbackOnly(): 设置只回滚标记
 *    - isRollbackOnly(): 是否只回滚
 * <p>
 * 3. 使用场景
 *    - 编程式事务中获取事务状态
 *    - 声明式事务中传递事务状态
 *    - 事务拦截器中控制事务提交/回滚
 *
 * @author mini-spring
 */
public interface TransactionStatus {

    /**
     * 是否是新事务
     * <p>
     * 如果是新事务,在方法结束时需要提交或回滚
     * 如果不是新事务,说明加入了已存在的事务
     *
     * @return true 表示新事务, false 表示加入已存在的事务
     */
    boolean isNewTransaction();

    /**
     * 是否已完成
     * <p>
     * 事务提交或回滚后,状态变为已完成
     *
     * @return true 表示已完成, false 表示进行中
     */
    boolean isCompleted();

    /**
     * 设置只回滚标记
     * <p>
     * 设置此标记后,事务只能回滚,不能提交
     * 通常在业务异常时设置此标记
     */
    void setRollbackOnly();

    /**
     * 是否只回滚
     * <p>
     * 检查是否设置了只回滚标记
     *
     * @return true 表示只能回滚, false 表示可以提交
     */
    boolean isRollbackOnly();

}
