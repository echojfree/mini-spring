package com.minispring.aop;

/**
 * Pointcut - 切点接口
 * <p>
 * AOP 核心概念之一，定义了"在哪里"应用通知
 * 切点 = 类过滤器 + 方法匹配器
 * <p>
 * 设计模式：组合模式
 * <p>
 * 面试要点：
 * 1. 切点的定义
 *    - Pointcut 定义了通知（Advice）应该应用到哪些方法上
 *    - 由 ClassFilter 和 MethodMatcher 组合而成
 *    - 两级过滤提高匹配效率
 * <p>
 * 2. 匹配流程
 *    - 第一步：ClassFilter 过滤类
 *    - 第二步：MethodMatcher 过滤方法
 *    - 只有两者都匹配才会应用通知
 * <p>
 * 3. 切点类型
 *    - NameMatchMethodPointcut：方法名匹配
 *    - AnnotationMatchingPointcut：注解匹配
 *    - AspectJExpressionPointcut：AspectJ 表达式匹配
 *    - ComposablePointcut：组合切点
 * <p>
 * 4. 切点表达式（AspectJ）
 *    - execution：匹配方法执行
 *    - within：匹配特定类型
 *    - this：匹配代理对象类型
 *    - target：匹配目标对象类型
 *    - args：匹配方法参数
 *    - @annotation：匹配方法注解
 *    - @within：匹配类注解
 * <p>
 * 5. 常用切点表达式示例
 *    - execution(* com.example.service.*.*(..))
 *      匹配 service 包下所有类的所有方法
 *    - execution(public * *(..))
 *      匹配所有 public 方法
 *    - @annotation(org.springframework.transaction.annotation.Transactional)
 *      匹配所有带 @Transactional 注解的方法
 *
 * @author mini-spring
 */
public interface Pointcut {

    /**
     * 获取类过滤器
     * <p>
     * 用于在类级别进行第一次过滤
     * 如果类不匹配，则不需要检查该类的方法
     *
     * @return 类过滤器实例
     */
    ClassFilter getClassFilter();

    /**
     * 获取方法匹配器
     * <p>
     * 用于在方法级别进行精确匹配
     * 只有在类过滤器通过后才会调用
     *
     * @return 方法匹配器实例
     */
    MethodMatcher getMethodMatcher();

    /**
     * 匹配所有类和方法的 Pointcut 实例
     * <p>
     * 用于不需要任何过滤的场景
     * 所有类的所有方法都会匹配成功
     * <p>
     * 使用场景：
     * - 测试时临时使用
     * - 特殊的全局拦截器
     */
    Pointcut TRUE = new Pointcut() {
        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return MethodMatcher.TRUE;
        }

        @Override
        public String toString() {
            return "Pointcut.TRUE";
        }
    };

}
