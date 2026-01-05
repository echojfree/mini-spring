package com.minispring.context.support;

import com.minispring.beans.PropertyValues;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.BeanReference;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.minispring.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * ClassPathXmlApplicationContext
 * <p>
 * 从 classpath 加载 XML 配置的 ApplicationContext
 * <p>
 * v0.11.0 更新：完整实现 XML 解析功能
 * - 支持从 XML 文件加载 BeanDefinition
 * - 使用 XmlBeanDefinitionReader 解析配置
 * - 支持两种使用方式：XML 配置 + 编程式注册
 * <p>
 * 使用方式1（XML 配置）：
 * <pre>
 * ClassPathXmlApplicationContext context =
 *     new ClassPathXmlApplicationContext("classpath:spring.xml");
 * UserService userService = context.getBean("userService", UserService.class);
 * </pre>
 * <p>
 * 使用方式2（编程式注册）：
 * <pre>
 * ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
 * context.registerBean("userService", UserService.class);
 * context.refresh();
 * UserService userService = context.getBean("userService", UserService.class);
 * </pre>
 * <p>
 * 面试考点：
 * 1. ApplicationContext 的初始化流程
 * 2. BeanDefinition 的加载过程
 * 3. Bean 的预加载机制
 * 4. XML 配置文件的解析流程
 *
 * @author mini-spring
 */
public class ClassPathXmlApplicationContext extends AbstractRefreshableApplicationContext {

    /**
     * 配置文件路径数组
     */
    private String[] configLocations;

    /**
     * BeanFactory，用于临时存储 BeanDefinition（编程式注册）
     */
    private DefaultListableBeanFactory tempBeanFactory = new DefaultListableBeanFactory();

    /**
     * 无参构造函数
     * <p>
     * 创建容器，但不自动刷新
     * 需要手动调用 refresh() 方法
     * <p>
     * 适用于编程式注册 Bean
     */
    public ClassPathXmlApplicationContext() {
        // 不自动刷新
    }

    /**
     * 从单个配置文件创建容器
     * <p>
     * 自动加载 XML 配置并刷新容器
     * <p>
     * 使用示例：
     * <pre>
     * ApplicationContext context =
     *     new ClassPathXmlApplicationContext("classpath:spring.xml");
     * </pre>
     *
     * @param configLocation 配置文件路径
     * @throws Exception 初始化失败时抛出异常
     */
    public ClassPathXmlApplicationContext(String configLocation) throws Exception {
        this(new String[]{configLocation});
    }

    /**
     * 从多个配置文件创建容器
     * <p>
     * 自动加载所有 XML 配置并刷新容器
     * <p>
     * 使用示例：
     * <pre>
     * ApplicationContext context = new ClassPathXmlApplicationContext(
     *     "classpath:spring-dao.xml",
     *     "classpath:spring-service.xml"
     * );
     * </pre>
     *
     * @param configLocations 配置文件路径数组
     * @throws Exception 初始化失败时抛出异常
     */
    public ClassPathXmlApplicationContext(String[] configLocations) throws Exception {
        this.configLocations = configLocations;
        refresh();
    }

    /**
     * 注册 Bean
     * <p>
     * 简化版本：通过编程方式注册 Bean
     * 实际的 Spring 是通过解析 XML 自动注册
     * <p>
     * 使用示例：
     * <pre>
     * context.registerBean("userService", UserService.class);
     * </pre>
     *
     * @param beanName  Bean 名称
     * @param beanClass Bean 类型
     */
    public void registerBean(String beanName, Class<?> beanClass) {
        BeanDefinition beanDefinition = new BeanDefinition(beanClass);
        tempBeanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    /**
     * 注册 Bean 及其属性
     * <p>
     * 简化版本：通过编程方式注册 Bean 和属性
     * <p>
     * 使用示例：
     * <pre>
     * PropertyValues pv = new PropertyValues();
     * pv.addPropertyValue("serviceName", "UserService");
     * context.registerBean("userService", UserService.class, pv);
     * </pre>
     *
     * @param beanName       Bean 名称
     * @param beanClass      Bean 类型
     * @param propertyValues 属性值
     */
    public void registerBean(String beanName, Class<?> beanClass, PropertyValues propertyValues) {
        BeanDefinition beanDefinition = new BeanDefinition(beanClass, propertyValues);
        tempBeanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    /**
     * 加载 BeanDefinition
     * <p>
     * 两种加载方式：
     * 1. 从 XML 配置文件加载（通过 XmlBeanDefinitionReader）
     * 2. 从临时 BeanFactory 加载（编程式注册）
     * <p>
     * 面试考点：
     * 1. Spring 如何加载配置？
     *    - 使用 BeanDefinitionReader 读取配置源
     *    - 将配置转换为 BeanDefinition
     *    - 注册到 BeanFactory
     * 2. 支持多种配置源
     *    - XML 配置文件
     *    - 注解配置
     *    - Java 配置类
     *
     * @param beanFactory BeanFactory 实例
     * @throws Exception 加载失败时抛出异常
     */
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws Exception {
        // 方式1：从 XML 配置文件加载
        if (configLocations != null && configLocations.length > 0) {
            // 创建 XML 读取器
            XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

            // 加载所有配置文件
            beanDefinitionReader.loadBeanDefinitions(configLocations);
        }

        // 方式2：从临时 BeanFactory 加载（编程式注册）
        String[] beanNames = tempBeanFactory.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = tempBeanFactory.getBeanDefinition(beanName);
            beanFactory.registerBeanDefinition(beanName, beanDefinition);
        }
    }

}
