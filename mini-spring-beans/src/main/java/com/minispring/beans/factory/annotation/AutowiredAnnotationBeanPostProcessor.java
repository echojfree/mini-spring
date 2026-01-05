package com.minispring.beans.factory.annotation;

import com.minispring.beans.PropertyValues;
import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.BeanFactory;
import com.minispring.beans.factory.BeanFactoryAware;
import com.minispring.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 处理 @Autowired 和 @Value 注解的 Bean 后置处理器
 * <p>
 * 实现原理：
 * 1. 实现 InstantiationAwareBeanPostProcessor 接口
 * 2. 在 postProcessPropertyValues() 方法中处理依赖注入
 * 3. 使用反射扫描 Bean 的字段和方法
 * 4. 发现 @Autowired 注解后从容器中获取依赖
 * 5. 发现 @Value 注解后解析占位符并注入值
 * 6. 通过反射设置字段值或调用方法
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
 * 4. @Value 如何工作？
 *    - 解析 ${key} 占位符
 *    - 支持默认值 ${key:defaultValue}
 *    - 进行类型转换
 *
 * @author mini-spring
 */
public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

    /**
     * PropertyPlaceholderConfigurer 用于解析 @Value 占位符
     */
    private com.minispring.beans.factory.config.PropertyPlaceholderConfigurer placeholderConfigurer;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 设置属性占位符配置器
     *
     * @param placeholderConfigurer PropertyPlaceholderConfigurer 实例
     */
    public void setPlaceholderConfigurer(com.minispring.beans.factory.config.PropertyPlaceholderConfigurer placeholderConfigurer) {
        this.placeholderConfigurer = placeholderConfigurer;
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
     * 扫描类的所有字段，发现 @Autowired 或 @Value 注解后进行注入
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
                    Object dependencyBean;

                    // 检查是否有 @Qualifier 注解
                    Qualifier qualifier = field.getAnnotation(Qualifier.class);
                    if (qualifier != null) {
                        // 如果有 @Qualifier，按名称和类型查找
                        String beanName = qualifier.value();
                        if (beanName == null || beanName.isEmpty()) {
                            // 如果 @Qualifier 没有指定值，使用字段名作为 Bean 名称
                            beanName = field.getName();
                        }
                        dependencyBean = beanFactory.getBean(beanName, fieldType);
                    } else {
                        // 如果没有 @Qualifier，按类型查找
                        dependencyBean = beanFactory.getBean(fieldType);
                    }

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
                continue; // 已处理 @Autowired，跳过 @Value 检查
            }

            // 检查是否有 @Value 注解
            Value value = field.getAnnotation(Value.class);
            if (value != null) {
                try {
                    // 获取注解值
                    String valueStr = value.value();

                    // 解析占位符
                    if (placeholderConfigurer != null && valueStr != null) {
                        valueStr = placeholderConfigurer.resolvePlaceholder(valueStr);
                    }

                    // 类型转换
                    Object convertedValue = convertValue(valueStr, field.getType());

                    // 设置字段可访问
                    field.setAccessible(true);

                    // 通过反射设置字段值
                    field.set(bean, convertedValue);

                } catch (IllegalAccessException e) {
                    throw new BeansException("无法访问字段: " + field.getName(), e);
                } catch (Exception e) {
                    throw new BeansException("无法注入 @Value 字段: " + field.getName(), e);
                }
            }
        }
    }

    /**
     * 简单的类型转换
     * <p>
     * 将字符串值转换为目标类型
     *
     * @param value      字符串值
     * @param targetType 目标类型
     * @return 转换后的值
     */
    private Object convertValue(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // String 类型直接返回
        if (targetType == String.class) {
            return value;
        }

        // 基本类型和包装类型
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        }
        if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        }
        if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        }
        if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(value);
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        if (targetType == short.class || targetType == Short.class) {
            return Short.parseShort(value);
        }
        if (targetType == byte.class || targetType == Byte.class) {
            return Byte.parseByte(value);
        }

        // 不支持的类型，直接返回字符串
        return value;
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
                    Object dependencyBean;

                    // 检查方法的第一个参数是否有 @Qualifier 注解
                    Qualifier qualifier = null;
                    if (method.getParameterAnnotations().length > 0 && method.getParameterAnnotations()[0].length > 0) {
                        for (java.lang.annotation.Annotation annotation : method.getParameterAnnotations()[0]) {
                            if (annotation instanceof Qualifier) {
                                qualifier = (Qualifier) annotation;
                                break;
                            }
                        }
                    }

                    if (qualifier != null) {
                        // 如果有 @Qualifier，按名称和类型查找
                        String beanName = qualifier.value();
                        if (beanName == null || beanName.isEmpty()) {
                            // 如果 @Qualifier 没有指定值，从方法名推导 Bean 名称
                            // 例如 setUserService -> userService
                            String methodName = method.getName();
                            if (methodName.startsWith("set") && methodName.length() > 3) {
                                beanName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                            } else {
                                throw new BeansException("无法从方法名推导 Bean 名称: " + methodName);
                            }
                        }
                        dependencyBean = beanFactory.getBean(beanName, parameterType);
                    } else {
                        // 如果没有 @Qualifier，按类型查找
                        dependencyBean = beanFactory.getBean(parameterType);
                    }

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
