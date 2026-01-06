package com.minispring.tx.support;

import java.util.HashMap;
import java.util.Map;

/**
 * TransactionSynchronizationManager - 事务同步管理器
 * <p>
 * 使用 ThreadLocal 管理事务资源,确保同一事务内共享资源
 * <p>
 * 设计模式:单例模式、工具类模式
 * <p>
 * 面试要点:
 * 1. ThreadLocal 在事务中的应用
 *    - 绑定 Connection 到当前线程
 *    - 同一线程的多个 DAO 共享同一个 Connection
 *    - 事务结束后清理 ThreadLocal,避免内存泄漏
 * <p>
 * 2. 资源管理
 *    - resources: 存储事务资源(如 DataSource -> Connection)
 *    - key 通常是 DataSource
 *    - value 通常是 Connection 或 ConnectionHolder
 * <p>
 * 3. 为什么需要 TransactionSynchronizationManager
 *    - JDBC Connection 不是线程安全的
 *    - 同一事务内必须使用同一个 Connection
 *    - 通过 ThreadLocal 实现线程隔离和资源共享
 * <p>
 * 4. 与事务传播的关系
 *    - REQUIRED: 共享父事务的 Connection
 *    - REQUIRES_NEW: 挂起父事务,创建新的 Connection
 *    - NOT_SUPPORTED: 挂起事务,不使用 Connection
 *
 * @author mini-spring
 */
public class TransactionSynchronizationManager {

    /**
     * 事务资源存储
     * key: DataSource
     * value: Connection 或 ConnectionHolder
     */
    private static final ThreadLocal<Map<Object, Object>> resources = new ThreadLocal<>();

    /**
     * 绑定资源到当前线程
     *
     * @param key   资源key(通常是 DataSource)
     * @param value 资源value(通常是 Connection)
     */
    public static void bindResource(Object key, Object value) {
        Map<Object, Object> map = resources.get();
        if (map == null) {
            map = new HashMap<>();
            resources.set(map);
        }
        map.put(key, value);
    }

    /**
     * 获取绑定到当前线程的资源
     *
     * @param key 资源key
     * @return 资源value, 如果不存在返回 null
     */
    public static Object getResource(Object key) {
        Map<Object, Object> map = resources.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    /**
     * 解绑资源
     *
     * @param key 资源key
     * @return 被解绑的资源value
     */
    public static Object unbindResource(Object key) {
        Map<Object, Object> map = resources.get();
        if (map == null) {
            return null;
        }
        Object value = map.remove(key);
        if (map.isEmpty()) {
            resources.remove();
        }
        return value;
    }

    /**
     * 检查是否有绑定的资源
     *
     * @param key 资源key
     * @return true 表示有绑定资源, false 表示没有
     */
    public static boolean hasResource(Object key) {
        Map<Object, Object> map = resources.get();
        return map != null && map.containsKey(key);
    }

    /**
     * 清理当前线程的所有资源
     */
    public static void clear() {
        resources.remove();
    }

}
