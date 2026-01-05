package com.minispring.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * 自动注入注解
 * <p>
 * 支持三种注入方式：
 * 1. 字段注入：直接在字段上标注
 * 2. Setter 方法注入：在 setter 方法上标注
 * 3. 构造器注入：在构造器上标注
 * <p>
 * 面试重点：
 * 1. 三种注入方式的区别和优缺点
 * 2. 字段注入（最常用但不推荐）：简洁但不利于测试
 * 3. Setter 注入：可选依赖，支持重新注入
 * 4. 构造器注入（推荐）：必需依赖，不可变性，利于测试
 * 5. required 属性：是否必须注入，默认 true
 *
 * @author mini-spring
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    /**
     * 是否必须注入
     * true: 如果找不到 Bean 则抛出异常
     * false: 如果找不到 Bean 则不注入（可选依赖）
     */
    boolean required() default true;
}
