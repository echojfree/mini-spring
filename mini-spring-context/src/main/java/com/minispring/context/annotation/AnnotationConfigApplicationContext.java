package com.minispring.context.annotation;

import com.minispring.beans.factory.config.BeanPostProcessor;
import com.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.minispring.context.support.AbstractRefreshableApplicationContext;

/**
 * AnnotationConfigApplicationContext 注解配置的 ApplicationContext
 * <p>
 * 基于注解的 ApplicationContext 实现，支持包扫描和注解配置
 * <p>
 * 核心功能（面试重点）：
 * 1. 零 XML 配置：完全基于注解，不需要 XML 文件
 * 2. 包扫描：自动扫描并注册带 @Component 注解的类
 * 3. 注解驱动：支持 @Service、@Repository 等派生注解
 * 4. 自动装配：配合 @Autowired 实现依赖注入
 * <p>
 * 与 ClassPathXmlApplicationContext 的区别（面试高频）：
 * 1. 配置方式：
 *    - XML：显式配置，XML 文件
 *    - 注解：隐式配置，注解 + 包扫描
 * 2. 灵活性：
 *    - XML：集中配置，易于修改
 *    - 注解：分散配置，与代码耦合
 * 3. 开发效率：
 *    - XML：需要手动配置
 *    - 注解：自动扫描，开发更快
 * <p>
 * 使用场景：
 * <pre>
 * // 方式1：指定包扫描路径
 * AnnotationConfigApplicationContext context =
 *     new AnnotationConfigApplicationContext("com.example.service", "com.example.repository");
 *
 * // 方式2：编程式注册 + 扫描
 * AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
 * context.scan("com.example");
 * context.refresh();
 *
 * // 获取 Bean
 * UserService userService = context.getBean(UserService.class);
 * </pre>
 * <p>
 * 典型应用架构：
 * <pre>
 * @Service
 * public class UserService {
 *     @Autowired
 *     private UserRepository userRepository;
 * }
 *
 * @Repository
 * public class UserRepository {
 *     // 数据访问逻辑
 * }
 *
 * // 启动容器
 * ApplicationContext context =
 *     new AnnotationConfigApplicationContext("com.example");
 * </pre>
 * <p>
 * 面试重点：
 * 1. 注解配置 vs XML 配置
 *    - 各有优缺点
 *    - 注解更简洁，XML 更灵活
 * 2. 包扫描的实现原理
 *    - ClassPathBeanDefinitionScanner
 *    - 递归扫描包下的所有类
 *    - 反射检查注解
 * 3. Spring Boot 为什么默认使用注解？
 *    - 开发效率高
 *    - 约定优于配置
 *    - 自动装配
 * 4. 注解配置的缺点
 *    - 与代码耦合
 *    - 修改需要重新编译
 *    - 配置分散，不易管理
 *
 * @author mini-spring
 */
public class AnnotationConfigApplicationContext extends AbstractRefreshableApplicationContext {

    /**
     * 包扫描路径数组
     */
    private String[] basePackages;

    /**
     * 无参构造函数
     * <p>
     * 创建容器，但不自动刷新
     * 需要手动调用 scan() 和 refresh() 方法
     * <p>
     * 适用于编程式配置：
     * <pre>
     * AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
     * context.scan("com.example");
     * context.refresh();
     * </pre>
     */
    public AnnotationConfigApplicationContext() {
        // 不自动刷新
    }

    /**
     * 指定包扫描路径的构造函数
     * <p>
     * 创建容器并自动扫描指定包
     * 自动调用 refresh() 完成初始化
     * <p>
     * 使用示例：
     * <pre>
     * ApplicationContext context =
     *     new AnnotationConfigApplicationContext("com.example.service", "com.example.dao");
     * </pre>
     *
     * @param basePackages 包扫描路径（可变参数）
     * @throws Exception 初始化失败时抛出异常
     */
    public AnnotationConfigApplicationContext(String... basePackages) throws Exception {
        this.basePackages = basePackages;
        refresh();
    }

    /**
     * 设置包扫描路径
     * <p>
     * 用于编程式配置
     * <p>
     * 使用示例：
     * <pre>
     * AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
     * context.scan("com.example.service", "com.example.dao");
     * context.refresh();
     * </pre>
     *
     * @param basePackages 包扫描路径（可变参数）
     */
    public void scan(String... basePackages) {
        this.basePackages = basePackages;
    }

    /**
     * 添加 BeanPostProcessor
     * <p>
     * 提供公开方法添加 BeanPostProcessor
     * 用于测试场景注册 AutowiredAnnotationBeanPostProcessor
     *
     * @param beanPostProcessor BeanPostProcessor 实例
     */
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        getBeanFactory().addBeanPostProcessor(beanPostProcessor);
    }

    /**
     * 加载 BeanDefinition
     * <p>
     * 通过包扫描加载 BeanDefinition
     * <p>
     * 实现步骤：
     * 1. 创建 ClassPathBeanDefinitionScanner
     * 2. 调用 scan() 方法扫描指定包
     * 3. 自动注册所有带 @Component 注解的类
     * <p>
     * v0.15.0 新增：自动注册 AutowiredAnnotationBeanPostProcessor
     * <p>
     * 面试考点：
     * 1. 包扫描的工作原理
     *    - 使用 ClassPathBeanDefinitionScanner
     *    - 递归扫描包下的所有类
     *    - 反射检查注解
     * 2. BeanDefinition 的注册时机
     *    - 在 refresh() 过程中
     *    - 在 BeanFactoryPostProcessor 之前
     *
     * @param beanFactory BeanFactory 实例
     * @throws Exception 加载失败时抛出异常
     */
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws Exception {
        // v0.15.0 新增：自动注册 AutowiredAnnotationBeanPostProcessor
        // 用于处理 @Autowired 注解
        beanFactory.addBeanPostProcessor(new com.minispring.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor());

        // 创建包扫描器
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);

        // 扫描指定包
        if (basePackages != null && basePackages.length > 0) {
            System.out.println("开始扫描包: " + String.join(", ", basePackages));
            scanner.scan(basePackages);
        }
    }

}
