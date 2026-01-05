package com.minispring.beans.factory.xml;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.minispring.beans.PropertyValue;
import com.minispring.beans.PropertyValues;
import com.minispring.beans.exception.BeansException;
import com.minispring.beans.factory.config.BeanDefinition;
import com.minispring.beans.factory.config.BeanReference;
import com.minispring.beans.factory.support.AbstractBeanDefinitionReader;
import com.minispring.beans.factory.support.BeanDefinitionRegistry;
import com.minispring.core.io.Resource;
import com.minispring.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;

/**
 * XML 配置文件的 BeanDefinition 读取器
 * <p>
 * 负责解析 XML 配置文件，将配置转换为 BeanDefinition
 * <p>
 * XML 格式示例：
 * <pre>
 * &lt;beans&gt;
 *     &lt;bean id="userService" class="com.example.UserService"&gt;
 *         &lt;property name="userName" value="张三"/&gt;
 *         &lt;property name="userDao" ref="userDao"/&gt;
 *     &lt;/bean&gt;
 *     &lt;bean id="userDao" class="com.example.UserDao"/&gt;
 * &lt;/beans&gt;
 * </pre>
 * <p>
 * 核心功能：
 * 1. 解析 &lt;bean&gt; 标签：提取 id、class 属性
 * 2. 解析 &lt;property&gt; 标签：处理 value 和 ref 属性
 * 3. 支持属性依赖注入：value（基本类型）、ref（Bean 引用）
 * <p>
 * 面试考点：
 * 1. Spring 如何解析 XML 配置？
 *    - 使用 DOM 解析 XML 文件
 *    - 遍历 bean 标签创建 BeanDefinition
 *    - 解析 property 标签填充 PropertyValues
 * 2. value 和 ref 的区别？
 *    - value：直接值，用于基本类型和字符串
 *    - ref：Bean 引用，用于依赖注入其他 Bean
 * 3. BeanReference 的作用？
 *    - 表示对另一个 Bean 的引用
 *    - 在 Bean 创建时解析为实际的 Bean 对象
 *
 * @author mini-spring
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    /**
     * 构造函数
     *
     * @param registry BeanDefinition 注册器
     */
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    /**
     * 构造函数
     *
     * @param registry       BeanDefinition 注册器
     * @param resourceLoader 资源加载器
     */
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }

    /**
     * 从资源中加载 BeanDefinition
     * <p>
     * 完整流程：
     * 1. 从 Resource 获取 InputStream
     * 2. 使用 DOM 解析 XML
     * 3. 遍历 bean 标签
     * 4. 为每个 bean 创建 BeanDefinition
     * 5. 解析 property 标签填充属性
     * 6. 注册 BeanDefinition
     *
     * @param resource 资源
     * @throws Exception 加载失败时抛出异常
     */
    @Override
    public void loadBeanDefinitions(Resource resource) throws Exception {
        try (InputStream inputStream = resource.getInputStream()) {
            doLoadBeanDefinitions(inputStream);
        }
    }

    /**
     * 实际执行 BeanDefinition 加载的方法
     * <p>
     * 使用 hutool 的 XmlUtil 解析 XML
     *
     * @param inputStream 输入流
     * @throws ClassNotFoundException 类不存在时抛出
     */
    protected void doLoadBeanDefinitions(InputStream inputStream) throws ClassNotFoundException {
        // 步骤1：解析 XML 文档
        Document doc = XmlUtil.readXML(inputStream);
        Element root = doc.getDocumentElement();

        // 步骤2：获取所有 bean 标签
        NodeList childNodes = root.getChildNodes();

        // 步骤3：遍历 bean 标签
        for (int i = 0; i < childNodes.getLength(); i++) {
            // 判断元素类型
            if (!(childNodes.item(i) instanceof Element)) {
                continue;
            }

            // 判断是否为 bean 标签
            if (!"bean".equals(childNodes.item(i).getNodeName())) {
                continue;
            }

            // 步骤4：解析 bean 标签
            Element bean = (Element) childNodes.item(i);
            String id = bean.getAttribute("id");
            String name = bean.getAttribute("name");
            String className = bean.getAttribute("class");

            // 获取 Class，用于实例化
            Class<?> clazz = Class.forName(className);

            // 优先级：id > name
            String beanName = StrUtil.isNotEmpty(id) ? id : name;
            if (StrUtil.isEmpty(beanName)) {
                // 如果都为空，使用类名首字母小写
                beanName = StrUtil.lowerFirst(clazz.getSimpleName());
            }

            // 步骤5：创建 BeanDefinition
            BeanDefinition beanDefinition = new BeanDefinition(clazz);

            // 步骤6：解析 property 标签
            NodeList propertyNodes = bean.getChildNodes();
            for (int j = 0; j < propertyNodes.getLength(); j++) {
                if (!(propertyNodes.item(j) instanceof Element)) {
                    continue;
                }

                if (!"property".equals(propertyNodes.item(j).getNodeName())) {
                    continue;
                }

                // 解析 property 标签
                Element property = (Element) propertyNodes.item(j);
                String attrName = property.getAttribute("name");
                String attrValue = property.getAttribute("value");
                String attrRef = property.getAttribute("ref");

                // 获取属性值：ref 优先
                Object value;
                if (StrUtil.isNotEmpty(attrRef)) {
                    // ref：Bean 引用
                    value = new BeanReference(attrRef);
                } else {
                    // value：直接值
                    value = attrValue;
                }

                // 创建 PropertyValue 并添加到 BeanDefinition
                PropertyValue propertyValue = new PropertyValue(attrName, value);
                beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
            }

            // 步骤7：检查 BeanDefinition 是否已存在
            if (getRegistry().containsBeanDefinition(beanName)) {
                throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
            }

            // 步骤8：注册 BeanDefinition
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }
    }

}
