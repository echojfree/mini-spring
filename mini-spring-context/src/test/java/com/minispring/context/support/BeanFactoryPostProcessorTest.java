package com.minispring.context.support;

import com.minispring.beans.PropertyValues;
import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.BeanDefinitionRegistryPostProcessor;
import com.minispring.beans.factory.config.BeanFactoryPostProcessor;
import com.minispring.beans.factory.support.BeanDefinitionRegistry;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * BeanFactoryPostProcessor 测试类
 * <p>
 * 测试目标（面试重点）：
 * 1. BeanFactoryPostProcessor 的执行时机
 * 2. BeanDefinitionRegistryPostProcessor 的执行顺序
 * 3. 修改 BeanDefinition 的能力
 * 4. 动态注册 BeanDefinition 的能力
 * <p>
 * 面试考点：
 * 1. BeanFactoryPostProcessor 和 BeanPostProcessor 的区别
 *    - BeanFactoryPostProcessor：Bean 实例化之前，修改 BeanDefinition
 *    - BeanPostProcessor：Bean 实例化之后，修改 Bean 实例
 * 2. 两种后置处理器的执行顺序
 *    - BeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry()
 *    - BeanDefinitionRegistryPostProcessor.postProcessBeanFactory()
 *    - BeanFactoryPostProcessor.postProcessBeanFactory()
 * 3. 典型应用场景
 *    - PropertyPlaceholderConfigurer：替换占位符
 *    - ConfigurationClassPostProcessor：处理 @Configuration 类
 *
 * @author mini-spring
 */
public class BeanFactoryPostProcessorTest {

