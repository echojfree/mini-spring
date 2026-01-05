package com.minispring.beans.factory.support;

import com.minispring.beans.factory.DisposableBean;
import com.minispring.beans.factory.InitializingBean;
import com.minispring.beans.factory.config.BeanDefinition;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Bean 初始化和销毁测试类
 * <p>
 * 测试目标：
 * 1. InitializingBean 接口的 afterPropertiesSet 方法调用
 * 2. init-method 配置的初始化方法调用
 * 3. DisposableBean 接口的 destroy 方法调用
 * 4. destroy-method 配置的销毁方法调用
 * 5. 初始化和销毁方法的执行顺序
 * 6. 单例 Bean 和原型 Bean 的销毁行为
 *
 * @author mini-spring
 */
public class BeanLifecycleTest {

    /**
     * 测试 Bean：实现了 InitializingBean 和 DisposableBean 接口
     */
    public static class LifecycleBean implements InitializingBean, DisposableBean {
        private String name;
        private boolean initialized = false;
        private boolean destroyed = false;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isInitialized() {
            return initialized;
        }

        public boolean isDestroyed() {
            return destroyed;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            System.out.println("InitializingBean.afterPropertiesSet() 被调用");
            this.initialized = true;
        }

        @Override
        public void destroy() throws Exception {
            System.out.println("DisposableBean.destroy() 被调用");
            this.destroyed = true;
        }
    }

    /**
     * 测试 Bean：使用自定义初始化和销毁方法
     */
    public static class CustomLifecycleBean {
        private String name;
        private boolean initialized = false;
        private boolean destroyed = false;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isInitialized() {
            return initialized;
        }

        public boolean isDestroyed() {
            return destroyed;
        }

        public void customInit() {
            System.out.println("customInit() 被调用");
            this.initialized = true;
        }

        public void customDestroy() {
            System.out.println("customDestroy() 被调用");
            this.destroyed = true;
        }
    }

    /**
     * 测试 InitializingBean 接口
     */
    @Test
    public void testInitializingBean() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(LifecycleBean.class);
        beanFactory.registerBeanDefinition("lifecycleBean", beanDefinition);

        // 获取 Bean
        LifecycleBean bean = beanFactory.getBean("lifecycleBean", LifecycleBean.class);

