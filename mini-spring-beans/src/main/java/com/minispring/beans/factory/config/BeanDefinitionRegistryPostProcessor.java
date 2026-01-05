package com.minispring.beans.factory.config;

import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.support.BeanDefinitionRegistry;

/**
 * BeanDefinitionRegistryPostProcessor 接口
 * <p>
 * BeanFactoryPostProcessor 的扩展接口，允许在常规 BeanFactoryPostProcessor 执行之前
 * 注册更多的 BeanDefinition
 * <p>
 * 核心功能（面试重点）：
 * 1. 作用时机：比 BeanFactoryPostProcessor 更早
 * 2. 作用对象：BeanDefinitionRegistry（Bean 定义注册表）
 * 3. 典型应用：
 *    - ConfigurationClassPostProcessor（处理 @Configuration 类）
 *    - MapperScannerConfigurer（MyBatis Mapper 扫描）
 *    - 动态注册 BeanDefinition
 * <p>
 * 执行顺序（面试高频）：
 * 1. BeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry()
 *    ⬇️ 注册新的 BeanDefinition
 * 2. BeanDefinitionRegistryPostProcessor.postProcessBeanFactory()
 *    ⬇️ 修改 BeanDefinition
 * 3. BeanFactoryPostProcessor.postProcessBeanFactory()
 *    ⬇️ 其他后置处理器修改 BeanDefinition
 * 4. Bean 实例化开始
 * <p>
 * 与 BeanFactoryPostProcessor 的关系：
 * - 继承自 BeanFactoryPostProcessor
 * - 增加了 postProcessBeanDefinitionRegistry() 方法
 * - 执行顺序更早，可以注册新的 BeanDefinition
 * <p>
 * 设计思想：
 * - 职责分离：注册和修改 BeanDefinition 分开
 * - 扩展性：提供更早的扩展点
 * - 灵活性：支持动态注册 Bean
 * <p>
 * 典型应用场景：
 * 1. 扫描并注册带有特定注解的类
 * 2. 根据配置动态注册 Bean
 * 3. 注册编程式配置的 Bean
 * 4. 实现自定义的组件扫描
 *
 * @author mini-spring
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

    /**
     * 后置处理 BeanDefinitionRegistry
     * <p>
     * 在所有 BeanDefinition 加载完成后，但在 BeanFactoryPostProcessor.postProcessBeanFactory()
     * 之前调用
     * <p>
     * 可以在此方法中：
     * 1. 注册新的 BeanDefinition
     * 2. 修改已有的 BeanDefinition
     * 3. 删除已有的 BeanDefinition
     * <p>
     * 注意事项（面试考点）：
     * 1. 此方法执行时机最早
     *    - 早于 BeanFactoryPostProcessor
     *    - 早于 Bean 实例化
     * 2. 可以注册新的 BeanDefinition
     *    - 新注册的 BeanDefinition 也会被后续的后置处理器处理
     * 3. 不要在此方法中实例化 Bean
     *    - 会破坏容器的初始化流程
     * <p>
     * 典型实现示例：
     * <pre>
     * public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
     *     // 1. 扫描包下的所有类
     *     Set&lt;Class&lt;?&gt;&gt; classes = scanClasses("com.example");
     *
     *     // 2. 过滤出带有 @Component 注解的类
     *     for (Class&lt;?&gt; clazz : classes) {
     *         if (clazz.isAnnotationPresent(Component.class)) {
     *             // 3. 创建 BeanDefinition
     *             BeanDefinition bd = new BeanDefinition(clazz);
     *
     *             // 4. 注册到容器
     *             String beanName = generateBeanName(clazz);
     *             registry.registerBeanDefinition(beanName, bd);
     *         }
     *     }
     * }
     * </pre>
     * <p>
     * Spring 框架中的典型实现：
     * - ConfigurationClassPostProcessor：
     *   处理 @Configuration、@ComponentScan、@Import 等注解
     * - MapperScannerConfigurer：
     *   扫描 MyBatis 的 Mapper 接口并注册
     * - CustomScopeConfigurer：
     *   注册自定义作用域
     *
     * @param registry BeanDefinition 注册表，可以通过它注册新的 BeanDefinition
     * @throws BeansException 处理失败时抛出异常
     */
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
