package com.minispring.core.io;

/**
 * 默认资源加载器实现
 * <p>
 * 实现原理：
 * 根据资源位置的前缀，选择合适的 Resource 实现类
 * <p>
 * 面试考点：
 * 1. 策略模式的实际应用
 * 2. Spring 如何统一处理不同来源的资源
 * 3. 资源位置的解析逻辑
 *
 * @author mini-spring
 */
public class DefaultResourceLoader implements ResourceLoader {

    /**
     * 根据位置加载资源
     * <p>
     * 加载策略：
     * 1. 以 "classpath:" 开头 -> ClassPathResource
     * 2. 以 "file:" 开头 -> FileSystemResource
     * 3. 默认 -> ClassPathResource
     *
     * @param location 资源位置
     * @return Resource 对象
     */
    @Override
    public Resource getResource(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("资源位置不能为空");
        }

        // 策略1：类路径资源
        // 例如："classpath:application.properties"
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            // 去掉 "classpath:" 前缀
            String path = location.substring(CLASSPATH_URL_PREFIX.length());
            return new ClassPathResource(path);
        }

        // 策略2：文件系统资源
        // 例如："file:///D:/config/app.properties" 或 "file:D:/config/app.properties"
        if (location.startsWith(FILE_URL_PREFIX)) {
            // 去掉 "file:" 前缀
            String path = location.substring(FILE_URL_PREFIX.length());
            // 处理 Windows 路径：file:///D:/... -> D:/...
            if (path.startsWith("///")) {
                path = path.substring(3);
            } else if (path.startsWith("//")) {
                path = path.substring(2);
            }
            return new FileSystemResource(path);
        }

        // 策略3：默认使用类路径资源
        // 没有前缀的情况，默认从类路径加载
        return new ClassPathResource(location);
    }

}
