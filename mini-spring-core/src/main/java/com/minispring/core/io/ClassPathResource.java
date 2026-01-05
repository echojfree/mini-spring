package com.minispring.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 类路径资源实现：从类路径（classpath）加载资源
 * <p>
 * 使用场景：加载 src/main/resources 下的配置文件
 * 面试考点：
 * 1. ClassLoader 的使用
 * 2. 类路径资源的加载原理
 * 3. getResourceAsStream 方法的工作机制
 *
 * @author mini-spring
 */
public class ClassPathResource implements Resource {

    /**
     * 资源路径（相对于类路径）
     * 例如："application.properties", "config/beans.xml"
     */
    private final String path;

    /**
     * 类加载器：用于加载资源
     * 如果为 null，则使用系统类加载器
     */
    private ClassLoader classLoader;

    /**
     * 构造函数：使用默认类加载器
     *
     * @param path 资源路径（相对于类路径）
     */
    public ClassPathResource(String path) {
        this(path, null);
    }

    /**
     * 构造函数：指定类加载器
     *
     * @param path        资源路径
     * @param classLoader 类加载器
     */
    public ClassPathResource(String path, ClassLoader classLoader) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("资源路径不能为空");
        }
        this.path = path;
        // 如果没有指定类加载器，使用当前类的类加载器
        this.classLoader = (classLoader != null ? classLoader : getClass().getClassLoader());
    }

    /**
     * 从类路径获取资源的输入流
     * <p>
     * 实现原理：
     * 1. 使用 ClassLoader.getResourceAsStream() 方法
     * 2. ClassLoader 会在类路径中搜索资源
     * 3. 返回资源的输入流，如果资源不存在则抛出异常
     *
     * @return 资源输入流
     * @throws IOException 如果资源不存在
     */
    @Override
    public InputStream getInputStream() throws IOException {
        // 使用类加载器加载资源
        InputStream inputStream = classLoader.getResourceAsStream(path);

        // 如果资源不存在，抛出异常
        if (inputStream == null) {
            throw new FileNotFoundException("类路径资源不存在: " + path);
        }

        return inputStream;
    }

    /**
     * 获取资源路径
     *
     * @return 资源路径
     */
    public String getPath() {
        return path;
    }

}
