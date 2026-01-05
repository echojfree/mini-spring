package com.minispring.aop;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * MethodInterceptor - 方法拦截器（环绕通知）
 * <p>
 * 这是 AOP Alliance 标准接口
 * 提供了最强大和灵活的通知类型，可以控制方法执行前后的所有行为
 * <p>
 * 注意：这个接口来自 org.aopalliance.intercept 包
 * 我们直接使用 AOP Alliance 的标准接口，确保兼容性
 * <p>
 * 设计模式：责任链模式、装饰器模式
 * <p>
 * 面试要点：
 * 1. MethodInterceptor 的特点
 *    - 最强大的通知类型
 *    - 可以在方法执行前后添加逻辑
 *    - 可以决定是否执行目标方法
 *    - 可以修改方法参数和返回值
 *    - 可以捕获和处理异常
 * <p>
 * 2. 与其他通知类型的对比
 *    - 前置通知：只在方法执行前执行
 *    - 后置通知：只在方法执行后执行
 *    - 异常通知：只在方法抛出异常时执行
 *    - 环绕通知：可以包含以上所有功能
 * <p>
 * 3. 使用场景
 *    - 事务管理：开启事务、提交/回滚事务
 *    - 性能监控：记录方法执行时间
 *    - 缓存管理：检查缓存、更新缓存
 *    - 异常处理：统一异常处理和转换
 *    - 重试机制：方法执行失败时自动重试
 * <p>
 * 4. MethodInvocation
 *    - 封装了目标方法的调用信息
 *    - proceed()：调用目标方法或下一个拦截器
 *    - getMethod()：获取目标方法
 *    - getArguments()：获取方法参数
 *    - getThis()：获取目标对象
 * <p>
 * 示例：
 * <pre>
 * {@code
 * public class PerformanceInterceptor implements MethodInterceptor {
 *     @Override
 *     public Object invoke(MethodInvocation invocation) throws Throwable {
 *         long start = System.currentTimeMillis();
 *         try {
 *             // 执行目标方法
 *             return invocation.proceed();
 *         } finally {
 *             long time = System.currentTimeMillis() - start;
 *             System.out.println(invocation.getMethod().getName() + " 耗时: " + time + "ms");
 *         }
 *     }
 * }
 * }
 * </pre>
 *
 * @author mini-spring
 * @see org.aopalliance.intercept.MethodInterceptor
 * @see org.aopalliance.intercept.MethodInvocation
 */
public interface BeforeAdviceInterceptor extends MethodInterceptor {
    // 这个接口只是为了标记，实际使用的是 org.aopalliance.intercept.MethodInterceptor
    // 我们会在后续创建适配器来转换各种 Advice 到 MethodInterceptor
}
