package com.minispring.beans.factory.support;

import com.minispring.beans.PropertyValue;
import com.minispring.beans.PropertyValues;
import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.Aware;
import com.minispring.beans.factory.BeanFactory;
import com.minispring.beans.factory.BeanFactoryAware;
import com.minispring.beans.factory.BeanNameAware;
import com.minispring.beans.factory.DisposableBean;
import com.minispring.beans.factory.InitializingBean;
import com.minispring.beans.factory.ObjectFactory;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.BeanPostProcessor;
import com.minispring.beans.factory.config.BeanReference;
import com.minispring.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认的 BeanFactory 实现：支持 Bean 的注册、获取和单例管理
 * <p>
 * 核心功能：
 * 1. 实现 BeanFactory 接口，提供 getBean 方法
 * 2. 实现 BeanDefinitionRegistry 接口，提供 Bean 定义的注册功能
 * 3. 管理单例 Bean 的缓存池
 * <p>
 * 设计模式：
 * - 工厂模式：创建 Bean 实例
 * - 单例模式：管理单例 Bean
 * - 注册表模式：管理 BeanDefinition
 * <p>
 * 面试考点：
 * 1. BeanFactory 的核心实现原理
 * 2. 单例 Bean 如何保证线程安全？
 * 3. BeanDefinition 和 Bean 实例的关系
 * 4. getBean 方法的完整流程
 *
 * @author mini-spring
 */
public class DefaultListableBeanFactory implements BeanFactory, BeanDefinitionRegistry {

    /**
     * Bean 定义注册表：存储所有的 BeanDefinition
     * key: Bean 名称
     * value: BeanDefinition 对象
     * <p>
     * 面试考点：为什么使用 Map 存储？
     * - 通过 Bean 名称快速查找 O(1)
     * - 支持动态注册和查询
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 一级缓存：单例 Bean 缓存池（成品对象）
     * key: Bean 名称
     * value: Bean 实例（完全初始化的 Bean）
     * <p>
     * 面试考点：
     * 1. 这是 Spring 三级缓存中的一级缓存
     * 2. 存储完全初始化好的单例 Bean
     * 3. 为什么需要缓存？避免重复创建，提高性能
     * 4. 线程安全：虽然使用 HashMap，但通过 synchronized 保证安全
     */
    private final Map<String, Object> singletonObjects = new HashMap<>();

    /**
     * 二级缓存：早期单例对象缓存（半成品对象）
     * key: Bean 名称
     * value: 早期 Bean 实例（实例化完成，但未初始化）
     * <p>
     * 面试考点：
     * 1. 存储提前暴露的半成品对象
     * 2. 用于解决循环依赖问题
     * 3. 对象已实例化但未完成属性注入和初始化
     * 4. 为什么需要二级缓存？
     *    - 提高性能：避免重复调用三级缓存的工厂方法
     *    - 保证对象唯一性：多次获取返回同一个半成品对象
     */
    private final Map<String, Object> earlySingletonObjects = new HashMap<>();

    /**
     * 三级缓存：单例对象工厂缓存
     * key: Bean 名称
     * value: ObjectFactory 对象工厂
     * <p>
     * 面试考点：
     * 1. 存储对象工厂，而不是对象本身
     * 2. 在需要时才调用工厂创建半成品对象
     * 3. 为什么需要三级缓存？
     *    - 支持 AOP 代理对象的循环依赖
     *    - 延迟代理对象的创建时机
     *    - 如果只有二级缓存，无法处理 AOP 场景
     * 4. ObjectFactory 的作用？
     *    - 封装对象创建逻辑（可能是原始对象，也可能是代理对象）
     *    - 提供扩展点，允许在创建对象时进行增强
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>();

    /**
     * 正在创建的 Bean 名称集合
     * 用于检测循环依赖
     * <p>
     * 面试考点：
     * 1. 如何检测循环依赖？
     *    - Bean 创建前加入此集合
     *    - Bean 创建完成后从此集合移除
     *    - 如果创建过程中再次请求同一个 Bean，说明存在循环依赖
     * 2. 为什么使用 ThreadLocal？
     *    - 避免多线程并发创建 Bean 时的干扰
     *    - 每个线程维护自己的"正在创建"集合
     * 3. 构造器循环依赖为什么无法解决？
     *    - 构造器注入时对象还未实例化，无法提前暴露
     *    - 三级缓存依赖于对象已经实例化
     */
    private final ThreadLocal<List<String>> singletonsCurrentlyInCreation = ThreadLocal.withInitial(ArrayList::new);

