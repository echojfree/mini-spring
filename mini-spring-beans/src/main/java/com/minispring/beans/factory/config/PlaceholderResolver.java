package com.minispring.beans.factory.config;

import java.util.Properties;

/**
 * 占位符解析器
 * <p>
 * 解析字符串中的占位符 ${key} 和 ${key:defaultValue}
 * <p>
 * 实现原理：
 * 1. 查找 ${ 和 } 标记
 * 2. 提取占位符中的 key 和 defaultValue
 * 3. 从 Properties 中查找 key 对应的值
 * 4. 如果找不到且有默认值，使用默认值
 * 5. 递归解析嵌套占位符
 * <p>
 * 面试要点：
 * 1. 占位符解析算法
 * 2. 递归处理嵌套占位符
 * 3. 默认值提取逻辑
 *
 * @author mini-spring
 */
public class PlaceholderResolver {

    /**
     * 占位符前缀
     */
    private static final String PLACEHOLDER_PREFIX = "${";

    /**
     * 占位符后缀
     */
    private static final String PLACEHOLDER_SUFFIX = "}";

    /**
     * 默认值分隔符
     */
    private static final String VALUE_SEPARATOR = ":";

    /**
     * 配置属性
     */
    private final Properties properties;

    public PlaceholderResolver(Properties properties) {
        this.properties = properties;
    }

    /**
     * 解析字符串中的占位符
     * <p>
     * 支持格式：
     * - ${key}: 必需属性
     * - ${key:defaultValue}: 可选属性，带默认值
     * - 普通字符串：直接返回
     *
     * @param value 待解析的字符串
     * @return 解析后的字符串
     */
    public String resolvePlaceholder(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // 如果不包含占位符，直接返回
        if (!value.contains(PLACEHOLDER_PREFIX)) {
            return value;
        }

        return parsePlaceholder(value);
    }

    /**
     * 解析占位符
     *
     * @param value 包含占位符的字符串
     * @return 解析后的字符串
     */
    private String parsePlaceholder(String value) {
        StringBuilder result = new StringBuilder();
        int startIndex = 0;

        while (startIndex < value.length()) {
            // 查找占位符开始位置
            int prefixIndex = value.indexOf(PLACEHOLDER_PREFIX, startIndex);

            // 没有找到占位符，添加剩余字符串
            if (prefixIndex == -1) {
                result.append(value.substring(startIndex));
                break;
            }

            // 添加占位符前的字符串
            result.append(value.substring(startIndex, prefixIndex));

            // 查找占位符结束位置
            int suffixIndex = value.indexOf(PLACEHOLDER_SUFFIX, prefixIndex + PLACEHOLDER_PREFIX.length());

            // 没有找到结束标记，说明格式错误
            if (suffixIndex == -1) {
                throw new IllegalArgumentException("占位符格式错误，缺少结束符号 }: " + value);
            }

            // 提取占位符内容（key 或 key:defaultValue）
            String placeholder = value.substring(prefixIndex + PLACEHOLDER_PREFIX.length(), suffixIndex);

            // 解析占位符并替换
            String resolvedValue = resolveKey(placeholder);
            result.append(resolvedValue);

            // 移动起始位置
            startIndex = suffixIndex + PLACEHOLDER_SUFFIX.length();
        }

        return result.toString();
    }

    /**
     * 解析占位符 key，支持默认值
     * <p>
     * 格式：
     * - key: 从配置中读取，找不到抛异常
     * - key:defaultValue: 从配置中读取，找不到使用默认值
     *
     * @param placeholder 占位符内容
     * @return 解析后的值
     */
    private String resolveKey(String placeholder) {
        // 分离 key 和 defaultValue
        String key;
        String defaultValue = null;

        int separatorIndex = placeholder.indexOf(VALUE_SEPARATOR);
        if (separatorIndex != -1) {
            // 存在默认值
            key = placeholder.substring(0, separatorIndex).trim();
            defaultValue = placeholder.substring(separatorIndex + 1).trim();
        } else {
            // 没有默认值
            key = placeholder.trim();
        }

        // 从配置中获取值
        String value = properties.getProperty(key);

        // 如果找到值，返回
        if (value != null) {
            return value;
        }

        // 如果没有找到值但有默认值，返回默认值
        if (defaultValue != null) {
            return defaultValue;
        }

        // 没有找到值且没有默认值，抛出异常
        throw new IllegalArgumentException("找不到占位符对应的属性: ${" + key + "}");
    }
}
