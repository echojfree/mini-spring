package com.minispring.beans.factory.support;

import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.config.BeanDefinition;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

/**
 * CGLIB 实例化策略：使用 CGLIB 创建 Bean 实例
 * <p>
 * CGLIB 原理：
 * - Code Generation Library（代码生成库）
 * - 通过继承目标类，生成子类
 * - 在子类中拦截方法调用
 * <p>
 * 优点：
 * - 不需要接口，可以代理任何类
 * - 性能比 JDK 动态代理高
 * - 可以拦截方法调用（AOP 的基础）
 * <p>
 * 限制：
 * - 不能代理 final 类
 * - 不能代理 final 方法
 * - 需要额外的 CGLIB 依赖
 * <p>
 * 面试考点：
 * 1. CGLIB 的工作原理（字节码生成）
 * 2. CGLIB vs JDK 动态代理的区别
 *    - JDK：基于接口，使用 InvocationHandler
 *    - CGLIB：基于继承，生成子类
 * 3. Spring AOP 如何选择代理方式？
 *    - 有接口：JDK 动态代理
 *    - 无接口：CGLIB 代理
 * 4. 为什么 final 类不能被 CGLIB 代理？
 *    - CGLIB 基于继承，final 类不能被继承
 *
 * @author mini-spring
 */
public class CglibSubclassingInstantiationStrategy implements InstantiationStrategy {

    /**
     * 使用 CGLIB 实例化 Bean
     * <p>
     * 实现步骤：
     * 1. 创建 Enhancer 对象（CGLIB 的核心类）
     * 2. 设置父类（目标类）
     * 3. 设置回调（这里使用 NoOp，表示不拦截）
     * 4. 创建代理对象（实际是目标类的子类）
     * <p>
     * 面试考点：
     * - Enhancer 的作用
     * - 为什么设置 NoOp 回调？
     *   答：这里只是用 CGLIB 创建实例，不需要方法拦截
     *   后续 AOP 章节会使用 MethodInterceptor 进行方法拦截
     *
     * @param beanDefinition Bean 定义
     * @return Bean 实例（实际是子类实例）
     * @throws BeansException 实例化失败
     */
    @Override
    public Object instantiate(BeanDefinition beanDefinition) throws BeansException {
        // 获取 Bean 的 Class 对象
        Class<?> beanClass = beanDefinition.getBeanClass();

        try {
            // 创建 Enhancer 对象
            // Enhancer 是 CGLIB 的核心类，用于生成代理类
            Enhancer enhancer = new Enhancer();

            // 设置父类（被代理的类）
            // CGLIB 通过继承来实现代理
            enhancer.setSuperclass(beanClass);

            // 设置回调
            // NoOp.INSTANCE 表示不做任何拦截，只是创建实例
            // 面试考点：为什么需要设置回调？
            // - CGLIB 要求必须设置回调接口
            // - NoOp 是一个空实现，不做任何拦截
            // - 后续 AOP 会使用 MethodInterceptor 进行方法拦截
            enhancer.setCallback(NoOp.INSTANCE);

            // 创建代理对象
            // 这个对象实际上是 beanClass 的子类实例
            // 面试考点：CGLIB 如何创建对象？
            // 1. 动态生成 beanClass 的子类字节码
            // 2. 加载子类的 Class 对象
            // 3. 创建子类实例
            return enhancer.create();

        } catch (Exception e) {
            // 可能的异常：
            // - IllegalArgumentException: final 类不能被代理
            // - 其他 CGLIB 相关异常
            throw new BeansException(
                    "使用 CGLIB 实例化 Bean 失败: " + beanClass.getName() +
                            "，原因：" + e.getMessage() +
                            "（提示：final 类不能被 CGLIB 代理）", e);
        }
    }

}
