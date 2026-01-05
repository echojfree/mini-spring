package com.minispring.beans.factory.support;

import com.minispring.beans.factory.config.BeanDefinition;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 实例化策略测试类
 * <p>
 * 测试目标：
 * 1. SimpleInstantiationStrategy 使用反射创建实例
 * 2. CglibSubclassingInstantiationStrategy 使用 CGLIB 创建实例
 * 3. 两种策略创建的实例功能一致
 * 4. CGLIB 创建的是子类实例
 * 5. 策略可以动态切换
 *
 * @author mini-spring
 */
public class InstantiationStrategyTest {

    /**
     * 测试 Bean 类：普通类
     */
    public static class UserService {
        private String name = "UserService";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 测试 Bean 类：带无参构造器的类
     */
    public static class OrderService {
        private int count;

        public OrderService() {
            this.count = 0;
        }

        public int getCount() {
            return count;
        }

        public void increment() {
            count++;
        }
    }

    /**
     * 测试 SimpleInstantiationStrategy：使用反射创建实例
     */
    @Test
    public void testSimpleInstantiationStrategy() {
        // 创建实例化策略
        InstantiationStrategy strategy = new SimpleInstantiationStrategy();

        // 创建 BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);

        // 实例化 Bean
        Object bean = strategy.instantiate(beanDefinition);

        // 验证
        assertNotNull("Bean 实例不应为 null", bean);
        assertTrue("Bean 应该是 UserService 类型", bean instanceof UserService);
        assertEquals("UserService", ((UserService) bean).getName());

        System.out.println("✅ SimpleInstantiationStrategy 实例化成功");
        System.out.println("Bean 类型: " + bean.getClass().getName());
        System.out.println("是否是原始类: " + (bean.getClass() == UserService.class));
    }

    /**
     * 测试 CglibSubclassingInstantiationStrategy：使用 CGLIB 创建实例
     */
    @Test
    public void testCglibInstantiationStrategy() {
        // 创建 CGLIB 实例化策略
        InstantiationStrategy strategy = new CglibSubclassingInstantiationStrategy();

        // 创建 BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);

        // 实例化 Bean
        Object bean = strategy.instantiate(beanDefinition);

        // 验证
        assertNotNull("Bean 实例不应为 null", bean);
        assertTrue("Bean 应该是 UserService 类型（或子类）", bean instanceof UserService);
        assertEquals("UserService", ((UserService) bean).getName());

        System.out.println("✅ CglibSubclassingInstantiationStrategy 实例化成功");
        System.out.println("Bean 类型: " + bean.getClass().getName());
        System.out.println("是否是子类: " + (bean.getClass() != UserService.class));
        System.out.println("父类是: " + bean.getClass().getSuperclass().getName());
    }

    /**
     * 测试两种策略创建的实例功能一致
     */
    @Test
    public void testBothStrategiesWorkSame() {
        BeanDefinition beanDefinition = new BeanDefinition(OrderService.class);

        // 使用简单反射策略
        InstantiationStrategy simpleStrategy = new SimpleInstantiationStrategy();
        OrderService bean1 = (OrderService) simpleStrategy.instantiate(beanDefinition);

        // 使用 CGLIB 策略
        InstantiationStrategy cglibStrategy = new CglibSubclassingInstantiationStrategy();
        OrderService bean2 = (OrderService) cglibStrategy.instantiate(beanDefinition);

        // 验证两个实例功能一致
        assertEquals(0, bean1.getCount());
        assertEquals(0, bean2.getCount());

        bean1.increment();
        bean2.increment();

        assertEquals(1, bean1.getCount());
        assertEquals(1, bean2.getCount());

        System.out.println("✅ 两种策略创建的实例功能一致");
        System.out.println("简单反射实例: " + bean1.getClass().getName());
        System.out.println("CGLIB 实例: " + bean2.getClass().getName());
    }

    /**
     * 测试 BeanFactory 可以切换实例化策略
     */
    @Test
    public void testBeanFactoryWithDifferentStrategies() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 Bean
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 默认使用 CGLIB 策略
        UserService bean1 = beanFactory.getBean("userService", UserService.class);
        Class<?> class1 = bean1.getClass();

        // 切换到简单反射策略
        beanFactory.setInstantiationStrategy(new SimpleInstantiationStrategy());

        // 重新注册（清除缓存的效果）
        beanFactory.registerBeanDefinition("userService2", beanDefinition);
        UserService bean2 = beanFactory.getBean("userService2", UserService.class);
        Class<?> class2 = bean2.getClass();

        // 验证
        assertTrue("Bean1 应该是 CGLIB 创建的（子类）", class1 != UserService.class);
        assertTrue("Bean2 应该是反射创建的（原始类）", class2 == UserService.class);

        System.out.println("✅ BeanFactory 策略切换成功");
        System.out.println("CGLIB 策略创建的类: " + class1.getName());
        System.out.println("反射策略创建的类: " + class2.getName());
    }

    /**
     * 测试 CGLIB 创建的是子类
     */
    @Test
    public void testCglibCreatesSubclass() {
        InstantiationStrategy strategy = new CglibSubclassingInstantiationStrategy();
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);

        Object bean = strategy.instantiate(beanDefinition);

        // 验证是子类
        assertNotEquals("CGLIB 创建的应该是子类，不是原始类", UserService.class, bean.getClass());
        assertEquals("父类应该是 UserService", UserService.class, bean.getClass().getSuperclass());
        assertTrue("应该是 UserService 的实例", bean instanceof UserService);

        System.out.println("✅ CGLIB 创建子类验证成功");
        System.out.println("Bean 类: " + bean.getClass().getName());
        System.out.println("父类: " + bean.getClass().getSuperclass().getName());
        System.out.println("类名包含 CGLIB: " + bean.getClass().getName().contains("CGLIB"));
    }

    /**
     * 测试简单反射策略创建的是原始类
     */
    @Test
    public void testSimpleCreatesOriginalClass() {
        InstantiationStrategy strategy = new SimpleInstantiationStrategy();
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);

        Object bean = strategy.instantiate(beanDefinition);

        // 验证是原始类
        assertEquals("简单反射创建的应该是原始类", UserService.class, bean.getClass());
        assertTrue("应该是 UserService 的实例", bean instanceof UserService);

        System.out.println("✅ 简单反射创建原始类验证成功");
        System.out.println("Bean 类: " + bean.getClass().getName());
    }

}
