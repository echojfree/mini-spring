# Mini-Spring 集成指南

本指南详细介绍如何将 Mini-Spring 集成到现有项目中，包括完整的配置步骤、最佳实践和常见场景。

## 目录

- [准备工作](#准备工作)
- [基础集成](#基础集成)
- [高级配置](#高级配置)
- [集成场景](#集成场景)
- [性能优化](#性能优化)
- [故障排查](#故障排查)

---

## 准备工作

### 1. 系统要求

| 组件 | 最低版本 | 推荐版本 |
|------|---------|---------|
| JDK | 1.8 | 11 或更高 |
| Maven | 3.6.0 | 3.8.0 或更高 |
| Servlet容器 | 3.1 | 4.0 (Web 项目) |

### 2. 项目结构建议

```
your-project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── config/         # 配置类
│   │   │           ├── controller/     # Web控制器
│   │   │           ├── service/        # 业务服务
│   │   │           ├── repository/     # 数据访问
│   │   │           ├── aspect/         # AOP切面
│   │   │           └── Application.java
│   │   ├── resources/
│   │   │   ├── application.properties  # 配置文件
│   │   │   └── applicationContext.xml  # XML配置(可选)
│   │   └── webapp/                     # Web资源(Web项目)
│   │       └── WEB-INF/
│   │           └── web.xml
│   └── test/
│       └── java/                       # 测试代码
└── pom.xml
```

---

## 基础集成

### 步骤 1: 添加依赖

根据你的需求选择合适的模块：

```xml
<properties>
    <mini-spring.version>1.0.0-SNAPSHOT</mini-spring.version>
</properties>

<dependencies>
    <!-- 基础场景: IoC + 注解 -->
    <dependency>
        <groupId>com.minispring</groupId>
        <artifactId>mini-spring-context</artifactId>
        <version>${mini-spring.version}</version>
    </dependency>

    <!-- AOP 场景 -->
    <dependency>
        <groupId>com.minispring</groupId>
        <artifactId>mini-spring-aop</artifactId>
        <version>${mini-spring.version}</version>
    </dependency>

    <!-- 数据库事务场景 -->
    <dependency>
        <groupId>com.minispring</groupId>
        <artifactId>mini-spring-tx</artifactId>
        <version>${mini-spring.version}</version>
    </dependency>

    <!-- Web 应用场景 -->
    <dependency>
        <groupId>com.minispring</groupId>
        <artifactId>mini-spring-web</artifactId>
        <version>${mini-spring.version}</version>
    </dependency>

    <!-- 第三方依赖 -->
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <version>4.0.1</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### 步骤 2: 创建配置类

**Java 配置方式** (推荐):

```java
package com.example.config;

import com.minispring.context.annotation.ComponentScan;
import com.minispring.context.annotation.Configuration;
import com.minispring.tx.annotation.EnableTransactionManagement;
import com.minispring.aop.aspectj.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.example")           // 扫描组件
@EnableAspectJAutoProxy                 // 启用 AOP (可选)
@EnableTransactionManagement            // 启用事务 (可选)
public class AppConfig {

    // 数据源配置
    @Bean
    public DataSource dataSource() {
        // H2 内存数据库示例
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:schema.sql")
            .addScript("classpath:data.sql")
            .build();
    }

    // 事务管理器
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

**XML 配置方式**:

```xml
<!-- applicationContext.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<beans>
    <!-- 组件扫描 -->
    <component-scan base-package="com.example"/>

    <!-- 数据源 -->
    <bean id="dataSource" class="org.h2.jdbcx.JdbcDataSource">
        <property name="URL" value="jdbc:h2:mem:testdb"/>
        <property name="user" value="sa"/>
        <property name="password" value=""/>
    </bean>

    <!-- 事务管理器 -->
    <bean id="transactionManager"
          class="com.minispring.tx.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 启用注解驱动的事务 -->
    <tx:annotation-driven transaction-manager="transactionManager"/>
</beans>
```

### 步骤 3: 启动容器

**独立应用**:

```java
package com.example;

import com.minispring.context.ApplicationContext;
import com.minispring.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {
        // 方式 1: Java 配置
        ApplicationContext context =
            new AnnotationConfigApplicationContext(AppConfig.class);

        // 方式 2: 包扫描
        // ApplicationContext context =
        //     new AnnotationConfigApplicationContext("com.example");

        // 方式 3: XML 配置
        // ApplicationContext context =
        //     new ClassPathXmlApplicationContext("applicationContext.xml");

        // 获取 Bean 并使用
        UserService userService = context.getBean(UserService.class);
        userService.doSomething();
    }
}
```

**Web 应用** (配置 web.xml):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         version="4.0">

    <!-- Spring 上下文监听器 (可选,用于预加载) -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>com.example.config.AppConfig</param-value>
    </context-param>

    <!-- DispatcherServlet -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>com.minispring.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>com.example.config.WebConfig</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    <!-- 字符编码过滤器 -->
    <filter>
        <filter-name>encodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>encodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
</web-app>
```

---

## 高级配置

### 1. 属性配置管理

**application.properties**:

```properties
# 数据库配置
db.url=jdbc:mysql://localhost:3306/mydb
db.username=root
db.password=123456
db.driver=com.mysql.cj.jdbc.Driver

# 连接池配置
db.pool.maxActive=20
db.pool.maxIdle=10
db.pool.minIdle=5

# 应用配置
app.name=My Application
app.version=1.0.0
```

**读取配置**:

```java
@Configuration
@PropertySource("classpath:application.properties")
public class DataSourceConfig {

    @Value("${db.url}")
    private String url;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
```

### 2. Bean 作用域配置

```java
// 单例 (默认)
@Component
@Scope("singleton")
public class SingletonBean {
    // 所有注入点共享同一实例
}

// 原型 (每次获取创建新实例)
@Component
@Scope("prototype")
public class PrototypeBean {
    // 每次获取创建新实例
}
```

### 3. 生命周期回调

```java
@Component
public class LifecycleBean
        implements InitializingBean, DisposableBean,
                   BeanNameAware, BeanFactoryAware {

    private String beanName;
    private BeanFactory beanFactory;

    // 1. BeanNameAware 回调
    @Override
    public void setBeanName(String name) {
        this.beanName = name;
        System.out.println("Bean name is: " + name);
    }

    // 2. BeanFactoryAware 回调
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        System.out.println("BeanFactory injected");
    }

    // 3. InitializingBean 回调
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Bean initialized");
        // 初始化逻辑
    }

    // 4. 自定义初始化方法
    @PostConstruct
    public void customInit() {
        System.out.println("Custom init method");
    }

    // 5. DisposableBean 回调
    @Override
    public void destroy() throws Exception {
        System.out.println("Bean destroyed");
        // 清理资源
    }

    // 6. 自定义销毁方法
    @PreDestroy
    public void customDestroy() {
        System.out.println("Custom destroy method");
    }
}
```

### 4. 条件化配置

```java
@Configuration
public class ConditionalConfig {

    // 开发环境配置
    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }

    // 生产环境配置
    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://prod-server:3306/mydb");
        // ... 其他配置
        return dataSource;
    }
}
```

---

## 集成场景

### 场景 1: 纯后端服务 (无 Web)

**依赖**:
- `mini-spring-context` (必需)
- `mini-spring-aop` (可选)
- `mini-spring-tx` (可选)

**示例结构**:

```java
// 服务层
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createUser(User user) {
        userRepository.save(user);
    }
}

// 数据访问层
@Repository
public class UserRepository {
    @Autowired
    private DataSource dataSource;

    public void save(User user) {
        // JDBC 操作
    }
}

// 启动类
public class Application {
    public static void main(String[] args) {
        ApplicationContext context =
            new AnnotationConfigApplicationContext("com.example");
        UserService service = context.getBean(UserService.class);
        service.createUser(new User("张三"));
    }
}
```

---

### 场景 2: RESTful API 服务

**依赖**:
- `mini-spring-web` (必需)
- `mini-spring-context` (必需)
- `mini-spring-aop` (可选)
- `jackson-databind` (JSON 序列化)

**Controller 示例**:

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    // GET /api/v1/users/123
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    // POST /api/v1/users
    @RequestMapping(method = RequestMethod.POST)
    public User createUser(@RequestBody User user) {
        return userService.create(user);
    }

    // GET /api/v1/users?name=张三&page=1
    @RequestMapping(method = RequestMethod.GET)
    public PageResult<User> searchUsers(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") int page) {
        return userService.search(name, page);
    }
}
```

**全局异常处理**:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        ModelAndView mv = new ModelAndView("json");
        mv.addObject("code", 404);
        mv.addObject("message", ex.getMessage());
        return mv;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView mv = new ModelAndView("json");
        mv.addObject("code", 500);
        mv.addObject("message", "服务器内部错误");
        return mv;
    }
}
```

---

### 场景 3: 传统 Web 应用 (MVC + 视图)

**依赖**:
- `mini-spring-web` (必需)
- JSP / Thymeleaf (视图技术)

**Controller 示例**:

```java
@Controller
@RequestMapping("/users")
public class UserViewController {

    @Autowired
    private UserService userService;

    // GET /users/list
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView listUsers() {
        List<User> users = userService.findAll();
        ModelAndView mv = new ModelAndView("user/list");
        mv.addObject("users", users);
        return mv;
    }

    // GET /users/1/edit
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public ModelAndView editUser(@PathVariable Long id) {
        User user = userService.findById(id);
        ModelAndView mv = new ModelAndView("user/edit");
        mv.addObject("user", user);
        return mv;
    }

    // POST /users/1
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String updateUser(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam String email) {
        userService.update(id, name, email);
        return "redirect:/users/list";
    }
}
```

**视图解析器配置**:

```java
@Configuration
public class WebMvcConfig {

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver =
            new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
}
```

**JSP 视图** (`/WEB-INF/views/user/list.jsp`):

```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>用户列表</title>
</head>
<body>
    <h1>用户列表</h1>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>姓名</th>
                <th>邮箱</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${users}" var="user">
                <tr>
                    <td>${user.id}</td>
                    <td>${user.name}</td>
                    <td>${user.email}</td>
                    <td>
                        <a href="/users/${user.id}/edit">编辑</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
```

---

### 场景 4: 集成第三方框架

**集成 MyBatis**:

```java
@Configuration
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage("com.example.entity");
        return factory.getObject();
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("com.example.mapper");
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return configurer;
    }
}
```

**集成 Redis**:

```java
@Configuration
public class RedisConfig {

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        return new JedisPool(config, "localhost", 6379);
    }

    @Bean
    public RedisTemplate redisTemplate(JedisPool jedisPool) {
        return new RedisTemplate(jedisPool);
    }
}
```

---

## 性能优化

### 1. 延迟加载

```java
@Component
@Lazy  // 延迟初始化,首次使用时才创建
public class HeavyBean {
    public HeavyBean() {
        // 耗时的初始化操作
    }
}
```

### 2. Bean 预加载

```java
// ApplicationContext 创建时立即实例化所有单例 Bean
ApplicationContext context =
    new AnnotationConfigApplicationContext(AppConfig.class);
