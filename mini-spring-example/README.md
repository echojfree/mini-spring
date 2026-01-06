# Mini Spring Example 使用示例

本项目展示了如何使用 Mini Spring 框架的各种功能，包括 IoC 容器、AOP、事务管理和 Web MVC。

## 项目结构

```
mini-spring-example/
├── src/main/java/com/minispring/example/
│   ├── ioc/                    # IoC 容器示例
│   │   ├── domain/            # 领域对象
│   │   ├── repository/        # 数据访问层
│   │   └── service/           # 业务逻辑层
│   ├── aop/                    # AOP 功能示例
│   │   ├── aspect/            # 切面类
│   │   └── service/           # 服务类
│   ├── tx/                     # 事务管理示例
│   │   ├── domain/            # 领域对象
│   │   ├── repository/        # 数据访问层
│   │   └── service/           # 业务逻辑层
│   └── web/                    # Web MVC 示例
│       ├── controller/        # 控制器
│       ├── domain/            # 领域对象
│       ├── dto/               # 数据传输对象
│       └── service/           # 业务逻辑层
└── src/test/java/             # 测试用例
```

## 功能示例

### 1. IoC 容器示例

演示了依赖注入和 Bean 管理的基本功能。

**核心文件**：
- `UserRepository.java` - 使用 `@Repository` 注解
- `UserService.java` - 使用 `@Service` 和 `@Autowired` 注解
- `User.java` - 领域对象

**使用方式**：
```java
// 创建应用上下文，扫描指定包
AnnotationConfigApplicationContext context =
    new AnnotationConfigApplicationContext("com.minispring.example.ioc");

// 获取 Bean
UserService userService = context.getBean("userService", UserService.class);

// 使用服务
User user = userService.createUser("Alice", "alice@example.com");
```

**测试用例**：
- `IocExampleTest.java` - 6 个测试用例，测试 CRUD 操作和异常处理

### 2. AOP 功能示例

演示了面向切面编程的基本概念。

**核心文件**：
- `LoggingAspect.java` - 日志切面（前置通知）
- `PerformanceAspect.java` - 性能监控切面（环绕通知）
- `CalculatorService.java` - 被代理的服务类

**使用方式**：
```java
// 创建应用上下文
AnnotationConfigApplicationContext context =
    new AnnotationConfigApplicationContext("com.minispring.example.aop");

// 获取服务（已被代理）
CalculatorService calculator = context.getBean("calculatorService", CalculatorService.class);

// 调用方法（切面会自动生效）
int result = calculator.add(10, 20);
```

**测试用例**：
- `AopExampleTest.java` - 6 个测试用例，测试各种计算操作

### 3. 事务管理示例

演示了声明式事务管理功能。

**核心文件**：
- `Account.java` - 账户领域对象
- `AccountRepository.java` - 数据访问层
- `AccountService.java` - 使用 `@Transactional` 注解的业务逻辑层

**使用方式**：
```java
// 创建应用上下文
AnnotationConfigApplicationContext context =
    new AnnotationConfigApplicationContext("com.minispring.example.tx");

// 获取服务
AccountService accountService = context.getBean("accountService", AccountService.class);

// 执行转账（事务保护）
accountService.transfer(1L, 2L, new BigDecimal("100.00"));
```

**测试用例**：
- `TransactionExampleTest.java` - 5 个测试用例，测试成功和失败的转账操作

**注意事项**：
- 完整的事务回滚功能需要额外的配置
- 当前示例演示了事务注解的使用和异常处理

### 4. Web MVC 示例

演示了 RESTful API 的开发。

**核心文件**：
- `ProductController.java` - 使用 `@RestController` 和 `@RequestMapping` 注解
- `ProductService.java` - 业务逻辑层
- `Product.java` - 产品领域对象
- `ApiResponse.java` - 统一响应格式

**API 端点**：
```
GET    /api/products          - 获取所有产品
GET    /api/products/{id}     - 根据 ID 获取产品
POST   /api/products          - 创建产品
PUT    /api/products/{id}     - 更新产品
DELETE /api/products/{id}     - 删除产品
GET    /api/products/search   - 搜索产品
```

**使用示例**：
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ApiResponse.success(products);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<Product> getProduct(@PathVariable("id") Long id) {
        Product product = productService.getProduct(id);
        return ApiResponse.success(product);
    }
}
```

## 运行测试

### 编译项目
```bash
mvn clean compile
```

### 运行所有测试
```bash
mvn test
```

### 运行特定测试
```bash
# IoC 测试
mvn test -Dtest=IocExampleTest

# AOP 测试
mvn test -Dtest=AopExampleTest

# 事务测试
mvn test -Dtest=TransactionExampleTest
```

## 测试结果

所有测试用例均已通过：
- **IoC 测试**：6/6 通过 ✅
- **AOP 测试**：6/6 通过 ✅
- **事务测试**：5/5 通过 ✅
- **总计**：17/17 通过 ✅

## 核心注解说明

### IoC 相关注解
- `@Service` - 标记服务层组件
- `@Repository` - 标记数据访问层组件
- `@Autowired` - 自动装配依赖

### AOP 相关类
- `MethodBeforeAdvice` - 前置通知接口
- `MethodInterceptor` - 环绕通知接口
- `AspectJExpressionPointcut` - 切点表达式

### 事务相关注解
- `@Transactional` - 标记需要事务管理的方法

### Web MVC 相关注解
- `@RestController` - 标记 REST 控制器
- `@RequestMapping` - 映射 URL 路径
- `@PathVariable` - 绑定路径变量
- `@RequestParam` - 绑定请求参数
- `@RequestBody` - 绑定请求体
- `@ResponseBody` - 标记返回响应体

## 学习建议

1. **从 IoC 开始**：理解依赖注入和 Bean 管理的基本概念
2. **学习 AOP**：掌握切面编程的思想和应用场景
3. **了解事务**：学习声明式事务管理的原理
4. **实践 Web MVC**：开发 RESTful API

## 扩展阅读

- [快速入门指南](../QUICKSTART.md)
- [集成指南](../INTEGRATION_GUIDE.md)
- [主 README](../README.md)

## 常见问题

### 1. 如何创建自己的示例？

参考现有示例的结构：
1. 创建领域对象（domain）
2. 创建数据访问层（repository）使用 `@Repository`
3. 创建业务逻辑层（service）使用 `@Service` 和 `@Autowired`
4. 编写测试用例验证功能

### 2. 如何配置 AOP 切面？

参考 `AopExampleTest.java` 中的配置方式：
1. 创建切面类实现 `MethodBeforeAdvice` 或 `MethodInterceptor`
2. 定义切点表达式 `AspectJExpressionPointcut`
3. 创建 Advisor 并注册到容器

### 3. 事务为什么没有回滚？

当前简化版本的事务管理需要额外的配置才能完全支持回滚功能。示例主要演示了事务注解的使用和异常处理机制。

### 4. 如何添加新的 API 端点？

在 Controller 中添加新方法：
```java
@RequestMapping(value = "/custom", method = RequestMethod.GET)
@ResponseBody
public ApiResponse<String> customEndpoint() {
    return ApiResponse.success("Custom response");
}
```

## 贡献

欢迎提交 Issue 和 Pull Request 来改进示例！

## 许可证

本项目采用 MIT 许可证。
