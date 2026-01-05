package com.minispring.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * @Component 注解
 * <p>
 * 标记一个类为 Spring 管理的组件
 * <p>
 * 核心功能（面试重点）：
 * 1. 标记组件：指示该类由 Spring 容器管理
 * 2. 自动扫描：配合 @ComponentScan 或 ClassPathBeanDefinitionScanner 使用
 * 3. 自动注册：被标记的类会自动注册为 BeanDefinition
 * <p>
 * 派生注解（面试考点）：
 * - @Service：服务层组件
 * - @Repository：数据访问层组件
 * - @Controller：控制层组件
 * 这些注解都包含 @Component，本质上是 @Component 的语义化版本
 * <p>
 * 使用场景：
 * <pre>
 * @Component
 * public class UserService {
 *     // ...
 * }
 *
 * @Component("myUserService")  // 指定 bean 名称
 * public class UserService {
 *     // ...
 * }
 * </pre>
 * <p>
 * Bean 命名规则（面试考点）：
 * 1. 如果指定了 value，使用指定的名称
 * 2. 如果未指定，使用类名首字母小写（UserService -> userService）
 * <p>
 * 注解元素说明：
 * - @Target(ElementType.TYPE)：只能用于类、接口、枚举
 * - @Retention(RetentionPolicy.RUNTIME)：运行时保留，可通过反射读取
 * - @Documented：包含在 JavaDoc 中
 * <p>
 * 面试重点：
 * 1. @Component 的作用和工作原理
 *    - 标记组件，由容器扫描并自动注册
 * 2. 与 XML 配置的区别
 *    - XML：手动配置，显式声明
 *    - 注解：自动扫描，隐式声明
 * 3. 派生注解的关系
 *    - @Service、@Repository、@Controller 都包含 @Component
 *    - 本质相同，只是语义不同
 * 4. Bean 的命名规则
 *    - 默认首字母小写
 *    - 可通过 value 指定
 *
 * @author mini-spring
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    /**
     * Bean 的名称
     * <p>
     * 如果不指定，默认使用类名首字母小写
     * <p>
     * 示例：
     * <pre>
     * @Component  // bean 名称为 "userService"
     * public class UserService {}
     *
     * @Component("myService")  // bean 名称为 "myService"
     * public class UserService {}
     * </pre>
     *
     * @return Bean 名称
     */
    String value() default "";

}
