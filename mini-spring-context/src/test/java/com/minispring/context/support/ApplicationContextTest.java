package com.minispring.context.support;

import com.minispring.beans.PropertyValues;
import com.minispring.beans.factory.BeanNameAware;
import com.minispring.beans.factory.InitializingBean;
import com.minispring.beans.factory.config.BeanPostProcessor;
import com.minispring.beans.factory.config.BeanReference;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * ApplicationContext 测试类
 * <p>
 * 测试目标：
 * 1. ApplicationContext 的基本功能
 * 2. Bean 的预加载
 * 3. BeanPostProcessor 的自动注册
 * 4. 完整的 Bean 生命周期
 * 5. 容器的关闭和资源释放
 *
 * @author mini-spring
 */
public class ApplicationContextTest {

    /**
     * 测试 Bean
     */
    public static class UserService {
        private String serviceName = "UserService";

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String doSomething() {
            return "UserService is working";
        }
    }

    /**
     * 依赖注入测试 Bean
     */
    public static class OrderService {
        private UserService userService;
        private String orderName;

        public UserService getUserService() {
            return userService;
        }

        public void setUserService(UserService userService) {
            this.userService = userService;
        }

        public String getOrderName() {
            return orderName;
        }

        public void setOrderName(String orderName) {
            this.orderName = orderName;
        }

        public String createOrder() {
            return userService.doSomething() + " -> OrderService creates order";
        }
    }

    /**
     * 生命周期测试 Bean
     */
    public static class LifecycleBean implements BeanNameAware, InitializingBean {
        private String beanName;
        private boolean initialized = false;
        public static int instanceCount = 0;

        public LifecycleBean() {
            instanceCount++;
            System.out.println("  LifecycleBean 实例化，实例数量: " + instanceCount);
        }

        @Override
        public void setBeanName(String beanName) {
            this.beanName = beanName;
            System.out.println("  BeanNameAware.setBeanName(): " + beanName);
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            this.initialized = true;
            System.out.println("  InitializingBean.afterPropertiesSet()");
        }

        public String getBeanName() {
            return beanName;
        }

        public boolean isInitialized() {
            return initialized;
        }
    }

    /**
     * 自定义 BeanPostProcessor
     */
    public static class CustomBeanPostProcessor implements BeanPostProcessor {
        private static int beforeCount = 0;
        private static int afterCount = 0;

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            if (bean instanceof LifecycleBean) {
                beforeCount++;
                System.out.println("  BeanPostProcessor.postProcessBeforeInitialization()");
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof LifecycleBean) {
                afterCount++;
                System.out.println("  BeanPostProcessor.postProcessAfterInitialization()");
            }
            return bean;
        }

        public static void reset() {
            beforeCount = 0;
            afterCount = 0;
        }

        public static int getBeforeCount() {
            return beforeCount;
        }

