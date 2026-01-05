package com.minispring.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * @Value 注解 - 属性注入
 * <p>
 * 用于注入配置属性值，支持占位符和默认值
 * <p>
 * 使用场景：
 * 1. 基本数据类型注入
 * 2. 配置文件属性注入（${key}）
 * 3. 带默认值的属性注入（${key:defaultValue}）
 * 4. Spring 表达式（#{expression}）- 本实现暂不支持
 * <p>
 * 使用示例：
 * <pre>
 * {@code
 * @Component
 * public class DatabaseConfig {
 *     @Value("${db.url}")
 *     private String url;
 *
 *     @Value("${db.username:root}")  // 默认值为 root
 *     private String username;
 *
 *     @Value("3306")
 *     private int port;
 * }
 * }
 * </pre>
 * <p>
 * 面试要点：
 * 1. @Value 的工作原理
 *    - BeanPostProcessor 在属性填充阶段生效
 *    - 通过反射设置字段值
 *    - PropertyPlaceholderConfigurer 解析占位符
 * 2. 占位符语法
 *    - ${key}: 必需属性，找不到抛异常
 *    - ${key:defaultValue}: 可选属性，使用默认值
 * 3. @Value vs @Autowired
 *    - @Value: 注入字面值或配置属性
 *    - @Autowired: 注入 Bean 对象
 * 4. @Value 的局限性
 *    - 不支持 static 字段
 *    - 不支持复杂对象注入
 *    - 类型转换有限
 *
 * @author mini-spring
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    /**
     * 属性值或占位符表达式
     * <p>
     * 支持格式：
     * 1. 字面值：直接使用值，如 "3306"
     * 2. 占位符：${key}，从配置文件读取
     * 3. 带默认值：${key:defaultValue}
     *
     * @return 属性值表达式
     */
    String value();
}
