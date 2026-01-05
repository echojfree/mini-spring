package com.minispring.beans.factory.support;

import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.config.BeanDefinition;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * BeanFactory 测试类
 * <p>
 * 测试目标：
 * 1. BeanDefinition 的注册功能
 * 2. Bean 的获取功能（getBean）
 * 3. 单例 Bean 的缓存机制
 * 4. 原型 Bean 每次创建新实例
 * 5. 异常情况处理
 *
 * @author mini-spring
 */
public class BeanFactoryTest {

    private DefaultListableBeanFactory beanFactory;

    /**
     * 测试 Bean 类：简单的用户服务类
     */
    public static class UserService {
        private String serviceName = "UserService";

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }
    }

    /**
     * 测试 Bean 类：订单服务类
     */
    public static class OrderService {
        private String serviceName = "OrderService";

        public String getServiceName() {
            return serviceName;
        }
    }

    /**
     * 每个测试方法执行前，创建新的 BeanFactory
     */
    @Before
    public void setUp() {
        beanFactory = new DefaultListableBeanFactory();
    }

    /**
     * 测试注册 BeanDefinition
     */
    @Test
    public void testRegisterBeanDefinition() {
        // 创建 BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);

        // 注册到容器
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 验证注册成功
        assertTrue("应该包含 userService", beanFactory.containsBeanDefinition("userService"));
        assertNotNull("应该能获取到 BeanDefinition", beanFactory.getBeanDefinition("userService"));

        System.out.println("✅ BeanDefinition 注册成功");
    }

    /**
     * 测试获取单例 Bean
     */
    @Test
    public void testGetSingletonBean() {
        // 注册单例 Bean
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 第一次获取 Bean
        Object bean1 = beanFactory.getBean("userService");
        assertNotNull("Bean 实例不应为 null", bean1);
        assertTrue("Bean 应该是 UserService 类型", bean1 instanceof UserService);

        // 第二次获取 Bean
        Object bean2 = beanFactory.getBean("userService");
        assertNotNull("Bean 实例不应为 null", bean2);

        // 验证是同一个实例（单例）
        assertSame("单例 Bean 应该是同一个实例", bean1, bean2);

        System.out.println("✅ 单例 Bean 获取成功，两次获取返回同一实例");
        System.out.println("Bean1: " + System.identityHashCode(bean1));
        System.out.println("Bean2: " + System.identityHashCode(bean2));
    }

    /**
     * 测试获取原型 Bean
     */
    @Test
    public void testGetPrototypeBean() {
        // 注册原型 Bean
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 第一次获取 Bean
        Object bean1 = beanFactory.getBean("userService");
        assertNotNull("Bean 实例不应为 null", bean1);

        // 第二次获取 Bean
        Object bean2 = beanFactory.getBean("userService");
        assertNotNull("Bean 实例不应为 null", bean2);

        // 验证是不同的实例（原型）
        assertNotSame("原型 Bean 应该是不同的实例", bean1, bean2);

        System.out.println("✅ 原型 Bean 获取成功，两次获取返回不同实例");
        System.out.println("Bean1: " + System.identityHashCode(bean1));
        System.out.println("Bean2: " + System.identityHashCode(bean2));
    }

    /**
     * 测试使用泛型方法获取 Bean
     */
    @Test
    public void testGetBeanWithType() {
        // 注册 Bean
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 使用泛型方法获取 Bean，无需类型转换
        UserService userService = beanFactory.getBean("userService", UserService.class);

        assertNotNull("Bean 实例不应为 null", userService);
        assertEquals("UserService", userService.getServiceName());

        System.out.println("✅ 泛型方法获取 Bean 成功");
        System.out.println("Service Name: " + userService.getServiceName());
    }

    /**
     * 测试类型不匹配的情况
     */
    @Test
    public void testGetBeanWithWrongType() {
        // 注册 UserService
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        try {
            // 尝试以错误的类型获取 Bean
            beanFactory.getBean("userService", OrderService.class);
            fail("应该抛出 BeansException");
        } catch (BeansException e) {
            // 预期抛出异常
            System.out.println("✅ 正确抛出类型不匹配异常: " + e.getMessage());
            assertTrue(e.getMessage().contains("类型不匹配"));
        }
    }

    /**
     * 测试获取不存在的 Bean
     */
    @Test
    public void testGetNonExistentBean() {
        try {
            beanFactory.getBean("nonExistent");
            fail("应该抛出 BeansException");
        } catch (BeansException e) {
            // 预期抛出异常
            System.out.println("✅ 正确抛出 Bean 不存在异常: " + e.getMessage());
            assertTrue(e.getMessage().contains("Bean 不存在"));
        }
    }

    /**
     * 测试注册多个 Bean
     */
    @Test
    public void testRegisterMultipleBeans() {
        // 注册多个 Bean
        beanFactory.registerBeanDefinition("userService",
                new BeanDefinition(UserService.class));
        beanFactory.registerBeanDefinition("orderService",
                new BeanDefinition(OrderService.class));

        // 获取所有 Bean 名称
        String[] beanNames = beanFactory.getBeanDefinitionNames();

        assertEquals("应该有 2 个 Bean", 2, beanNames.length);
        assertTrue("应该包含 userService", beanFactory.containsBeanDefinition("userService"));
        assertTrue("应该包含 orderService", beanFactory.containsBeanDefinition("orderService"));

        // 获取并验证
        UserService userService = beanFactory.getBean("userService", UserService.class);
        OrderService orderService = beanFactory.getBean("orderService", OrderService.class);

        assertNotNull(userService);
        assertNotNull(orderService);
        assertEquals("UserService", userService.getServiceName());
        assertEquals("OrderService", orderService.getServiceName());

        System.out.println("✅ 多个 Bean 注册和获取成功");
        System.out.println("注册的 Bean 数量: " + beanNames.length);
        for (String name : beanNames) {
            System.out.println("  - " + name);
        }
    }

    /**
     * 测试 Bean 名称为空的情况
     */
    @Test
    public void testRegisterBeanWithEmptyName() {
        try {
            BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
            beanFactory.registerBeanDefinition("", beanDefinition);
            fail("应该抛出 BeansException");
        } catch (BeansException e) {
            // 预期抛出异常
            System.out.println("✅ 正确抛出空名称异常: " + e.getMessage());
            assertTrue(e.getMessage().contains("不能为空"));
        }
    }

    /**
     * 测试 BeanDefinition 为 null 的情况
     */
    @Test
    public void testRegisterNullBeanDefinition() {
        try {
            beanFactory.registerBeanDefinition("test", null);
            fail("应该抛出 BeansException");
        } catch (BeansException e) {
            // 预期抛出异常
            System.out.println("✅ 正确抛出 null BeanDefinition 异常: " + e.getMessage());
            assertTrue(e.getMessage().contains("不能为 null"));
        }
    }

    /**
     * 测试单例 Bean 的缓存机制
     * 验证第二次获取时不会再次创建实例
     */
    @Test
    public void testSingletonBeanCache() {
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 第一次获取
        long startTime1 = System.nanoTime();
        UserService bean1 = beanFactory.getBean("userService", UserService.class);
        long endTime1 = System.nanoTime();

        // 第二次获取（应该从缓存获取，速度更快）
        long startTime2 = System.nanoTime();
        UserService bean2 = beanFactory.getBean("userService", UserService.class);
        long endTime2 = System.nanoTime();

        assertSame("应该是同一个实例", bean1, bean2);

        long time1 = endTime1 - startTime1;
        long time2 = endTime2 - startTime2;

        System.out.println("✅ 单例缓存机制验证成功");
        System.out.println("第一次获取耗时: " + time1 + " ns（包含实例化）");
        System.out.println("第二次获取耗时: " + time2 + " ns（从缓存获取）");
        System.out.println("性能提升: " + (time2 < time1 ? "是" : "否"));
    }

}
