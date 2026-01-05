package com.minispring.beans.factory.config;

import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * BeanPostProcessor 测试类
 * <p>
 * 测试目标：
 * 1. BeanPostProcessor 的前置处理方法调用
 * 2. BeanPostProcessor 的后置处理方法调用
 * 3. BeanPostProcessor 的调用顺序
 * 4. 多个 BeanPostProcessor 的责任链处理
 * 5. BeanPostProcessor 修改 Bean 属性
 * 6. BeanPostProcessor 返回代理对象（模拟 AOP）
 *
 * @author mini-spring
 */
public class BeanPostProcessorTest {

    /**
     * 测试 Bean
     */
    public static class UserService {
        private String serviceName = "UserService";
        private boolean processed = false;

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public boolean isProcessed() {
            return processed;
        }

        public void setProcessed(boolean processed) {
            this.processed = processed;
        }

        public String doSomething() {
            return "Original method";
        }
    }

    /**
     * 简单的 BeanPostProcessor：修改 Bean 属性
     */
    public static class CustomBeanPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            System.out.println("CustomBeanPostProcessor.postProcessBeforeInitialization: " + beanName);
            if (bean instanceof UserService) {
                ((UserService) bean).setProcessed(true);
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            System.out.println("CustomBeanPostProcessor.postProcessAfterInitialization: " + beanName);
            if (bean instanceof UserService) {
                ((UserService) bean).setServiceName("ProcessedUserService");
            }
            return bean;
        }
    }

    /**
     * 代理 BeanPostProcessor：返回代理对象（模拟 AOP）
     */
    public static class ProxyBeanPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            System.out.println("ProxyBeanPostProcessor.postProcessAfterInitialization: " + beanName);

            // 只对 UserService 创建代理
            if (bean instanceof UserService) {
                System.out.println("  创建 UserService 的代理对象");
                return new UserServiceProxy((UserService) bean);
            }
            return bean;
        }
    }

    /**
     * UserService 的代理类（模拟 AOP）
     */
    public static class UserServiceProxy extends UserService {
        private final UserService target;

        public UserServiceProxy(UserService target) {
            this.target = target;
        }

        @Override
        public String doSomething() {
            // 前置增强
            String result = "Proxy: Before -> " + target.doSomething() + " -> After";
            // 后置增强
            return result;
        }

        @Override
        public String getServiceName() {
            return target.getServiceName();
        }

        @Override
        public boolean isProcessed() {
            return target.isProcessed();
        }
    }

    /**
     * 记录调用顺序的 BeanPostProcessor
     */
    public static class OrderTrackingBeanPostProcessor implements BeanPostProcessor {
        private final String name;
        private static java.util.List<String> callOrder = new java.util.ArrayList<>();

        public OrderTrackingBeanPostProcessor(String name) {
            this.name = name;
        }

        public static void reset() {
            callOrder.clear();
        }

        public static java.util.List<String> getCallOrder() {
            return callOrder;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            callOrder.add(name + ".before");
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            callOrder.add(name + ".after");
            return bean;
        }
    }

    /**
     * 测试 BeanPostProcessor 的基本功能
     */
    @Test
    public void testBeanPostProcessor() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 BeanPostProcessor
        beanFactory.addBeanPostProcessor(new CustomBeanPostProcessor());

        // 注册 Bean
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 获取 Bean
        UserService userService = beanFactory.getBean("userService", UserService.class);

        // 验证 BeanPostProcessor 生效
        assertTrue("Bean 应该被前置处理", userService.isProcessed());
        assertEquals("Bean 应该被后置处理", "ProcessedUserService", userService.getServiceName());

        System.out.println("✅ BeanPostProcessor 基本功能测试通过");
    }

    /**
     * 测试多个 BeanPostProcessor 的执行顺序
     */
    @Test
    public void testMultipleBeanPostProcessors() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 重置调用顺序
        OrderTrackingBeanPostProcessor.reset();

        // 注册多个 BeanPostProcessor（按顺序）
        beanFactory.addBeanPostProcessor(new OrderTrackingBeanPostProcessor("Processor1"));
        beanFactory.addBeanPostProcessor(new OrderTrackingBeanPostProcessor("Processor2"));
        beanFactory.addBeanPostProcessor(new OrderTrackingBeanPostProcessor("Processor3"));

        // 注册 Bean
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 获取 Bean
        UserService userService = beanFactory.getBean("userService", UserService.class);

        // 验证调用顺序
        java.util.List<String> callOrder = OrderTrackingBeanPostProcessor.getCallOrder();
        assertEquals(6, callOrder.size());

        // 验证前置处理顺序
        assertEquals("Processor1.before", callOrder.get(0));
        assertEquals("Processor2.before", callOrder.get(1));
        assertEquals("Processor3.before", callOrder.get(2));

        // 验证后置处理顺序
        assertEquals("Processor1.after", callOrder.get(3));
        assertEquals("Processor2.after", callOrder.get(4));
        assertEquals("Processor3.after", callOrder.get(5));

        System.out.println("✅ 多个 BeanPostProcessor 执行顺序测试通过");
        System.out.println("调用顺序: " + String.join(" → ", callOrder));
    }

    /**
     * 测试 BeanPostProcessor 创建代理对象（模拟 AOP）
     */
    @Test
    public void testProxyCreation() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 BeanPostProcessor
        beanFactory.addBeanPostProcessor(new CustomBeanPostProcessor());
        beanFactory.addBeanPostProcessor(new ProxyBeanPostProcessor());

        // 注册 Bean
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 获取 Bean
        UserService userService = beanFactory.getBean("userService", UserService.class);

        // 验证是代理对象
        assertTrue("应该是代理对象", userService instanceof UserServiceProxy);

        // 验证代理功能
        String result = userService.doSomething();
        assertTrue("代理应该增强原始方法", result.contains("Proxy"));
        assertTrue("代理应该调用原始方法", result.contains("Original method"));

        // 验证原始属性仍然可访问
        assertTrue(userService.isProcessed());
        assertEquals("ProcessedUserService", userService.getServiceName());

        System.out.println("✅ BeanPostProcessor 创建代理对象测试通过");
        System.out.println("代理增强结果: " + result);
    }

    /**
     * 测试完整的 Bean 生命周期（包含 BeanPostProcessor）
     */
    @Test
    public void testCompleteLifecycle() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 创建生命周期跟踪 Bean
        LifecycleBean.reset();

        // 注册 BeanPostProcessor
        beanFactory.addBeanPostProcessor(new LifecycleBeanPostProcessor());

        // 注册 Bean
        BeanDefinition beanDefinition = new BeanDefinition(LifecycleBean.class);
        beanFactory.registerBeanDefinition("lifecycleBean", beanDefinition);

        // 获取 Bean
        LifecycleBean bean = beanFactory.getBean("lifecycleBean", LifecycleBean.class);

        // 验证生命周期顺序
        java.util.List<String> lifecycle = LifecycleBean.lifecycle;
        assertEquals(4, lifecycle.size());
        assertEquals("setBeanName", lifecycle.get(0));
        assertEquals("postProcessBefore", lifecycle.get(1));
        assertEquals("afterPropertiesSet", lifecycle.get(2));
        assertEquals("postProcessAfter", lifecycle.get(3));

        System.out.println("✅ 完整生命周期测试通过");
        System.out.println("Bean 生命周期：");
        System.out.println("  1. 实例化");
        System.out.println("  2. 属性注入");
        System.out.println("  3. BeanNameAware.setBeanName()");
        System.out.println("  4. BeanPostProcessor.postProcessBeforeInitialization()");
        System.out.println("  5. InitializingBean.afterPropertiesSet()");
        System.out.println("  6. BeanPostProcessor.postProcessAfterInitialization()");
    }

    /**
     * 生命周期跟踪 Bean
     */
    public static class LifecycleBean implements
            com.minispring.beans.factory.BeanNameAware,
            com.minispring.beans.factory.InitializingBean {
        private static java.util.List<String> lifecycle = new java.util.ArrayList<>();

        public static void reset() {
            lifecycle.clear();
        }

        @Override
        public void setBeanName(String beanName) {
            lifecycle.add("setBeanName");
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            lifecycle.add("afterPropertiesSet");
        }
    }

    /**
     * 生命周期跟踪 BeanPostProcessor
     */
    public static class LifecycleBeanPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof LifecycleBean) {
                LifecycleBean.lifecycle.add("postProcessBefore");
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof LifecycleBean) {
                LifecycleBean.lifecycle.add("postProcessAfter");
            }
            return bean;
        }
    }

}
