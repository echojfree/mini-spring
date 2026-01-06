package com.minispring.web.servlet.mvc.method.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PathMatcher - 路径匹配器
 * <p>
 * 支持路径变量的 URL 匹配
 * <p>
 * 面试要点:
 * 1. URL 模板匹配
 *    - /user/{id} 匹配 /user/123
 *    - /order/{orderId}/item/{itemId}
 * <p>
 * 2. 正则表达式
 *    - 将 {id} 转换为正则表达式
 *    - 提取路径变量值
 * <p>
 * 3. 变量提取
 *    - 记录变量名称和位置
 *    - 从实际 URL 中提取值
 *
 * @author mini-spring
 */
public class PathMatcher {

    /**
     * 路径变量模式: {varName}
     */
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([^/]+?)\\}");

    /**
     * URL 模板
     */
    private final String pattern;

    /**
     * 编译后的正则表达式
     */
    private final Pattern compiledPattern;

    /**
     * 路径变量名称列表
     */
    private final List<String> variableNames;

    public PathMatcher(String pattern) {
        this.pattern = pattern;
        this.variableNames = new ArrayList<>();
        this.compiledPattern = compilePattern(pattern);
    }

    /**
     * 编译 URL 模板为正则表达式
     */
    private Pattern compilePattern(String pattern) {
        Matcher matcher = VARIABLE_PATTERN.matcher(pattern);
        StringBuffer regex = new StringBuffer();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            variableNames.add(variableName);
            // 将 {id} 替换为 ([^/]+) 正则表达式
            matcher.appendReplacement(regex, "([^/]+)");
        }
        matcher.appendTail(regex);

        // 添加开始和结束标记
        return Pattern.compile("^" + regex.toString() + "$");
    }

    /**
     * 判断 URL 是否匹配
     */
    public boolean matches(String url) {
        return compiledPattern.matcher(url).matches();
    }

    /**
     * 提取路径变量
     */
    public Map<String, String> extractVariables(String url) {
        Map<String, String> variables = new HashMap<>();
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.matches()) {
            for (int i = 0; i < variableNames.size(); i++) {
                String name = variableNames.get(i);
                String value = matcher.group(i + 1);
                variables.put(name, value);
            }
        }

        return variables;
    }

    /**
     * 判断是否包含路径变量
     */
    public boolean hasVariables() {
        return !variableNames.isEmpty();
    }

    public String getPattern() {
        return pattern;
    }

    public List<String> getVariableNames() {
        return variableNames;
    }

}
