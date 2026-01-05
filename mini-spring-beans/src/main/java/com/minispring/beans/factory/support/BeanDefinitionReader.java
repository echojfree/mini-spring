package com.minispring.beans.factory.support;

import com.minispring.core.io.Resource;
import com.minispring.core.io.ResourceLoader;

/**
 * BeanDefinition 读取器接口
 * <p>
 * 定义从不同配置源读取 BeanDefinition 的统一接口
 * <p>
 * 设计思想（面试考点）:
 * 1. 策略模式：不同的配置源有不同的读取策略
 *    - XmlBeanDefinitionReader：从 XML 读取
 *    - AnnotationBeanDefinitionReader：从注解读取
 *    - PropertiesBeanDefinitionReader：从 properties 读取
 * 2. 单一职责原则：只负责读取和注册 BeanDefinition
 * 3. 依赖倒置原则：依赖于抽象的 Resource 和 ResourceLoader
 * <p>
 * 面试重点：
 * 1. 为什么需要 BeanDefinitionReader？
 *    - 分离配置读取和 Bean 创建的职责
 *    - 支持多种配置源（XML、注解、Properties）
 *    - 便于扩展新的配置方式
 * 2. 与 BeanDefinitionRegistry 的关系？
 *    - Reader 负责读取配置
 *    - Registry 负责存储 BeanDefinition
 *    - Reader 将读取到的 BeanDefinition 注册到 Registry
 *
 * @author mini-spring
 */
public interface BeanDefinitionReader {

    /**
     * 获取 BeanDefinition 注册器
     * <p>
     * 返回用于注册 BeanDefinition 的注册器
     *
     * @return BeanDefinition 注册器
     */
    BeanDefinitionRegistry getRegistry();

    /**
     * 获取资源加载器
     * <p>
     * 返回用于加载配置文件的资源加载器
     *
     * @return 资源加载器
     */
    ResourceLoader getResourceLoader();

    /**
     * 从资源中加载 BeanDefinition
     * <p>
     * 从单个资源加载 BeanDefinition 并注册到 Registry
     *
     * @param resource 资源
     * @throws Exception 加载失败时抛出异常
     */
    void loadBeanDefinitions(Resource resource) throws Exception;

    /**
     * 从多个资源中加载 BeanDefinition
     * <p>
     * 从多个资源加载 BeanDefinition 并注册到 Registry
     *
     * @param resources 资源数组
     * @throws Exception 加载失败时抛出异常
     */
    void loadBeanDefinitions(Resource... resources) throws Exception;

    /**
     * 从指定位置加载 BeanDefinition
     * <p>
     * 使用 ResourceLoader 加载指定位置的资源
     * 然后从资源中读取 BeanDefinition
     *
     * @param location 资源位置
     * @throws Exception 加载失败时抛出异常
     */
    void loadBeanDefinitions(String location) throws Exception;

    /**
     * 从多个位置加载 BeanDefinition
     * <p>
     * 使用 ResourceLoader 加载多个位置的资源
     * 然后从资源中读取 BeanDefinition
     *
     * @param locations 资源位置数组
     * @throws Exception 加载失败时抛出异常
     */
    void loadBeanDefinitions(String... locations) throws Exception;

}
