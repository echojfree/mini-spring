package com.minispring.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * @Scope 注解
 * <p>
 * 指定 Bean 的作用域
 * <p>
 * 核心功能（面试重点）：
 * 1. 控制 Bean 的生命周期和创建策略
 * 2. 支持单例和原型两种基本作用域
 * 3. 可扩展支持自定义作用域
 * <p>
 * 作用域类型（面试高频）：
 * 1. singleton（默认）：
 *    - 单例模式，容器中只有一个实例
 *    - 在容器启动时创建
 *    - 线程不安全（需注意并发问题）
 * 2. prototype：
 *    - 原型模式，每次获取都创建新实例
 *    - 在 getBean() 时创建
 *    - 容器不负责销毁（需手动管理）
 * 3. Web 作用域（扩展）：
 *    - request：每个 HTTP 请求一个实例
 *    - session：每个 HTTP 会话一个实例
 *    - application：整个 ServletContext 一个实例
 * <p>
 * 使用场景：
 * <pre>
 * @Component
 * @Scope("singleton")  // 默认，可省略
 * public class UserService {
 *     // 无状态服务，适合单例
 * }
 *
 * @Component
 * @Scope("prototype")
 * public class UserForm {
 *     // 有状态对象，每次使用新实例
 * }
 * </pre>
 * <p>
 * 面试重点：
 * 1. singleton 和 prototype 的区别
 *    - 创建时机：启动时 vs 使用时
 *    - 实例数量：唯一 vs 多个
 *    - 销毁管理：容器管理 vs 手动管理
 * 2. 默认作用域
 *    - Spring Bean 默认是 singleton
 * 3. 线程安全问题
 *    - singleton Bean 需注意线程安全
 *    - 无状态对象适合 singleton
 *    - 有状态对象考虑 prototype
 * 4. 循环依赖
 *    - singleton 可以解决循环依赖
 *    - prototype 无法解决循环依赖
 *
 * @author mini-spring
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {

    /**
     * 作用域名称
     * <p>
     * 常用值：
     * - "singleton"：单例（默认）
     * - "prototype"：原型
     * <p>
     * 示例：
     * <pre>
     * @Scope("singleton")  // 单例，默认
     * @Scope("prototype")  // 原型，每次创建新实例
     * </pre>
     *
     * @return 作用域名称
     */
    String value() default "singleton";

}