// 此时所有非 @Lazy 的单例 Bean 已创建完成
```

### 3. 循环依赖处理

Mini-Spring 通过三级缓存自动解决单例 Bean 的循环依赖:

```java
@Service
public class ServiceA {
    @Autowired
    private ServiceB serviceB;  // 自动解决循环依赖
}

@Service
public class ServiceB {
    @Autowired
    private ServiceA serviceA;
}
```

**注意**: 原型 Bean 的循环依赖无法解决,会抛出异常。

### 4. AOP 性能优化

```java
// 精确的切点表达式,避免不必要的代理
@Around("execution(* com.example.service.*.create*(..)) && " +
        "!execution(* com.example.service.LogService.*(..))")
public Object logExecutionTime(ProceedingJoinPoint joinPoint) {
    // ...
}
```

---

## 故障排查

### 常见问题 1: Bean 找不到

**错误信息**:
```
BeansException: Bean 未找到: userService
```

**解决方案**:

1. 检查包扫描路径:
```java
@ComponentScan("com.example")  // 确保包含 Bean 所在的包
```

2. 检查注解是否正确:
```java
@Service  // 确保使用了正确的组件注解
public class UserService { }
```

3. 检查 Bean 名称:
```java
// 获取 Bean 时使用正确的名称或类型
context.getBean("userService");
context.getBean(UserService.class);
```

---

### 常见问题 2: 循环依赖错误

**错误信息**:
```
BeansException: 循环依赖检测到: serviceA -> serviceB -> serviceA
```

**解决方案**:

1. 使用 `@Lazy` 打破循环:
```java
@Service
public class ServiceA {
    @Autowired
    @Lazy  // 延迟注入,打破循环
    private ServiceB serviceB;
}
```

2. 重构代码,消除循环依赖:
```java
// 抽取公共逻辑到新的服务
@Service
public class CommonService { }

