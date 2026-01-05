package com.minispring.aop;

/**
 * Advisor - 切面接口
 * <p>
 * AOP 核心概念之一，定义了完整的切面
 * Advisor = Pointcut（在哪里） + Advice（做什么）
 * <p>
 * 设计模式：组合模式、策略模式
 * <p>
 * 面试要点：
 * 1. Advisor 的定义
 *    - 切面是切点和通知的组合
 *    - 切点定义了"在哪里"应用通知
 *    - 通知定义了"做什么"
 *    - Advisor 将二者组合起来形成完整的切面
 * <p>
 * 2. Advisor 类型
 *    - PointcutAdvisor：带切点的切面（最常用）
 *    - IntroductionAdvisor：引介切面（为类添加新的接口）
 *    - 默认 Advisor：应用到所有方法
 * <p>
 * 3. Advisor vs Aspect
 *    - Advisor：Spring AOP 概念，一个切点+一个通知
 *    - Aspect：AspectJ 概念，可以包含多个切点和通知
 *    - Spring AOP 中的一个 Aspect 会被拆分成多个 Advisor
 * <p>
 * 4. Advisor 的执行流程
 *    - 1. 使用 Pointcut 检查是否匹配目标类和方法
 *    - 2. 如果匹配，应用 Advice
 *    - 3. 多个 Advisor 形成拦截器链
 *    - 4. 按顺序执行拦截器链
 * <p>
 * 5. Advisor 的排序
 *    - 实现 Ordered 接口
 *    - @Order 注解
 *    - 数字越小，优先级越高
 *    - 默认优先级为 Ordered.LOWEST_PRECEDENCE
 * <p>
 * 6. 使用场景
 *    - 声明式事务：@Transactional → TransactionAttributeSourceAdvisor
 *    - 缓存管理：@Cacheable → CacheOperationSourceAdvisor
 *    - 异步执行：@Async → AsyncAnnotationAdvisor
 *    - 重试机制：@Retryable → RetryOperationInterceptor
 *
 * @author mini-spring
 */
public interface Advisor {

    /**
     * 获取通知
     * <p>
     * 返回此切面要执行的通知
     * 通知定义了在切点位置要执行的代码
     *
     * @return 通知实例
     */
    Advice getAdvice();

    /**
     * 是否是单例
     * <p>
     * 默认返回 true，表示 Advisor 是单例的
     * 大多数情况下 Advisor 都是无状态的，可以共享
     *
     * @return true 如果是单例，否则返回 false
     */
    default boolean isPerInstance() {
        return false;
    }

}