        // 验证初始化方法被调用
        assertTrue("afterPropertiesSet() 应该被调用", bean.isInitialized());
        System.out.println("✅ InitializingBean 接口测试通过");
    }

    /**
     * 测试自定义 init-method
     */
    @Test
    public void testCustomInitMethod() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 BeanDefinition，配置 init-method
        BeanDefinition beanDefinition = new BeanDefinition(CustomLifecycleBean.class);
        beanDefinition.setInitMethodName("customInit");
        beanFactory.registerBeanDefinition("customBean", beanDefinition);

        // 获取 Bean
        CustomLifecycleBean bean = beanFactory.getBean("customBean", CustomLifecycleBean.class);

        // 验证初始化方法被调用
        assertTrue("customInit() 应该被调用", bean.isInitialized());
        System.out.println("✅ 自定义 init-method 测试通过");
    }

    /**
     * 测试 DisposableBean 接口
     */
    @Test
    public void testDisposableBean() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(LifecycleBean.class);
        beanFactory.registerBeanDefinition("lifecycleBean", beanDefinition);

        // 获取 Bean
        LifecycleBean bean = beanFactory.getBean("lifecycleBean", LifecycleBean.class);

        // 验证初始化完成
        assertTrue(bean.isInitialized());
        assertFalse(bean.isDestroyed());

        // 销毁所有单例 Bean
        beanFactory.destroySingletons();

        // 验证销毁方法被调用
        assertTrue("destroy() 应该被调用", bean.isDestroyed());
        System.out.println("✅ DisposableBean 接口测试通过");
    }

    /**
     * 测试自定义 destroy-method
     */
    @Test
    public void testCustomDestroyMethod() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 BeanDefinition，配置 destroy-method
        BeanDefinition beanDefinition = new BeanDefinition(CustomLifecycleBean.class);
        beanDefinition.setDestroyMethodName("customDestroy");
        beanFactory.registerBeanDefinition("customBean", beanDefinition);

        // 获取 Bean
        CustomLifecycleBean bean = beanFactory.getBean("customBean", CustomLifecycleBean.class);

        // 验证未销毁
        assertFalse(bean.isDestroyed());

        // 销毁所有单例 Bean
        beanFactory.destroySingletons();

        // 验证销毁方法被调用
        assertTrue("customDestroy() 应该被调用", bean.isDestroyed());
        System.out.println("✅ 自定义 destroy-method 测试通过");
    }

    /**
     * 测试初始化和销毁方法的执行顺序
     */
    @Test
    public void testLifecycleOrder() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 创建测试 Bean
        OrderTestBean.reset();
        BeanDefinition beanDefinition = new BeanDefinition(OrderTestBean.class);
        beanDefinition.setInitMethodName("customInit");
        beanDefinition.setDestroyMethodName("customDestroy");
        beanFactory.registerBeanDefinition("orderBean", beanDefinition);

        // 获取 Bean（触发初始化）
        OrderTestBean bean = beanFactory.getBean("orderBean", OrderTestBean.class);

        // 验证初始化顺序
        assertEquals("应该先调用 afterPropertiesSet()", "afterPropertiesSet", OrderTestBean.order.get(0));
        assertEquals("应该后调用 customInit()", "customInit", OrderTestBean.order.get(1));

        // 销毁 Bean
        beanFactory.destroySingletons();

        // 验证销毁顺序
        assertEquals("应该先调用 destroy()", "destroy", OrderTestBean.order.get(2));
        assertEquals("应该后调用 customDestroy()", "customDestroy", OrderTestBean.order.get(3));

        System.out.println("✅ 生命周期方法执行顺序测试通过");
        System.out.println("初始化顺序: " + OrderTestBean.order.get(0) + " → " + OrderTestBean.order.get(1));
        System.out.println("销毁顺序: " + OrderTestBean.order.get(2) + " → " + OrderTestBean.order.get(3));
    }

    /**
     * 测试原型 Bean 不会被销毁
     */
    @Test
    public void testPrototypeBeanNotDestroyed() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册原型 Bean
        BeanDefinition beanDefinition = new BeanDefinition(LifecycleBean.class);
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        beanFactory.registerBeanDefinition("prototypeBean", beanDefinition);

        // 获取两个实例
        LifecycleBean bean1 = beanFactory.getBean("prototypeBean", LifecycleBean.class);
        LifecycleBean bean2 = beanFactory.getBean("prototypeBean", LifecycleBean.class);

        // 验证是不同实例
        assertNotSame(bean1, bean2);

        // 验证都被初始化
        assertTrue(bean1.isInitialized());
        assertTrue(bean2.isInitialized());

        // 销毁所有单例 Bean
        beanFactory.destroySingletons();

        // 验证原型 Bean 不会被销毁
        assertFalse("原型 Bean 不应该被容器销毁", bean1.isDestroyed());
        assertFalse("原型 Bean 不应该被容器销毁", bean2.isDestroyed());

        System.out.println("✅ 原型 Bean 不被销毁测试通过");
    }

    /**
     * 测试完整的 Bean 生命周期
     */
    @Test
    public void testCompleteLifecycle() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(LifecycleBean.class);
        beanFactory.registerBeanDefinition("lifecycleBean", beanDefinition);

        // 1. 获取 Bean（实例化 → 属性注入 → 初始化）
        LifecycleBean bean = beanFactory.getBean("lifecycleBean", LifecycleBean.class);

        // 验证生命周期阶段
        assertNotNull("Bean 应该被实例化", bean);
        assertTrue("Bean 应该被初始化", bean.isInitialized());
        assertFalse("Bean 不应该被销毁", bean.isDestroyed());

        // 2. 再次获取 Bean（应该从缓存获取）
        LifecycleBean bean2 = beanFactory.getBean("lifecycleBean", LifecycleBean.class);
        assertSame("应该是同一个实例（单例）", bean, bean2);

        // 3. 销毁 Bean
        beanFactory.destroySingletons();
        assertTrue("Bean 应该被销毁", bean.isDestroyed());

        System.out.println("✅ 完整的 Bean 生命周期测试通过");
        System.out.println("Bean 生命周期：");
        System.out.println("  1. 实例化 ✓");
        System.out.println("  2. 属性注入 ✓");
        System.out.println("  3. 初始化 ✓");
        System.out.println("  4. 使用 ✓");
        System.out.println("  5. 销毁 ✓");
    }

    /**
     * 用于测试执行顺序的 Bean
     */
    public static class OrderTestBean implements InitializingBean, DisposableBean {
        private static java.util.List<String> order = new java.util.ArrayList<>();

        public static void reset() {
            order.clear();
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            order.add("afterPropertiesSet");
        }

        public void customInit() {
            order.add("customInit");
        }

        @Override
        public void destroy() throws Exception {
            order.add("destroy");
        }

        public void customDestroy() {
            order.add("customDestroy");
        }
    }

}
