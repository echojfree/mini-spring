package com.minispring.context;

import com.minispring.beans.factory.BeanFactory;

/**
 * ApplicationContext 接口
 * <p>
 * Spring 的高级容器接口，继承自 BeanFactory
 * 提供了更多企业级功能
 * <p>
 * ApplicationContext vs BeanFactory（面试重点）：
 * 1. BeanFactory：
 *    - Spring 的基础容器
 *    - 延迟加载：getBean 时才创建 Bean
 *    - 功能简单，适合资源受限的环境
 * 2. ApplicationContext：
 *    - Spring 的高级容器
 *    - 预加载：容器启动时就创建所有单例 Bean
 *    - 功能丰富：事件发布、国际化、资源加载等
 *    - 企业应用推荐使用
 * <p>
 * ApplicationContext 的主要功能：
 * 1. Bean 管理：继承 BeanFactory 的所有功能
 * 2. 资源加载：统一的资源访问方式
 * 3. 事件发布：ApplicationEvent 事件机制
 * 4. 国际化：MessageSource 支持
 * 5. 环境抽象：Environment 配置管理
 * 6. 生命周期管理：容器启动和关闭
 * <p>
 * 常见实现类：
 * - ClassPathXmlApplicationContext：从 classpath 加载 XML 配置
 * - FileSystemXmlApplicationContext：从文件系统加载 XML 配置
 * - AnnotationConfigApplicationContext：基于注解的配置
 * - WebApplicationContext：Web 应用专用容器
 * <p>
 * 面试考点：
 * 1. ApplicationContext 和 BeanFactory 的区别？
 * 2. ApplicationContext 的主要实现类有哪些？
 * 3. ApplicationContext 的初始化流程？
 * 4. 如何实现容器的优雅关闭？
 * 5. ApplicationContext 如何支持事件机制？
 *
 * @author mini-spring
 */
public interface ApplicationContext extends BeanFactory {

    /**
     * 刷新容器
     * <p>
     * 这是 ApplicationContext 的核心方法
     * 负责容器的初始化和 Bean 的预加载
     * <p>
     * 主要步骤：
     * 1. 准备刷新：设置启动时间、标志位等
     * 2. 获取 BeanFactory
     * 3. 准备 BeanFactory：配置类加载器、表达式解析器等
     * 4. 后置处理 BeanFactory：扩展点，子类可以修改 BeanFactory
     * 5. 执行 BeanFactoryPostProcessor
     * 6. 注册 BeanPostProcessor
     * 7. 初始化消息源：国际化支持
     * 8. 初始化事件广播器
     * 9. 刷新子上下文：模板方法，子类可以扩展
     * 10. 注册监听器
     * 11. 完成 BeanFactory 初始化：实例化所有单例 Bean
     * 12. 完成刷新：发布容器刷新事件
     * <p>
     * 面试考点：
     * - refresh() 方法的 12 个步骤
     * - 为什么需要 refresh() 方法？
     * - 容器启动的完整流程
     *
     * @throws Exception 刷新失败时抛出异常
     */
    void refresh() throws Exception;

    /**
     * 关闭容器
     * <p>
     * 优雅地关闭容器，释放所有资源
     * <p>
     * 主要步骤：
     * 1. 发布容器关闭事件
     * 2. 销毁所有单例 Bean
     * 3. 关闭 BeanFactory
     * 4. 清理资源
     * <p>
     * 面试考点：
     * - 容器关闭时会做哪些事情？
     * - Bean 的销毁顺序是怎样的？
     * - 如何实现优雅关闭？
     */
    void close();

    /**
     * 注册关闭钩子
     * <p>
     * 向 JVM 注册关闭钩子，确保容器在 JVM 关闭时也能优雅关闭
     * <p>
     * 使用场景：
     * - 应用正常退出
     * - 应用异常终止
     * - 收到 kill 信号（SIGTERM）
     * <p>
     * 面试考点：
     * - 什么是 JVM 关闭钩子？
     * - 关闭钩子的执行时机？
     * - 如何保证资源正确释放？
     */
    void registerShutdownHook();

}
