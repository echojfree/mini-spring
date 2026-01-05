package com.minispring.beans.factory.support;

import com.minispring.beans.PropertyValue;
import com.minispring.beans.PropertyValues;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.BeanReference;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 属性注入测试类
 * <p>
 * 测试目标：
 * 1. 普通属性值注入（String, int 等）
 * 2. Bean 引用注入（依赖注入）
 * 3. 多个属性同时注入
 * 4. PropertyValues 的功能
 *
 * @author mini-spring
 */
public class PropertyInjectionTest {

    /**
     * 测试 Bean：用户服务类（依赖 UserDao）
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

        public String query(String username) {
            return userDao.queryUser(username);
        }
    }

    /**
     * 测试 Bean：用户 DAO 类
     */
    public static class UserDao {
        private String daoName;

        public String getDaoName() {
            return daoName;
        }

        public void setDaoName(String daoName) {
            this.daoName = daoName;
        }

        public String queryUser(String username) {
            return daoName + " 查询用户: " + username;
        }
    }

    /**
     * 测试 PropertyValues 的基本功能
     */
    @Test
    public void testPropertyValues() {
        PropertyValues propertyValues = new PropertyValues();

        // 添加属性
        propertyValues.addPropertyValue("name", "test");
        propertyValues.addPropertyValue("age", 18);

        // 验证
        assertEquals(2, propertyValues.size());
        assertTrue(propertyValues.contains("name"));
        assertTrue(propertyValues.contains("age"));
        assertFalse(propertyValues.contains("notExist"));

        // 获取属性值
        PropertyValue pv = propertyValues.getPropertyValue("name");
        assertNotNull(pv);
        assertEquals("name", pv.getName());
        assertEquals("test", pv.getValue());

        System.out.println("✅ PropertyValues 基本功能测试通过");
    }

    /**
     * 测试属性值替换功能
     */
    @Test
    public void testPropertyValueReplacement() {
        PropertyValues propertyValues = new PropertyValues();

        // 添加属性
        propertyValues.addPropertyValue("name", "oldValue");
        assertEquals("oldValue", propertyValues.getPropertyValue("name").getValue());

        // 替换属性值
        propertyValues.addPropertyValue("name", "newValue");
        assertEquals(1, propertyValues.size());  // 数量不变
        assertEquals("newValue", propertyValues.getPropertyValue("name").getValue());

        System.out.println("✅ 属性值替换测试通过");
    }

    /**
     * 测试注入普通属性值（String）
     */
    @Test
    public void testInjectSimpleProperty() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 创建 BeanDefinition，设置属性值
        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue("daoName", "MyUserDao");

        BeanDefinition beanDefinition = new BeanDefinition(UserDao.class, propertyValues);

        // 注册并获取 Bean
        beanFactory.registerBeanDefinition("userDao", beanDefinition);
        UserDao userDao = beanFactory.getBean("userDao", UserDao.class);

        // 验证属性注入成功
        assertNotNull(userDao);
        assertEquals("MyUserDao", userDao.getDaoName());

