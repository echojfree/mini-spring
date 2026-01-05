package com.minispring.context.event;

import com.minispring.context.ApplicationEvent;
import com.minispring.context.ApplicationListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * SimpleApplicationEventMulticaster - 简单应用事件广播器实现
 * <p>
 * ApplicationEventMulticaster 的简单实现，提供同步事件广播功能
 * <p>
 * 实现特点：
 * 1. 使用 CopyOnWriteArraySet 存储监听器（线程安全）
 * 2. 同步广播事件（按注册顺序依次调用）
 * 3. 通过反射获取泛型类型进行类型匹配
 * 4. 捕获并忽略监听器异常，不影响其他监听器
 * <p>
 * 面试要点：
 * 1. 为什么使用 CopyOnWriteArraySet？
 *    - 读多写少的场景
 *    - 避免并发修改异常
 *    - 保证线程安全
 * <p>
 * 2. 泛型类型匹配原理
 *    - 使用反射获取接口泛型参数
 *    - 判断事件类型是否是泛型参数的子类
 *    - 支持事件继承关系
 * <p>
 * 3. 异常处理
 *    - 捕获单个监听器异常
 *    - 打印异常信息
 *    - 继续执行其他监听器
 *
 * @author mini-spring
 */
public class SimpleApplicationEventMulticaster implements ApplicationEventMulticaster {

    /**
     * 监听器集合
     * 使用 CopyOnWriteArraySet 保证线程安全
     */
    private final Set<ApplicationListener> applicationListeners = new CopyOnWriteArraySet<>();

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.add(listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.remove(listener);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void multicastEvent(ApplicationEvent event) {
        // 遍历所有监听器
        for (ApplicationListener listener : applicationListeners) {
            // 检查监听器是否支持该事件类型
            if (supportsEvent(listener, event)) {
                try {
                    // 调用监听器处理事件
                    listener.onApplicationEvent(event);
                } catch (Exception e) {
                    // 捕获异常，不影响其他监听器
                    System.err.println("监听器执行异常: " + listener.getClass().getName());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 检查监听器是否支持该事件类型
     * <p>
     * 通过反射获取监听器的泛型类型，判断事件是否匹配
     *
     * @param listener 监听器
     * @param event    事件
     * @return 是否支持
     */
    protected boolean supportsEvent(ApplicationListener<?> listener, ApplicationEvent event) {
        Class<?> listenerClass = listener.getClass();

        // 如果是 CGLIB 代理，获取父类
        if (listenerClass.getName().contains("$$")) {
            listenerClass = listenerClass.getSuperclass();
        }

        // 获取监听器实现的所有接口
        Type[] genericInterfaces = listenerClass.getGenericInterfaces();

        for (Type genericInterface : genericInterfaces) {
            // 检查是否是 ParameterizedType（带泛型参数的类型）
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;

                // 检查是否是 ApplicationListener 接口
                Type rawType = parameterizedType.getRawType();
                if (rawType instanceof Class && ApplicationListener.class.isAssignableFrom((Class<?>) rawType)) {
                    // 获取泛型参数（事件类型）
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length > 0) {
                        Type eventType = actualTypeArguments[0];

                        // 检查事件类型是否匹配
                        if (eventType instanceof Class) {
                            Class<?> eventClass = (Class<?>) eventType;
                            // 判断事件是否是泛型类型的实例（支持继承）
                            return eventClass.isAssignableFrom(event.getClass());
                        }
                    }
                }
            }
        }

        // 如果无法确定类型，默认不支持
        return false;
    }

}
