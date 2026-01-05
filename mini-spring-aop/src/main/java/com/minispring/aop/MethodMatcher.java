package com.minispring.aop;

import java.lang.reflect.Method;

/**
 * MethodMatcher - 方法匹配器接口
 * <p>
 * AOP 切点的方法级别匹配器
 * 判断一个方法是否匹配切点条件
 * <p>
 * 设计模式：策略模式
 * <p>
 * 面试要点：
 * 1. MethodMatcher 的作用
 *    - 在方法级别进行精确匹配
 *    - 支持方法名、参数、返回值、注解等匹配
 *    - 配合 ClassFilter 实现两级过滤
 * <p>
 * 2. 匹配策略
 *    - 基于方法名匹配
 *    - 基于方法参数匹配
 *    - 基于方法注解匹配
 *    - 基于返回值类型匹配
 * <p>
 * 3. 静态匹配 vs 动态匹配
 *    - 静态匹配：只看方法签名，不看运行时参数值
 *    - 动态匹配：考虑运行时参数值
 *    - isRuntime() 返回 false 表示只需要静态匹配
 * <p>
 * 4. 常用实现
 *    - TrueMethodMatcher：匹配所有方法
 *    - AnnotationMethodMatcher：匹配带特定注解的方法
 *    - AspectJExpressionPointcut：AspectJ 表达式匹配
 *
 * @author mini-spring
 */
public interface MethodMatcher {

    /**
     * 执行静态匹配检查
     * <p>
     * 只考虑方法的静态信息（方法名、参数类型、返回值类型、注解等）
     * 不考虑运行时参数值
     * <p>
     * 这个方法会被频繁调用，应该尽可能高效
     * 如果这个方法返回 false，则不需要调用运行时匹配
     *
     * @param method      目标方法
     * @param targetClass 目标类
     * @return true 如果方法匹配，否则返回 false
     */
    boolean matches(Method method, Class<?> targetClass);

    /**
     * 是否需要运行时匹配
     * <p>
     * 返回 true 表示需要考虑方法的运行时参数值
     * 返回 false 表示只需要静态匹配（性能更好）
     * <p>
     * 大多数情况下返回 false 即可
     * 只有需要根据参数值做判断时才返回 true
     * <p>
     * 面试考点：
     * - isRuntime() 返回 true 时，会额外调用3参数的 matches 方法
     * - 这会带来性能开销，只在必要时使用
     *
     * @return true 如果需要运行时匹配，否则返回 false
     */
    boolean isRuntime();

    /**
     * 执行运行时匹配检查
     * <p>
     * 只有在 isRuntime() 返回 true 时才会调用
     * 可以根据实际的参数值进行匹配判断
     * <p>
     * 注意：这个方法在每次方法调用时都会执行，性能开销较大
     *
     * @param method      目标方法
     * @param targetClass 目标类
     * @param args        方法的实际参数值
     * @return true 如果方法和参数匹配，否则返回 false
     */
    boolean matches(Method method, Class<?> targetClass, Object[] args);

    /**
     * 匹配所有方法的 MethodMatcher 实例
     * <p>
     * 用于不需要方法级别过滤的场景
     * 所有方法都会匹配成功
     */
    MethodMatcher TRUE = new MethodMatcher() {
        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return true;
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object[] args) {
            throw new UnsupportedOperationException("不支持运行时匹配");
        }
    };

}
