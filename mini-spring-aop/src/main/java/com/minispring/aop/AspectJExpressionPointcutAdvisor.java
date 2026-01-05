package com.minispring.aop;

import java.io.Serializable;

/**
 * AspectJExpressionPointcutAdvisor - 基于 AspectJ 表达式的切面
 * <p>
 * 实现了 PointcutAdvisor 接口，支持 AspectJ 表达式切点
 * <p>
 * 这是最常用的 Advisor 实现，配合 AspectJExpressionPointcut 使用
 * <p>
 * 面试要点：
 * 1. AspectJ 表达式支持
 *    - 支持 execution、within、args 等表达式
 *    - 提供灵活的切点定义方式
 *    - 兼容 Spring AOP 和 AspectJ
 * <p>
 * 2. 使用方式
 *    - 设置 expression：定义切点表达式
 *    - 设置 advice：定义通知行为
 *    - 自动创建 AspectJExpressionPointcut
 * <p>
 * 示例：
 * <pre>
 * {@code
 * AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
 * advisor.setExpression("execution(* com.example.service.*.*(..))");
 * advisor.setAdvice(new MyMethodInterceptor());
 * }
 * </pre>
 *
 * @author mini-spring
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor, Serializable {

    /**
     * AspectJ 切点表达式
     */
    private String expression;

    /**
     * 切点实例
     */
    private AspectJExpressionPointcut pointcut;

    /**
     * 通知实例
     */
    private Advice advice;

    /**
     * 设置切点表达式
     *
     * @param expression AspectJ 表达式
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * 获取切点表达式
     *
     * @return AspectJ 表达式
     */
    public String getExpression() {
        return expression;
    }

    /**
     * 设置通知
     *
     * @param advice 通知实例
     */
    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public Pointcut getPointcut() {
        if (pointcut == null) {
            pointcut = new AspectJExpressionPointcut(expression);
        }
        return pointcut;
    }

}
