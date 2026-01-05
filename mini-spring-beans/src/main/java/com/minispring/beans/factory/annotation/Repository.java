package com.minispring.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * @Repository 注解
 * <p>
 * 标记数据访问层（DAO）组件
 * <p>
 * 核心说明（面试重点）：
 * 1. 语义化注解：明确表示这是数据访问层组件
 * 2. 功能等同：本质上等同于 @Component
 * 3. 特殊增强：Spring 对 @Repository 有特殊处理
 * <p>
 * 与 @Component 的关系（面试高频）：
 * - @Repository 包含了 @Component 元注解
 * - Spring 扫描时会识别 @Repository
 * - 功能基本相同，但有额外的异常转换功能
 * <p>
 * @Repository 的特殊功能（面试考点）：
 * 1. 异常转换：
 *    - 自动将数据库异常转换为 Spring 的 DataAccessException
 *    - 统一异常体系，便于处理
 * 2. 持久层标识：
 *    - 明确标识这是持久层组件
 *    - 可以针对持久层做特殊的 AOP 处理
 * <p>
 * 使用场景：
 * <pre>
 * @Repository
 * public class UserRepository {
 *     public User findById(Long id) {
 *         // 数据访问逻辑
 *         return ...;
 *     }
 * }
 *
 * @Repository("myUserDao")  // 指定 bean 名称
 * public class UserRepository {
 *     // ...
 * }
 * </pre>
 * <p>
 * 典型使用模式：
 * <pre>
 * @Repository
 * public class UserJpaRepository implements UserRepository {
 *     @PersistenceContext
 *     private EntityManager entityManager;
 *
 *     public User findById(Long id) {
 *         return entityManager.find(User.class, id);
 *     }
 * }
 * </pre>
 * <p>
 * 面试重点：
 * 1. @Repository 和 @Component 的区别
 *    - 功能基本相同
 *    - @Repository 有异常转换功能
 *    - 语义更明确（数据访问层）
 * 2. 为什么要转换异常？
 *    - 统一异常体系
 *    - 屏蔽底层实现细节
 *    - 便于异常处理和事务回滚
 * 3. DAO 和 Repository 的区别？
 *    - DAO：Data Access Object，数据访问对象
 *    - Repository：仓储模式，领域驱动设计概念
 *    - Spring 中两者基本等同
 *
 * @author mini-spring
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component  // 关键：包含 @Component 元注解
public @interface Repository {

    /**
     * Bean 的名称
     * <p>
     * 如果不指定，默认使用类名首字母小写
     * <p>
     * 示例：
     * <pre>
     * @Repository  // bean 名称为 "userRepository"
     * public class UserRepository {}
     *
     * @Repository("myDao")  // bean 名称为 "myDao"
     * public class UserRepository {}
     * </pre>
     *
     * @return Bean 名称
     */
    String value() default "";

}
