package com.minispring.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * @Service 注解
 * <p>
 * 标记服务层组件
 * <p>
 * 核心说明（面试重点）：
 * 1. 语义化注解：明确表示这是服务层组件
 * 2. 功能等同：本质上等同于 @Component
 * 3. 层次划分：帮助区分不同层次的组件
 * <p>
 * 与 @Component 的关系（面试高频）：
 * - @Service 包含了 @Component 元注解
 * - Spring 扫描时会识别 @Service
 * - 功能完全相同，只是语义不同
 * <p>
 * 为什么要有派生注解？（面试考点）
 * 1. 语义清晰：
 *    - @Service 表示服务层
 *    - @Repository 表示数据访问层
 *    - @Controller 表示控制层
 * 2. 便于管理：
 *    - 可以针对不同层次做特殊处理
 *    - 如 @Repository 可以自动进行异常转换
 * 3. 代码可读性：
 *    - 一眼就能看出组件的职责
 * <p>
 * 使用场景：
 * <pre>
 * @Service
 * public class UserService {
 *     @Autowired
 *     private UserRepository userRepository;
 *
 *     public User findById(Long id) {
 *         return userRepository.findById(id);
 *     }
 * }
 *
 * @Service("myUserService")  // 指定 bean 名称
 * public class UserService {
 *     // ...
 * }
 * </pre>
 * <p>
 * 典型架构分层：
 * - Controller 层：@Controller，处理 HTTP 请求
 * - Service 层：@Service，业务逻辑处理
 * - Repository 层：@Repository，数据访问
 * <p>
 * 面试重点：
 * 1. @Service 和 @Component 的区别
 *    - 功能相同，只是语义不同
 *    - @Service 更明确表示服务层
 * 2. 为什么 Spring 设计这些派生注解？
 *    - 语义清晰，代码可读性高
 *    - 便于分层管理和 AOP 切面
 * 3. 可以混用吗？
 *    - 可以，但不推荐
 *    - 应该按照层次使用对应的注解
 *
 * @author mini-spring
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component  // 关键：包含 @Component 元注解
public @interface Service {

    /**
     * Bean 的名称
     * <p>
     * 如果不指定，默认使用类名首字母小写
     * <p>
     * 示例：
     * <pre>
     * @Service  // bean 名称为 "userService"
     * public class UserService {}
     *
     * @Service("myService")  // bean 名称为 "myService"
     * public class UserService {}
     * </pre>
     *
     * @return Bean 名称
     */
    String value() default "";

}
