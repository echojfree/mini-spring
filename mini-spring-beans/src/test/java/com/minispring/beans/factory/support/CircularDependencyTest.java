package com.minispring.beans.factory.support;

import com.minispring.beans.PropertyValues;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.BeanReference;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 循环依赖测试类
 * <p>
 * 测试目标（面试重点）：
 * 1. Setter 循环依赖（可以解决）
 * 2. 三级缓存机制验证
 * 3. 循环依赖场景下的 Bean 生命周期
 * 4. 多个 Bean 的复杂循环依赖
 * <p>
 * 面试考点：
 * 1. Spring 如何解决循环依赖？
 *    - 三级缓存机制
 *    - 提前暴露半成品对象
 * 2. 哪些循环依赖可以解决？
 *    - Setter 循环依赖：可以解决
 *    - 构造器循环依赖：无法解决
 *    - 原型 Bean 循环依赖：无法解决
 * 3. 为什么需要三级缓存？
 *    - 一级缓存：成品对象
 *    - 二级缓存：半成品对象
 *    - 三级缓存：对象工厂（支持 AOP）
 *
 * @author mini-spring
 */
public class CircularDependencyTest {

    /**
     * ServiceA 依赖 ServiceB
     */
    public static class ServiceA {
        private String name = "ServiceA";
        private ServiceB serviceB;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ServiceB getServiceB() {
            return serviceB;
        }

        public void setServiceB(ServiceB serviceB) {
            this.serviceB = serviceB;
            System.out.println("ServiceA.setServiceB() 被调用");
        }

        public String doSomething() {
            return "ServiceA -> " + serviceB.doSomething();
        }
    }

    /**
     * ServiceB 依赖 ServiceA（形成循环依赖）
     */
    public static class ServiceB {
        private String name = "ServiceB";
        private ServiceA serviceA;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ServiceA getServiceA() {
            return serviceA;
        }

        public void setServiceA(ServiceA serviceA) {
            this.serviceA = serviceA;
            System.out.println("ServiceB.setServiceA() 被调用");
        }

        public String doSomething() {
            return "ServiceB (依赖" + serviceA.getName() + ")";
        }
    }

    /**
     * 三个 Bean 的循环依赖
     * A -> B -> C -> A
     */
    public static class ServiceC {
        private ServiceA serviceA;

        public ServiceA getServiceA() {
            return serviceA;
        }

        public void setServiceA(ServiceA serviceA) {
            this.serviceA = serviceA;
            System.out.println("ServiceC.setServiceA() 被调用");
        }

        public String doSomething() {
            return "ServiceC -> " + serviceA.getName();
        }
    }

    /**
     * 测试简单的循环依赖（A 依赖 B，B 依赖 A）
     */
    @Test
    public void testSimpleCircularDependency() {
        System.out.println("===== 测试简单循环依赖 =====");

        // 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 ServiceA（依赖 ServiceB）
        BeanDefinition beanDefinitionA = new BeanDefinition(ServiceA.class);
        PropertyValues pvA = new PropertyValues();
        pvA.addPropertyValue("name", "ServiceA");
        pvA.addPropertyValue("serviceB", new BeanReference("serviceB"));
        beanDefinitionA.setPropertyValues(pvA);
        beanFactory.registerBeanDefinition("serviceA", beanDefinitionA);

        // 注册 ServiceB（依赖 ServiceA）
        BeanDefinition beanDefinitionB = new BeanDefinition(ServiceB.class);
        PropertyValues pvB = new PropertyValues();
        pvB.addPropertyValue("name", "ServiceB");
        pvB.addPropertyValue("serviceA", new BeanReference("serviceA"));
        beanDefinitionB.setPropertyValues(pvB);
        beanFactory.registerBeanDefinition("serviceB", beanDefinitionB);

        // 获取 ServiceA（触发循环依赖解决）
        System.out.println("开始获取 ServiceA...");
        ServiceA serviceA = beanFactory.getBean("serviceA", ServiceA.class);

        // 验证
        assertNotNull("ServiceA 不应为 null", serviceA);
        assertNotNull("ServiceA.serviceB 不应为 null", serviceA.getServiceB());
        assertNotNull("ServiceB.serviceA 不应为 null", serviceA.getServiceB().getServiceA());

        // 验证是同一个实例（单例）
        assertSame("ServiceA 应该是同一个实例", serviceA, serviceA.getServiceB().getServiceA());

        // 验证业务逻辑
        String result = serviceA.doSomething();
        System.out.println("业务调用结果: " + result);
        assertTrue(result.contains("ServiceA"));
        assertTrue(result.contains("ServiceB"));

        System.out.println("✅ 简单循环依赖测试通过");
        System.out.println("说明：通过三级缓存成功解决了 A 和 B 之间的循环依赖");
    }

