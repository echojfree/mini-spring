# Mini-Spring 快速入门指南

欢迎使用 Mini-Spring！这是一个轻量级的 Spring 框架实现，帮助你深入理解 Spring 核心原理。

## 目录

- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [5分钟上手](#5分钟上手)
- [核心功能示例](#核心功能示例)
- [下一步](#下一步)

---

## 环境要求

- **JDK**: 8 或更高版本
- **Maven**: 3.6 或更高版本
- **IDE**: IntelliJ IDEA / Eclipse (可选)

---

## 快速开始

### 1. 添加 Maven 依赖

在你的项目 `pom.xml` 中添加 Mini-Spring 依赖：

```xml
<dependencies>
    <!-- IoC 容器 (必需) -->
    <dependency>
        <groupId>com.minispring</groupId>
        <artifactId>mini-spring-beans</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <!-- 注解支持 (推荐) -->
    <dependency>
        <groupId>com.minispring</groupId>
        <artifactId>mini-spring-context</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <!-- AOP 支持 (可选) -->
    <dependency>
        <groupId>com.minispring</groupId>
        <artifactId>mini-spring-aop</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <!-- 事务管理 (可选) -->
    <dependency>
        <groupId>com.minispring</groupId>
        <artifactId>mini-spring-tx</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <!-- Web MVC (可选) -->
    <dependency>
        <groupId>com.minispring</groupId>
        <artifactId>mini-spring-web</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### 2. 安装到本地仓库

由于这是一个学习项目，需要先安装到本地 Maven 仓库：

```bash
# 克隆项目
git clone <your-repo-url>
cd mini-spring

# 编译并安装到本地
mvn clean install
```

---

## 5分钟上手

### 示例 1: Hello World (最简单)

创建一个简单的服务类和启动类：

```java
// 1. 创建服务类
package com.example.demo;

import com.minispring.stereotype.Component;

@Component
public class HelloService {
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }
}
```

```java
// 2. 创建启动类
package com.example.demo;

import com.minispring.context.ApplicationContext;
import com.minispring.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {
        // 创建注解配置的应用上下文
        ApplicationContext context =
            new AnnotationConfigApplicationContext("com.example.demo");

        // 获取 Bean
        HelloService helloService = context.getBean(HelloService.class);

        // 调用方法
        String message = helloService.sayHello("Mini-Spring");
        System.out.println(message);  // 输出: Hello, Mini-Spring!
    }
}
```

**运行结果**:
```
Hello, Mini-Spring!
```

---

## 核心功能示例

### 1. 依赖注入

```java
// Repository 层
@Repository
public class UserRepository {
    public User findById(Long id) {
        return new User(id, "张三");
    }
}

// Service 层 (自动注入 Repository)
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUser(Long id) {
        return userRepository.findById(id);
    }
}

// 使用
ApplicationContext context =
    new AnnotationConfigApplicationContext("com.example.demo");
UserService userService = context.getBean(UserService.class);
User user = userService.getUser(1L);
```

**关键点**:
- 使用 `@Component`, `@Service`, `@Repository` 标记组件
- 使用 `@Autowired` 自动注入依赖
- 容器自动扫描并管理 Bean 生命周期

---

### 2. AOP 日志拦截

```java
// 定义切面
@Aspect
@Component
public class LoggingAspect {

    // 前置通知: 在方法执行前打印日志
    @Before("execution(* com.example.demo.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println(">>> 调用方法: " + joinPoint.getSignature().getName());
    }

    // 后置通知: 在方法成功返回后打印结果
    @AfterReturning(
        pointcut = "execution(* com.example.demo.service.*.*(..))",
        returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("<<< 方法返回: " + result);
    }

    // 环绕通知: 统计方法执行时间
    @Around("execution(* com.example.demo.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        System.out.println("执行时间: " + executionTime + "ms");
        return result;
    }
}
```

**运行效果**:
```
>>> 调用方法: getUser
执行时间: 15ms
<<< 方法返回: User{id=1, name='张三'}
```

---

### 3. 声明式事务

```java
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryService inventoryService;

    // 声明式事务: 方法执行失败自动回滚
    @Transactional(propagation = Propagation.REQUIRED)
    public void createOrder(Order order) {
        // 1. 保存订单
        orderRepository.save(order);

        // 2. 扣减库存
        inventoryService.decreaseStock(order.getProductId(), order.getQuantity());

        // 如果这里抛出异常,前面的操作会自动回滚
        if (order.getAmount() > 10000) {
            throw new RuntimeException("订单金额超限!");
        }
    }
}
```

**配置数据源**:
```java
@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        // H2 内存数据库示例
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

