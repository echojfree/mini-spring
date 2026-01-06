package com.minispring.aop.framework;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * CglibAopProxy - CGLIB 动态代理实现
 * <p>
 * 基于 CGLIB 的 Enhancer 和 MethodInterceptor 实现 AOP 代理
 * <p>
 * 设计模式：代理模式、装饰器模式
 * <p>
 * 面试要点：
 * 1. CGLIB 动态代理原理
 *    - 基于继承实现
 *    - 使用 Enhancer 创建目标类的子类
 *    - 实现 MethodInterceptor 接口拦截方法调用
 *    - 通过字节码技术生成子类
 * <p>
 * 2. CGLIB 动态代理的限制
 *    - 无法代理 final 类（无法继承）
 *    - 无法代理 final 方法（无法重写）
 *    - 无法代理 private 方法（无法重写）
 *    - 无法代理 static 方法（属于类，不属于对象）
 * <p>
 * 3. 方法拦截流程
 *    - 1. intercept() 方法被调用
 *    - 2. 检查方法是否匹配切点
 *    - 3. 如果匹配，执行拦截器链（通知）
 *    - 4. 如果不匹配，使用 methodProxy.invoke() 调用目标方法
 * <p>
 * 4. 与 JDK 动态代理的对比
 *    - CGLIB：基于继承，生成目标类的子类
 *    - JDK：基于接口，生成接口实现类
 *    - CGLIB：不需要接口，功能更强大
 *    - JDK：性能略优（Java 8+）
 * <p>
 * 5. MethodProxy 的优势
 *    - FastClass 机制，避免反射调用
 *    - 性能比直接反射调用好
 *    - 使用索引而不是方法名查找
 * <p>
 * 6. 使用场景
 *    - 目标类没有实现接口
 *    - 需要代理目标类本身而非接口
 *    - Spring AOP 中 proxyTargetClass=true 时使用
 *
 * @author mini-spring
 */
public class CglibAopProxy implements AopProxy {

    /**
     * AOP 配置信息
     */
    private final AdvisedSupport advised;

    public CglibAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        // 创建 CGLIB Enhancer
        Enhancer enhancer = new Enhancer();
        // 设置父类（目标类）
        enhancer.setSuperclass(advised.getTarget().getClass());
        // 设置回调（拦截器）
        enhancer.setCallback(new DynamicAdvisedInterceptor(advised));
        // 创建代理对象
        return enhancer.create();
    }

    /**
     * DynamicAdvisedInterceptor - 动态通知拦截器
     * <p>
     * 实现 CGLIB 的 MethodInterceptor 接口
     * 拦截目标方法的调用，织入通知逻辑
     * <p>
     * 注意：这个 MethodInterceptor 是 CGLIB 的，不是 AOP Alliance 的
     */
    private static class DynamicAdvisedInterceptor implements MethodInterceptor {

        private final AdvisedSupport advised;

        public DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy)
                throws Throwable {

            // 检查方法是否匹配切点
            if (advised.getMethodMatcher() != null &&
                    advised.getMethodMatcher().matches(method, advised.getTarget().getClass())) {

                // 获取拦截器链
                List<org.aopalliance.intercept.MethodInterceptor> interceptors = new ArrayList<>();

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
                    // 创建 CglibMethodInvocation 对象，支持拦截器链
                    CglibMethodInvocation methodInvocation = new CglibMethodInvocation(
                            advised.getTarget(),
                            method,
                            args,
                            methodProxy,
                            interceptors
                    );
                    // 执行拦截器链
                    return methodInvocation.proceed();
                }
            }

            // 不匹配或没有拦截器，直接调用目标方法
            return methodProxy.invoke(advised.getTarget(), args);
        }
    }

    /**
     * CglibMethodInvocation - CGLIB 方法调用
     * <p>
     * 继承 ReflectiveMethodInvocation，增加对 MethodProxy 的支持
     * 使用 MethodProxy 提高性能
     */
    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

        private final MethodProxy methodProxy;

        public CglibMethodInvocation(Object target, Method method, Object[] arguments,
                                      MethodProxy methodProxy,
                                      List<org.aopalliance.intercept.MethodInterceptor> interceptors) {
            super(target, method, arguments, interceptors);
            this.methodProxy = methodProxy;
        }

        @Override
        protected Object invokeJoinpoint() throws Throwable {
            // 使用 MethodProxy 调用目标方法，性能更好
            return methodProxy.invoke(target, arguments);
        }
    }

}
