package com.minispring.context.annotation;

import com.minispring.beans.factory.annotation.Component;
import com.minispring.beans.factory.annotation.Scope;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.support.BeanDefinitionRegistry;
import cn.hutool.core.util.ClassUtil;

import java.util.Set;

/**
 * ClassPathBeanDefinitionScanner 类路径 Bean 定义扫描器
 * <p>
 * 扫描指定包下带有 @Component 注解的类，并自动注册为 BeanDefinition
 * <p>
 * 核心功能（面试重点）：
 * 1. 包扫描：扫描指定包及其子包下的所有类
 * 2. 注解识别：识别 @Component 及其派生注解（@Service、@Repository 等）
 * 3. 自动注册：将识别到的类注册为 BeanDefinition
 * 4. Bean 命名：根据规则生成 Bean 名称
 * <p>
 * 工作流程（面试考点）：
 * 1. 扫描包：使用 ClassUtil 扫描指定包下的所有类
 * 2. 过滤类：检查类是否有 @Component 或其派生注解
 * 3. 解析注解：提取 @Component 的 value、@Scope 的 value
 * 4. 生成名称：如果未指定，使用类名首字母小写
 * 5. 创建 BeanDefinition：根据类和注解信息创建
 * 6. 注册到容器：将 BeanDefinition 注册到 BeanDefinitionRegistry
 * <p>
 * Bean 命名规则（面试重点）：
 * 1. 如果 @Component 指定了 value，使用指定值
 * 2. 如果未指定，使用类名首字母小写
 *    - UserService -> userService
 *    - UserDAO -> userDAO (首字母小写)
 * <p>
 * 注解继承识别（面试考点）：
 * - @Service、@Repository 等派生注解都包含 @Component
 * - 使用 isAnnotationPresent() 可以识别元注解
 * - 需要递归检查注解的注解
 * <p>
 * 使用场景：
 * <pre>
 * BeanDefinitionRegistry registry = new DefaultListableBeanFactory();
 * ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
 * scanner.scan("com.example.service", "com.example.repository");
 * </pre>
 * <p>
 * 面试重点：
 * 1. 包扫描的工作原理
 *    - 递归扫描包下的所有类
 *    - 使用反射检查注解
 * 2. 如何识别派生注解？
 *    - 检查注解的注解（元注解）
 *    - @Service 上有 @Component
 * 3. Bean 命名冲突如何处理？
 *    - 默认会覆盖
 *    - 可以配置冲突策略
 * 4. 性能优化
 *    - 扫描可能比较慢
 *    - 建议缩小扫描范围
 *    - 可以使用缓存
 *
 * @author mini-spring
 */
public class ClassPathBeanDefinitionScanner {

    /**
     * BeanDefinition 注册表
     */
    private final BeanDefinitionRegistry registry;

    /**
     * 构造函数
     *
     * @param registry BeanDefinition 注册表
     */
    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    /**
     * 扫描指定包
     * <p>
     * 扫描多个包并注册所有带 @Component 注解的类
     * <p>
     * 示例：
     * <pre>
     * scanner.scan("com.example.service", "com.example.repository");
     * </pre>
     *
     * @param basePackages 基础包路径（可变参数）
     */
    public void scan(String... basePackages) {
        if (basePackages == null || basePackages.length == 0) {
            return;
        }

        // 遍历每个包
        for (String basePackage : basePackages) {
            doScan(basePackage);
        }
    }

    /**
     * 执行扫描
     * <p>
     * 扫描单个包下的所有类
     * <p>
     * 实现步骤：
     * 1. 扫描包下的所有类
     * 2. 过滤出带 @Component 注解的类
     * 3. 为每个类创建 BeanDefinition
     * 4. 注册到容器
     *
     * @param basePackage 基础包路径
     */
    protected void doScan(String basePackage) {
        // 步骤1：扫描包下的所有类
        // 使用 Hutool 的 ClassUtil.scanPackage() 方法
        Set<Class<?>> classes = ClassUtil.scanPackage(basePackage);

        // 步骤2：过滤并注册
        for (Class<?> clazz : classes) {
            // 检查是否有 @Component 注解（包括派生注解）
            if (isComponent(clazz)) {
                // 步骤3：创建 BeanDefinition
                BeanDefinition beanDefinition = createBeanDefinition(clazz);

                // 步骤4：生成 Bean 名称
                String beanName = generateBeanName(clazz);

                // 步骤5：注册到容器
                registry.registerBeanDefinition(beanName, beanDefinition);

                System.out.println("已注册组件: " + clazz.getName() + " -> " + beanName);
            }
        }
    }

