# Mini Spring Framework

手写轻量级 Spring 框架，覆盖所有 Spring 面试核心知识点。

## 项目介绍

本项目是一个轻量级的 Spring 框架实现，旨在通过手写核心功能来深入理解 Spring 的设计原理，而不是死记硬背面试八股文。

## 技术栈

- **语言**: Java 8
- **构建工具**: Maven
- **测试框架**: JUnit 4
- **代理技术**: JDK 动态代理 + CGLIB

## 模块结构

```
mini-spring/
├── mini-spring-core        # 核心模块：资源加载、工具类
├── mini-spring-beans       # Bean 容器：实例化、属性注入、生命周期
├── mini-spring-context     # 应用上下文：注解扫描、事件机制（待实现）
├── mini-spring-aop         # AOP 支持：动态代理、切面（待实现）
├── mini-spring-tx          # 事务管理（待实现）
└── mini-spring-web         # Web MVC（待实现）
```

## 核心功能

### 已实现
- [x] Maven 多模块项目结构
- [ ] 资源加载机制（Resource）
- [ ] Bean 定义和注册（BeanDefinition）
- [ ] Bean 工厂（BeanFactory）
- [ ] Bean 实例化策略
- [ ] 属性注入
- [ ] Bean 生命周期管理
- [ ] 三级缓存解决循环依赖

### 待实现
- [ ] 注解支持（@Component, @Autowired 等）
- [ ] AOP 功能
- [ ] 事务管理
- [ ] Web MVC

## 涵盖的面试知识点

1. **IoC 容器原理**：Bean 的定义、注册、实例化、初始化完整流程
2. **依赖注入**：构造器注入、Setter 注入、字段注入
3. **循环依赖**：三级缓存解决方案
4. **Bean 生命周期**：实例化、属性填充、初始化、销毁
5. **AOP 原理**：JDK 动态代理、CGLIB 代理
6. **事务管理**：声明式事务、事务传播行为
7. **设计模式**：工厂、单例、代理、模板方法、策略、观察者等

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+

### 构建项目

```bash
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd mini-spring

# 编译项目
mvn clean install

# 运行测试
mvn test
```

## 开发进度

详见 [plan.md](./plan.md)

## 学习建议

1. 按照 plan.md 中的阶段顺序学习
2. 每实现一个功能，写单元测试验证
3. 对照 Spring 源码理解设计思路
4. 总结每个模块涉及的设计模式和面试考点

## 许可证

MIT License
