package com.minispring.aop;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * AspectJExpressionPointcut - 基于 AspectJ 表达式的切点
 * <p>
 * 实现了 Pointcut、ClassFilter 和 MethodMatcher 接口
 * 支持 AspectJ 表达式语法进行切点定义
 * <p>
 * 面试要点：
 * 1. AspectJ 表达式支持
 *    - execution：匹配方法执行
 *    - within：匹配特定类型
 *    - args：匹配方法参数
 *    - this：匹配代理对象类型
 *    - target：匹配目标对象类型
 *    - @annotation：匹配方法注解
 *    - @within：匹配类注解
 * <p>
 * 2. execution 表达式语法
 *    - execution(修饰符? 返回值 包名.类名.方法名(参数) 异常?)
 *    - *：匹配任意字符
 *    - ..：匹配任意参数或包路径
 *    - 示例：execution(* com.example.service.*.*(..))
 * <p>
 * 3. 匹配流程
 *    - 使用 AspectJ Weaver 库解析表达式
 *    - ClassFilter：匹配类
 *    - MethodMatcher：匹配方法
 *    - 两级过滤提高性能
 * <p>
 * 4. 性能优化
 *    - 表达式解析结果会被缓存
 *    - 类过滤在前，方法匹配在后
 *    - 只进行静态匹配，不支持运行时匹配
 * <p>
 * 示例表达式：
 * <pre>
 * // 匹配 service 包下所有类的所有方法
 * execution(* com.example.service.*.*(..))
 *
 * // 匹配所有 public 方法
 * execution(public * *(..))
 *
 * // 匹配返回值为 String 的方法
 * execution(String *(..))
 *
 * // 匹配带 @Transactional 注解的方法
 * @annotation(org.springframework.transaction.annotation.Transactional)
 * </pre>
 *
 * @author mini-spring
 */
public class AspectJExpressionPointcut implements Pointcut, ClassFilter, MethodMatcher {

    /**
     * 支持的切点原语类型
     */
    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
    }

    /**
     * AspectJ 表达式字符串
     */
    private final String expression;

    /**
     * AspectJ 切点表达式对象
     */
    private PointcutExpression pointcutExpression;

    /**
     * 构造函数
     *
     * @param expression AspectJ 表达式
     */
    public AspectJExpressionPointcut(String expression) {
        this.expression = expression;
        // 创建切点解析器
        PointcutParser parser = PointcutParser
                .getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
                        SUPPORTED_PRIMITIVES,
                        this.getClass().getClassLoader()
                );
        // 解析表达式
        this.pointcutExpression = parser.parsePointcutExpression(expression);
    }

    /**
     * 获取表达式字符串
     *
     * @return AspectJ 表达式
     */
    public String getExpression() {
        return expression;
    }

    // ==================== Pointcut 接口实现 ====================

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }

    // ==================== ClassFilter 接口实现 ====================

    @Override
    public boolean matches(Class<?> clazz) {
        // 使用 AspectJ 的 couldMatchJoinPointsInType 方法判断类是否匹配
        return pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    // ==================== MethodMatcher 接口实现 ====================

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        // 使用 AspectJ 的 matchesMethodExecution 方法判断方法是否匹配
        return pointcutExpression.matchesMethodExecution(method).alwaysMatches();
    }

    @Override
    public boolean isRuntime() {
        // 不支持运行时匹配，只进行静态匹配
        return false;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass, Object[] args) {
        // 不支持运行时匹配
        throw new UnsupportedOperationException("不支持运行时匹配");
    }

    @Override
    public String toString() {
        return "AspectJExpressionPointcut{" +
                "expression='" + expression + '\'' +
                '}';
    }

}
