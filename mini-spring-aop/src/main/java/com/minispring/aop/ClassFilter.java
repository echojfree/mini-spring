package com.minispring.aop;

/**
 * ClassFilter - 类过滤器接口
 * <p>
 * AOP 切点的类级别过滤器
 * 判断一个类是否匹配切点条件
 * <p>
 * 设计模式：策略模式
 * <p>
 * 面试要点：
 * 1. ClassFilter 的作用
 *    - 在类级别进行第一次过滤
 *    - 提高匹配效率（先过滤类再过滤方法）
 *    - 支持包名、类名、注解等多种匹配方式
 * <p>
 * 2. 匹配策略
 *    - 基于类名匹配
 *    - 基于包名匹配
 *    - 基于注解匹配
 *    - 基于继承关系匹配
 * <p>
 * 3. 常用实现
 *    - TrueClassFilter：匹配所有类
 *    - AnnotationClassFilter：匹配带特定注解的类
 *    - AspectJExpressionPointcut：AspectJ 表达式匹配
 *
 * @author mini-spring
 */
@FunctionalInterface
public interface ClassFilter {

    /**
     * 判断给定的类是否匹配
     * <p>
     * 判断逻辑示例：
     * - 类名匹配：clazz.getName().matches("com.example.service.*")
     * - 注解匹配：clazz.isAnnotationPresent(Service.class)
     * - 包名匹配：clazz.getPackage().getName().startsWith("com.example")
     *
     * @param clazz 目标类
     * @return true 如果类匹配，否则返回 false
     */
    boolean matches(Class<?> clazz);

    /**
     * 匹配所有类的 ClassFilter 实例
     * <p>
     * 用于不需要类级别过滤的场景
     * 所有类都会通过，然后在方法级别进行过滤
     */
    ClassFilter TRUE = clazz -> true;

}
