package com.minispring.beans.factory.support;

import com.minispring.beans.PropertyValue;
import com.minispring.beans.PropertyValues;
import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.BeanFactory;
import com.minispring.beans.factory.DisposableBean;
import com.minispring.beans.factory.InitializingBean;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.BeanReference;

import java.lang.reflect.Method;
import java.util.HashMap;
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
     * 单例 Bean 缓存池（一级缓存）
     * key: Bean 名称
     * value: Bean 实例（成品对象）
     * <p>
     * 面试考点：
     * 1. 这是 Spring 三级缓存中的一级缓存
     * 2. 存储完全初始化好的单例 Bean
     * 3. 为什么需要缓存？避免重复创建，提高性能
     */
    private final Map<String, Object> singletonObjects = new HashMap<>();

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
     * 获取 Bean 实例
     * <p>
     * 完整流程（面试重点）：
     * 1. 查找 BeanDefinition，不存在则抛出异常
     * 2. 如果是单例 Bean：
     *    - 先从缓存池查找，存在则直接返回
     *    - 不存在则创建新实例，并放入缓存池
     * 3. 如果是原型 Bean：
     *    - 每次都创建新实例，不缓存
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
            // 从单例缓存池获取
            Object singletonBean = singletonObjects.get(name);
            if (singletonBean != null) {
                // 缓存命中，直接返回
                return singletonBean;
            }

            // 缓存未命中，创建新实例
            singletonBean = createBean(name, beanDefinition);

            // 放入单例缓存池
            singletonObjects.put(name, singletonBean);

            return singletonBean;
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

    // ==================== Bean 创建方法 ====================

    /**
     * 创建 Bean 实例
     * <p>
     * 完整的 Bean 创建流程（面试重点）：
     * 1. 实例化：创建 Bean 实例
     * 2. 属性注入：填充 Bean 的属性值
     * 3. 初始化：调用初始化方法
     * 4. 注册销毁方法：如果是单例 Bean
     * <p>
     * 面试考点：
     * 1. Bean 的完整生命周期
     * 2. 实例化和初始化的区别
     * 3. 属性注入的时机
     *
     * @param beanName       Bean 名称
     * @param beanDefinition Bean 定义
     * @return Bean 实例
     * @throws BeansException 创建失败
     */
    protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
        // 步骤1：实例化 Bean
        Object bean = instantiationStrategy.instantiate(beanDefinition);

        // 步骤2：属性注入
        applyPropertyValues(beanName, bean, beanDefinition);

        // 步骤3：初始化 Bean
        bean = initializeBean(beanName, bean, beanDefinition);

        // 步骤4：注册销毁方法（只有单例 Bean 需要）
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        return bean;
    }

    /**
     * 属性注入：为 Bean 填充属性值
     * <p>
     * 核心流程（面试重点）：
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
     * 4. 循环依赖问题（当前版本会出现，后续解决）
     *
     * @param beanName       Bean 名称
     * @param bean           Bean 实例
     * @param beanDefinition Bean 定义
     */
    protected void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        try {
            // 获取属性值集合
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
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

    // ==================== Bean 初始化方法 ====================

    /**
     * 初始化 Bean
     * <p>
     * Bean 初始化流程（面试重点）：
     * 1. 调用 Aware 接口方法（后续实现）
     * 2. 调用 BeanPostProcessor 的前置处理方法（后续实现）
     * 3. 调用初始化方法：
     *    - 先调用 InitializingBean 接口的 afterPropertiesSet 方法
     *    - 再调用自定义的 init-method
     * 4. 调用 BeanPostProcessor 的后置处理方法（后续实现）
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
        // 步骤1：调用 InitializingBean 接口方法
        if (bean instanceof InitializingBean) {
            try {
                ((InitializingBean) bean).afterPropertiesSet();
            } catch (Exception e) {
                throw new BeansException("InitializingBean.afterPropertiesSet() 调用失败: " + beanName, e);
            }
        }

        // 步骤2：调用自定义 init-method
        String initMethodName = beanDefinition.getInitMethodName();
        if (initMethodName != null && !initMethodName.isEmpty()) {
            try {
                Method initMethod = beanDefinition.getBeanClass().getMethod(initMethodName);
                initMethod.invoke(bean);
            } catch (Exception e) {
                throw new BeansException("init-method 调用失败: " + beanName + "." + initMethodName, e);
            }
        }

        return bean;
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

}
