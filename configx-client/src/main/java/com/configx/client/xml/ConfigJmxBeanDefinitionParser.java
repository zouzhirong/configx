package com.configx.client.xml;

import com.configx.client.jmx.ConfigJmxBeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * 处理configx:jmx标签
 * <p>
 * Created by zouzhirong on 2017/9/17.
 */
public class ConfigJmxBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    protected Class getBeanClass(Element element) {
        return ConfigJmxBeanDefinitionRegistryPostProcessor.class;
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        return ConfigJmxBeanDefinitionRegistryPostProcessor.class.getName();
    }

    protected void doParse(Element element, BeanDefinitionBuilder bean) {

    }

}
