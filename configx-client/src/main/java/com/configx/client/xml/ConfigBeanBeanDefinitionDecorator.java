package com.configx.client.xml;

import com.configx.client.dependency.DependencyConfigUtils;
import com.configx.client.properties.ConfigBeanConfigUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 处理configx:bean标签
 * <p>
 * Created by zouzhirong on 2017/9/17.
 */
public class ConfigBeanBeanDefinitionDecorator implements BeanDefinitionDecorator {

    private static final String PROPERTY_NAME = "propertyName";
    private static final String CONVERTER = "converter";

    @Override
    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext) {
        String propertyName = null;
        String converterClassName = null;
        if (node instanceof Element) {
            Element ele = (Element) node;
            if (ele.hasAttribute(PROPERTY_NAME)) {
                propertyName = ele.getAttribute(PROPERTY_NAME);
            }
            if (ele.hasAttribute(CONVERTER)) {
                converterClassName = ele.getAttribute(CONVERTER);
            }
        }

        // 为配置bean设置factory method
        Class<?> converterType = null;
        if (StringUtils.isNotEmpty(converterClassName)) {
            converterType = ClassUtils.resolveClassName(converterClassName, parserContext.getReaderContext().getBeanClassLoader());
        }
        ConfigBeanConfigUtils.setConfigBeanFactoryMethod(parserContext.getRegistry(),
                definition.getBeanName(), definition.getBeanDefinition(), propertyName, converterType);

        // 注册属性依赖
        DependencyConfigUtils.registerDependency(parserContext.getRegistry(), definition.getBeanName(), new String[]{propertyName});

        return definition;
    }
}