@Service
public class ServiceA {
    @Autowired
    private CommonService commonService;
}

@Service
public class ServiceB {
    @Autowired
    private CommonService commonService;
}
```

---

### 常见问题 3: AOP 不生效

**解决方案**:

1. 启用 AOP 自动代理:
```java
@Configuration
@EnableAspectJAutoProxy  // 必需
public class AppConfig { }
```

2. 确保切面类被扫描:
```java
@Aspect
@Component  // 必需
public class LoggingAspect { }
```

3. 检查切点表达式:
```java
// 正确
@Before("execution(* com.example.service.*.*(..))")

// 错误 (包路径不匹配)
@Before("execution(* com.wrong.path.*.*(..))")
```

---

### 常见问题 4: 事务不生效

**解决方案**:

1. 启用事务管理:
```java
@Configuration
@EnableTransactionManagement  // 必需
public class AppConfig { }
```

2. 配置事务管理器:
```java
@Bean
public PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
}
```

3. 确保方法是 public:
```java
@Transactional
public void createUser(User user) {  // 必须是 public
    // ...
}
```

4. 避免同类调用:
```java
@Service
public class UserService {

    @Transactional
    public void method1() { }

    public void method2() {
        this.method1();  // ❌ 同类调用,事务不生效
    }
}

// 正确做法: 通过代理对象调用
@Service
public class UserService {
    @Autowired
    private UserService self;  // 注入自己

