package com.minispring.beans.factory.support;

import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.BeanFactory;
import com.minispring.beans.factory.BeanFactoryAware;
import com.minispring.beans.factory.BeanNameAware;
import com.minispring.beans.factory.config.BeanDefinition;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Aware 接口测试类
 * <p>
 * 测试目标：
 * 1. BeanNameAware 接口的调用
 * 2. BeanFactoryAware 接口的调用
 * 3. Aware 接口的调用时机
 * 4. Aware 接口的调用顺序
 * 5. 通过 BeanFactory 动态获取其他 Bean
 *
 * @author mini-spring
 */
public class AwareInterfaceTest {

    /**
     * 测试 Bean：实现 BeanNameAware 接口
     */
    public static class BeanNameAwareBean implements BeanNameAware {
        private String beanName;

        @Override
        public void setBeanName(String beanName) {
            this.beanName = beanName;
            System.out.println("BeanNameAware.setBeanName() 被调用，Bean 名称: " + beanName);
        }

        public String getBeanName() {
            return beanName;
        }
    }

    /**
     * 测试 Bean：实现 BeanFactoryAware 接口
     */
    public static class BeanFactoryAwareBean implements BeanFactoryAware {
        private BeanFactory beanFactory;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
            System.out.println("BeanFactoryAware.setBeanFactory() 被调用");
        }

