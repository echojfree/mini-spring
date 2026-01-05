package com.minispring.core.io;

/**
 * 资源加载器接口：按照指定位置加载资源
 * <p>
 * 设计模式：策略模式的工厂应用
 * 面试考点：
 * 1. 如何根据不同的资源位置前缀选择不同的加载策略
 * 2. classpath: 前缀 vs file: 前缀的区别
 * 3. Spring 中资源加载的统一抽象
 *
 * @author mini-spring
 */
public interface ResourceLoader {

    /**
     * 类路径资源前缀：用于标识从类路径加载资源
     * 例如："classpath:application.properties"
     */
    String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * 文件系统资源前缀：用于标识从文件系统加载资源
     * 例如："file:///D:/config/app.properties"
     */
    String FILE_URL_PREFIX = "file:";

    /**
     * 根据位置加载资源
     * <p>
     * 实现策略：
     * 1. 如果 location 以 "classpath:" 开头，使用 ClassPathResource
     * 2. 如果 location 以 "file:" 开头，使用 FileSystemResource
     * 3. 否则，默认使用 ClassPathResource
     *
     * @param location 资源位置
     *                 支持的格式：
     *                 - "classpath:config/beans.xml"
     *                 - "file:///D:/config/beans.xml"
     *                 - "config/beans.xml" (默认为 classpath)
     * @return Resource 资源对象
     */
    Resource getResource(String location);

}
