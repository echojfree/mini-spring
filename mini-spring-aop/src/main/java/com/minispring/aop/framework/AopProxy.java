package com.minispring.aop.framework;

/**
 * AopProxy - AOP 代理接口
 * <p>
 * 定义了创建代理对象的标准方法
 * 不同的代理实现（JDK 动态代理、CGLIB 代理）都实现此接口
 * <p>
 * 设计模式：工厂方法模式、策略模式
 * <p>
 * 面试要点：
 * 1. AopProxy 的作用
 *    - 抽象代理创建过程
 *    - 屏蔽 JDK 和 CGLIB 的实现细节
 *    - 提供统一的代理创建接口
 * <p>
 * 2. 实现类
 *    - JdkDynamicAopProxy：JDK 动态代理实现
 *    - CglibAopProxy：CGLIB 代理实现
 * <p>
 * 3. 代理对象特点
 *    - 代理对象会拦截目标方法调用
 *    - 在目标方法执行前后插入通知逻辑
 *    - 支持方法级别的切点匹配
 *
 * @author mini-spring
 */
public interface AopProxy {

    /**
     * 创建代理对象
     * <p>
     * 根据配置信息创建代理对象
     * 代理对象会拦截目标对象的方法调用
     *
     * @return 代理对象
     */
    Object getProxy();

}
