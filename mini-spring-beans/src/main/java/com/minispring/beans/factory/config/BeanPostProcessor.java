package com.minispring.beans.factory.config;

import com.minispring.beans.exception.BeansException;

/**
 * BeanPostProcessor 接口
 * <p>
 * Bean 后置处理器接口，允许在 Bean 初始化前后进行自定义处理
 * 这是 Spring AOP 的基础，也是扩展 Spring 容器功能的重要机制
 * <p>
 * 核心功能：
 * - 在 Bean 初始化前后插入自定义逻辑
 * - 可以修改 Bean 实例或返回代理对象
 * - 支持 AOP、事务、缓存等功能的实现
 * <p>
 * 使用场景：
 * - AOP 代理创建（如 @Transactional、@Async）
 * - Bean 属性修改和验证
 * - 自动注入（如 @Autowired、@Resource）
 * - 自定义注解处理
 * - 监控和日志记录
 * <p>
 * 调用时机：
 * 1. postProcessBeforeInitialization：在初始化方法之前调用
 *    - 在 Aware 接口回调之后
 *    - 在 InitializingBean.afterPropertiesSet() 之前
 * 2. postProcessAfterInitialization：在初始化方法之后调用
 *    - 在 init-method 之后
 *    - Bean 完全初始化完成
 * <p>
 * 设计模式：
 * - 责任链模式：多个 BeanPostProcessor 按顺序执行
 * - 装饰器模式：可以包装原始 Bean 返回代理对象
 * - 模板方法模式：定义处理流程，具体实现由子类完成
 * <p>
 * 面试考点：
 * 1. BeanPostProcessor 的作用是什么？
 *    - 在 Bean 初始化前后进行自定义处理
 *    - Spring AOP 的实现基础
 * 2. BeanPostProcessor 的调用时机？
 *    - postProcessBeforeInitialization：InitializingBean 之前
 *    - postProcessAfterInitialization：init-method 之后
 * 3. BeanPostProcessor 和 BeanFactoryPostProcessor 的区别？
 *    - BeanPostProcessor：处理 Bean 实例
 *    - BeanFactoryPostProcessor：处理 BeanDefinition（后续实现）
 * 4. BeanPostProcessor 如何实现 AOP？
 *    - 在 postProcessAfterInitialization 中返回代理对象
 *    - 代理对象包装原始 Bean，添加额外功能
 * 5. 多个 BeanPostProcessor 的执行顺序？
 *    - 按照注册顺序执行
 *    - 可以通过 Ordered 接口指定优先级（后续实现）
 *
 * @author mini-spring
 */
public interface BeanPostProcessor {

    /**
     * 在 Bean 初始化之前调用
     * <p>
     * 调用时机：
     * 1. 实例化完成
     * 2. 属性注入完成
     * 3. Aware 接口回调完成
     * 4. 调用此方法 ← 当前位置
     * 5. InitializingBean.afterPropertiesSet()
     * 6. init-method
     * 7. postProcessAfterInitialization
     * <p>
     * 典型应用：
     * - @PostConstruct 注解处理
     * - @Autowired、@Resource 注解处理
     * - Bean 属性验证
     * - 自定义初始化前置处理
     * <p>
     * 返回值说明：
     * - 返回原始 Bean：不做任何修改
     * - 返回修改后的 Bean：可以修改 Bean 的属性
     * - 返回代理对象：包装原始 Bean（一般在 After 方法中做）
     * - 返回 null：会导致后续处理中断
     *
     * @param bean     Bean 实例
     * @param beanName Bean 名称
     * @return 处理后的 Bean 实例（可以是原始对象或包装对象）
     * @throws BeansException 处理失败时抛出异常
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

    /**
     * 在 Bean 初始化之后调用
     * <p>
     * 调用时机：
     * 1. 实例化完成
     * 2. 属性注入完成
     * 3. Aware 接口回调完成
     * 4. postProcessBeforeInitialization
     * 5. InitializingBean.afterPropertiesSet()
     * 6. init-method
     * 7. 调用此方法 ← 当前位置
     * <p>
     * 典型应用：
     * - AOP 代理创建（最常用）
     * - @Transactional 事务代理
     * - @Async 异步代理
     * - @Cacheable 缓存代理
     * - 自定义注解代理
     * <p>
     * AOP 实现原理（面试重点）：
     * 1. 判断 Bean 是否需要代理（如是否有 @Transactional 注解）
     * 2. 如果需要代理，创建代理对象
     *    - JDK 动态代理（Bean 实现了接口）
     *    - CGLIB 代理（Bean 没有实现接口）
     * 3. 返回代理对象替换原始 Bean
     * 4. 容器中存储的是代理对象，用户使用的也是代理对象
     * <p>
     * 返回值说明：
     * - 返回原始 Bean：不需要代理
     * - 返回代理对象：用代理对象替换原始 Bean
     * - 返回 null：会导致 Bean 创建失败
     *
     * @param bean     Bean 实例（已经完成初始化）
     * @param beanName Bean 名称
     * @return 处理后的 Bean 实例（通常是代理对象）
     * @throws BeansException 处理失败时抛出异常
     */
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;

}