        System.out.println("✅ 普通属性注入成功");
        System.out.println("DAO 名称: " + userDao.getDaoName());
    }

    /**
     * 测试注入 Bean 引用（依赖注入）
     */
    @Test
    public void testInjectBeanReference() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 1. 注册 UserDao
        PropertyValues daoPV = new PropertyValues();
        daoPV.addPropertyValue("daoName", "PrimaryUserDao");
        BeanDefinition daoBD = new BeanDefinition(UserDao.class, daoPV);
        beanFactory.registerBeanDefinition("userDao", daoBD);

        // 2. 注册 UserService，并注入 UserDao
        PropertyValues servicePV = new PropertyValues();
        servicePV.addPropertyValue("serviceName", "UserServiceImpl");
        // 使用 BeanReference 表示对另一个 Bean 的引用
        servicePV.addPropertyValue("userDao", new BeanReference("userDao"));
        BeanDefinition serviceBD = new BeanDefinition(UserService.class, servicePV);
        beanFactory.registerBeanDefinition("userService", serviceBD);

        // 3. 获取 UserService
        UserService userService = beanFactory.getBean("userService", UserService.class);

        // 验证
        assertNotNull(userService);
        assertEquals("UserServiceImpl", userService.getServiceName());
        assertNotNull(userService.getUserDao());
        assertEquals("PrimaryUserDao", userService.getUserDao().getDaoName());

        // 测试业务逻辑
        String result = userService.query("张三");
        assertTrue(result.contains("PrimaryUserDao"));
        assertTrue(result.contains("张三"));

        System.out.println("✅ Bean 引用注入成功");
        System.out.println("Service 名称: " + userService.getServiceName());
        System.out.println("DAO 名称: " + userService.getUserDao().getDaoName());
        System.out.println("查询结果: " + result);
    }

    /**
     * 测试多个属性同时注入
     */
    @Test
    public void testInjectMultipleProperties() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册 UserDao
        PropertyValues daoPV = new PropertyValues();
        daoPV.addPropertyValue("daoName", "MultiDao");
        BeanDefinition daoBD = new BeanDefinition(UserDao.class, daoPV);
        beanFactory.registerBeanDefinition("userDao", daoBD);

        // 注册 UserService，同时注入普通值和 Bean 引用
        PropertyValues servicePV = new PropertyValues();
        servicePV.addPropertyValue("serviceName", "MultiService");
        servicePV.addPropertyValue("userDao", new BeanReference("userDao"));
        BeanDefinition serviceBD = new BeanDefinition(UserService.class, servicePV);
        beanFactory.registerBeanDefinition("userService", serviceBD);

        // 获取并验证
        UserService userService = beanFactory.getBean("userService", UserService.class);

        assertEquals("MultiService", userService.getServiceName());
        assertEquals("MultiDao", userService.getUserDao().getDaoName());

        System.out.println("✅ 多属性同时注入成功");
    }

    /**
     * 测试依赖注入的完整流程
     * 验证 Bean 生命周期中的属性注入阶段
     */
    @Test
    public void testDependencyInjectionLifecycle() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册依赖的 Bean
        PropertyValues daoPV = new PropertyValues();
        daoPV.addPropertyValue("daoName", "LifecycleDao");
        beanFactory.registerBeanDefinition("userDao", new BeanDefinition(UserDao.class, daoPV));

        // 注册主 Bean
        PropertyValues servicePV = new PropertyValues();
        servicePV.addPropertyValue("serviceName", "LifecycleService");
        servicePV.addPropertyValue("userDao", new BeanReference("userDao"));
        beanFactory.registerBeanDefinition("userService", new BeanDefinition(UserService.class, servicePV));

        // 获取 Bean
        UserService userService = beanFactory.getBean("userService", UserService.class);

        // 验证生命周期的正确性
        // 1. 实例化完成
        assertNotNull(userService);

        // 2. 属性注入完成
        assertNotNull(userService.getServiceName());
        assertNotNull(userService.getUserDao());

        // 3. Bean 可以正常使用
        String result = userService.query("测试用户");
        assertNotNull(result);

        System.out.println("✅ 依赖注入生命周期验证成功");
        System.out.println("Bean 生命周期阶段：");
        System.out.println("  1. 实例化 ✓");
        System.out.println("  2. 属性注入 ✓");
        System.out.println("  3. 初始化（待实现）");
        System.out.println("查询结果: " + result);
    }

    /**
     * 测试单例 Bean 的属性注入只执行一次
     */
    @Test
    public void testSingletonPropertyInjection() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 注册单例 Bean
        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue("daoName", "SingletonDao");
        BeanDefinition beanDefinition = new BeanDefinition(UserDao.class, propertyValues);
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        beanFactory.registerBeanDefinition("userDao", beanDefinition);

        // 多次获取
        UserDao dao1 = beanFactory.getBean("userDao", UserDao.class);
        UserDao dao2 = beanFactory.getBean("userDao", UserDao.class);

        // 验证是同一个实例，属性值相同
        assertSame(dao1, dao2);
        assertEquals("SingletonDao", dao1.getDaoName());
        assertEquals("SingletonDao", dao2.getDaoName());

        System.out.println("✅ 单例 Bean 属性注入验证成功");
        System.out.println("两次获取是同一实例: " + (dao1 == dao2));
    }

}
