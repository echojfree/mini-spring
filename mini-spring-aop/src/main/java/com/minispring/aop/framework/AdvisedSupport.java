package com.minispring.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * AdvisedSupport - AOP 代理配置支持类
 * <p>
 * 保存创建代理所需的所有配置信息
 * 作为 JDK 动态代理和 CGLIB 代理的配置基础
 * <p>
 * 面试要点：
 * 1. AdvisedSupport 的作用
 *    - 保存代理配置信息
 *    - 目标对象、接口、拦截器等
 *    - 作为代理创建的数据源
 * <p>
 * 2. 配置项说明
 *    - targetSource：目标对象（被代理对象）
 *    - methodInterceptor：方法拦截器（通知）
 *    - methodInterceptors：拦截器链（支持多个通知）
 *    - methodMatcher：方法匹配器（切点）
 *    - proxyTargetClass：是否使用 CGLIB 代理
 * <p>
 * 3. 代理选择策略
 *    - proxyTargetClass = true：强制使用 CGLIB
 *    - 目标类有接口 && proxyTargetClass = false：使用 JDK 动态代理
 *    - 目标类无接口：使用 CGLIB 代理
 *
 * @author mini-spring
 */
public class AdvisedSupport {

    /**
     * 是否使用 CGLIB 代理
     * true: 强制使用 CGLIB
     * false: 如果有接口则使用 JDK 动态代理，否则使用 CGLIB
     */
    private boolean proxyTargetClass = false;

    /**
     * 目标对象
     */
    private Object target;

    /**
     * 目标对象实现的接口
     */
    private Class<?>[] interfaces;

    /**
     * 方法拦截器（通知）- 单个拦截器
     */
    private MethodInterceptor methodInterceptor;

    /**
     * 拦截器链 - 支持多个拦截器
     */
    private List<MethodInterceptor> methodInterceptors = new ArrayList<>();

    /**
     * 方法匹配器（切点）
     */
    private com.minispring.aop.MethodMatcher methodMatcher;

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Class<?>[] interfaces) {
        this.interfaces = interfaces;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public List<MethodInterceptor> getMethodInterceptors() {
        return methodInterceptors;
    }

    public void setMethodInterceptors(List<MethodInterceptor> methodInterceptors) {
        this.methodInterceptors = methodInterceptors;
    }

    public void addMethodInterceptor(MethodInterceptor interceptor) {
        this.methodInterceptors.add(interceptor);
    }

    public com.minispring.aop.MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(com.minispring.aop.MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

}
