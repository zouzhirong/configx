package com.configx.client.xml;

import com.configx.client.messagesource.ConfigMessageSourceConfigurer;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * 处理configx:message-source标签
 * <p>
 * Created by zouzhirong on 2017/9/17.
 */
public class ConfigMessageSourceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private static final String FALLBACK_TO_SYSTEM_LOCALE = "fallbackToSystemLocale";
    private static final String BASENAMES = "basenames";

    protected Class getBeanClass(Element element) {
        return ConfigMessageSourceConfigurer.class;
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        return ConfigMessageSourceConfigurer.class.getName();
    }

    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        boolean fallbackToSystemLocale = false;
        String[] basenames = null;

        if (element.hasAttribute(FALLBACK_TO_SYSTEM_LOCALE)) {
            fallbackToSystemLocale = Boolean.valueOf(element.getAttribute(FALLBACK_TO_SYSTEM_LOCALE));
        }
        if (element.hasAttribute(BASENAMES)) {
            basenames = StringUtils.tokenizeToStringArray(element.getAttribute(BASENAMES), ",");
        }

        bean.addPropertyValue("fallbackToSystemLocale", fallbackToSystemLocale);
        bean.addPropertyValue("basenames", basenames);
    }
}
