package com.minispring.beans.factory.support;

import com.minispring.core.io.DefaultResourceLoader;
import com.minispring.core.io.Resource;
import com.minispring.core.io.ResourceLoader;

/**
 * BeanDefinitionReader 抽象基类
 * <p>
 * 实现 BeanDefinitionReader 接口的通用逻辑
 * 具体的解析逻辑由子类实现
 * <p>
 * 设计模式：
 * - 模板方法模式：定义通用流程，具体解析由子类实现
 * - 适配器模式：适配不同的资源加载方式
 * <p>
 * 核心职责：
 * 1. 管理 BeanDefinitionRegistry 和 ResourceLoader
 * 2. 提供资源加载的通用实现
 * 3. 定义解析模板方法
 * <p>
 * 面试考点：
 * 1. 为什么需要抽象基类？
 *    - 提取公共逻辑，避免重复代码
 *    - 定义统一的资源加载流程
 *    - 简化子类实现
 * 2. Registry 和 ResourceLoader 的作用？
 *    - Registry：存储解析后的 BeanDefinition
 *    - ResourceLoader：加载配置文件资源
 *
 * @author mini-spring
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    /**
     * BeanDefinition 注册器
     */
    private final BeanDefinitionRegistry registry;

    /**
     * 资源加载器
     */
    private ResourceLoader resourceLoader;

    /**
     * 构造函数
     * <p>
     * 创建 Reader 并指定 Registry
     * 默认使用 DefaultResourceLoader
     *
     * @param registry BeanDefinition 注册器
     */
    protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this(registry, new DefaultResourceLoader());
    }

    /**
     * 构造函数
     * <p>
     * 创建 Reader 并指定 Registry 和 ResourceLoader
     *
     * @param registry       BeanDefinition 注册器
     * @param resourceLoader 资源加载器
     */
    protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        this.registry = registry;
        this.resourceLoader = resourceLoader;
    }

    /**
     * 获取 BeanDefinition 注册器
     *
     * @return BeanDefinition 注册器
     */
    @Override
    public BeanDefinitionRegistry getRegistry() {
        return registry;
    }

    /**
     * 获取资源加载器
     *
     * @return 资源加载器
     */
    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    /**
     * 设置资源加载器
     *
     * @param resourceLoader 资源加载器
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 从多个资源中加载 BeanDefinition
     * <p>
     * 遍历资源数组，逐个加载
     *
     * @param resources 资源数组
     * @throws Exception 加载失败时抛出异常
     */
    @Override
    public void loadBeanDefinitions(Resource... resources) throws Exception {
        for (Resource resource : resources) {
            loadBeanDefinitions(resource);
        }
    }

    /**
     * 从指定位置加载 BeanDefinition
     * <p>
     * 使用 ResourceLoader 加载资源
     * 然后调用 loadBeanDefinitions(Resource) 解析
     *
     * @param location 资源位置
     * @throws Exception 加载失败时抛出异常
     */
    @Override
    public void loadBeanDefinitions(String location) throws Exception {
        Resource resource = resourceLoader.getResource(location);
        loadBeanDefinitions(resource);
    }

    /**
     * 从多个位置加载 BeanDefinition
     * <p>
     * 遍历位置数组，逐个加载
     *
     * @param locations 资源位置数组
     * @throws Exception 加载失败时抛出异常
     */
    @Override
    public void loadBeanDefinitions(String... locations) throws Exception {
        for (String location : locations) {
            loadBeanDefinitions(location);
        }
    }

}
