package com.minispring.context.annotation;

import com.minispring.test.beans.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 注解配置测试类
 * <p>
 * 测试目标（面试重点）：
 * 1. @Component 注解的识别和注册
 * 2. 派生注解的支持（@Service、@Repository）
 * 3. @Scope 注解的作用域控制
 * 4. Bean 命名规则
 * 5. 包扫描机制
 * <p>
 * 面试考点：
 * 1. 注解配置 vs XML 配置
 *    - 注解更简洁，XML 更灵活
 * 2. 包扫描的工作原理
 *    - ClassPathBeanDefinitionScanner
 *    - 递归扫描，反射检查注解
 * 3. 派生注解的识别
 *    - @Service、@Repository 包含 @Component 元注解
 * 4. Bean 命名规则
 *    - 默认首字母小写
 *    - 可通过 value 指定
 *
 * @author mini-spring
 */
public class AnnotationConfigTest {

    /**
     * 测试 @Component 注解的基本功能
     */
    @Test
    public void testComponentAnnotation() throws Exception {
        System.out.println("===== 测试 @Component 注解 =====");

        // 创建注解配置容器
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.minispring.test.beans");

        // 验证 @Component 注解的 Bean 被注册
        UserComponent userComponent = context.getBean("userComponent", UserComponent.class);
        assertNotNull("@Component 注解的 Bean 应该被注册", userComponent);
        assertEquals("UserComponent", userComponent.getName());

        System.out.println("✅ @Component 注解测试通过");
        System.out.println("说明：@Component 注解的类被自动扫描并注册");

        context.close();
    }

    /**
     * 测试派生注解（@Service、@Repository）
     */
    @Test
    public void testDerivedAnnotations() throws Exception {
        System.out.println("===== 测试派生注解 =====");

        // 创建容器
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.minispring.test.beans");

        // 验证 @Service
        UserService userService = context.getBean("userService", UserService.class);
        assertNotNull("@Service 注解的 Bean 应该被注册", userService);
        assertEquals("UserService", userService.getName());

        // 验证 @Repository
        UserRepository userRepository = context.getBean("userRepository", UserRepository.class);
        assertNotNull("@Repository 注解的 Bean 应该被注册", userRepository);
        assertEquals("UserRepository", userRepository.getName());

        System.out.println("✅ 派生注解测试通过");
        System.out.println("说明：@Service 和 @Repository 作为 @Component 的派生注解被正确识别");

        context.close();
    }

    /**
     * 测试自定义 Bean 名称
     */
    @Test
    public void testCustomBeanName() throws Exception {
        System.out.println("===== 测试自定义 Bean 名称 =====");

        // 创建容器
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.minispring.test.beans");

        // 验证自定义名称
        CustomNameComponent component = context.getBean("myComponent", CustomNameComponent.class);
        assertNotNull("自定义名称的 Bean 应该存在", component);
        assertEquals("CustomNameComponent", component.getName());

        System.out.println("✅ 自定义 Bean 名称测试通过");
        System.out.println("说明：@Component(\"myComponent\") 指定的名称生效");

        context.close();
    }

    /**
     * 测试 @Scope 注解
     */
    @Test
    public void testScopeAnnotation() throws Exception {
        System.out.println("===== 测试 @Scope 注解 =====");

        // 创建容器
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.minispring.test.beans");

        // 获取 prototype 作用域的 Bean
        PrototypeComponent component1 = context.getBean("prototypeComponent", PrototypeComponent.class);
        PrototypeComponent component2 = context.getBean("prototypeComponent", PrototypeComponent.class);

        // 验证是不同的实例
        assertNotNull("Prototype Bean 应该存在", component1);
        assertNotNull("Prototype Bean 应该存在", component2);
        assertNotSame("Prototype 作用域的 Bean 应该是不同的实例", component1, component2);

        // 验证 singleton（默认）
        UserComponent single1 = context.getBean("userComponent", UserComponent.class);
        UserComponent single2 = context.getBean("userComponent", UserComponent.class);
        assertSame("Singleton 作用域的 Bean 应该是同一个实例", single1, single2);

        System.out.println("✅ @Scope 注解测试通过");
        System.out.println("说明：@Scope 注解正确控制了 Bean 的作用域");

        context.close();
    }

    /**
     * 测试 Bean 命名规则
     */
    @Test
    public void testBeanNamingRules() throws Exception {
        System.out.println("===== 测试 Bean 命名规则 =====");

        // 创建容器
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.minispring.test.beans");

        // 验证默认命名（首字母小写）
        UserComponent userComponent = context.getBean("userComponent", UserComponent.class);
        assertNotNull("默认命名应该是首字母小写", userComponent);

        UserService userService = context.getBean("userService", UserService.class);
        assertNotNull("默认命名应该是首字母小写", userService);

        UserRepository userRepository = context.getBean("userRepository", UserRepository.class);
        assertNotNull("默认命名应该是首字母小写", userRepository);

        System.out.println("✅ Bean 命名规则测试通过");
        System.out.println("说明：Bean 默认使用类名首字母小写作为名称");

        context.close();
    }

    /**
     * 测试编程式配置
     */
    @Test
    public void testProgrammaticConfiguration() throws Exception {
        System.out.println("===== 测试编程式配置 =====");

        // 创建容器（不自动刷新）
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // 设置扫描包
        context.scan("com.minispring.test.beans");

        // 手动刷新
        context.refresh();

        // 验证
        UserComponent userComponent = context.getBean("userComponent", UserComponent.class);
        assertNotNull("编程式配置的 Bean 应该被注册", userComponent);

        System.out.println("✅ 编程式配置测试通过");
        System.out.println("说明：支持编程式设置扫描包并手动刷新");

        context.close();
    }

}
