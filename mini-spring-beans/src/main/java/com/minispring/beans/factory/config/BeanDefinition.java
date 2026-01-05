package com.minispring.beans.factory.config;

/**
 * Bean 定义：存储 Bean 的元数据信息
 * <p>
 * 这是 Spring IoC 容器的核心概念之一
 * BeanDefinition 描述了一个 Bean 实例的所有信息，包括：
 * - Bean 的类型（Class）
 * - 作用域（单例/原型）
 * - 是否延迟初始化
 * - 初始化方法和销毁方法
 * - 属性值等
 * <p>
 * 面试考点：
 * 1. BeanDefinition 是什么？它存储了哪些信息？
 * 2. BeanDefinition 和 Bean 实例的区别
 * 3. Spring 容器如何利用 BeanDefinition 创建 Bean
 * 4. Bean 的作用域有哪些？单例和原型的区别？
 *
 * @author mini-spring
 */
public class BeanDefinition {

    /**
     * Bean 的 Class 对象
     * 用于反射创建 Bean 实例
     */
    private Class<?> beanClass;

    /**
     * Bean 的作用域：singleton（单例）或 prototype（原型）
     * 默认为单例
     * <p>
     * 面试考点：
     * - singleton：容器中只有一个实例，多次 getBean 返回同一个对象
     * - prototype：每次 getBean 都创建新实例
     */
    private String scope = SCOPE_SINGLETON;

    /**
     * 是否延迟初始化
     * true：第一次使用时才创建
     * false：容器启动时就创建（默认）
     */
    private boolean lazyInit = false;

    /**
     * 初始化方法名称
     * Bean 创建后会调用此方法进行初始化
     */
    private String initMethodName;

    /**
     * 销毁方法名称
     * Bean 销毁前会调用此方法进行清理
     */
    private String destroyMethodName;

    /**
     * Bean 是否是单例
     * 用于快速判断作用域
     */
    private boolean singleton = true;

    /**
     * Bean 是否是原型
     */
    private boolean prototype = false;

    // ==================== 常量定义 ====================

    /**
     * 单例作用域常量
     */
    public static final String SCOPE_SINGLETON = "singleton";

    /**
     * 原型作用域常量
     */
    public static final String SCOPE_PROTOTYPE = "prototype";

    // ==================== 构造函数 ====================

    /**
     * 构造函数：通过 Class 对象创建 BeanDefinition
     *
     * @param beanClass Bean 的 Class
     */
    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    // ==================== Getter/Setter ====================

    /**
     * 获取 Bean 的 Class 对象
     */
    public Class<?> getBeanClass() {
        return beanClass;
    }

    /**
     * 设置 Bean 的 Class 对象
     */
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    /**
     * 获取作用域
     */
    public String getScope() {
        return scope;
    }

    /**
     * 设置作用域
     * <p>
     * 面试考点：设置作用域时，需要同时更新 singleton 和 prototype 标志
     */
    public void setScope(String scope) {
        this.scope = scope;
        this.singleton = SCOPE_SINGLETON.equals(scope);
        this.prototype = SCOPE_PROTOTYPE.equals(scope);
    }

    /**
     * 是否单例
     */
    public boolean isSingleton() {
        return singleton;
    }

    /**
     * 是否原型
     */
    public boolean isPrototype() {
        return prototype;
    }

    /**
     * 是否延迟初始化
     */
    public boolean isLazyInit() {
        return lazyInit;
    }

    /**
     * 设置是否延迟初始化
     */
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    /**
     * 获取初始化方法名
     */
    public String getInitMethodName() {
        return initMethodName;
    }

    /**
     * 设置初始化方法名
     */
    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    /**
     * 获取销毁方法名
     */
    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    /**
     * 设置销毁方法名
     */
    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

}