    /**
     * 销毁 Bean 的适配器集合
     * key: Bean 名称
     * value: DisposableBeanAdapter 实例
     * <p>
     * 面试考点：
     * 1. 为什么需要单独存储销毁适配器？
     *    - 容器关闭时需要调用销毁方法
     *    - 统一管理 DisposableBean 接口和 destroy-method
     * 2. 只有单例 Bean 才会被管理销毁
     *    - 原型 Bean 的生命周期由使用者管理
     */
    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();

    /**
     * BeanPostProcessor 列表
     * 按照注册顺序存储所有的 BeanPostProcessor
     * <p>
     * 面试考点：
     * 1. BeanPostProcessor 的执行顺序
     *    - 按照注册顺序执行
     *    - 可以通过 Ordered 接口指定优先级（后续实现）
     * 2. BeanPostProcessor 的应用场景
     *    - AOP 代理创建
     *    - 自动注入（@Autowired）
     *    - 自定义注解处理
     */
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    /**
     * Bean 实例化策略
     * 默认使用 CGLIB 实例化策略
     * <p>
     * 面试考点：
     * 1. 为什么使用策略模式？
     *    - 支持多种实例化方式（JDK 反射、CGLIB）
     *    - 便于扩展和替换
     * 2. Spring 默认使用哪种实例化方式？
     *    - Spring 5.x 之前：CGLIB
     *    - Spring 5.x 之后：优先 CGLIB，某些场景用反射
     */
    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    // ==================== BeanDefinitionRegistry 接口实现 ====================

    /**
     * 注册 BeanDefinition
     * <p>
     * 将 Bean 的元数据信息注册到容器中
     * 如果已存在同名 Bean，会被覆盖
     *
     * @param beanName       Bean 名称
     * @param beanDefinition Bean 定义
     */
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        if (beanName == null || beanName.trim().isEmpty()) {
            throw new BeansException("Bean 名称不能为空");
        }
        if (beanDefinition == null) {
            throw new BeansException("BeanDefinition 不能为 null");
        }