        public static int getAfterCount() {
            return afterCount;
        }
    }

    /**
     * 测试 ApplicationContext 的基本功能
     */
    @Test
    public void testBasicFunctionality() throws Exception {
        // 创建容器
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();

        // 注册 Bean
        context.registerBean("userService", UserService.class);

        // 刷新容器
        context.refresh();

        // 获取 Bean
        UserService userService = context.getBean("userService", UserService.class);

        // 验证
        assertNotNull(userService);
        assertEquals("UserService", userService.getServiceName());
        assertEquals("UserService is working", userService.doSomething());

        System.out.println("✅ ApplicationContext 基本功能测试通过");

        // 关闭容器
        context.close();
    }

    /**
     * 测试 Bean 的预加载
     */
    @Test
    public void testPreInstantiation() throws Exception {
        // 重置实例计数
        LifecycleBean.instanceCount = 0;

        // 创建容器
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();

        // 注册 Bean
        context.registerBean("lifecycleBean", LifecycleBean.class);

        System.out.println("容器刷新前，Bean 未创建");
        assertEquals("刷新前不应该创建 Bean", 0, LifecycleBean.instanceCount);

        // 刷新容器（触发预加载）
        System.out.println("开始刷新容器...");
        context.refresh();

        System.out.println("容器刷新后，Bean 已创建");
        assertEquals("刷新后应该创建 Bean", 1, LifecycleBean.instanceCount);

        // 获取 Bean（不会再次创建）
        LifecycleBean bean1 = context.getBean("lifecycleBean", LifecycleBean.class);
        LifecycleBean bean2 = context.getBean("lifecycleBean", LifecycleBean.class);

        // 验证单例
        assertSame("应该是同一个实例", bean1, bean2);
        assertEquals("不应该再次创建", 1, LifecycleBean.instanceCount);

        System.out.println("✅ Bean 预加载测试通过");
        System.out.println("说明：ApplicationContext 在 refresh() 时就创建了所有单例 Bean");

        context.close();
    }

    /**
     * 测试依赖注入
     */
    @Test
    public void testDependencyInjection() throws Exception {
        // 创建容器
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();

        // 注册 UserService
        context.registerBean("userService", UserService.class);

        // 注册 OrderService 并注入 UserService
        PropertyValues pv = new PropertyValues();
        pv.addPropertyValue("orderName", "TestOrder");
        pv.addPropertyValue("userService", new BeanReference("userService"));
        context.registerBean("orderService", OrderService.class, pv);

        // 刷新容器
        context.refresh();

        // 获取 OrderService
        OrderService orderService = context.getBean("orderService", OrderService.class);

        // 验证
        assertNotNull(orderService);
        assertEquals("TestOrder", orderService.getOrderName());
        assertNotNull(orderService.getUserService());
        assertEquals("UserService", orderService.getUserService().getServiceName());

        // 验证业务逻辑
        String result = orderService.createOrder();
        assertTrue(result.contains("UserService is working"));
        assertTrue(result.contains("OrderService creates order"));

        System.out.println("✅ 依赖注入测试通过");
        System.out.println("OrderService 成功注入了 UserService: " + result);

        context.close();
    }

    /**
     * 测试 BeanPostProcessor 的自动注册
     */
    @Test
    public void testBeanPostProcessorAutoRegistration() throws Exception {
        // 重置计数器
        CustomBeanPostProcessor.reset();
        LifecycleBean.instanceCount = 0;

        // 创建容器
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();

        // 注册 BeanPostProcessor（作为普通 Bean）
        context.registerBean("customProcessor", CustomBeanPostProcessor.class);

        // 注册普通 Bean
        context.registerBean("lifecycleBean", LifecycleBean.class);

        // 刷新容器
        System.out.println("容器刷新，展示完整生命周期：");
        context.refresh();

        // 验证 BeanPostProcessor 被调用
        assertEquals("前置处理应该被调用", 1, CustomBeanPostProcessor.getBeforeCount());
        assertEquals("后置处理应该被调用", 1, CustomBeanPostProcessor.getAfterCount());

        // 验证 Bean 的状态
        LifecycleBean bean = context.getBean("lifecycleBean", LifecycleBean.class);
        assertTrue(bean.isInitialized());
        assertEquals("lifecycleBean", bean.getBeanName());

        System.out.println("✅ BeanPostProcessor 自动注册测试通过");
        System.out.println("说明：ApplicationContext 自动发现并注册了 BeanPostProcessor");

        context.close();
    }

    /**
     * 测试容器关闭
     */
    @Test
    public void testContextClose() throws Exception {
        // 创建容器
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();

        // 注册 Bean
        context.registerBean("userService", UserService.class);

        // 刷新容器
        context.refresh();

        // 获取 Bean
        UserService userService = context.getBean("userService", UserService.class);
        assertNotNull(userService);

        // 关闭容器
        context.close();

        System.out.println("✅ 容器关闭测试通过");
        System.out.println("说明：容器成功关闭并释放资源");
    }

    /**
     * 测试关闭钩子
     */
    @Test
    public void testShutdownHook() throws Exception {
        // 创建容器
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();

        // 注册 Bean
        context.registerBean("userService", UserService.class);

        // 刷新容器
        context.refresh();

        // 注册关闭钩子
        context.registerShutdownHook();

        // 获取 Bean
        UserService userService = context.getBean("userService", UserService.class);
        assertNotNull(userService);

        System.out.println("✅ 关闭钩子测试通过");
        System.out.println("说明：已注册 JVM 关闭钩子，JVM 退出时会自动关闭容器");

        // 注意：这里不调用 close()，让关闭钩子在 JVM 退出时执行
        // 实际测试中，关闭钩子会在所有测试结束后执行
    }

}
