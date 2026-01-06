# Mini Spring 框架实现计划

基于深度架构分析，这是一个完整的 **Mini Spring** 实现路线图，涵盖所有 Spring 面试核心知识点。

---

## 📋 项目概览

**目标**：手写轻量级 Spring 框架，掌握原理而非死记硬背
**技术栈**：Java + Maven
**预计周期**：7-10 周
**知识点覆盖**：IoC、DI、AOP、事务、MVC + 10+ 设计模式

---

## 🏗️ 模块架构设计

```
mini-spring (父工程)
├── mini-spring-core        (核心基础)
├── mini-spring-beans       (Bean 容器)
├── mini-spring-context     (应用上下文)
├── mini-spring-aop         (AOP 支持)
├── mini-spring-tx          (事务管理)
└── mini-spring-web         (MVC 框架)
```

**依赖链路**：`core → beans → context → aop → tx → web`

---

## 📍 第一阶段：基础 IoC 容器（2-3 周）

### 模块：`mini-spring-core` + `mini-spring-beans`

### 实现步骤

**1.1 项目结构搭建**
- 创建 Maven 父工程 `mini-spring`
- 创建子模块 `mini-spring-core`、`mini-spring-beans`
- 配置 `pom.xml`：JUnit 5, CGLIB, ASM

**1.2 资源加载机制**
```
Resource 接口                    → 抽象资源
├── ClassPathResource            → 类路径资源
├── FileSystemResource           → 文件系统资源
└── ResourceLoader 接口          → 资源加载器
```
💡 **面试考点**：策略模式、装饰器模式

**1.3 Bean 定义体系**
```
BeanDefinition 类
├── beanClass                    → Bean 类型
├── scope                        → 作用域（singleton/prototype）
├── lazyInit                     → 延迟初始化
├── propertyValues               → 属性值集合
├── initMethodName               → 初始化方法
└── destroyMethodName            → 销毁方法

BeanDefinitionRegistry 接口      → Bean 定义注册表
```
💡 **面试考点**：Bean 元数据管理

**1.4 简单 BeanFactory**
```
BeanFactory 接口
└── DefaultListableBeanFactory 实现
    ├── registerBeanDefinition()  → 注册 Bean
    ├── getBean()                 → 获取 Bean
    └── singletonObjects Map      → 单例缓存池
```
💡 **面试考点**：工厂模式、单例模式、IoC 容器概念

**1.5 Bean 实例化策略**
```
InstantiationStrategy 接口
├── SimpleInstantiationStrategy        → 反射实例化
└── CglibSubclassingInstantiationStrategy → CGLIB 实例化
```
💡 **面试考点**：策略模式、反射、CGLIB 原理

**1.6 属性注入**
```
PropertyValue 类                 → 单个属性值
PropertyValues 类                → 属性值集合
BeanReference 类                 → Bean 引用
applyPropertyValues()            → 属性填充逻辑
```
💡 **面试考点**：依赖注入实现原理

**1.7 Bean 后置处理器**
```
BeanPostProcessor 接口
├── postProcessBeforeInitialization()  → 初始化前
└── postProcessAfterInitialization()   → 初始化后
```
💡 **面试考点**：模板方法模式、扩展点设计

**1.8 Aware 接口体系**
```
BeanNameAware                    → 获取 Bean 名称
BeanFactoryAware                 → 获取 BeanFactory
ApplicationContextAware          → 获取 ApplicationContext
```
💡 **面试考点**：Bean 如何感知容器

**1.9 初始化和销毁**
```
InitializingBean 接口           → afterPropertiesSet()
DisposableBean 接口              → destroy()
自定义 init-method / destroy-method
DisposableBeanAdapter            → 适配器模式
```
💡 **面试考点**：完整 Bean 生命周期流程

**1.10 循环依赖解决（核心难点）**
```
三级缓存机制：
├── singletonObjects             → 一级缓存：成品对象
├── earlySingletonObjects        → 二级缓存：半成品对象
└── singletonFactories           → 三级缓存：对象工厂

ObjectFactory 接口               → 延迟获取对象
循环依赖检测和处理逻辑
```
💡 **面试考点**：Spring 如何解决循环依赖（高频重点）

### ✅ 第一阶段验收标准
- [ ] 能够注册和获取 Bean
- [ ] 支持单例和原型作用域
- [ ] 完整的 Bean 生命周期管理
- [ ] 解决构造器和 setter 循环依赖
- [ ] 单元测试覆盖率 ≥80%

---

## 📍 第二阶段：注解支持（1-2 周）

### 模块：`mini-spring-context`

