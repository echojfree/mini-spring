package com.minispring.beans.factory.annotation;

import com.minispring.beans.PropertyValues;
import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.BeanFactory;
import com.minispring.beans.factory.BeanFactoryAware;
import com.minispring.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 处理 @Autowired 注解的 Bean 后置处理器
 * <p>
 * 实现原理：
 * 1. 实现 InstantiationAwareBeanPostProcessor 接口
 * 2. 在 postProcessPropertyValues() 方法中处理依赖注入
 * 3. 使用反射扫描 Bean 的字段和方法
 * 4. 发现 @Autowired 注解后从容器中获取依赖
 * 5. 通过反射设置字段值或调用方法
 * <p>
 * 面试重点：
 * 1. @Autowired 是如何工作的？
 *    - BeanPostProcessor 在 Bean 实例化后、属性填充阶段生效
 *    - 通过反射扫描类的字段和方法
 *    - 从 BeanFactory 中按类型获取依赖
 * 2. 字段注入 vs Setter 注入 vs 构造器注入
 *    - 字段注入：最简单，但不利于测试，破坏封装
 *    - Setter 注入：可选依赖，支持循环依赖
 *    - 构造器注入：强制依赖，不可变，最推荐
 * 3. @Autowired 如何按类型注入？
 *    - 通过 BeanFactory.getBean(type) 获取
 *    - 如果有多个候选 Bean，需要 @Qualifier 指定
 *
 * @author mini-spring
 */
public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 在属性填充前处理 @Autowired 注解
     */
    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        // 1. 处理字段注入
        processFieldInjection(bean);

        // 2. 处理方法注入（Setter 方法）
        processMethodInjection(bean);

        return pvs;
    }

    /**
     * 处理字段注入
     * 扫描类的所有字段，发现 @Autowired 注解后进行注入
     */
    private void processFieldInjection(Object bean) {
        Class<?> clazz = bean.getClass();

        // CGLIB 代理类的字段不包含注解，需要获取父类（原始类）
        // 如果是 CGLIB 代理类（类名包含 $$），获取父类
        if (clazz.getName().contains("$$")) {
            clazz = clazz.getSuperclass();
        }

        // 遍历所有字段
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            // 检查是否有 @Autowired 注解
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (autowired != null) {
                // 获取字段类型
                Class<?> fieldType = field.getType();

                try {
                    // 从容器中按类型获取 Bean
                    Object dependencyBean = beanFactory.getBean(fieldType);

                    // 设置字段可访问（处理 private 字段）
                    field.setAccessible(true);

                    // 通过反射设置字段值（注意：设置到实际的 bean 对象上）
                    field.set(bean, dependencyBean);

                } catch (BeansException e) {
                    // 如果 required=true 且找不到 Bean，抛出异常
                    if (autowired.required()) {
                        throw new BeansException("无法注入字段: " + field.getName() + "，找不到类型为 " + fieldType.getName() + " 的 Bean");
                    }
                    // 如果 required=false，忽略异常
                } catch (IllegalAccessException e) {
                    throw new BeansException("无法访问字段: " + field.getName(), e);
                }
            }
        }
    }

    /**
     * 处理方法注入（主要是 Setter 方法）
     * 扫描类的所有方法，发现 @Autowired 注解后进行注入
     */
    private void processMethodInjection(Object bean) {
        Class<?> clazz = bean.getClass();

        // CGLIB 代理类的方法不包含注解，需要获取父类（原始类）
        if (clazz.getName().contains("$$")) {
            clazz = clazz.getSuperclass();
        }

        // 遍历所有方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            // 检查是否有 @Autowired 注解
            Autowired autowired = method.getAnnotation(Autowired.class);
            if (autowired != null) {
                // 获取方法参数类型（只处理单参数方法）
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new BeansException("@Autowired 方法必须只有一个参数: " + method.getName());
                }

                Class<?> parameterType = parameterTypes[0];

                try {
                    // 从容器中按类型获取 Bean
                    Object dependencyBean = beanFactory.getBean(parameterType);

                    // 设置方法可访问
                    method.setAccessible(true);

                    // 通过反射调用方法
                    method.invoke(bean, dependencyBean);

                } catch (BeansException e) {
                    // 如果 required=true 且找不到 Bean，抛出异常
                    if (autowired.required()) {
                        throw new BeansException("无法注入方法: " + method.getName() + "，找不到类型为 " + parameterType.getName() + " 的 Bean");
                    }
                    // 如果 required=false，忽略异常
                } catch (Exception e) {
                    throw new BeansException("无法调用方法: " + method.getName(), e);
                }
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }
}