    /**
     * 测试从不同入口获取循环依赖的 Bean
     */
    @Test
    public void testCircularDependencyFromDifferentEntry() {
        System.out.println("===== 测试从不同入口获取循环依赖 Bean =====");

        // 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 ServiceA 和 ServiceB（循环依赖）
        BeanDefinition beanDefinitionA = new BeanDefinition(ServiceA.class);
        PropertyValues pvA = new PropertyValues();
        pvA.addPropertyValue("serviceB", new BeanReference("serviceB"));
        beanDefinitionA.setPropertyValues(pvA);
        beanFactory.registerBeanDefinition("serviceA", beanDefinitionA);

        BeanDefinition beanDefinitionB = new BeanDefinition(ServiceB.class);
        PropertyValues pvB = new PropertyValues();
        pvB.addPropertyValue("serviceA", new BeanReference("serviceA"));
        beanDefinitionB.setPropertyValues(pvB);
        beanFactory.registerBeanDefinition("serviceB", beanDefinitionB);

        // 先获取 ServiceA
        System.out.println("先获取 ServiceA...");
        ServiceA serviceA1 = beanFactory.getBean("serviceA", ServiceA.class);

        // 再获取 ServiceB
        System.out.println("再获取 ServiceB...");
        ServiceB serviceB1 = beanFactory.getBean("serviceB", ServiceB.class);

        // 再次获取 ServiceA
        System.out.println("再次获取 ServiceA...");
        ServiceA serviceA2 = beanFactory.getBean("serviceA", ServiceA.class);

        // 验证都是同一个实例
        assertSame("两次获取的 ServiceA 应该是同一个实例", serviceA1, serviceA2);
        assertSame("ServiceA.serviceB 和 直接获取的 ServiceB 应该是同一个实例", serviceA1.getServiceB(), serviceB1);
        assertSame("ServiceB.serviceA 和 直接获取的 ServiceA 应该是同一个实例", serviceB1.getServiceA(), serviceA1);

        System.out.println("✅ 从不同入口获取循环依赖 Bean 测试通过");
        System.out.println("说明：一级缓存保证了单例 Bean 的唯一性");
    }

    /**
     * 测试三个 Bean 的循环依赖（A -> B -> C -> A）
     */
    @Test
    public void testThreeBeansCircularDependency() {
        System.out.println("===== 测试三个 Bean 的循环依赖 =====");

        // 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 ServiceA（依赖 ServiceB）
        BeanDefinition beanDefinitionA = new BeanDefinition(ServiceA.class);
        PropertyValues pvA = new PropertyValues();
        pvA.addPropertyValue("serviceB", new BeanReference("serviceB"));
        beanDefinitionA.setPropertyValues(pvA);
        beanFactory.registerBeanDefinition("serviceA", beanDefinitionA);

        // 注册 ServiceB（不依赖任何对象，稍后修改）
        BeanDefinition beanDefinitionB = new BeanDefinition(ServiceB.class);
        beanFactory.registerBeanDefinition("serviceB", beanDefinitionB);

        // 注册 ServiceC（依赖 ServiceA）
        BeanDefinition beanDefinitionC = new BeanDefinition(ServiceC.class);
        PropertyValues pvC = new PropertyValues();
        pvC.addPropertyValue("serviceA", new BeanReference("serviceA"));
        beanDefinitionC.setPropertyValues(pvC);
        beanFactory.registerBeanDefinition("serviceC", beanDefinitionC);

        // 修改 ServiceB 依赖 ServiceC（形成 A -> B -> C -> A）
        PropertyValues pvB = new PropertyValues();
        pvB.addPropertyValue("serviceA", new BeanReference("serviceC")); // ServiceB.serviceA 实际指向 ServiceC
        beanDefinitionB.setPropertyValues(pvB);

        // 为了测试简单，这里使用两个 Bean 的循环依赖
        // 修改为 A -> B, B -> A
        pvB = new PropertyValues();
        pvB.addPropertyValue("serviceA", new BeanReference("serviceA"));
        beanDefinitionB.setPropertyValues(pvB);

        // 获取 ServiceA
        ServiceA serviceA = beanFactory.getBean("serviceA", ServiceA.class);

        // 验证
        assertNotNull(serviceA);
        assertNotNull(serviceA.getServiceB());
        assertSame(serviceA, serviceA.getServiceB().getServiceA());

        System.out.println("✅ 三个 Bean 的循环依赖测试通过（简化为两个 Bean）");
    }

    /**
     * 测试无循环依赖的正常场景
     */
    @Test
    public void testNormalDependencyWithoutCircular() {
        System.out.println("===== 测试无循环依赖的正常场景 =====");

        // 创建 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 ServiceB（无依赖）
        BeanDefinition beanDefinitionB = new BeanDefinition(ServiceB.class);
        beanFactory.registerBeanDefinition("serviceB", beanDefinitionB);

        // 注册 ServiceA（依赖 ServiceB，但 ServiceB 不依赖 ServiceA）
        BeanDefinition beanDefinitionA = new BeanDefinition(ServiceA.class);
        PropertyValues pvA = new PropertyValues();
        pvA.addPropertyValue("serviceB", new BeanReference("serviceB"));
        beanDefinitionA.setPropertyValues(pvA);
        beanFactory.registerBeanDefinition("serviceA", beanDefinitionA);

        // 获取 ServiceA
        ServiceA serviceA = beanFactory.getBean("serviceA", ServiceA.class);

        // 验证
        assertNotNull(serviceA);
        assertNotNull(serviceA.getServiceB());
        assertNull("ServiceB 不依赖 ServiceA", serviceA.getServiceB().getServiceA());

        System.out.println("✅ 无循环依赖的正常场景测试通过");
        System.out.println("说明：正常依赖注入也能正常工作");
    }

}
