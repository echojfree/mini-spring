package com.minispring.beans.factory.config;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * BeanDefinition 测试类
 * <p>
 * 测试目标：
 * 1. BeanDefinition 能否正确存储 Bean 的元数据
 * 2. 作用域设置是否正确
 * 3. 单例和原型标志是否正确
 *
 * @author mini-spring
 */
public class BeanDefinitionTest {

    /**
     * 测试 Bean 类：用于测试的简单类
     */
    static class TestBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void init() {
            System.out.println("TestBean 初始化");
        }

        public void destroy() {
            System.out.println("TestBean 销毁");
        }
    }

    /**
     * 测试创建 BeanDefinition
     */
    @Test
    public void testCreateBeanDefinition() {
        // 创建 BeanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class);

        // 验证 Class 正确
        assertEquals(TestBean.class, beanDefinition.getBeanClass());

        // 验证默认是单例
        assertTrue("默认应该是单例", beanDefinition.isSingleton());
        assertFalse("默认不应该是原型", beanDefinition.isPrototype());
        assertEquals(BeanDefinition.SCOPE_SINGLETON, beanDefinition.getScope());

        // 验证默认不是延迟初始化
        assertFalse("默认不应该延迟初始化", beanDefinition.isLazyInit());

        System.out.println("✅ BeanDefinition 创建成功");
    }

    /**
     * 测试设置作用域为单例
     */
    @Test
    public void testSetScopeSingleton() {
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class);

        // 设置为单例（虽然默认就是单例）
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);

        // 验证
        assertTrue("应该是单例", beanDefinition.isSingleton());
        assertFalse("不应该是原型", beanDefinition.isPrototype());
        assertEquals(BeanDefinition.SCOPE_SINGLETON, beanDefinition.getScope());

        System.out.println("✅ 单例作用域设置成功");
    }

    /**
     * 测试设置作用域为原型
     */
    @Test
    public void testSetScopePrototype() {
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class);

        // 设置为原型
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);

        // 验证
        assertFalse("不应该是单例", beanDefinition.isSingleton());
        assertTrue("应该是原型", beanDefinition.isPrototype());
        assertEquals(BeanDefinition.SCOPE_PROTOTYPE, beanDefinition.getScope());

        System.out.println("✅ 原型作用域设置成功");
    }

    /**
     * 测试延迟初始化设置
     */
    @Test
    public void testSetLazyInit() {
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class);

        // 设置延迟初始化
        beanDefinition.setLazyInit(true);

        // 验证
        assertTrue("应该是延迟初始化", beanDefinition.isLazyInit());

        // 取消延迟初始化
        beanDefinition.setLazyInit(false);
        assertFalse("不应该是延迟初始化", beanDefinition.isLazyInit());

        System.out.println("✅ 延迟初始化设置成功");
    }

    /**
     * 测试初始化方法和销毁方法设置
     */
    @Test
    public void testSetInitAndDestroyMethod() {
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class);

        // 设置初始化方法
        beanDefinition.setInitMethodName("init");
        assertEquals("init", beanDefinition.getInitMethodName());

        // 设置销毁方法
        beanDefinition.setDestroyMethodName("destroy");
        assertEquals("destroy", beanDefinition.getDestroyMethodName());

        System.out.println("✅ 初始化和销毁方法设置成功");
    }

    /**
     * 测试完整的 BeanDefinition 配置
     */
    @Test
    public void testFullBeanDefinitionConfiguration() {
        BeanDefinition beanDefinition = new BeanDefinition(TestBean.class);

        // 配置所有属性
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        beanDefinition.setLazyInit(true);
        beanDefinition.setInitMethodName("init");
        beanDefinition.setDestroyMethodName("destroy");

        // 验证所有配置
        assertEquals(TestBean.class, beanDefinition.getBeanClass());
        assertTrue(beanDefinition.isPrototype());
        assertFalse(beanDefinition.isSingleton());
        assertTrue(beanDefinition.isLazyInit());
        assertEquals("init", beanDefinition.getInitMethodName());
        assertEquals("destroy", beanDefinition.getDestroyMethodName());

        System.out.println("✅ 完整配置验证成功");
        System.out.println("Bean Class: " + beanDefinition.getBeanClass().getSimpleName());
        System.out.println("Scope: " + beanDefinition.getScope());
        System.out.println("Lazy Init: " + beanDefinition.isLazyInit());
        System.out.println("Init Method: " + beanDefinition.getInitMethodName());
        System.out.println("Destroy Method: " + beanDefinition.getDestroyMethodName());
    }

}
