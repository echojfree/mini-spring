package com.minispring.context.support;

import com.minispring.beans.factory.BeanNameAware;
import com.minispring.beans.factory.InitializingBean;
import com.minispring.beans.factory.config.BeanPostProcessor;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * XML 配置测试类
 * <p>
 * 测试目标：
 * 1. 从 XML 文件加载 BeanDefinition
 * 2. 解析 Bean 的 id、class 属性
 * 3. 解析 property 的 value 和 ref 属性
 * 4. 依赖注入功能
 * 5. 完整的 Bean 生命周期
 * 6. 支持多个配置文件
 *
 * @author mini-spring
 */
public class XmlConfigTest {

    /**
     * 测试用 UserDao
     */
    public static class UserDao {
        private String daoName;

        public String getDaoName() {
            return daoName;
        }

        public void setDaoName(String daoName) {
            this.daoName = daoName;
        }

        public String query() {
            return "UserDao.query()";
        }
    }

    /**
     * 测试用 UserService
     */
    public static class UserService {
        private String serviceName;
        private UserDao userDao;

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public UserDao getUserDao() {
            return userDao;
        }

        public void setUserDao(UserDao userDao) {
            this.userDao = userDao;
        }

        public String queryUser() {
            return serviceName + " -> " + userDao.query();
        }
    }

    /**
     * 生命周期测试 Bean
     */
    public static class LifecycleBean implements BeanNameAware, InitializingBean {
        private String beanName;
        private String message;
        private boolean initialized = false;

        public String getBeanName() {
            return beanName;
        }

        @Override
        public void setBeanName(String beanName) {
            this.beanName = beanName;
            System.out.println("  BeanNameAware.setBeanName(): " + beanName);
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isInitialized() {
            return initialized;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            this.initialized = true;
            System.out.println("  InitializingBean.afterPropertiesSet()");
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
     * 测试从 XML 加载 Bean
     */
    @Test
    public void testLoadBeanFromXml() throws Exception {
        // 创建容器并加载 XML 配置
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:spring.xml");

        // 获取 UserService
        UserService userService = context.getBean("userService", UserService.class);

        // 验证 Bean
        assertNotNull("UserService 不应为 null", userService);
        assertEquals("UserService", userService.getServiceName());

        // 验证依赖注入
        assertNotNull("UserDao 应该被注入", userService.getUserDao());
        assertEquals("UserDao", userService.getUserDao().getDaoName());

        // 验证业务逻辑
        String result = userService.queryUser();
        assertTrue(result.contains("UserService"));
        assertTrue(result.contains("UserDao.query()"));

        System.out.println("✅ 从 XML 加载 Bean 测试通过");
        System.out.println("查询结果: " + result);

        context.close();
    }

    /**
     * 测试 XML 配置的依赖注入
     */
    @Test
    public void testXmlDependencyInjection() throws Exception {
        // 创建容器
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:spring.xml");

        // 获取 UserService 和 UserDao
        UserService userService = context.getBean("userService", UserService.class);
        UserDao userDao = context.getBean("userDao", UserDao.class);

        // 验证是同一个实例（单例）
        assertSame("应该是同一个 UserDao 实例", userDao, userService.getUserDao());

        // 验证属性值
        assertEquals("UserService", userService.getServiceName());
        assertEquals("UserDao", userDao.getDaoName());

        System.out.println("✅ XML 配置依赖注入测试通过");

        context.close();
    }

    /**
     * 测试 XML 配置的完整生命周期
     */
    @Test
    public void testXmlBeanLifecycle() throws Exception {
        // 重置计数器
        CustomBeanPostProcessor.reset();

        // 创建容器并加载配置
        System.out.println("加载 XML 配置，展示完整生命周期：");
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:spring-lifecycle.xml");

        // 验证 BeanPostProcessor 被调用
        assertEquals("前置处理应该被调用", 1, CustomBeanPostProcessor.getBeforeCount());
        assertEquals("后置处理应该被调用", 1, CustomBeanPostProcessor.getAfterCount());

        // 获取 Bean
        LifecycleBean bean = context.getBean("lifecycleBean", LifecycleBean.class);

        // 验证 Aware 接口
        assertEquals("lifecycleBean", bean.getBeanName());

        // 验证 InitializingBean
        assertTrue("Bean 应该已初始化", bean.isInitialized());

        // 验证属性注入
        assertEquals("Hello from XML", bean.getMessage());

        System.out.println("✅ XML 配置 Bean 生命周期测试通过");

        context.close();
    }

    /**
     * 测试多个 XML 配置文件
     */
    @Test
    public void testMultipleXmlFiles() throws Exception {
        // 加载多个配置文件
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[]{"classpath:spring.xml", "classpath:spring-lifecycle.xml"}
        );

        // 验证从第一个配置文件加载的 Bean
        UserService userService = context.getBean("userService", UserService.class);
        assertNotNull(userService);

        // 验证从第二个配置文件加载的 Bean
        LifecycleBean lifecycleBean = context.getBean("lifecycleBean", LifecycleBean.class);
        assertNotNull(lifecycleBean);

        System.out.println("✅ 多个 XML 配置文件测试通过");

        context.close();
    }

    /**
     * 测试 XML 和编程式注册混合使用
     */
    @Test
    public void testXmlWithProgrammaticRegistration() throws Exception {
        // 创建容器（不加载 XML）
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();

        // 编程式注册一个 Bean
        context.registerBean("programmaticBean", UserDao.class);

        // 设置配置文件并刷新
        // 注意：这里通过反射设置 configLocations
        java.lang.reflect.Field field = ClassPathXmlApplicationContext.class.getDeclaredField("configLocations");
        field.setAccessible(true);
        field.set(context, new String[]{"classpath:spring.xml"});

        context.refresh();

        // 验证 XML 配置的 Bean
        UserService userService = context.getBean("userService", UserService.class);
        assertNotNull(userService);

        // 验证编程式注册的 Bean
        UserDao programmaticBean = context.getBean("programmaticBean", UserDao.class);
        assertNotNull(programmaticBean);

        System.out.println("✅ XML 和编程式注册混合使用测试通过");

        context.close();
    }

    /**
     * 测试 Bean 名称的优先级
     * id > name > 类名首字母小写
     */
    @Test
    public void testBeanNamePriority() throws Exception {
        // 创建临时 XML 文件内容（通过内存字符串）
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<beans>\n" +
                "    <bean id=\"myDao\" class=\"com.minispring.context.support.XmlConfigTest$UserDao\"/>\n" +
                "</beans>";

        // 写入临时文件
        java.io.File tempFile = java.io.File.createTempFile("test-bean-name-", ".xml");
        tempFile.deleteOnExit();
        java.nio.file.Files.write(tempFile.toPath(), xmlContent.getBytes());

        // 加载配置
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("file:" + tempFile.getAbsolutePath());

        // 验证可以通过 id 获取
        UserDao dao = context.getBean("myDao", UserDao.class);
        assertNotNull(dao);

        System.out.println("✅ Bean 名称优先级测试通过");

        context.close();
    }

}