    @Transactional
    public void method1() { }

    public void method2() {
        self.method1();  // ✅ 通过代理调用,事务生效
    }
}
```

---

### 常见问题 5: Web 请求映射不生效

**解决方案**:

1. 检查 web.xml 配置:
```xml
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>com.minispring.web.servlet.DispatcherServlet</servlet-class>
    <load-on-startup>1</load-on-startup>  <!-- 必需,确保启动时加载 -->
</servlet>
```

2. 检查 Controller 注解:
```java
@RestController  // 或 @Controller
@RequestMapping("/api/users")
public class UserController { }
```

3. 检查路径冲突:
```java
// ❌ 路径冲突
@RequestMapping("/users")
public ModelAndView method1() { }

@RequestMapping("/users")
public ModelAndView method2() { }

// ✅ 不同路径或不同 HTTP 方法
@RequestMapping(value = "/users", method = RequestMethod.GET)
public ModelAndView method1() { }

@RequestMapping(value = "/users", method = RequestMethod.POST)
public ModelAndView method2() { }
```

---

## 调试技巧

### 1. 启用详细日志

```java
// 在启动时添加系统属性
System.setProperty("mini-spring.debug", "true");

ApplicationContext context =
    new AnnotationConfigApplicationContext("com.example");
```

### 2. 查看所有已注册的 Bean

```java
ApplicationContext context = ...;
String[] beanNames = context.getBeanDefinitionNames();
for (String name : beanNames) {
    System.out.println("Bean: " + name);
}
```

### 3. 检查 Bean 类型

```java
Class<?> beanType = context.getType("userService");
System.out.println("Bean type: " + beanType.getName());
```

### 4. 调试 AOP 代理

```java
UserService service = context.getBean(UserService.class);
System.out.println("Is proxy: " + AopUtils.isAopProxy(service));
System.out.println("Proxy class: " + service.getClass().getName());
```

---

## 最佳实践

### 1. 命名规范

- **类名**: 大驼峰 (UserService, OrderRepository)
- **方法名**: 小驼峰 (findById, createUser)
- **Bean 名**: 小驼峰,默认为类名首字母小写
- **包结构**: 按功能分层 (controller, service, repository, config)

### 2. 配置优先级

1. Java 配置 (`@Configuration`)
2. 注解配置 (`@Component` 等)
3. XML 配置 (不推荐新项目使用)

### 3. 异常处理

```java
// 统一异常处理
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusinessException(BusinessException ex) {
        // 业务异常处理
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        // 系统异常处理
        log.error("System error", ex);
    }
}
```

### 4. 事务边界

```java
// ✅ 在 Service 层开启事务
@Service
public class OrderService {

    @Transactional
    public void createOrder(Order order) {
        // 多个数据库操作在一个事务中
        orderRepository.save(order);
        inventoryService.decreaseStock(order.getProductId());
    }
}

// ❌ 不要在 Controller 层开启事务
@RestController
public class OrderController {

    @Transactional  // 不推荐
    @RequestMapping(...)
    public Order createOrder(@RequestBody Order order) {
        return orderService.create(order);
    }
}
```

---

## 迁移指南

### 从 Spring Framework 迁移

Mini-Spring 的 API 设计与 Spring Framework 保持一致,大部分代码可以无缝迁移:

| Spring Framework | Mini-Spring | 支持情况 |
|-----------------|-------------|---------|
| @Component | @Component | ✅ 完全兼容 |
| @Autowired | @Autowired | ✅ 完全兼容 |
| @Transactional | @Transactional | ✅ 完全兼容 |
| @Aspect | @Aspect | ✅ 完全兼容 |
| @RestController | @RestController | ✅ 完全兼容 |
| @Async | - | ❌ 不支持 |
| @Scheduled | - | ❌ 不支持 |

**迁移步骤**:

1. 替换 Maven 依赖
2. 修改 import 语句 (将 `org.springframework` 改为 `com.minispring`)
3. 测试功能是否正常

---

## 获取帮助

如果遇到问题,请按以下顺序寻求帮助:

1. 查阅本文档和 [QUICKSTART.md](QUICKSTART.md)
2. 查看 [常见问题](#故障排查)
3. 在 [GitHub Issues](https://github.com/your-repo/mini-spring/issues) 搜索类似问题
4. 提交新的 Issue,并提供:
   - 详细的错误信息
   - 相关配置和代码片段
   - Mini-Spring 版本号
   - JDK 版本

---

**祝集成顺利！🚀**