---

### 4. Web MVC (RESTful API)

```java
// Controller 层
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // GET /api/users/1
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    // POST /api/users
    @RequestMapping(method = RequestMethod.POST)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // GET /api/users?name=张三
    @RequestMapping(method = RequestMethod.GET)
    public List<User> searchUsers(@RequestParam String name) {
        return userService.searchByName(name);
    }
}

// 全局异常处理
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFound(UserNotFoundException ex) {
        ModelAndView mv = new ModelAndView("error/404");
        mv.addObject("message", ex.getMessage());
        return mv;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView mv = new ModelAndView("error/500");
        mv.addObject("message", "服务器内部错误");
        return mv;
    }
}
```

**配置 DispatcherServlet** (web.xml):
```xml
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>com.minispring.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/spring-mvc.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

---

## 配置方式对比

### 方式 1: 纯注解配置 (推荐)

```java
// 启动类
ApplicationContext context =
    new AnnotationConfigApplicationContext("com.example.demo");
```

**优点**: 简洁、现代、类型安全
**缺点**: 需要重新编译

---

### 方式 2: XML 配置

```xml
<!-- applicationContext.xml -->
<beans>
    <bean id="userService" class="com.example.demo.UserService">
        <property name="userRepository" ref="userRepository"/>
    </bean>

    <bean id="userRepository" class="com.example.demo.UserRepository"/>
</beans>
```

```java
// 启动类
ApplicationContext context =
    new ClassPathXmlApplicationContext("applicationContext.xml");
```

**优点**: 无需重新编译、配置集中
**缺点**: 冗长、容易出错

---

### 方式 3: 混合配置

```java
@Configuration
@ImportResource("classpath:legacy-beans.xml")  // 导入 XML 配置
@ComponentScan("com.example.demo")             // 扫描注解
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        // Java 配置方式创建 Bean
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
}
```

**优点**: 灵活、兼容老项目
**缺点**: 配置分散

---

## 下一步

### 📖 深入学习

1. **[集成指南](INTEGRATION_GUIDE.md)** - 详细的集成步骤和最佳实践
2. **[API 文档](docs/API.md)** - 完整的 API 参考手册
3. **[示例项目](examples/)** - 包含所有功能的完整示例

### 🎯 学习建议

建议按以下顺序学习各个模块：

1. ✅ **Phase 1: IoC 容器** - 理解依赖注入和 Bean 生命周期
2. ✅ **Phase 2: 注解支持** - 掌握 `@Component`, `@Autowired` 等注解
3. ✅ **Phase 3: AOP** - 学习面向切面编程
4. ✅ **Phase 4: 事务管理** - 理解声明式事务
5. ✅ **Phase 5: Web MVC** - 构建 RESTful API

### 🔍 常见问题

**Q: Mini-Spring 和 Spring Framework 的区别？**

A: Mini-Spring 是 Spring 的简化实现，专注于核心功能和原理学习。生产环境建议使用官方 Spring Framework。

**Q: 如何调试 Bean 加载问题？**

A: 在 `AnnotationConfigApplicationContext` 创建时会打印扫描和注册日志，查看控制台输出即可。

**Q: 支持哪些注解？**

A:
- IoC: `@Component`, `@Service`, `@Repository`, `@Controller`
- DI: `@Autowired`, `@Qualifier`, `@Value`
- AOP: `@Aspect`, `@Before`, `@After`, `@Around`, `@Pointcut`
- Web: `@RestController`, `@RequestMapping`, `@PathVariable`, `@RequestParam`, `@RequestBody`, `@ResponseBody`
- TX: `@Transactional`
- Global: `@ExceptionHandler`, `@ControllerAdvice`

**Q: 如何贡献代码？**

A: 欢迎提交 Pull Request！请先阅读 [CONTRIBUTING.md](CONTRIBUTING.md)

---

## 获取帮助

- 📧 **Email**: mini-spring@example.com
- 💬 **Issues**: [GitHub Issues](https://github.com/your-repo/mini-spring/issues)
- 📚 **文档**: [完整文档](docs/)

---

**祝你学习愉快！🎉**
