package com.minispring.beans.factory.support;

import com.minispring.beans.factory.config.BeanDefinition;

/**
 * BeanDefinition 注册表接口
 * <p>
 * 职责：提供 BeanDefinition 的注册和查询功能
 * <p>
 * 设计模式：注册表模式（Registry Pattern）
 * 面试考点：
 * 1. BeanDefinition 如何注册到容器中？
 * 2. 容器如何管理大量的 BeanDefinition？
 * 3. 为什么需要 BeanDefinition 注册表？
 * 4. BeanDefinition 的生命周期
 *
 * @author mini-spring
 */
public interface BeanDefinitionRegistry {

    /**
     * 注册 BeanDefinition
     * <p>
     * 将 Bean 的元数据信息注册到容器中
     * 注册后，容器就知道如何创建这个 Bean
     *
     * @param beanName       Bean 的名称（唯一标识）
     * @param beanDefinition Bean 的定义信息
     * @throws RuntimeException 如果 beanName 已存在（覆盖策略由实现类决定）
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    /**
     * 根据 Bean 名称获取 BeanDefinition
     * <p>
     * 面试考点：
     * 容器创建 Bean 实例前，会先查询 BeanDefinition
     * BeanDefinition 包含了创建 Bean 所需的所有信息
     *
     * @param beanName Bean 名称
     * @return BeanDefinition 对象，如果不存在则返回 null
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 判断是否包含指定名称的 BeanDefinition
     *
     * @param beanName Bean 名称
     * @return true 表示已注册，false 表示未注册
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * 获取所有注册的 Bean 名称
     * <p>
     * 用于：
     * 1. 容器启动时批量初始化单例 Bean
     * 2. 容器关闭时批量销毁 Bean
     * 3. 查看容器中所有的 Bean
     *
     * @return Bean 名称数组
     */
    String[] getBeanDefinitionNames();

}
