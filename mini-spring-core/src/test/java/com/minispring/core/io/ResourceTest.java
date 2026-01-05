package com.minispring.core.io;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * 资源加载测试类
 * <p>
 * 测试目标：
 * 1. ClassPathResource 能否正确加载类路径资源
 * 2. FileSystemResource 能否正确加载文件系统资源
 * 3. DefaultResourceLoader 能否根据前缀选择正确的加载策略
 * 4. 异常情况的处理（资源不存在、路径为空等）
 *
 * @author mini-spring
 */
public class ResourceTest {

    /**
     * 测试 ClassPathResource：加载类路径下的资源
     */
    @Test
    public void testClassPathResource() throws IOException {
        // 加载 test.properties 文件
        Resource resource = new ClassPathResource("test.properties");
        InputStream inputStream = resource.getInputStream();

        // 验证资源是否加载成功
        assertNotNull("资源输入流不应为 null", inputStream);

        // 读取资源内容
        String content = readContent(inputStream);
        System.out.println("ClassPathResource 加载的内容：");
        System.out.println(content);

        // 验证内容包含预期的配置
        assertTrue("内容应包含 app.name", content.contains("app.name"));
        assertTrue("内容应包含 Mini Spring", content.contains("Mini Spring"));

        inputStream.close();
    }

    /**
     * 测试 ClassPathResource：资源不存在的情况
     */
    @Test
    public void testClassPathResourceNotFound() {
        try {
            Resource resource = new ClassPathResource("not-exist.properties");
            resource.getInputStream();
            fail("应该抛出 IOException");
        } catch (IOException e) {
            // 预期抛出异常
            System.out.println("正确抛出异常: " + e.getMessage());
            assertTrue(e.getMessage().contains("类路径资源不存在"));
        }
    }

    /**
     * 测试 ClassPathResource：路径为空的情况
     */
    @Test
    public void testClassPathResourceEmptyPath() {
        try {
            new ClassPathResource("");
            fail("应该抛出 IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // 预期抛出异常
            System.out.println("正确抛出异常: " + e.getMessage());
            assertEquals("资源路径不能为空", e.getMessage());
        }
    }

    /**
     * 测试 FileSystemResource：加载文件系统资源
     */
    @Test
    public void testFileSystemResource() throws IOException {
        // 首先创建一个临时文件
        java.io.File tempFile = java.io.File.createTempFile("test-", ".txt");
        tempFile.deleteOnExit();

        // 写入测试内容（使用 UTF-8 编码）
        java.io.FileWriter writer = new java.io.FileWriter(tempFile);
        writer.write("File System Resource Test Content\n");
        writer.write("This is a test file");
        writer.close();

        // 使用 FileSystemResource 加载
        Resource resource = new FileSystemResource(tempFile);
        InputStream inputStream = resource.getInputStream();

        assertNotNull("资源输入流不应为 null", inputStream);

        String content = readContent(inputStream);
        System.out.println("FileSystemResource 加载的内容：");
        System.out.println(content);

        assertTrue("内容应包含测试文本", content.contains("File System Resource"));
        inputStream.close();
    }

    /**
     * 测试 FileSystemResource：文件不存在的情况
     */
    @Test
    public void testFileSystemResourceNotFound() {
        try {
            Resource resource = new FileSystemResource("not-exist-file.txt");
            resource.getInputStream();
            fail("应该抛出 IOException");
        } catch (IOException e) {
            // 预期抛出异常
            System.out.println("正确抛出异常: " + e.getMessage());
            assertTrue(e.getMessage().contains("文件不存在"));
        }
    }

    /**
     * 测试 DefaultResourceLoader：使用 classpath: 前缀
     */
    @Test
    public void testDefaultResourceLoaderWithClasspathPrefix() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:test.properties");

        InputStream inputStream = resource.getInputStream();
        assertNotNull("资源输入流不应为 null", inputStream);

        String content = readContent(inputStream);
        assertTrue("应加载到正确的资源", content.contains("Mini Spring"));
        inputStream.close();
    }

    /**
     * 测试 DefaultResourceLoader：不使用前缀（默认 classpath）
     */
    @Test
    public void testDefaultResourceLoaderWithoutPrefix() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("test.properties");

        InputStream inputStream = resource.getInputStream();
        assertNotNull("资源输入流不应为 null", inputStream);

        String content = readContent(inputStream);
        assertTrue("应加载到正确的资源", content.contains("Mini Spring"));
        inputStream.close();
    }

    /**
     * 测试 DefaultResourceLoader：使用 file: 前缀
     */
    @Test
    public void testDefaultResourceLoaderWithFilePrefix() throws IOException {
        // 创建临时文件
        java.io.File tempFile = java.io.File.createTempFile("test-", ".txt");
        tempFile.deleteOnExit();

        java.io.FileWriter writer = new java.io.FileWriter(tempFile);
        writer.write("File URL Test");
        writer.close();

        // 使用 file: 前缀加载
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("file:" + tempFile.getAbsolutePath());

        InputStream inputStream = resource.getInputStream();
        String content = readContent(inputStream);
        assertTrue("应加载到正确的文件", content.contains("File URL Test"));
        inputStream.close();
    }

    /**
     * 辅助方法：读取输入流内容
     */
    private String readContent(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        return content.toString();
    }

}
