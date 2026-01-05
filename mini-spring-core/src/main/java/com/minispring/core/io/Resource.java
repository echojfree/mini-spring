package com.minispring.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源接口：抽象底层资源的访问
 * <p>
 * 设计模式：策略模式
 * 面试考点：
 * 1. 如何抽象不同来源的资源（类路径、文件系统、URL等）
 * 2. 策略模式的应用：定义统一接口，不同实现类提供不同策略
 * 3. Spring 如何加载配置文件
 *
 * @author mini-spring
 */
public interface Resource {

    /**
     * 获取资源的输入流
     * <p>
     * 这是资源访问的核心方法，所有类型的资源都通过此方法获取内容
     *
     * @return 资源的输入流
     * @throws IOException 如果资源不存在或无法读取
     */
    InputStream getInputStream() throws IOException;

}
