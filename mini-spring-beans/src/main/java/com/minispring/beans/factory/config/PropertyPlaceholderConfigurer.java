package com.minispring.beans.factory.config;

import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.minispring.core.io.Resource;
import com.minispring.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * PropertyPlaceholderConfigurer - 属性占位符配置器
 * <p>
 * 实现 BeanFactoryPostProcessor，在 Bean 实例化前替换 BeanDefinition 中的占位符
 * <p>
 * 实现原理：
 * 1. 加载配置文件（.properties）
 * 2. 遍历所有 BeanDefinition
 * 3. 替换属性值中的占位符 ${key}
 * 4. 支持默认值 ${key:defaultValue}
 * <p>
 * 使用场景：
 * <pre>
 * {@code
 * // application.properties
 * db.url=jdbc:mysql://localhost:3306/test
 * db.username=root
 * db.password=123456
 *
 * // Bean 定义
 * @Component
 * public class DatabaseConfig {
 *     @Value("${db.url}")
 *     private String url;
 *
 *     @Value("${db.username}")
 *     private String username;
 * }
 * }
 * </pre>
 * <p>
 * 面试要点：
 * 1. BeanFactoryPostProcessor 的执行时机
 *    - 在 BeanDefinition 加载完成后
 *    - 在 Bean 实例化之前
 * 2. 占位符解析过程
 *    - 递归解析嵌套占位符
 *    - 处理默认值
 * 3. 配置文件加载机制
 *    - 使用 ResourceLoader 加载资源
 *    - 支持 classpath: 和 file: 前缀
 *
 * @author mini-spring
 */
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {

    /**
     * 配置文件位置
     */
    private String location;

    /**
     * 资源加载器
     */
    private ResourceLoader resourceLoader;

    /**
     * 配置属性
     */
    private Properties properties;

    /**
     * 占位符解析器
     */
    private PlaceholderResolver resolver;

    public PropertyPlaceholderConfigurer() {
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 加载配置文件
     *
     * @throws IOException 配置文件加载失败
     */
    private void loadProperties() throws IOException {
        if (properties != null) {
            return; // 已加载
        }

        properties = new Properties();

        if (location == null || location.isEmpty()) {
            return; // 没有指定配置文件
        }

        // 使用 ResourceLoader 加载资源
        if (resourceLoader == null) {
            resourceLoader = new com.minispring.core.io.DefaultResourceLoader();
        }

        Resource resource = resourceLoader.getResource(location);
        try (InputStream inputStream = resource.getInputStream()) {
            properties.load(inputStream);
        }

        // 创建占位符解析器
        resolver = new PlaceholderResolver(properties);
    }

    /**
     * 后置处理 BeanFactory
     * <p>
     * 在 Mini Spring 中，我们简化了实现：
     * PropertyPlaceholderConfigurer 只负责加载配置文件
     * 实际的占位符解析由 AutowiredAnnotationBeanPostProcessor 在属性注入时调用
     *
     * @param beanFactory BeanFactory 实例
     * @throws BeansException Bean 异常
     */
    @Override
    public void postProcessBeanFactory(DefaultListableBeanFactory beanFactory) throws BeansException {
        try {
            // 加载配置文件
            loadProperties();

        } catch (IOException e) {
            throw new BeansException("无法加载配置文件: " + location, e);
        }
    }

    /**
     * 解析占位符（公开方法，供其他组件使用）
     *
     * @param value 包含占位符的字符串
     * @return 解析后的字符串
     */
    public String resolvePlaceholder(String value) {
        try {
            loadProperties();
        } catch (IOException e) {
            throw new BeansException("无法加载配置文件: " + location, e);
        }

        if (resolver == null) {
            return value;
        }

        return resolver.resolvePlaceholder(value);
    }

    /**
     * 获取配置属性
     *
     * @return Properties 对象
     */
    public Properties getProperties() {
        try {
            loadProperties();
        } catch (IOException e) {
            throw new BeansException("无法加载配置文件: " + location, e);
        }
        return properties;
    }
}
