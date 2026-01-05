package com.minispring.aop.framework;

/**
 * ProxyFactory - 代理工厂
 * <p>
 * 简化代理对象的创建过程
 * 根据配置自动选择 JDK 动态代理或 CGLIB 代理
 * <p>
 * 设计模式：工厂模式、策略模式
 * <p>
 * 面试要点：
 * 1. ProxyFactory 的作用
 *    - 简化代理创建
 *    - 自动选择代理方式
 *    - 统一代理配置
 * <p>
 * 2. 代理方式选择策略（重要！）
 *    - proxyTargetClass = true：强制使用 CGLIB
 *    - 目标对象有接口 && proxyTargetClass = false：使用 JDK 动态代理
 *    - 目标对象无接口：使用 CGLIB 代理
 * <p>
 * 3. Spring AOP 中的代理选择
 *    - 默认：有接口用 JDK，无接口用 CGLIB
 *    - @EnableAspectJAutoProxy(proxyTargetClass=true)：强制 CGLIB
 *    - Spring Boot 2.0+：默认使用 CGLIB
 * <p>
 * 4. JDK vs CGLIB 对比
 *    - JDK：基于接口，性能略优，兼容性好
 *    - CGLIB：基于继承，功能强大，使用灵活
 *    - 建议：有接口优先 JDK，无接口用 CGLIB
 * <p>
 * 5. 使用示例
 * <pre>
 * {@code
 * ProxyFactory factory = new ProxyFactory();
 * factory.setTarget(targetObject);
 * factory.setMethodInterceptor(methodInterceptor);
 * factory.setMethodMatcher(methodMatcher);
 * Object proxy = factory.getProxy();
 * }
 * </pre>
 *
 * @author mini-spring
 */
public class ProxyFactory extends AdvisedSupport {

    public ProxyFactory() {
    }

    /**
     * 获取代理对象
     * <p>
     * 根据配置自动选择合适的代理方式：
     * 1. 如果 proxyTargetClass = true，使用 CGLIB
     * 2. 如果目标对象有接口，使用 JDK 动态代理
     * 3. 如果目标对象无接口，使用 CGLIB
     *
     * @return 代理对象
     */
    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    /**
     * 创建 AopProxy 对象
     * <p>
     * 根据配置选择 JdkDynamicAopProxy 或 CglibAopProxy
     *
     * @return AopProxy 实例
     */
    private AopProxy createAopProxy() {
        // 判断代理方式
        if (isProxyTargetClass() || getInterfaces() == null || getInterfaces().length == 0) {
            // 使用 CGLIB 代理
            return new CglibAopProxy(this);
        }

        // 使用 JDK 动态代理
        return new JdkDynamicAopProxy(this);
    }

}
