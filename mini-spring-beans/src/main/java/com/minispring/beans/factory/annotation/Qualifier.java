package com.minispring.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * Qualifier 注解 - 限定符注解
 * <p>
 * 用于解决多个候选 Bean 的歧义问题，通过指定 Bean 名称来精确匹配
 * <p>
 * 使用场景：
 * <pre>
 * {@code
 * // 场景1：多个实现类
 * @Component("mysqlDataSource")
 * public class MySQLDataSource implements DataSource { }
 *
 * @Component("postgresDataSource")
 * public class PostgresDataSource implements DataSource { }
 *
 * // 使用 @Qualifier 指定具体实现
 * @Service
 * public class UserService {
 *     @Autowired
 *     @Qualifier("mysqlDataSource")
 *     private DataSource dataSource;  // 注入 MySQLDataSource
 * }
 *
 * // 场景2：同一类型多个 Bean
 * @Configuration
 * public class DataSourceConfig {
 *     @Bean("masterDB")
 *     public DataSource masterDataSource() { ... }
 *
 *     @Bean("slaveDB")
 *     public DataSource slaveDataSource() { ... }
 * }
 *
 * @Service
 * public class ReportService {
 *     @Autowired
 *     @Qualifier("slaveDB")
 *     private DataSource dataSource;  // 注入 slaveDB
 * }
 * }
 * </pre>
 * <p>
 * 面试要点：
 * 1. 为什么需要 @Qualifier？
 *    - @Autowired 默认按类型注入
 *    - 当有多个相同类型的 Bean 时会产生歧义
 *    - @Qualifier 通过指定 Bean 名称来解决歧义
 * <p>
 * 2. @Qualifier 的工作原理
 *    - 与 @Autowired 配合使用
 *    - 在 BeanPostProcessor 处理时，先检查 @Qualifier
 *    - 如果有 @Qualifier，按名称查找；否则按类型查找
 * <p>
 * 3. @Qualifier vs @Resource
 *    - @Qualifier: Spring 注解，与 @Autowired 配合，先类型后名称
 *    - @Resource: JSR-250 注解，独立使用，先名称后类型
 * <p>
 * 4. @Qualifier vs @Primary
 *    - @Qualifier: 在注入点指定 Bean 名称（使用侧）
 *    - @Primary: 在 Bean 定义处标记为首选（提供侧）
 *    - @Qualifier 优先级更高
 * <p>
 * 实现原理：
 * 1. AutowiredAnnotationBeanPostProcessor 处理 @Autowired 时
 * 2. 检查字段/方法是否有 @Qualifier 注解
 * 3. 如果有，使用 getBean(name, type) 按名称和类型查找
 * 4. 如果没有，使用 getBean(type) 按类型查找
 * <p>
 * 注意事项：
 * 1. @Qualifier 必须与 @Autowired 配合使用
 * 2. 指定的 Bean 名称必须存在，否则抛出异常
 * 3. 名称匹配区分大小写
 *
 * @author mini-spring
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Qualifier {

    /**
     * 指定要注入的 Bean 名称
     * <p>
     * 默认值为空字符串，表示使用字段名作为 Bean 名称
     *
     * @return Bean 名称
     */
    String value() default "";

}
