package com.minispring.aop;

/**
 * Advice - 通知接口（标记接口）
 * <p>
 * AOP 核心概念之一，定义了"做什么"
 * 通知是切面的具体行为，定义了在切点位置要执行的代码
 * <p>
 * 这是一个标记接口，所有通知类型都继承自此接口
 * <p>
 * 设计模式：策略模式
 * <p>
 * 面试要点：
 * 1. Advice 的类型
 *    - MethodBeforeAdvice：前置通知（方法执行前）
 *    - AfterReturningAdvice：后置通知（方法成功返回后）
 *    - ThrowsAdvice：异常通知（方法抛出异常后）
 *    - MethodInterceptor：环绕通知（方法执行前后）
 *    - AfterAdvice：最终通知（方法执行后，无论成功或异常）
 * <p>
 * 2. 通知执行顺序
 *    - 正常流程：@Around 前置 → @Before → 目标方法 → @Around 后置 → @After → @AfterReturning
 *    - 异常流程：@Around 前置 → @Before → 目标方法 → @AfterThrowing → @After
 * <p>
 * 3. Advice vs Interceptor
 *    - Advice：Spring AOP 概念，定义通知行为
 *    - Interceptor：AOP Alliance 标准接口
 *    - MethodInterceptor 实现了 Interceptor，是环绕通知
 * <p>
 * 4. Advice 的应用场景
 *    - 日志记录：记录方法调用信息
 *    - 性能监控：统计方法执行时间
 *    - 事务管理：开启和提交事务
 *    - 权限检查：验证用户权限
 *    - 缓存管理：缓存方法返回值
 *    - 异常处理：统一异常处理
 * <p>
 * 5. Advice 的实现方式
 *    - 实现相应的通知接口
 *    - 使用 @AspectJ 注解
 *    - 使用 XML 配置
 *
 * @author mini-spring
 */
public interface Advice {
    // 标记接口，无需定义方法
}