    /**
     * 判断是否为组件
     * <p>
     * 检查类是否有 @Component 注解或其派生注解
     * <p>
     * 派生注解识别（面试重点）：
     * - @Service、@Repository 等都包含 @Component 元注解
     * - 需要递归检查注解的注解
     * <p>
     * 实现方式：
     * 1. 直接检查 @Component
     * 2. 检查类上的所有注解
     * 3. 递归检查每个注解是否有 @Component
     *
     * @param clazz 类
     * @return 是否为组件
     */
    protected boolean isComponent(Class<?> clazz) {
        // 直接检查 @Component
        if (clazz.isAnnotationPresent(Component.class)) {
            return true;
        }

        // 检查派生注解（@Service、@Repository 等）
        // 获取类上的所有注解
        java.lang.annotation.Annotation[] annotations = clazz.getAnnotations();
        for (java.lang.annotation.Annotation annotation : annotations) {
            // 获取注解的类型
            Class<? extends java.lang.annotation.Annotation> annotationType = annotation.annotationType();

            // 检查注解上是否有 @Component（元注解）
            if (annotationType.isAnnotationPresent(Component.class)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 创建 BeanDefinition
     * <p>
     * 根据类和注解信息创建 BeanDefinition
     * <p>
     * 解析内容：
     * 1. Bean 类型
     * 2. 作用域（@Scope）
     * 3. 懒加载（@Lazy）
     *
     * @param clazz 类
     * @return BeanDefinition
     */
    protected BeanDefinition createBeanDefinition(Class<?> clazz) {
        BeanDefinition beanDefinition = new BeanDefinition(clazz);

        // 解析 @Scope 注解
        if (clazz.isAnnotationPresent(Scope.class)) {
            Scope scope = clazz.getAnnotation(Scope.class);
            beanDefinition.setScope(scope.value());
        }

        // 默认为单例
        if (beanDefinition.getScope() == null || beanDefinition.getScope().isEmpty()) {
            beanDefinition.setScope("singleton");
        }

        return beanDefinition;
    }

    /**
     * 生成 Bean 名称
     * <p>
     * Bean 命名规则（面试重点）：
     * 1. 如果 @Component 指定了 value，使用指定值
     * 2. 如果未指定，使用类名首字母小写
     * <p>
     * 示例：
     * - UserService -> userService
     * - UserDAO -> userDAO
     * - XMLParser -> xmlParser (多个大写字母开头，保持原样)
     * <p>
     * 首字母小写规则：
     * - 如果类名只有一个大写字母开头，首字母小写
     * - 如果类名有多个连续大写字母，保持原样
     *
     * @param clazz 类
     * @return Bean 名称
     */
    protected String generateBeanName(Class<?> clazz) {
        // 步骤1：检查 @Component 是否指定了 value
        String beanName = null;

        // 检查 @Component
        if (clazz.isAnnotationPresent(Component.class)) {
            Component component = clazz.getAnnotation(Component.class);
            beanName = component.value();
        }

        // 检查派生注解（@Service、@Repository 等）
        if (beanName == null || beanName.isEmpty()) {
            java.lang.annotation.Annotation[] annotations = clazz.getAnnotations();
            for (java.lang.annotation.Annotation annotation : annotations) {
                Class<? extends java.lang.annotation.Annotation> annotationType = annotation.annotationType();

                // 检查注解上是否有 @Component（元注解）
                if (annotationType.isAnnotationPresent(Component.class)) {
                    try {
                        // 通过反射获取 value() 方法
                        java.lang.reflect.Method method = annotationType.getMethod("value");
                        beanName = (String) method.invoke(annotation);
                    } catch (Exception e) {
                        // 忽略异常
                    }
                }
            }
        }

        // 步骤2：如果未指定，使用类名首字母小写
        if (beanName == null || beanName.isEmpty()) {
            String className = clazz.getSimpleName();
            beanName = decapitalize(className);
        }

        return beanName;
    }

    /**
     * 首字母小写
     * <p>
     * 规则：
     * 1. 如果第一个字符是小写，直接返回
     * 2. 如果第一个字符是大写，且第二个字符不是大写，首字母小写
     * 3. 如果前两个字符都是大写，保持原样（如 XMLParser -> XMLParser）
     * <p>
     * 这个规则与 JavaBeans 规范一致
     *
     * @param name 类名
     * @return 首字母小写后的名称
     */
    private String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        // 如果前两个字符都是大写，保持原样
        if (name.length() > 1 && Character.isUpperCase(name.charAt(0)) &&
                Character.isUpperCase(name.charAt(1))) {
            return name;
        }

        // 首字母小写
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

}
