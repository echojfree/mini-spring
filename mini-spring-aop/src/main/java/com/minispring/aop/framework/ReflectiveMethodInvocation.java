package com.minispring.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

/**
 * ReflectiveMethodInvocation - 反射方法调用
 * <p>
 * 核心功能：
 * 1. 封装目标方法的调用
 * 2. 管理拦截器链的执行
 * 3. 实现责任链模式，递归调用拦截器
 * <p>
 * 设计模式：
 * 1. 责任链模式：拦截器链按顺序执行
 * 2. 递归调用：proceed() 方法递归调用下一个拦截器
 * <p>
 * 面试要点：
 * 1. 拦截器链执行流程
 *    - proceed() 递归调用拦截器
 *    - currentInterceptorIndex 记录当前位置
 *    - 当所有拦截器执行完毕，调用目标方法
 * <p>
 * 2. 责任链模式应用
 *    - 每个拦截器处理请求并决定是否继续
 *    - 通过 proceed() 将控制权传递给下一个拦截器
 *    - 支持前置、后置、环绕等多种通知类型
 * <p>
 * 3. 拦截器链示例
 *    before1 → before2 → around1-前 → around2-前
 *    → 目标方法
 *    → around2-后 → around1-后 → after2 → after1
 *
 * @author mini-spring
 */
public class ReflectiveMethodInvocation implements MethodInvocation {

    /**
     * 目标对象
     */
    protected final Object target;

    /**
     * 目标方法
     */
    protected final Method method;

    /**
     * 方法参数
     */
    protected final Object[] arguments;

    /**
     * 拦截器链
     */
    protected final List<MethodInterceptor> interceptors;

    /**
     * 当前拦截器索引
     */
    private int currentInterceptorIndex = -1;

    /**
     * 构造函数
     *
     * @param target       目标对象
     * @param method       目标方法
     * @param arguments    方法参数
     * @param interceptors 拦截器链
     */
    public ReflectiveMethodInvocation(Object target, Method method, Object[] arguments,
                                       List<MethodInterceptor> interceptors) {
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.interceptors = interceptors;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Object proceed() throws Throwable {
        // 所有拦截器都执行完毕，调用目标方法
        if (this.currentInterceptorIndex == this.interceptors.size() - 1) {
            return invokeJoinpoint();
        }

        // 获取下一个拦截器并执行
        MethodInterceptor interceptor = this.interceptors.get(++this.currentInterceptorIndex);
        return interceptor.invoke(this);
    }

    /**
     * 调用目标方法
     */
    protected Object invokeJoinpoint() throws Throwable {
        return method.invoke(target, arguments);
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public AccessibleObject getStaticPart() {
        return method;
    }

}
