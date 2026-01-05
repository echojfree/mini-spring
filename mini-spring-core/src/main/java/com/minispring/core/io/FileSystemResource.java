package com.minispring.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件系统资源实现：从文件系统加载资源
 * <p>
 * 使用场景：加载文件系统中的配置文件
 * 面试考点：
 * 1. File 和 FileInputStream 的使用
 * 2. 文件路径的处理（绝对路径、相对路径）
 * 3. 与 ClassPathResource 的区别
 *
 * @author mini-spring
 */
public class FileSystemResource implements Resource {

    /**
     * 文件对象
     */
    private final File file;

    /**
     * 文件路径
     */
    private final String path;

    /**
     * 构造函数：通过文件路径创建
     *
     * @param path 文件路径（可以是绝对路径或相对路径）
     */
    public FileSystemResource(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        this.path = path;
        this.file = new File(path);
    }

    /**
     * 构造函数：通过 File 对象创建
     *
     * @param file 文件对象
     */
    public FileSystemResource(File file) {
        if (file == null) {
            throw new IllegalArgumentException("文件对象不能为空");
        }
        this.file = file;
        this.path = file.getPath();
    }

    /**
     * 从文件系统获取资源的输入流
     * <p>
     * 实现原理：
     * 1. 使用 FileInputStream 读取文件
     * 2. 在读取前检查文件是否存在
     * 3. 如果文件不存在或不可读，抛出异常
     *
     * @return 文件输入流
     * @throws IOException 如果文件不存在或无法读取
     */
    @Override
    public InputStream getInputStream() throws IOException {
        // 检查文件是否存在
        if (!file.exists()) {
            throw new IOException("文件不存在: " + path);
        }

        // 检查是否为文件（而不是目录）
        if (!file.isFile()) {
            throw new IOException("路径不是一个文件: " + path);
        }

        // 检查文件是否可读
        if (!file.canRead()) {
            throw new IOException("文件不可读: " + path);
        }

        // 返回文件输入流
        return new FileInputStream(file);
    }

    /**
     * 获取文件路径
     *
     * @return 文件路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 获取 File 对象
     *
     * @return File 对象
     */
    public File getFile() {
        return file;
    }

}