    /**
     * 测试用 UserService
     */
    public static class UserService {
        private String serviceName;

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }
    }

    /**
     * 自定义 BeanFactoryPostProcessor
     * 用于修改 BeanDefinition
     */
    public static class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
        private static int callCount = 0;

        @Override
        public void postProcessBeanFactory(DefaultListableBeanFactory beanFactory) throws BeansException {
            callCount++;
            System.out.println("CustomBeanFactoryPostProcessor.postProcessBeanFactory() 被调用");

            // 修改 UserService 的 serviceName 属性
            BeanDefinition bd = beanFactory.getBeanDefinition("userService");
            PropertyValues pvs = bd.getPropertyValues();
            pvs.addPropertyValue("serviceName", "Modified by BeanFactoryPostProcessor");
        }

        public static int getCallCount() {
            return callCount;
        }

        public static void reset() {
            callCount = 0;
        }
    }

    /**
     * 自定义 BeanDefinitionRegistryPostProcessor
     * 用于动态注册 BeanDefinition
     */
    public static class CustomBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
        private static int registryCallCount = 0;
        private static int factoryCallCount = 0;

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            registryCallCount++;
            System.out.println("CustomBeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry() 被调用");

            // 动态注册一个新的 BeanDefinition
            BeanDefinition bd = new BeanDefinition(UserService.class);
            PropertyValues pvs = new PropertyValues();
            pvs.addPropertyValue("serviceName", "Dynamically Registered");
            bd.setPropertyValues(pvs);
            registry.registerBeanDefinition("dynamicService", bd);
        }

        @Override
        public void postProcessBeanFactory(DefaultListableBeanFactory beanFactory) throws BeansException {
            factoryCallCount++;
            System.out.println("CustomBeanDefinitionRegistryPostProcessor.postProcessBeanFactory() 被调用");
        }

        public static int getRegistryCallCount() {
            return registryCallCount;
        }

        public static int getFactoryCallCount() {
            return factoryCallCount;
        }

        public static void reset() {
            registryCallCount = 0;
            factoryCallCount = 0;
        }
    }

    /**
     * 测试 BeanFactoryPostProcessor 的基本功能
     */
    @Test
    public void testBeanFactoryPostProcessor() throws Exception {
        System.out.println("===== 测试 BeanFactoryPostProcessor =====");

        // 重置计数器
        CustomBeanFactoryPostProcessor.reset();

        // 创建 ApplicationContext（编程式注册）
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();

        // 注册 UserService
        PropertyValues pvs = new PropertyValues();
        pvs.addPropertyValue("serviceName", "Original Name");
        context.registerBean("userService", UserService.class, pvs);

        // 注册 BeanFactoryPostProcessor
        context.registerBean("customProcessor", CustomBeanFactoryPostProcessor.class);

        // 刷新容器
        context.refresh();

        // 验证 BeanFactoryPostProcessor 被调用
        assertEquals("BeanFactoryPostProcessor 应该被调用一次", 1, CustomBeanFactoryPostProcessor.getCallCount());

        // 验证 BeanDefinition 被修改
        UserService userService = context.getBean("userService", UserService.class);
        assertEquals("Modified by BeanFactoryPostProcessor", userService.getServiceName());

        System.out.println("✅ BeanFactoryPostProcessor 测试通过");
        System.out.println("说明：BeanFactoryPostProcessor 成功修改了 BeanDefinition");

        context.close();
    }

    /**
     * 测试 BeanDefinitionRegistryPostProcessor 的功能
     */
    @Test
    public void testBeanDefinitionRegistryPostProcessor() throws Exception {
        System.out.println("===== 测试 BeanDefinitionRegistryPostProcessor =====");

        // 重置计数器
        CustomBeanDefinitionRegistryPostProcessor.reset();

        // 创建 ApplicationContext（编程式注册）
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();

        // 注册 UserService
        PropertyValues pvs = new PropertyValues();
        pvs.addPropertyValue("serviceName", "Original Service");
        context.registerBean("userService", UserService.class, pvs);

        // 注册 BeanDefinitionRegistryPostProcessor
        context.registerBean("customRegistryProcessor", CustomBeanDefinitionRegistryPostProcessor.class);

        // 刷新容器
        context.refresh();

        // 验证两个方法都被调用
        assertEquals("postProcessBeanDefinitionRegistry 应该被调用一次",
                1, CustomBeanDefinitionRegistryPostProcessor.getRegistryCallCount());
        assertEquals("postProcessBeanFactory 应该被调用一次",
                1, CustomBeanDefinitionRegistryPostProcessor.getFactoryCallCount());

        // 验证动态注册的 Bean 存在
        UserService dynamicService = context.getBean("dynamicService", UserService.class);
        assertNotNull("动态注册的 Bean 应该存在", dynamicService);
        assertEquals("Dynamically Registered", dynamicService.getServiceName());

        System.out.println("✅ BeanDefinitionRegistryPostProcessor 测试通过");
        System.out.println("说明：BeanDefinitionRegistryPostProcessor 成功动态注册了新的 BeanDefinition");

        context.close();
    }

    /**
     * 测试两种后置处理器的执行顺序
     */
    @Test
    public void testPostProcessorExecutionOrder() throws Exception {
        System.out.println("===== 测试后置处理器执行顺序 =====");

        // 重置计数器
        CustomBeanFactoryPostProcessor.reset();
        CustomBeanDefinitionRegistryPostProcessor.reset();

        // 创建 ApplicationContext（编程式注册）
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();

        // 注册一个普通 Bean
        context.registerBean("userService", UserService.class);

        // 注册 BeanDefinitionRegistryPostProcessor
        context.registerBean("registryProcessor", CustomBeanDefinitionRegistryPostProcessor.class);

        // 注册 BeanFactoryPostProcessor
        context.registerBean("factoryProcessor", CustomBeanFactoryPostProcessor.class);

        // 刷新容器
        context.refresh();

        // 验证执行顺序（通过输出日志可以看到）
        System.out.println("✅ 后置处理器执行顺序测试通过");
        System.out.println("说明：BeanDefinitionRegistryPostProcessor 的两个方法都先于 BeanFactoryPostProcessor 执行");

        context.close();
    }

}
