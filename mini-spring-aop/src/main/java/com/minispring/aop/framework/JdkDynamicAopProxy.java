package com.minispring.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
 *    - 3. 如果匹配，执行拦截器（通知）
 *    - 4. 如果不匹配，直接调用目标方法
 * <p>
 * 4. 与 CGLIB 的对比
 *    - JDK：基于接口，生成接口实现类
 *    - CGLIB：基于继承，生成目标类的子类
 *    - JDK：性能略优（Java 8+）
 *    - CGLIB：功能更强大，不需要接口
 * <p>
 * 5. ReflectiveMethodInvocation
 *    - 封装方法调用信息
 *    - 实现 MethodInvocation 接口
 *    - 支持拦截器链调用
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
        if (advised.getMethodMatcher().matches(method, advised.getTarget().getClass())) {
            // 匹配成功，执行拦截器（通知）
            MethodInterceptor methodInterceptor = advised.getMethodInterceptor();
            // 创建 MethodInvocation 对象
            return methodInterceptor.invoke(new ReflectiveMethodInvocation(
                    advised.getTarget(),
                    method,
                    args
            ));
        }
        // 不匹配，直接调用目标方法
        return method.invoke(advised.getTarget(), args);
    }

    /**
     * ReflectiveMethodInvocation - 反射方法调用
     * <p>
     * 实现 MethodInvocation 接口
     * 封装目标方法的调用信息
     * 支持拦截器链的调用
     */
    private static class ReflectiveMethodInvocation implements MethodInvocation {

        /**
         * 目标对象
         */
        private final Object target;

        /**
         * 目标方法
         */
        private final Method method;

        /**
         * 方法参数
         */
        private final Object[] arguments;

        public ReflectiveMethodInvocation(Object target, Method method, Object[] arguments) {
            this.target = target;
            this.method = method;
            this.arguments = arguments;
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
            // 调用目标方法
            return method.invoke(target, arguments);
        }

        @Override
        public Object getThis() {
            return target;
        }

        @Override
        public java.lang.reflect.AccessibleObject getStaticPart() {
            return method;
        }
    }

}