        public BeanFactory getBeanFactory() {
            return beanFactory;
        }
    }

    /**
     * 测试 Bean：同时实现两个 Aware 接口
     */
    public static class MultipleAwareBean implements BeanNameAware, BeanFactoryAware {
        private String beanName;
        private BeanFactory beanFactory;
        private static java.util.List<String> callOrder = new java.util.ArrayList<>();

        public static void reset() {
            callOrder.clear();
        }

        @Override
        public void setBeanName(String beanName) {
            this.beanName = beanName;
            callOrder.add("setBeanName");
            System.out.println("1. setBeanName() 被调用");
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
            callOrder.add("setBeanFactory");
            System.out.println("2. setBeanFactory() 被调用");
        }

        public String getBeanName() {
            return beanName;
        }

        public BeanFactory getBeanFactory() {
            return beanFactory;
        }

        public static java.util.List<String> getCallOrder() {
            return callOrder;
        }
    }

    /**
     * 服务 Bean：用于测试动态获取
     */
    public static class UserService {
        private String serviceName = "UserService";

        public String getServiceName() {
            return serviceName;
        }
    }

    /**
     * 工厂 Bean：使用 BeanFactory 动态创建对象
     */
    public static class ServiceFactory implements BeanFactoryAware {
        private BeanFactory beanFactory;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        public UserService getUserService() {
            // 动态获取 UserService
            return beanFactory.getBean("userService", UserService.class);
        }
    }

    /**
     * 测试 BeanNameAware 接口
     */
    @Test
    public void testBeanNameAware() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(BeanNameAwareBean.class);
        beanFactory.registerBeanDefinition("myBean", beanDefinition);

        // 获取 Bean
        BeanNameAwareBean bean = beanFactory.getBean("myBean", BeanNameAwareBean.class);

        // 验证 Bean 名称被正确设置
        assertNotNull(bean.getBeanName());
        assertEquals("Bean 名称应该是 'myBean'", "myBean", bean.getBeanName());

        System.out.println("✅ BeanNameAware 接口测试通过");
    }

    /**
     * 测试 BeanFactoryAware 接口
     */
    @Test
    public void testBeanFactoryAware() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(BeanFactoryAwareBean.class);
        beanFactory.registerBeanDefinition("factoryAwareBean", beanDefinition);

        // 获取 Bean
        BeanFactoryAwareBean bean = beanFactory.getBean("factoryAwareBean", BeanFactoryAwareBean.class);

        // 验证 BeanFactory 被正确设置
        assertNotNull(bean.getBeanFactory());
        assertSame("BeanFactory 应该是同一个实例", beanFactory, bean.getBeanFactory());

        System.out.println("✅ BeanFactoryAware 接口测试通过");
    }

    /**
     * 测试多个 Aware 接口的调用顺序
     */
    @Test
    public void testAwareInterfaceOrder() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 重置调用顺序
        MultipleAwareBean.reset();

        // 注册 BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(MultipleAwareBean.class);
        beanFactory.registerBeanDefinition("multipleAwareBean", beanDefinition);

        // 获取 Bean
        MultipleAwareBean bean = beanFactory.getBean("multipleAwareBean", MultipleAwareBean.class);

        // 验证调用顺序
        java.util.List<String> callOrder = MultipleAwareBean.getCallOrder();
        assertEquals("应该调用了 2 个方法", 2, callOrder.size());
        assertEquals("应该先调用 setBeanName", "setBeanName", callOrder.get(0));
        assertEquals("应该后调用 setBeanFactory", "setBeanFactory", callOrder.get(1));

        // 验证值都被正确设置
        assertEquals("multipleAwareBean", bean.getBeanName());
        assertSame(beanFactory, bean.getBeanFactory());

        System.out.println("✅ Aware 接口调用顺序测试通过");
        System.out.println("调用顺序: " + callOrder.get(0) + " → " + callOrder.get(1));
    }

    /**
     * 测试通过 BeanFactory 动态获取其他 Bean
     */
    @Test
    public void testDynamicBeanRetrieval() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 UserService
        BeanDefinition userServiceDef = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("userService", userServiceDef);

        // 注册 ServiceFactory
        BeanDefinition factoryDef = new BeanDefinition(ServiceFactory.class);
        beanFactory.registerBeanDefinition("serviceFactory", factoryDef);

        // 获取 ServiceFactory
        ServiceFactory factory = beanFactory.getBean("serviceFactory", ServiceFactory.class);

        // 通过工厂动态获取 UserService
        UserService userService = factory.getUserService();

        // 验证
        assertNotNull(userService);
        assertEquals("UserService", userService.getServiceName());

        // 验证是单例
        UserService userService2 = beanFactory.getBean("userService", UserService.class);
        assertSame("应该是同一个实例", userService, userService2);

        System.out.println("✅ 动态获取 Bean 测试通过");
        System.out.println("通过 BeanFactory 动态获取 UserService: " + userService.getServiceName());
    }

    /**
     * 测试 Aware 接口不会影响普通 Bean
     */
    @Test
    public void testNormalBeanNotAffected() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册普通 Bean（不实现 Aware 接口）
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("normalBean", beanDefinition);

        // 获取 Bean
        UserService bean = beanFactory.getBean("normalBean", UserService.class);

        // 验证正常工作
        assertNotNull(bean);
        assertEquals("UserService", bean.getServiceName());

        System.out.println("✅ 普通 Bean 不受影响测试通过");
    }

    /**
     * 测试完整的 Bean 生命周期（包含 Aware）
     */
    @Test
    public void testCompleteLifecycleWithAware() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 创建测试 Bean
        LifecycleTestBean.reset();
        BeanDefinition beanDefinition = new BeanDefinition(LifecycleTestBean.class);
        beanFactory.registerBeanDefinition("lifecycleBean", beanDefinition);

        // 获取 Bean（触发完整生命周期）
        LifecycleTestBean bean = beanFactory.getBean("lifecycleBean", LifecycleTestBean.class);

        // 验证生命周期顺序
        java.util.List<String> order = LifecycleTestBean.order;
        assertEquals("应该调用了 3 个方法", 3, order.size());
        assertEquals("应该先调用 setBeanName", "setBeanName", order.get(0));
        assertEquals("应该再调用 setBeanFactory", "setBeanFactory", order.get(1));
        assertEquals("应该最后调用 afterPropertiesSet", "afterPropertiesSet", order.get(2));

        System.out.println("✅ 完整生命周期测试通过");
        System.out.println("Bean 生命周期顺序：");
        System.out.println("  1. 实例化");
        System.out.println("  2. 属性注入");
        System.out.println("  3. BeanNameAware.setBeanName()");
        System.out.println("  4. BeanFactoryAware.setBeanFactory()");
        System.out.println("  5. InitializingBean.afterPropertiesSet()");
    }

    /**
     * 用于测试完整生命周期的 Bean
     */
    public static class LifecycleTestBean implements BeanNameAware, BeanFactoryAware,
            com.minispring.beans.factory.InitializingBean {
        private static java.util.List<String> order = new java.util.ArrayList<>();

        public static void reset() {
            order.clear();
        }

        @Override
        public void setBeanName(String beanName) {
            order.add("setBeanName");
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            order.add("setBeanFactory");
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            order.add("afterPropertiesSet");
        }
    }

}