        // 存储到注册表
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    /**
     * 获取 BeanDefinition
     *
     * @param beanName Bean 名称
     * @return BeanDefinition 对象，如果不存在返回 null
     */
    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanDefinitionMap.get(beanName);
    }

    /**
     * 判断是否包含指定的 BeanDefinition
     *
     * @param beanName Bean 名称
     * @return true 表示存在
     */
    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    /**
     * 获取所有 Bean 名称
     *
     * @return Bean 名称数组
     */
    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    // ==================== BeanFactory 接口实现 ====================

    /**
     * 从三级缓存获取单例 Bean
     * <p>
     * 这是 Spring 三级缓存的核心实现（面试重点）
     * <p>
     * 查找顺序：
     * 1. 一级缓存 singletonObjects：成品对象
     * 2. 二级缓存 earlySingletonObjects：半成品对象
     * 3. 三级缓存 singletonFactories：对象工厂
     * <p>
     * 面试考点：
     * 1. 为什么按这个顺序查找？
     *    - 优先返回完全初始化的对象（一级缓存）
     *    - 其次返回半成品对象（二级缓存）
     *    - 最后调用工厂创建半成品对象（三级缓存）
     * 2. 什么时候会查到三级缓存？
     *    - 循环依赖场景：A 依赖 B，B 依赖 A
     *    - A 创建过程中需要 B，B 创建过程中又需要 A
     *    - 此时 A 在三级缓存中，需要提前暴露
     * 3. 为什么要移动到二级缓存？
     *    - 提高性能：避免重复调用工厂方法
     *    - 保证唯一性：多次获取返回同一个对象
     *
     * @param beanName        Bean 名称
     * @param allowEarlyReference 是否允许提前引用
     * @return Bean 实例，可能是成品对象或半成品对象
     */
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        // 步骤1：从一级缓存获取成品对象
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject != null) {
            return singletonObject;
        }

        // 步骤2：从二级缓存获取半成品对象
        singletonObject = earlySingletonObjects.get(beanName);
        if (singletonObject != null) {
            return singletonObject;
        }

        // 步骤3：从三级缓存获取对象工厂
        if (allowEarlyReference) {
            ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
            if (singletonFactory != null) {
                try {
                    // 调用工厂方法创建半成品对象
                    singletonObject = singletonFactory.getObject();

                    // 移动到二级缓存
                    earlySingletonObjects.put(beanName, singletonObject);

                    // 从三级缓存移除
                    singletonFactories.remove(beanName);
                } catch (Exception e) {
                    throw new BeansException("创建早期 Bean 引用失败: " + beanName, e);
                }
            }
        }

        return singletonObject;
    }

    /**
     * 添加单例对象工厂到三级缓存
     * <p>
     * 提前暴露对象工厂，用于解决循环依赖
     * <p>
     * 面试考点：
     * 1. 什么时候调用此方法？
     *    - Bean 实例化之后，属性注入之前
     *    - 仅对单例 Bean 进行提前暴露
     * 2. 为什么要提前暴露工厂而不是对象？
     *    - 支持 AOP 代理对象的循环依赖
     *    - 延迟代理对象的创建时机
     * 3. ObjectFactory 如何创建对象？
     *    - 正常情况：返回原始对象
     *    - AOP 场景：返回代理对象（通过 BeanPostProcessor）
     *
     * @param beanName Bean 名称
     * @param singletonFactory 对象工厂
     */
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        synchronized (singletonObjects) {
            // 只有在一级缓存不存在时才添加
            if (!singletonObjects.containsKey(beanName)) {
                singletonFactories.put(beanName, singletonFactory);
                // 从二级缓存移除（保证只存在于一个缓存中）
                earlySingletonObjects.remove(beanName);
            }
        }
    }

    /**
     * 添加单例 Bean 到一级缓存
     * <p>
     * Bean 完全初始化后，放入一级缓存
     *
     * @param beanName Bean 名称
     * @param singletonObject 单例对象
     */
    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (singletonObjects) {
            singletonObjects.put(beanName, singletonObject);
            // 从二级和三级缓存移除
            earlySingletonObjects.remove(beanName);
            singletonFactories.remove(beanName);
        }
    }

    /**
     * Bean 是否正在创建中
     *
     * @param beanName Bean 名称
     * @return true 表示正在创建
     */
    protected boolean isSingletonCurrentlyInCreation(String beanName) {
        return singletonsCurrentlyInCreation.get().contains(beanName);
    }

    /**
     * 标记 Bean 开始创建
     *
     * @param beanName Bean 名称
     */
    protected void beforeSingletonCreation(String beanName) {
        if (!singletonsCurrentlyInCreation.get().add(beanName)) {
            throw new BeansException("检测到循环依赖: " + beanName + " 正在创建中");
        }
    }

    /**
     * 标记 Bean 创建完成
     *
     * @param beanName Bean 名称
     */
    protected void afterSingletonCreation(String beanName) {
        if (!singletonsCurrentlyInCreation.get().remove(beanName)) {
            throw new BeansException("Bean 创建状态异常: " + beanName);
        }
    }

    /**
     * 获取 Bean 实例
     * <p>
     * 完整流程（面试重点）：
     * 1. 查找 BeanDefinition，不存在则抛出异常
     * 2. 如果是单例 Bean：
     *    - 先从三级缓存查找（一级→二级→三级）
     *    - 不存在则创建新实例，支持循环依赖
     * 3. 如果是原型 Bean：
     *    - 每次都创建新实例，不缓存
     *    - 不支持循环依赖（无法提前暴露）
     * <p>
     * v0.12.0 更新：支持三级缓存和循环依赖解决
     *
     * @param name Bean 名称
     * @return Bean 实例
     * @throws BeansException 如果 Bean 不存在或创建失败
     */
    @Override
    public Object getBean(String name) throws BeansException {
        // 步骤1：查找 BeanDefinition
        BeanDefinition beanDefinition = getBeanDefinition(name);
        if (beanDefinition == null) {
            throw new BeansException("Bean 不存在: " + name);
        }

        // 步骤2：处理单例 Bean
        if (beanDefinition.isSingleton()) {
            // 从三级缓存获取（支持循环依赖）
            Object singletonBean = getSingleton(name, true);
            if (singletonBean != null) {
                // 缓存命中，直接返回
                return singletonBean;
            }

            // 缓存未命中，创建新实例
            try {
                // 标记 Bean 开始创建
                beforeSingletonCreation(name);

                // 创建 Bean 实例（支持提前暴露）
                singletonBean = createBean(name, beanDefinition);

                // 放入一级缓存
                addSingleton(name, singletonBean);

                return singletonBean;
            } finally {
                // 标记 Bean 创建完成
                afterSingletonCreation(name);
            }
        }

        // 步骤3：处理原型 Bean（每次创建新实例）
        return createBean(name, beanDefinition);
    }

    /**
     * 获取指定类型的 Bean 实例
     *
     * @param name         Bean 名称
     * @param requiredType 要求的类型
     * @param <T>          泛型类型
     * @return 指定类型的 Bean 实例
     * @throws BeansException 如果类型不匹配
     */
    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        Object bean = getBean(name);

        // 类型检查
        if (requiredType != null && !requiredType.isInstance(bean)) {
            throw new BeansException(
                    "Bean 类型不匹配: 期望类型 " + requiredType.getName() +
                            ", 实际类型 " + bean.getClass().getName()
            );
        }

        return (T) bean;
    }

    /**
     * 根据类型获取 Bean 实例
     * <p>
     * 按类型查找 Bean，如果有多个相同类型的 Bean 会抛出异常
     * 这是 @Autowired 按类型注入的基础
     * <p>
     * 面试考点：
     * 1. @Autowired 是如何按类型注入的？
     *    - 遍历所有 BeanDefinition，找到类型匹配的 Bean
     *    - 如果找到多个，需要 @Qualifier 指定
     * 2. 为什么 @Autowired 默认按类型注入？
     *    - 更符合面向对象编程（依赖接口而非名称）
     *    - 重构友好（修改 Bean 名称不影响注入）
     *
     * @param requiredType 要求的类型
     * @param <T>          泛型类型
     * @return 指定类型的 Bean 实例
     * @throws BeansException 如果找不到 Bean、有多个候选 Bean 或创建失败
     */
    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        // 遍历所有 BeanDefinition，找到类型匹配的
        String foundBeanName = null;
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            Class<?> beanClass = beanDefinition.getBeanClass();

            // 检查类型是否匹配（包括子类和接口实现）
            if (requiredType.isAssignableFrom(beanClass)) {
                if (foundBeanName != null) {
                    // 找到多个候选 Bean
                    throw new BeansException(
                            "找到多个类型为 " + requiredType.getName() + " 的 Bean: " +
                                    foundBeanName + ", " + entry.getKey() +
                                    "。请使用 @Qualifier 指定 Bean 名称"
                    );
                }
                foundBeanName = entry.getKey();
            }
        }

        // 如果找不到匹配的 Bean
        if (foundBeanName == null) {
            throw new BeansException("找不到类型为 " + requiredType.getName() + " 的 Bean");
        }

        // 获取 Bean 实例
        return getBean(foundBeanName, requiredType);
    }

    // ==================== Bean 创建方法 ====================

    /**
     * 创建 Bean 实例
     * <p>
     * 完整的 Bean 创建流程（面试重点）：
     * 1. 实例化：创建 Bean 实例
     * 2. 提前暴露：将对象工厂放入三级缓存（解决循环依赖）
     * 3. 属性注入：填充 Bean 的属性值
     * 4. 初始化：调用初始化方法
     * 5. 注册销毁方法：如果是单例 Bean
     * <p>
     * v0.12.0 更新：支持提前暴露和循环依赖解决
     * <p>
     * 面试考点：
     * 1. Bean 的完整生命周期
     * 2. 实例化和初始化的区别
     * 3. 为什么在属性注入前提前暴露？
     *    - 支持循环依赖解决
     *    - 允许其他 Bean 获取半成品对象
     * 4. 提前暴露的是什么？
     *    - ObjectFactory 对象工厂
     *    - 不是 Bean 实例本身
     *
     * @param beanName       Bean 名称
     * @param beanDefinition Bean 定义
     * @return Bean 实例
     * @throws BeansException 创建失败
     */
    protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
        // 步骤1：实例化 Bean
        Object bean = instantiationStrategy.instantiate(beanDefinition);

        // 步骤2：提前暴露 Bean（仅对单例 Bean）
        // 面试考点：为什么要提前暴露？解决循环依赖
        if (beanDefinition.isSingleton()) {
            // 创建对象工厂，延迟获取 Bean 引用
            // 使用匿名内部类实现 ObjectFactory 接口
            final Object finalBean = bean; // Lambda 需要 final 变量
            addSingletonFactory(beanName, new ObjectFactory<Object>() {
                @Override
                public Object getObject() throws Exception {
                    return getEarlyBeanReference(beanName, beanDefinition, finalBean);
                }
            });
        }

        // 步骤3：属性注入
        // 如果存在循环依赖，这里会触发提前暴露
        applyPropertyValues(beanName, bean, beanDefinition);

        // 步骤4：初始化 Bean
        bean = initializeBean(beanName, bean, beanDefinition);

        // 步骤5：注册销毁方法（只有单例 Bean 需要）
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        return bean;
    }

    /**
     * 获取早期 Bean 引用
     * <p>
     * 这是 ObjectFactory 的实现方法
     * 在需要提前暴露 Bean 时调用
     * <p>
     * 面试考点：
     * 1. 什么时候调用此方法？
     *    - 循环依赖场景，其他 Bean 需要当前 Bean 的引用
     * 2. 为什么要单独一个方法？
     *    - 提供扩展点，支持 AOP 代理
     *    - 正常情况返回原始对象
     *    - AOP 场景返回代理对象（通过 BeanPostProcessor）
     * 3. 与 BeanPostProcessor 的关系？
     *    - 可以在这里调用 BeanPostProcessor.getEarlyBeanReference()
     *    - 实现 AOP 代理对象的提前暴露（后续版本实现）
     *
     * @param beanName       Bean 名称
     * @param beanDefinition Bean 定义
     * @param bean           Bean 实例
     * @return Bean 引用（可能是原始对象，也可能是代理对象）
     */
    protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
        Object exposedObject = bean;

        // TODO: 后续版本实现 AOP 时，在这里调用 BeanPostProcessor.getEarlyBeanReference()
        // 示例代码：
        // for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
        //     if (beanPostProcessor instanceof SmartInstantiationAwareBeanPostProcessor) {
        //         SmartInstantiationAwareBeanPostProcessor processor = ...;
        //         exposedObject = processor.getEarlyBeanReference(exposedObject, beanName);
        //     }
        // }

        return exposedObject;
    }

    /**
     * 属性注入：为 Bean 填充属性值
     * <p>
     * 核心流程（面试重点）：
     * 0. 调用 InstantiationAwareBeanPostProcessor.postProcessPropertyValues（v0.15.0 新增）
     * 1. 获取 BeanDefinition 中的所有属性值
     * 2. 遍历每个属性值：
     *    - 如果是 BeanReference，通过 getBean 获取引用的 Bean
     *    - 如果是普通值，直接使用
     * 3. 使用反射设置属性值
     * <p>
     * 面试考点：
     * 1. 依赖注入的实现原理
     * 2. 如何区分 Bean 引用和普通值
     * 3. 反射设置属性的过程
     * 4. @Autowired 注解是如何工作的（通过 InstantiationAwareBeanPostProcessor）
     *
     * @param beanName       Bean 名称
     * @param bean           Bean 实例
     * @param beanDefinition Bean 定义
     */
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        try {
            // v0.15.0 新增：调用 InstantiationAwareBeanPostProcessor.postProcessPropertyValues
            // 用于处理 @Autowired 注解
            PropertyValues propertyValues = beanDefinition.getPropertyValues();

            // 如果为 null，创建空的 PropertyValues
            if (propertyValues == null) {
                propertyValues = new PropertyValues();
            }

            // 调用 InstantiationAwareBeanPostProcessor
            for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                    InstantiationAwareBeanPostProcessor processor =
                            (InstantiationAwareBeanPostProcessor) beanPostProcessor;
                    propertyValues = processor.postProcessPropertyValues(propertyValues, bean, beanName);
                }
            }

            // 获取属性值集合
            if (propertyValues == null || propertyValues.isEmpty()) {
                // 没有属性需要注入
                return;
            }

            // 遍历所有属性值
            for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                String name = propertyValue.getName();
                Object value = propertyValue.getValue();

                // 处理 Bean 引用
                // 面试考点：依赖注入的核心逻辑
                if (value instanceof BeanReference) {
                    BeanReference beanReference = (BeanReference) value;
                    // 获取引用的 Bean
                    // 这里可能会触发循环依赖问题
                    value = getBean(beanReference.getBeanName());
                }

                // 使用反射设置属性值
                // 面试考点：反射的应用
                java.beans.PropertyDescriptor pd = new java.beans.PropertyDescriptor(name, bean.getClass());
                java.lang.reflect.Method writeMethod = pd.getWriteMethod();
                writeMethod.invoke(bean, value);
            }

        } catch (Exception e) {
            throw new BeansException("属性注入失败: " + beanName, e);
        }
    }

    /**
     * 设置实例化策略
     * <p>
     * 允许外部更换实例化策略
     * 例如：从 CGLIB 切换到简单反射
     *
     * @param instantiationStrategy 实例化策略
     */
    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

    // ==================== BeanPostProcessor 管理 ====================

    /**
     * 添加 BeanPostProcessor
     * <p>
     * 注册 BeanPostProcessor 到容器
     * BeanPostProcessor 会在 Bean 初始化前后被调用
     * <p>
     * 面试考点：
     * 1. BeanPostProcessor 的注册时机
     *    - 应该在任何 Bean 创建之前注册
     *    - 通常在容器启动时注册
     * 2. BeanPostProcessor 本身也是 Bean 吗？
     *    - 可以是普通 Bean，也可以手动注册
     *    - Spring 会自动发现并注册实现了 BeanPostProcessor 的 Bean
     *
     * @param beanPostProcessor BeanPostProcessor 实例
     */
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        // 移除已存在的相同实例（避免重复注册）
        this.beanPostProcessors.remove(beanPostProcessor);

        // 如果实现了 BeanFactoryAware，注入 BeanFactory
        if (beanPostProcessor instanceof BeanFactoryAware) {
            ((BeanFactoryAware) beanPostProcessor).setBeanFactory(this);
        }

        // 添加到列表
        this.beanPostProcessors.add(beanPostProcessor);
    }

    /**
     * 获取所有 BeanPostProcessor
     *
     * @return BeanPostProcessor 列表
     */
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    // ==================== Bean 初始化方法 ====================

    /**
     * 初始化 Bean
     * <p>
     * Bean 初始化流程（面试重点）：
     * 1. 调用 Aware 接口方法
     * 2. 调用 BeanPostProcessor 的前置处理方法
     * 3. 调用初始化方法：
     *    - 先调用 InitializingBean 接口的 afterPropertiesSet 方法
     *    - 再调用自定义的 init-method
     * 4. 调用 BeanPostProcessor 的后置处理方法
     * <p>
     * 面试考点：
     * 1. 初始化方法的执行顺序
     * 2. InitializingBean 和 init-method 的区别
     * 3. 初始化阶段在 Bean 生命周期中的位置
     *
     * @param beanName       Bean 名称
     * @param bean           Bean 实例
     * @param beanDefinition Bean 定义
     * @return 初始化后的 Bean 实例
     */
    protected Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 步骤1：调用 Aware 接口方法
        invokeAwareMethods(beanName, bean);

        // 步骤2：调用 BeanPostProcessor 的前置处理方法
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        // 步骤3：调用初始化方法
        try {
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("初始化方法调用失败: " + beanName, e);
        }

        // 步骤4：调用 BeanPostProcessor 的后置处理方法
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);

        return wrappedBean;
    }

    /**
     * 调用初始化方法
     * <p>
     * 调用顺序：
     * 1. InitializingBean.afterPropertiesSet()
     * 2. init-method
     *
     * @param beanName       Bean 名称
     * @param bean           Bean 实例
     * @param beanDefinition Bean 定义
     * @throws Exception 初始化失败
     */
    private void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
        // 1. 调用 InitializingBean 接口方法
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }

        // 2. 调用自定义 init-method
        String initMethodName = beanDefinition.getInitMethodName();
        if (initMethodName != null && !initMethodName.isEmpty()) {
            Method initMethod = beanDefinition.getBeanClass().getMethod(initMethodName);
            initMethod.invoke(bean);
        }
    }

    /**
     * 调用 Aware 接口方法
     * <p>
     * 按照特定顺序调用 Aware 接口：
     * 1. BeanNameAware
     * 2. BeanFactoryAware
     * 3. ApplicationContextAware（后续实现）
     * <p>
     * 面试考点：
     * 1. Aware 接口的调用时机
     *    - 在属性注入之后
     *    - 在 InitializingBean 之前
     * 2. Aware 接口的调用顺序
     *    - BeanNameAware → BeanFactoryAware → ApplicationContextAware
     * 3. 为什么需要 Aware 接口？
     *    - 让 Bean 能够感知容器环境
     *    - 实现与容器的交互
     *
     * @param beanName Bean 名称
     * @param bean     Bean 实例
     */
    private void invokeAwareMethods(String beanName, Object bean) {
        if (bean instanceof Aware) {
            // 1. BeanNameAware
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }

            // 2. BeanFactoryAware
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory(this);
            }

            // 3. ApplicationContextAware 将在 ApplicationContext 中实现
        }
    }

    /**
     * 应用 BeanPostProcessor 的前置处理方法
     * <p>
     * 遍历所有 BeanPostProcessor，依次调用其 postProcessBeforeInitialization 方法
     * <p>
     * 面试考点：
     * 1. 责任链模式的应用
     *    - 多个 BeanPostProcessor 按顺序处理
     *    - 每个处理器都可以修改 Bean 或返回包装对象
     * 2. 短路处理
     *    - 如果某个处理器返回 null，则直接返回 null
     *    - 后续处理器不会被调用
     *
     * @param existingBean 已存在的 Bean 实例
     * @param beanName     Bean 名称
     * @return 处理后的 Bean 实例
     */
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                // 短路返回
                return result;
            }
            result = current;
        }
        return result;
    }

    /**
     * 应用 BeanPostProcessor 的后置处理方法
     * <p>
     * 遍历所有 BeanPostProcessor，依次调用其 postProcessAfterInitialization 方法
     * <p>
     * 面试考点：
     * 1. AOP 代理创建的时机
     *    - 通常在 postProcessAfterInitialization 中创建代理
     *    - 此时 Bean 已经完全初始化，可以安全创建代理
     * 2. 代理对象替换原始对象
     *    - 返回的代理对象会替换原始 Bean
     *    - 容器中存储的是代理对象
     * 3. 多个代理的嵌套
     *    - 多个 BeanPostProcessor 可以创建多层代理
     *    - 代理1 包装 代理2 包装 原始Bean
     *
     * @param existingBean 已存在的 Bean 实例
     * @param beanName     Bean 名称
     * @return 处理后的 Bean 实例（可能是代理对象）
     */
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                // 短路返回
                return result;
            }
            result = current;
        }
        return result;
    }

    /**
     * 注册销毁方法
     * <p>
     * 判断 Bean 是否需要注册销毁方法：
     * 1. 只有单例 Bean 需要注册
     * 2. 实现了 DisposableBean 接口或配置了 destroy-method
     * <p>
     * 面试考点：
     * 1. 为什么原型 Bean 不注册销毁方法？
     *    - 原型 Bean 的生命周期由使用者管理
     *    - 容器不负责销毁原型 Bean
     * 2. 销毁方法的管理方式
     *    - 使用适配器模式统一管理
     *
     * @param beanName       Bean 名称
     * @param bean           Bean 实例
     * @param beanDefinition Bean 定义
     */
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 只有单例 Bean 才注册销毁方法
        if (!beanDefinition.isSingleton()) {
            return;
        }

        // 判断是否需要销毁
        // 1. 实现了 DisposableBean 接口
        // 2. 配置了 destroy-method
        if (bean instanceof DisposableBean || beanDefinition.getDestroyMethodName() != null) {
            // 创建销毁适配器并注册
            disposableBeans.put(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition.getDestroyMethodName()));
        }
    }

    // ==================== 容器销毁方法 ====================

    /**
     * 销毁所有单例 Bean
     * <p>
     * 容器关闭时调用此方法
     * 遍历所有注册的销毁适配器，调用销毁方法
     * <p>
     * 面试考点：
     * 1. 销毁顺序：按照注册的逆序销毁（后创建的先销毁）
     * 2. 异常处理：一个 Bean 销毁失败不影响其他 Bean
     * 3. 销毁时机：容器关闭时（ApplicationContext.close()）
     */
    public void destroySingletons() {
        // 获取所有需要销毁的 Bean 名称
        String[] disposableBeanNames = disposableBeans.keySet().toArray(new String[0]);

        // 逆序销毁（后创建的先销毁）
        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            String beanName = disposableBeanNames[i];
            DisposableBean disposableBean = disposableBeans.remove(beanName);

            try {
                disposableBean.destroy();
            } catch (Exception e) {
                // 记录异常，但不影响其他 Bean 的销毁
                System.err.println("销毁 Bean 失败: " + beanName + ", 错误: " + e.getMessage());
            }
        }
    }

    /**
     * 预实例化所有单例 Bean
     * <p>
     * ApplicationContext 的特性：容器启动时就创建所有单例 Bean
     * BeanFactory 是延迟加载，getBean 时才创建
     * <p>
     * 面试考点：
     * 1. ApplicationContext 和 BeanFactory 的区别
     *    - ApplicationContext：预加载，容器启动时创建
     *    - BeanFactory：延迟加载，使用时创建
     * 2. 预加载的优点
     *    - 提前发现配置错误
     *    - 提高运行时性能（不需要第一次调用时创建）
     * 3. 预加载的缺点
     *    - 启动时间较长
     *    - 占用更多内存
     */
    public void preInstantiateSingletons() {
        // 获取所有 BeanDefinition 的名称
        String[] beanNames = getBeanDefinitionNames();

        // 遍历实例化所有单例 Bean
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = getBeanDefinition(beanName);

            // 只实例化单例 Bean
            if (beanDefinition != null && beanDefinition.isSingleton()) {
                // 调用 getBean 触发 Bean 的创建
                getBean(beanName);
            }
        }
    }

    /**
     * 根据类型获取所有 Bean
     * <p>
     * 查找容器中所有符合指定类型的 Bean
     * <p>
     * 使用场景：
     * 1. AOP 自动代理：查找所有 Advisor
     * 2. 事件机制：查找所有 ApplicationListener
     * 3. 插件机制：查找所有插件实例
     * <p>
     * 实现逻辑：
     * 1. 遍历所有 BeanDefinition
     * 2. 判断 Bean 类型是否匹配
     * 3. 获取或创建 Bean 实例
     * 4. 返回所有匹配的 Bean
     *
     * @param type Bean 类型
     * @param <T>  泛型类型
     * @return Bean 名称 → Bean 实例的 Map
     * @throws BeansException Bean 异常
     */
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String, T> result = new HashMap<>();

        // 遍历所有 BeanDefinition
        String[] beanNames = getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = getBeanDefinition(beanName);
            if (beanDefinition == null) {
                continue;
            }

            // 判断类型是否匹配
            Class<?> beanClass = beanDefinition.getBeanClass();
            if (type.isAssignableFrom(beanClass)) {
                // 获取或创建 Bean 实例
                @SuppressWarnings("unchecked")
                T bean = (T) getBean(beanName);
                result.put(beanName, bean);
            }
        }

        return result;
    }

    /**
     * 注册单例 Bean
     * <p>
     * 直接将已创建的 Bean 实例注册到单例缓存中
     * <p>
     * 使用场景：
     * 1. 手动注册外部创建的对象
     * 2. 测试场景中快速注册 Bean
     * 3. 框架内部注册特殊 Bean
     *
     * @param beanName Bean 名称
     * @param singletonObject 单例对象
     */
    public void registerSingleton(String beanName, Object singletonObject) {
        addSingleton(beanName, singletonObject);
    }

}
