package com.minispring.context.support;

import com.minispring.beans.PropertyValues;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.BeanReference;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;

/**
 * ClassPathXmlApplicationContext
 * <p>
 * 从 classpath 加载 XML 配置的 ApplicationContext
 * <p>
 * 当前版本：简化实现
 * - 暂不实现 XML 解析
 * - 通过编程方式手动注册 BeanDefinition
 * - 后续版本可以扩展 XML 解析功能
 * <p>
 * 使用方式：
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
 *
 * @author mini-spring
 */
public class ClassPathXmlApplicationContext extends AbstractRefreshableApplicationContext {

    /**
     * BeanFactory，用于临时存储 BeanDefinition
     */
    private DefaultListableBeanFactory tempBeanFactory = new DefaultListableBeanFactory();

    /**
     * 无参构造函数
     * <p>
     * 创建容器，但不自动刷新
     * 需要手动调用 refresh() 方法
     */
    public ClassPathXmlApplicationContext() {
        // 不自动刷新
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
     * 将临时 BeanFactory 中的 BeanDefinition 复制到正式的 BeanFactory
     * <p>
     * 注意：
     * - 实际的 Spring 会解析 XML 文件
     * - 这里是简化版本，直接使用已注册的 BeanDefinition
     *
     * @param beanFactory BeanFactory 实例
     */
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        // 将临时 BeanFactory 中的 BeanDefinition 复制到正式的 BeanFactory
        String[] beanNames = tempBeanFactory.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = tempBeanFactory.getBeanDefinition(beanName);
            beanFactory.registerBeanDefinition(beanName, beanDefinition);
        }
    }

}