### 实现步骤

**2.1 ApplicationContext 体系**
```
ApplicationContext 接口          → 继承 BeanFactory
└── AbstractApplicationContext   → 模板方法模式
    └── refresh() 核心流程：
        1. prepareRefresh()
        2. obtainFreshBeanFactory()
        3. prepareBeanFactory()
        4. postProcessBeanFactory()
        5. invokeBeanFactoryPostProcessors()
        6. registerBeanPostProcessors()
        7. initMessageSource()
        8. initApplicationEventMulticaster()
        9. onRefresh()
        10. registerListeners()
        11. finishBeanFactoryInitialization()
        12. finishRefresh()
```
💡 **面试考点**：ApplicationContext 和 BeanFactory 区别，refresh 流程

**2.2 BeanFactory 后置处理器**
```
BeanFactoryPostProcessor 接口         → 修改 BeanDefinition
BeanDefinitionRegistryPostProcessor   → 注册 BeanDefinition
```
💡 **面试考点**：后置处理器作用时机

**2.3 包扫描机制**
```
ClassPathBeanDefinitionScanner
├── 扫描指定包下的类
├── 识别 @Component 注解
└── 自动注册 BeanDefinition
```
💡 **面试考点**：组件扫描原理

**2.4 核心注解**
```
@Component                       → 标记组件
@Service / @Repository / @Controller → 语义化注解
@Scope                           → 作用域
@Lazy                            → 延迟初始化

AnnotationConfigApplicationContext  → 注解配置上下文
```
💡 **面试考点**：注解底层实现原理

**2.5 依赖注入注解**
```
@Autowired                       → 自动注入
├── 支持字段注入
├── 支持构造器注入
└── 支持 Setter 注入

AutowiredAnnotationBeanPostProcessor
```
💡 **面试考点**：三种注入方式的区别

**2.6 属性注入注解**
```
@Value                           → 属性注入
PropertyPlaceholderConfigurer    → 占位符解析
支持 ${key} 和 ${key:defaultValue}
```
💡 **面试考点**：配置文件加载和属性注入

**2.7 限定符注解**
```
@Qualifier                       → 按名称注入
结合 @Autowired 使用
```
💡 **面试考点**：多个候选 Bean 的处理

**2.8 事件机制**
```
ApplicationEvent                 → 事件抽象类
ApplicationListener              → 监听器接口
ApplicationEventMulticaster      → 事件多播器
SimpleApplicationEventMulticaster
publishEvent()                   → 发布事件
```
💡 **面试考点**：观察者模式，Spring 事件机制

### ✅ 第二阶段验收标准
- [ ] 支持注解配置（零 XML）
- [ ] 自动扫描和注册组件
- [ ] @Autowired 和 @Value 正常工作
- [ ] 事件发布订阅机制正常
- [ ] 单元测试覆盖率 ≥80%

---

## 📍 第三阶段：AOP 实现（2 周）

### 模块：`mini-spring-aop`

### 实现步骤

**3.1 AOP 核心概念**
```
Pointcut 接口                    → 切点（where）
├── ClassFilter                  → 类过滤器
└── MethodMatcher                → 方法匹配器

Advice 接口                      → 通知（what）
├── MethodBeforeAdvice           → 前置通知
├── AfterReturningAdvice         → 后置通知
├── ThrowsAdvice                 → 异常通知
└── MethodInterceptor            → 环绕通知

Advisor 接口                     → 切面 = 切点 + 通知
```
💡 **面试考点**：AOP 术语，切点、通知、切面关系

**3.2 JDK 动态代理**
```
JdkDynamicAopProxy
├── 实现 InvocationHandler
├── invoke() 方法织入通知
└── 构建拦截器链
```
💡 **面试考点**：JDK 动态代理原理，必须基于接口

**3.3 CGLIB 代理**
```
CglibAopProxy
├── 使用 Enhancer 创建子类
├── 实现 MethodInterceptor
└── 方法拦截和通知织入
```
💡 **面试考点**：CGLIB 原理，基于继承，final 限制

**3.4 代理工厂**
```
ProxyFactory
├── 自动选择代理方式
│   ├── 有接口 → JDK 动态代理
│   └── 无接口 → CGLIB 代理
└── AdvisedSupport（保存代理配置）
```
💡 **面试考点**：Spring 如何选择代理方式

**3.5 切点表达式**
```
AspectJExpressionPointcut
├── 支持 AspectJ 表达式解析
└── execution、within、args 等
```
💡 **面试考点**：切点表达式语法

