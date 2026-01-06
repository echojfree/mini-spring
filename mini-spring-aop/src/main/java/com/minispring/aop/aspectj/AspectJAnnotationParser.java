package com.minispring.aop.aspectj;

import com.minispring.aop.Advisor;
import com.minispring.aop.AfterReturningAdvice;
import com.minispring.aop.AspectJExpressionPointcut;
import com.minispring.aop.MethodBeforeAdvice;
import com.minispring.aop.aspectj.annotation.*;
import com.minispring.aop.framework.adapter.AfterReturningAdviceInterceptor;
import com.minispring.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * AspectJAnnotationParser - AspectJ 注解解析器
 * <p>
 * 解析 @Aspect 注解的类,将通知方法转换为 Advisor
 * <p>
 * 设计模式:解析器模式、工厂模式
 * <p>
 * 面试要点:
 * 1. 注解式 AOP 原理
 *    - 扫描 @Aspect 注解的类
 *    - 解析通知方法上的注解(@Before, @After, @Around 等)
 *    - 提取切点表达式
 *    - 创建对应的 Advice 和 Advisor
 * <p>
 * 2. 通知类型映射
 *    - @Before → MethodBeforeAdvice
 *    - @AfterReturning → AfterReturningAdvice
 *    - @Around → MethodInterceptor
 *    - @After → AfterAdvice
 *    - @AfterThrowing → ThrowsAdvice
 * <p>
 * 3. 执行流程
 *    - 1. 检查类是否有 @Aspect 注解
 *    - 2. 遍历所有方法,查找通知注解
 *    - 3. 为每个通知方法创建 Advisor
 *    - 4. Advisor = Pointcut + Advice
 * <p>
 * 4. 与 Spring 的对比
 *    - Spring 使用 AspectJProxyFactory 和 AspectJAdvisorFactory
 *    - 支持更复杂的切点表达式和参数绑定
 *    - 本实现是简化版,核心原理相同
 *
 * @author mini-spring
 */
public class AspectJAnnotationParser {

    /**
     * 解析切面类,生成 Advisor 列表
     *
     * @param aspectClass 切面类
     * @return Advisor 列表
     */
    public static List<Advisor> parseAspect(Class<?> aspectClass) {
        // 检查是否有 @Aspect 注解
        if (!aspectClass.isAnnotationPresent(Aspect.class)) {
            throw new IllegalArgumentException("Class " + aspectClass.getName() + " is not an @Aspect");
        }

        List<Advisor> advisors = new ArrayList<>();

        // 遍历所有方法
        for (Method method : aspectClass.getDeclaredMethods()) {
            // 解析通知注解
            Advisor advisor = parseAdviceAnnotation(aspectClass, method);
            if (advisor != null) {
                advisors.add(advisor);
            }
        }

        return advisors;
    }

    /**
     * 解析方法上的通知注解
     *
     * @param aspectClass 切面类
     * @param method      通知方法
     * @return Advisor 或 null
     */
    private static Advisor parseAdviceAnnotation(Class<?> aspectClass, Method method) {
        // 检查各种通知注解
        Before before = method.getAnnotation(Before.class);
        if (before != null) {
            return createBeforeAdvisor(aspectClass, method, before.value());
        }

        AfterReturning afterReturning = method.getAnnotation(AfterReturning.class);
        if (afterReturning != null) {
            return createAfterReturningAdvisor(aspectClass, method, afterReturning.value());
        }

        Around around = method.getAnnotation(Around.class);
        if (around != null) {
            return createAroundAdvisor(aspectClass, method, around.value());
        }

        // 暂不支持 @After 和 @AfterThrowing
        // 可以后续扩展

        return null;
    }

    /**
     * 创建前置通知 Advisor
     *
     * @param aspectClass 切面类
     * @param method      通知方法
     * @param expression  切点表达式
     * @return Advisor
     */
    private static Advisor createBeforeAdvisor(Class<?> aspectClass, Method method, String expression) {
        // 创建切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut(expression);

        // 创建前置通知
        MethodBeforeAdvice advice = (targetMethod, args, target) -> {
            try {
                // 创建切面实例
                Object aspectInstance = aspectClass.getDeclaredConstructor().newInstance();
                // 调用通知方法
                method.invoke(aspectInstance);
            } catch (Exception e) {
                throw new RuntimeException("Error invoking before advice", e);
            }
        };

        // 创建拦截器
        MethodInterceptor interceptor = new MethodBeforeAdviceInterceptor(advice);

        // 返回 Advisor
        return new AspectJPointcutAdvisor(pointcut, interceptor);
    }

    /**
     * 创建后置返回通知 Advisor
     *
     * @param aspectClass 切面类
     * @param method      通知方法
     * @param expression  切点表达式
     * @return Advisor
     */
    private static Advisor createAfterReturningAdvisor(Class<?> aspectClass, Method method, String expression) {
        // 创建切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut(expression);

        // 创建后置返回通知
        AfterReturningAdvice advice = (returnValue, targetMethod, args, target) -> {
            try {
                // 创建切面实例
                Object aspectInstance = aspectClass.getDeclaredConstructor().newInstance();
                // 调用通知方法
                method.invoke(aspectInstance);
            } catch (Exception e) {
                throw new RuntimeException("Error invoking after returning advice", e);
            }
        };

        // 创建拦截器
        MethodInterceptor interceptor = new AfterReturningAdviceInterceptor(advice);

        // 返回 Advisor
        return new AspectJPointcutAdvisor(pointcut, interceptor);
    }

    /**
     * 创建环绕通知 Advisor
     *
     * @param aspectClass 切面类
     * @param method      通知方法
     * @param expression  切点表达式
     * @return Advisor
     */
    private static Advisor createAroundAdvisor(Class<?> aspectClass, Method method, String expression) {
        // 创建切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut(expression);

        // 创建环绕通知(直接实现 MethodInterceptor)
        MethodInterceptor interceptor = invocation -> {
            try {
                // 创建切面实例
                Object aspectInstance = aspectClass.getDeclaredConstructor().newInstance();
                // 调用通知方法,传入 MethodInvocation 作为参数
                return method.invoke(aspectInstance, invocation);
            } catch (Exception e) {
                throw new RuntimeException("Error invoking around advice", e);
            }
        };

        // 返回 Advisor
        return new AspectJPointcutAdvisor(pointcut, interceptor);
    }

}
