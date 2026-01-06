package com.minispring.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * JdkDynamicAopProxy - JDK 动态代理实现
 * <p>
 * 基于 JDK 的 Proxy 和 InvocationHandler 实现 AOP 代理
 * <p>
 * 设计模式：代理模式、装饰器模式
 * <p>
 * 面试要点：
 * 1. JDK 动态代理原理
 *    - 基于接口实现
 *    - 使用 Proxy.newProxyInstance() 创建代理对象
 *    - 实现 InvocationHandler 接口拦截方法调用
 *    - 代理对象实现了目标对象的所有接口
 * <p>
 * 2. JDK 动态代理的限制
 *    - 必须基于接口，目标类必须实现接口
 *    - 只能代理接口中定义的方法
 *    - 无法代理 final 方法
 * <p>
 * 3. 方法拦截流程
 *    - 1. invoke() 方法被调用
 *    - 2. 检查方法是否匹配切点
 *    - 3. 如果匹配，执行拦截器链（通知）
 *    - 4. 如果不匹配，直接调用目标方法
 * <p>
 * 4. 与 CGLIB 的对比
 *    - JDK：基于接口，生成接口实现类
 *    - CGLIB：基于继承，生成目标类的子类
 *    - JDK：性能略优（Java 8+）
 *    - CGLIB：功能更强大，不需要接口
 * <p>
 * 5. 拦截器链执行
 *    - 使用 ReflectiveMethodInvocation 管理拦截器链
 *    - 支持多个拦截器按顺序执行
 *    - 责任链模式实现拦截器链调用
 *
 * @author mini-spring
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    /**
     * AOP 配置信息
     */
    private final AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                advised.getInterfaces(),
                this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 检查方法是否匹配切点
        if (advised.getMethodMatcher() != null &&
                advised.getMethodMatcher().matches(method, advised.getTarget().getClass())) {

            // 获取拦截器链
            List<MethodInterceptor> interceptors = new ArrayList<>();

            // 如果有拦截器链，使用拦截器链
            if (advised.getMethodInterceptors() != null && !advised.getMethodInterceptors().isEmpty()) {
                interceptors.addAll(advised.getMethodInterceptors());
            }
            // 否则使用单个拦截器（向后兼容）
            else if (advised.getMethodInterceptor() != null) {
                interceptors.add(advised.getMethodInterceptor());
            }

            // 如果有拦截器，使用拦截器链执行
            if (!interceptors.isEmpty()) {
                // 创建 ReflectiveMethodInvocation 对象，支持拦截器链
                return new ReflectiveMethodInvocation(
                        advised.getTarget(),
                        method,
                        args,
                        interceptors
                ).proceed();
            }
        }

        // 不匹配或没有拦截器，直接调用目标方法
        return method.invoke(advised.getTarget(), args);
    }

}