**3.6 AOP 自动代理**
```
AbstractAutoProxyCreator
├── 实现 BeanPostProcessor
└── postProcessAfterInitialization 创建代理

DefaultAdvisorAutoProxyCreator
├── 查找所有 Advisor
├── 匹配目标 Bean
└── 创建代理对象
```
💡 **面试考点**：AOP 与 IoC 集成，代理创建时机

**3.7 注解式 AOP**
```
@Aspect
@Before / @After / @Around / @AfterReturning / @AfterThrowing
AspectJAnnotationParser          → 解析注解
自动转换为 Advisor
```
💡 **面试考点**：注解式 AOP 原理

**3.8 通知链执行**
```
ReflectiveMethodInvocation
├── 递归调用拦截器链
└── proceed() 方法实现
```
💡 **面试考点**：责任链模式，拦截器链执行

**3.9 AOP 循环依赖处理**
```
SmartInstantiationAwareBeanPostProcessor
├── getEarlyBeanReference()
└── 三级缓存中提前暴露代理对象
```
💡 **面试考点**：AOP 代理的循环依赖解决（难点）

### ✅ 第三阶段验收标准
- [ ] 支持 JDK 和 CGLIB 两种代理
- [ ] 支持多种通知类型
- [ ] 支持切点表达式
- [ ] AOP 与 IoC 无缝集成
- [ ] 处理 AOP 场景下的循环依赖
- [ ] 单元测试覆盖率 ≥80%

---

## 📍 第四阶段：事务管理（1 周）

### 模块：`mini-spring-tx`

### 实现步骤

**4.1 事务管理器**
```
PlatformTransactionManager 接口
├── getTransaction()             → 获取事务
├── commit()                     → 提交
└── rollback()                   → 回滚

TransactionStatus 接口           → 事务状态
├── isNewTransaction()
├── isCompleted()
└── setRollbackOnly()
```
💡 **面试考点**：事务管理器抽象

**4.2 事务定义**
```
TransactionDefinition
├── 传播行为（7 种）：
│   ├── REQUIRED（默认）
│   ├── REQUIRES_NEW
│   ├── SUPPORTS
│   ├── NOT_SUPPORTED
│   ├── MANDATORY
│   ├── NEVER
│   └── NESTED
├── 隔离级别（4 种）：
│   ├── READ_UNCOMMITTED
│   ├── READ_COMMITTED
│   ├── REPEATABLE_READ
│   └── SERIALIZABLE
├── timeout
└── readOnly
```
💡 **面试考点**：事务传播行为和隔离级别（高频重点）

**4.3 数据源事务管理器**
```
DataSourceTransactionManager
├── 管理 JDBC Connection
├── 事务开启、提交、回滚
└── ThreadLocal 绑定连接
```
💡 **面试考点**：事务与数据库连接绑定

**4.4 事务同步管理器**
```
TransactionSynchronizationManager
├── ThreadLocal 存储事务资源
├── resources（事务资源）
└── synchronizations（同步回调）
```
💡 **面试考点**：ThreadLocal 在事务中的应用

**4.5 声明式事务**
```
@Transactional 注解
├── propagation
├── isolation
├── timeout
├── readOnly
├── rollbackFor
└── noRollbackFor
```
💡 **面试考点**：声明式事务配置

**4.6 事务拦截器**
```
TransactionInterceptor
├── 实现 MethodInterceptor
└── invoke() 方法：
    1. 开启事务
    2. 执行目标方法
    3. 正常提交
    4. 异常回滚

TransactionAspectSupport
```
💡 **面试考点**：声明式事务原理（AOP + 事务管理器）

**4.7 事务自动配置**
```
TransactionAttributeSource
AnnotationTransactionAttributeSource
BeanFactoryTransactionAttributeSourceAdvisor
自动为 @Transactional 方法创建代理
```
💡 **面试考点**：@Transactional 如何生效

### ✅ 第四阶段验收标准
- [ ] 支持编程式事务
- [ ] 支持声明式事务
- [ ] 正确处理事务传播行为
- [ ] 异常回滚机制正常
- [ ] 单元测试覆盖传播行为
- [ ] 单元测试覆盖率 ≥80%

---

## 📍 第五阶段：Web MVC（1-2 周）

### 模块：`mini-spring-web`

### 实现步骤

**5.1 DispatcherServlet**
```
DispatcherServlet（继承 HttpServlet）
├── init() 初始化流程：
│   ├── 创建 WebApplicationContext
│   ├── 初始化 HandlerMapping
│   ├── 初始化 HandlerAdapter
│   └── 初始化 ViewResolver
└── service() 请求分发：
    1. 查找 Handler
    2. 适配执行
    3. 处理结果
    4. 渲染视图
```
💡 **面试考点**：DispatcherServlet 核心流程（高频重点）

