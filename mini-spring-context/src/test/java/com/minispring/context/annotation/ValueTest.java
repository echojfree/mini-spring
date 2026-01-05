package com.minispring.context.annotation;

import com.minispring.beans.factory.config.PropertyPlaceholderConfigurer;
import com.minispring.test.beans.DatabaseConfig;
import com.minispring.test.beans.ServerConfig;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Value 注解测试类
 * <p>
 * 测试目标（面试重点）：
 * 1. @Value 注解的创建
 * 2. PropertyPlaceholderConfigurer 的实现
 * 3. PlaceholderResolver 的占位符解析
 * 4. 类型转换功能
 * <p>
 * 面试考点：
 * 1. @Value 的工作原理
 *    - BeanPostProcessor 在属性填充阶段处理
 *    - PropertyPlaceholderConfigurer 解析占位符
 *    - 自动进行类型转换
 * 2. 占位符语法
 *    - ${key}: 必需属性
 *    - ${key:defaultValue}: 可选属性
 * 3. @Value vs @Autowired
 *    - @Value: 注入配置值
 *    - @Autowired: 注入 Bean 对象
 *
 * @author mini-spring
 */
public class ValueTest {

    /**
     * 测试占位符解析功能
     */
    @Test
    public void testPlaceholderResolver() throws Exception {
        System.out.println("===== 测试占位符解析 =====");

        // 创建 PropertyPlaceholderConfigurer
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocation("classpath:application.properties");

        // 测试占位符解析
        String url = configurer.resolvePlaceholder("${db.url}");
        String username = configurer.resolvePlaceholder("${db.username}");
        String password = configurer.resolvePlaceholder("${db.password}");
        String driver = configurer.resolvePlaceholder("${db.driver}");

        // 验证占位符解析
        assertEquals("jdbc:mysql://localhost:3306/test", url);
        assertEquals("root", username);
        assertEquals("123456", password);
        assertEquals("com.mysql.cj.jdbc.Driver", driver);

        System.out.println("数据库 URL: " + url);
        System.out.println("用户名: " + username);
        System.out.println("密码: " + password);
        System.out.println("驱动: " + driver);

        System.out.println("✅ 占位符解析测试通过");
        System.out.println("说明：PropertyPlaceholderConfigurer 能正确解析 ${key} 占位符");
    }

    /**
     * 测试默认值解析
     */
    @Test
    public void testDefaultValue() throws Exception {
        System.out.println("===== 测试默认值 =====");

        // 创建 PropertyPlaceholderConfigurer
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocation("classpath:application.properties");

        // 测试默认值：配置文件中存在的值
        String host = configurer.resolvePlaceholder("${server.host:0.0.0.0}");
        assertEquals("localhost", host); // 应该使用配置文件中的值

        // 测试默认值：配置文件中不存在的值
        String maxConn = configurer.resolvePlaceholder("${server.maxConnections:100}");
        assertEquals("100", maxConn); // 应该使用默认值

        System.out.println("主机: " + host + " (配置文件中的值)");
        System.out.println("最大连接数: " + maxConn + " (默认值)");

        System.out.println("✅ 默认值测试通过");
        System.out.println("说明：${key:defaultValue} 语法能正确处理默认值");
    }

    /**
     * 测试字面值（不是占位符）
     */
    @Test
    public void testLiteralValue() throws Exception {
        System.out.println("===== 测试字面值 =====");

        // 创建 PropertyPlaceholderConfigurer
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();

        // 测试字面值（不包含 ${}）
        String port = configurer.resolvePlaceholder("3306");
        assertEquals("3306", port);

        String enabled = configurer.resolvePlaceholder("true");
        assertEquals("true", enabled);

        System.out.println("端口: " + port);
        System.out.println("启用: " + enabled);

        System.out.println("✅ 字面值测试通过");
        System.out.println("说明：PropertyPlaceholderConfigurer 正确处理字面值");
    }

    /**
     * 测试 @Value 注解存在性
     */
    @Test
    public void testValueAnnotationExists() throws Exception {
        System.out.println("===== 测试 @Value 注解存在性 =====");

        // 验证 DatabaseConfig 类存在 @Value 注解
        java.lang.reflect.Field urlField = DatabaseConfig.class.getDeclaredField("url");
        assertNotNull("url 字段应该存在", urlField);

        com.minispring.beans.factory.annotation.Value valueAnnotation =
                urlField.getAnnotation(com.minispring.beans.factory.annotation.Value.class);
        assertNotNull("url 字段应该有 @Value 注解", valueAnnotation);
        assertEquals("${db.url}", valueAnnotation.value());

        System.out.println("✅ @Value 注解存在性测试通过");
        System.out.println("说明：@Value 注解已正确创建并可以使用");
    }
}

