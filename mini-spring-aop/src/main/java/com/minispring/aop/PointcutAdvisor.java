package com.minispring.aop;

/**
 * PointcutAdvisor - 带切点的切面接口
 * <p>
 * 最常用的 Advisor 类型，明确定义了切点和通知
 * <p>
 * 面试要点：
 * 1. PointcutAdvisor 的作用
 *    - 明确定义了切点（Pointcut）
 *    - 明确定义了通知（Advice）
 *    - 将切点和通知组合成完整的切面
 * <p>
 * 2. 与普通 Advisor 的区别
 *    - Advisor：只定义了通知，切点默认匹配所有方法
 *    - PointcutAdvisor：明确定义了切点和通知
 * <p>
 * 3. 常见实现类
 *    - DefaultPointcutAdvisor：最简单的实现，直接组合切点和通知
 *    - AspectJPointcutAdvisor：支持 AspectJ 表达式的切面
 *    - RegexpMethodPointcutAdvisor：支持正则表达式的切面
 * <p>
 * 4. 使用方式
 *    - 编程方式：new DefaultPointcutAdvisor(pointcut, advice)
 *    - XML 配置：<aop:advisor pointcut="..." advice-ref="..."/>
 *    - 注解方式：@Aspect + @Before/@After 等
 *
 * @author mini-spring
 */
public interface PointcutAdvisor extends Advisor {

    /**
     * 获取切点
     * <p>
     * 返回此切面的切点
     * 切点定义了通知应该应用到哪些方法上
     *
     * @return 切点实例
     */
    Pointcut getPointcut();

}
