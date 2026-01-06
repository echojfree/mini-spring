package com.minispring.aop.support;

import com.minispring.aop.ClassFilter;
import com.minispring.aop.MethodMatcher;
import com.minispring.aop.Pointcut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * AnnotationMatchingPointcut - 注解匹配切点
 * <p>
 * 根据注解匹配类或方法
 * <p>
 * 面试要点:
 * 1. 注解匹配策略
 *    - 类级别注解:匹配类上的注解
 *    - 方法级别注解:匹配方法上的注解
 *    - checkInherited: 是否检查继承的注解
 * <p>
 * 2. 使用场景
 *    - @Transactional 事务注解匹配
 *    - @Cacheable 缓存注解匹配
 *    - @Async 异步注解匹配
 *
 * @author mini-spring
 */
public class AnnotationMatchingPointcut implements Pointcut {

    /**
     * 注解类型
     */
    private final Class<? extends Annotation> annotationType;

    /**
     * 是否检查继承的注解
     */
    private final boolean checkInherited;

    public AnnotationMatchingPointcut(Class<? extends Annotation> annotationType) {
        this(annotationType, false);
    }

    public AnnotationMatchingPointcut(Class<? extends Annotation> annotationType, boolean checkInherited) {
        this.annotationType = annotationType;
        this.checkInherited = checkInherited;
    }

    @Override
    public ClassFilter getClassFilter() {
        return new AnnotationClassFilter();
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return new AnnotationMethodMatcher();
    }

    /**
     * 注解类过滤器
     */
    private class AnnotationClassFilter implements ClassFilter {
        @Override
        public boolean matches(Class<?> clazz) {
            // 检查类上是否有注解
            if (checkInherited) {
                return clazz.isAnnotationPresent(annotationType);
            } else {
                return clazz.getDeclaredAnnotation(annotationType) != null;
            }
        }
    }

    /**
     * 注解方法匹配器
     */
    private class AnnotationMethodMatcher implements MethodMatcher {
        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            // 1. 检查方法上是否有注解
            if (checkInherited) {
                if (method.isAnnotationPresent(annotationType)) {
                    return true;
                }
            } else {
                if (method.getDeclaredAnnotation(annotationType) != null) {
                    return true;
                }
            }

            // 2. 检查类上是否有注解
            if (checkInherited) {
                return targetClass.isAnnotationPresent(annotationType);
            } else {
                return targetClass.getDeclaredAnnotation(annotationType) != null;
            }
        }

        @Override
        public boolean isRuntime() {
            // 注解匹配不需要运行时检查
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object[] args) {
            // 不支持运行时匹配
            throw new UnsupportedOperationException("不支持运行时匹配");
        }
    }

}
