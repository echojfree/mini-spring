package com.minispring.aop.aspectj;

import com.minispring.aop.Advice;
import com.minispring.aop.MethodMatcher;
import com.minispring.aop.Pointcut;
import com.minispring.aop.PointcutAdvisor;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * AspectJPointcutAdvisor - AspectJ 切点顾问
 * <p>
 * 组合切点和通知,表示一个完整的切面
 * <p>
 * 面试要点:
 * 1. Advisor = Pointcut + Advice
 * 2. Pointcut 决定在哪里织入通知
 * 3. Advice 决定织入什么逻辑
 * 4. 一个切面可以包含多个 Advisor
 *
 * @author mini-spring
 */
public class AspectJPointcutAdvisor implements PointcutAdvisor {

    /**
     * 切点
     */
    private final Pointcut pointcut;

    /**
     * 通知(拦截器)
     */
    private final MethodInterceptor advice;

    public AspectJPointcutAdvisor(Pointcut pointcut, MethodInterceptor advice) {
        this.pointcut = pointcut;
        this.advice = advice;
    }

    @Override
    public Advice getAdvice() {
        return (Advice) advice;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    /**
     * 获取方法匹配器
     * 为了向后兼容,提供此方法
     */
    public MethodMatcher getMethodMatcher() {
        return pointcut.getMethodMatcher();
    }

    /**
     * 获取拦截器
     * 为了方便使用,提供此方法
     */
    public MethodInterceptor getMethodInterceptor() {
        return advice;
    }

}