**5.2 HandlerMapping**
```
HandlerMapping 接口             → URL → Handler
RequestMappingHandlerMapping
├── 扫描 @Controller + @RequestMapping
├── 构建 URL → Method 映射
└── 支持 RESTful 路径（/user/{id}）

HandlerExecutionChain            → Handler + 拦截器链
```
💡 **面试考点**：URL 映射原理

**5.3 HandlerAdapter**
```
HandlerAdapter 接口              → 适配不同 Handler
RequestMappingHandlerAdapter
├── 参数解析
├── 反射调用 Controller
└── 返回值处理
```
💡 **面试考点**：适配器模式应用

**5.4 参数解析**
```
HandlerMethodArgumentResolver
支持类型：
├── @RequestParam                → 请求参数
├── @PathVariable                → 路径变量
├── @RequestBody                 → 请求体
├── HttpServletRequest
├── HttpServletResponse
├── Model
└── ModelAndView
```
💡 **面试考点**：参数绑定原理

**5.5 视图解析**
```
ViewResolver 接口                → 逻辑视图名 → View
InternalResourceViewResolver
├── 前缀 + 视图名 + 后缀
└── 解析为 JSP 路径

View 接口                        → 渲染视图
InternalResourceView             → JSP 视图
```
💡 **面试考点**：视图解析流程

**5.6 ModelAndView**
```
ModelAndView 类
Model 接口                       → 模型数据
ModelMap 实现
```
💡 **面试考点**：数据传递到视图

**5.7 拦截器**
```
HandlerInterceptor
├── preHandle()                  → 前置处理
├── postHandle()                 → 后置处理
└── afterCompletion()            → 完成后处理

InterceptorRegistry
```
💡 **面试考点**：拦截器 vs 过滤器

**5.8 RESTful 支持**
```
@RestController                  → @Controller + @ResponseBody
@ResponseBody                    → 返回 JSON
HttpMessageConverter
MappingJackson2HttpMessageConverter
```
💡 **面试考点**：RESTful API 实现

**5.9 异常处理**
```
@ExceptionHandler
HandlerExceptionResolver
ExceptionHandlerExceptionResolver
@ControllerAdvice                → 全局异常处理
```
💡 **面试考点**：统一异常处理

### ✅ 第五阶段验收标准
- [ ] 完整的 MVC 请求流程
- [ ] 支持常用注解
- [ ] 参数绑定和视图渲染正常
- [ ] 拦截器机制工作
- [ ] 支持 RESTful JSON
- [ ] 异常处理完善
- [ ] 单元测试覆盖率 ≥80%

---

## 🎯 涵盖的设计模式总结

| 序号 | 设计模式 | 应用场景 | 面试重点 |
|-----|---------|---------|---------|
| 1 | **工厂模式** | BeanFactory, FactoryBean | 工厂模式应用，两者区别 |
| 2 | **单例模式** | Bean 单例作用域管理 | Spring 如何保证单例 |
| 3 | **代理模式** | JDK 动态代理, CGLIB | 两种动态代理区别 |
| 4 | **模板方法** | AbstractApplicationContext.refresh() | 模板方法应用场景 |
| 5 | **策略模式** | InstantiationStrategy, Resource | 策略模式优势 |
| 6 | **观察者模式** | ApplicationEvent, Listener | Spring 事件机制 |
| 7 | **适配器模式** | HandlerAdapter, DisposableBeanAdapter | 适配器解决问题 |
| 8 | **装饰器模式** | BeanWrapper, BeanPostProcessor | 装饰器 vs 代理 |
| 9 | **责任链模式** | InterceptorChain, 拦截器链 | 责任链实现 |
| 10 | **前端控制器** | DispatcherServlet | MVC 设计模式 |

---

## 📚 核心面试知识点清单

### ✅ 原理类（必考）
- [ ] IoC 和 DI 概念及实现原理
- [ ] Bean 完整生命周期流程
- [ ] 三级缓存解决循环依赖原理
- [ ] BeanFactory 和 ApplicationContext 区别
- [ ] BeanPostProcessor 和 BeanFactoryPostProcessor 区别和作用时机
- [ ] AOP 原理和两种代理方式区别
- [ ] 事务传播行为（7 种）和隔离级别（4 种）
- [ ] DispatcherServlet 请求处理流程
- [ ] Spring MVC 九大组件

### ✅ 技术类
- [ ] 反射的深度应用
- [ ] 注解处理机制
- [ ] ThreadLocal 使用场景
- [ ] JDK 动态代理 vs CGLIB 性能对比
- [ ] 泛型在框架中的应用

