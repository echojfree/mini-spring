package com.minispring.context.event;

import com.minispring.context.ApplicationEvent;
import com.minispring.context.ApplicationListener;

/**
 * ApplicationEventMulticaster - 应用事件广播器接口
 * <p>
 * 负责管理监听器和广播事件的核心组件
 * <p>
 * 核心功能（面试重点）：
 * 1. 监听器管理：添加、移除监听器
 * 2. 事件广播：将事件分发给所有匹配的监听器
 * 3. 类型匹配：根据泛型类型过滤监听器
 * <p>
 * 实现原理：
 * 1. 维护监听器集合
 * 2. 接收事件后，遍历所有监听器
 * 3. 通过反射获取监听器的泛型类型
 * 4. 如果事件类型匹配，调用监听器的 onApplicationEvent() 方法
 * <p>
 * 面试要点：
 * 1. 如何匹配监听器类型？
 *    - 使用反射获取 ApplicationListener 的泛型参数
 *    - 检查事件类型是否是泛型类型的子类
 *    - 支持事件继承关系
 * <p>
 * 2. 异常处理策略
 *    - 捕获单个监听器的异常
 *    - 记录异常日志
 *    - 继续执行其他监听器
 * <p>
 * 3. 执行顺序控制
 *    - 默认按注册顺序执行
 *    - 可实现 Ordered 接口指定优先级
 *    - 数字越小，优先级越高
 *
 * @author mini-spring
 */
public interface ApplicationEventMulticaster {

    /**
     * 添加监听器
     *
     * @param listener 应用事件监听器
     */
    void addApplicationListener(ApplicationListener<?> listener);

    /**
     * 移除监听器
     *
     * @param listener 应用事件监听器
     */
    void removeApplicationListener(ApplicationListener<?> listener);

    /**
     * 广播事件给所有匹配的监听器
     *
     * @param event 应用事件
     */
    void multicastEvent(ApplicationEvent event);

}
