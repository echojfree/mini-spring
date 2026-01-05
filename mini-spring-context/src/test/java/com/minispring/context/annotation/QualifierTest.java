package com.minispring.context.annotation;

import com.minispring.test.beans.DataService;
import com.minispring.test.beans.DataSource;
import com.minispring.test.beans.MySQLDataSource;
import com.minispring.test.beans.PostgresDataSource;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Qualifier 注解测试类
 * <p>
 * 测试目标（面试重点）：
 * 1. 字段注入中使用 @Qualifier
 * 2. Setter 方法注入中使用 @Qualifier
 * 3. 多个相同类型 Bean 的歧义解决
 * 4. @Qualifier 与 @Autowired 配合使用
 * <p>
 * 面试考点：
 * 1. 为什么需要 @Qualifier？
 *    - @Autowired 默认按类型注入
 *    - 当有多个相同类型的 Bean 时会产生歧义
 *    - @Qualifier 通过指定 Bean 名称来解决歧义
 * <p>
 * 2. @Qualifier 的工作原理
 *    - 与 @Autowired 配合使用
 *    - 在 BeanPostProcessor 处理时，先检查 @Qualifier
 *    - 如果有 @Qualifier，按名称+类型查找；否则按类型查找
 * <p>
 * 3. @Qualifier vs @Resource
 *    - @Qualifier: Spring 注解，与 @Autowired 配合，先类型后名称
 *    - @Resource: JSR-250 注解，独立使用，先名称后类型
 * <p>
 * 4. @Qualifier vs @Primary
 *    - @Qualifier: 在注入点指定 Bean 名称（使用侧）
 *    - @Primary: 在 Bean 定义处标记为首选（提供侧）
 *    - @Qualifier 优先级更高
 *
 * @author mini-spring
 */
public class QualifierTest {

    /**
     * 测试字段注入中的 @Qualifier
     */
    @Test
    public void testFieldInjectionWithQualifier() throws Exception {
        System.out.println("===== 测试字段注入中的 @Qualifier =====");

        // 创建容器并扫描
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.minispring.test.beans");

        // 获取 DataService
        DataService dataService = context.getBean("dataService", DataService.class);
        assertNotNull("DataService 应该被注册", dataService);

        // 验证注入的 MySQL 数据源
        DataSource mysqlDataSource = dataService.getMysqlDataSource();
        assertNotNull("MySQL 数据源应该被注入", mysqlDataSource);
        assertTrue("应该是 MySQLDataSource 实例", mysqlDataSource instanceof MySQLDataSource);
        assertEquals("MySQL DataSource", mysqlDataSource.getName());
        assertEquals("jdbc:mysql://localhost:3306/test", mysqlDataSource.getConnectionString());

        // 验证注入的 PostgreSQL 数据源
        DataSource postgresDataSource = dataService.getPostgresDataSource();
        assertNotNull("PostgreSQL 数据源应该被注入", postgresDataSource);
        assertTrue("应该是 PostgresDataSource 实例", postgresDataSource instanceof PostgresDataSource);
        assertEquals("PostgreSQL DataSource", postgresDataSource.getName());
        assertEquals("jdbc:postgresql://localhost:5432/test", postgresDataSource.getConnectionString());

        // 验证两个数据源不是同一个对象
        assertNotSame("两个数据源应该是不同的实例", mysqlDataSource, postgresDataSource);

        System.out.println("MySQL 数据源: " + mysqlDataSource.getName());
        System.out.println("PostgreSQL 数据源: " + postgresDataSource.getName());

        System.out.println("✅ 字段注入 @Qualifier 测试通过");
        System.out.println("说明：@Qualifier 能正确指定具体的 Bean 进行注入");

        context.close();
    }

    /**
     * 测试 Setter 方法注入中的 @Qualifier
     */
    @Test
    public void testSetterInjectionWithQualifier() throws Exception {
        System.out.println("===== 测试 Setter 方法注入中的 @Qualifier =====");

        // 创建容器并扫描
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.minispring.test.beans");

        // 获取 DataService
        DataService dataService = context.getBean("dataService", DataService.class);
        assertNotNull("DataService 应该被注册", dataService);

        // 验证通过 Setter 注入的数据源
        DataSource setterDataSource = dataService.getSetterDataSource();
        assertNotNull("Setter 数据源应该被注入", setterDataSource);
        assertTrue("应该是 MySQLDataSource 实例", setterDataSource instanceof MySQLDataSource);
        assertEquals("MySQL DataSource", setterDataSource.getName());

        System.out.println("Setter 注入的数据源: " + setterDataSource.getName());

        System.out.println("✅ Setter 方法 @Qualifier 测试通过");
        System.out.println("说明：@Qualifier 可以在方法参数上使用");

        context.close();
    }

    /**
     * 测试 @Qualifier 注解的存在性
     */
    @Test
    public void testQualifierAnnotationExists() throws Exception {
        System.out.println("===== 测试 @Qualifier 注解存在性 =====");

        // 验证 DataService 类的 mysqlDataSource 字段有 @Qualifier 注解
        java.lang.reflect.Field mysqlField = DataService.class.getDeclaredField("mysqlDataSource");
        assertNotNull("mysqlDataSource 字段应该存在", mysqlField);

        com.minispring.beans.factory.annotation.Qualifier qualifierAnnotation =
                mysqlField.getAnnotation(com.minispring.beans.factory.annotation.Qualifier.class);
        assertNotNull("mysqlDataSource 字段应该有 @Qualifier 注解", qualifierAnnotation);
        assertEquals("mysqlDataSource", qualifierAnnotation.value());

        System.out.println("✅ @Qualifier 注解存在性测试通过");
        System.out.println("说明：@Qualifier 注解已正确创建并可以使用");
    }

    /**
     * 测试多个 Bean 的正确注入
     */
    @Test
    public void testMultipleBeanInjection() throws Exception {
        System.out.println("===== 测试多个 Bean 的正确注入 =====");

        // 创建容器并扫描
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.minispring.test.beans");

        // 直接获取两个数据源 Bean
        MySQLDataSource mysqlBean = context.getBean("mysqlDataSource", MySQLDataSource.class);
        PostgresDataSource postgresBean = context.getBean("postgresDataSource", PostgresDataSource.class);

        assertNotNull("MySQL Bean 应该存在", mysqlBean);
        assertNotNull("PostgreSQL Bean 应该存在", postgresBean);

        // 获取 DataService 并验证注入的是同一个 Bean 实例
        DataService dataService = context.getBean("dataService", DataService.class);

        assertSame("注入的 MySQL 数据源应该是容器中的同一个实例",
                mysqlBean, dataService.getMysqlDataSource());
        assertSame("注入的 PostgreSQL 数据源应该是容器中的同一个实例",
                postgresBean, dataService.getPostgresDataSource());

        System.out.println("✅ 多个 Bean 正确注入测试通过");
        System.out.println("说明：@Qualifier 注入的是容器中的单例 Bean");

        context.close();
    }
}