### ✅ 实战类
- [ ] 三种依赖注入方式的选择
- [ ] 循环依赖的场景和解决方案
- [ ] 事务失效的常见场景
- [ ] AOP 切面的执行顺序
- [ ] 拦截器 vs 过滤器区别

---

## 🚀 实施建议

### 时间规划
| 阶段 | 周期 | 核心产出 |
|-----|------|---------|
| 第一阶段 | 2-3 周 | 基础 IoC 容器 |
| 第二阶段 | 1-2 周 | 注解支持 |
| 第三阶段 | 2 周 | AOP 实现 |
| 第四阶段 | 1 周 | 事务管理 |
| 第五阶段 | 1-2 周 | Web MVC |
| **总计** | **7-10 周** | **完整 Mini Spring** |

### 学习方法
1. **边写边学**：每实现一个模块，写技术博客总结
2. **对照源码**：对比 Spring 源码，学习设计思路
3. **准备面试题**：每个知识点整理问答
4. **完善测试**：编写完整的单元测试和集成测试
5. **绘制图表**：画出架构图、流程图、时序图

### 注意事项
⚠️ **循序渐进**：不要跳步骤，每个阶段验收后再进入下一阶段
⚠️ **重视测试**：确保每个功能都有测试覆盖
⚠️ **清晰注释**：代码要有详细注释，方便回顾
⚠️ **代码简洁**：避免过度设计，保持代码可读性
⚠️ **性能意识**：关注性能，但不过早优化

### 项目价值
✨ **深入理解** Spring 核心原理，不是表面理解
✨ **实践应用** 10+ 种设计模式
✨ **面试优势**：有具体项目经历，能讲出深度
✨ **架构能力**：提升系统设计和架构思维
✨ **知识体系**：形成完整的 Spring 知识网络

---

## 🎓 项目完成后的收获

实现这个 Mini Spring 后，你将能够：

1. **面试时自信回答**所有 Spring 相关问题
2. **用实际代码**解释 Spring 核心原理
3. **理解设计模式**在真实项目中的应用
4. **掌握框架设计**的思维方式
5. **不再死记硬背**，而是基于深入理解

这不是一个简单的练习项目，而是一次**系统化的框架设计实践**，值得投入时间深耕！

---

## 📝 开发日志

### 阶段进度追踪

**第一阶段：基础 IoC 容器**
- [ ] 1.1 项目结构搭建
- [ ] 1.2 资源加载机制
- [ ] 1.3 Bean 定义体系
- [ ] 1.4 简单 BeanFactory
- [ ] 1.5 Bean 实例化策略
- [ ] 1.6 属性注入
- [ ] 1.7 Bean 后置处理器
- [ ] 1.8 Aware 接口体系
- [ ] 1.9 初始化和销毁
- [ ] 1.10 循环依赖解决

**第二阶段：注解支持**
- [ ] 2.1 ApplicationContext 体系
- [ ] 2.2 BeanFactory 后置处理器
- [ ] 2.3 包扫描机制
- [ ] 2.4 核心注解
- [ ] 2.5 依赖注入注解
- [ ] 2.6 属性注入注解
- [ ] 2.7 限定符注解
- [ ] 2.8 事件机制

**第三阶段：AOP 实现**
- [x] 3.1 AOP 核心概念
- [x] 3.2 JDK 动态代理
- [x] 3.3 CGLIB 代理
- [x] 3.4 代理工厂
- [x] 3.5 切点表达式
- [x] 3.6 AOP 自动代理
- [x] 3.7 注解式 AOP
- [x] 3.8 通知链执行
- [x] 3.9 AOP 循环依赖处理

**第四阶段：事务管理**
- [ ] 4.1 事务管理器
- [ ] 4.2 事务定义
- [ ] 4.3 数据源事务管理器
- [ ] 4.4 事务同步管理器
- [ ] 4.5 声明式事务
- [ ] 4.6 事务拦截器
- [ ] 4.7 事务自动配置

**第五阶段：Web MVC**
- [ ] 5.1 DispatcherServlet
- [ ] 5.2 HandlerMapping
- [ ] 5.3 HandlerAdapter
- [ ] 5.4 参数解析
- [ ] 5.5 视图解析
- [ ] 5.6 ModelAndView
- [ ] 5.7 拦截器
- [ ] 5.8 RESTful 支持
- [ ] 5.9 异常处理

---

**创建时间**：2026-01-05
**最后更新**：2026-01-05
**文档版本**：v1.0
