package com.configx.client.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * configx namespace Handler
 * <p>
 * Created by zouzhirong on 2017/9/17.
 */
public class ConfigxNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());

        registerBeanDefinitionDecorator("bean", new ConfigBeanBeanDefinitionDecorator());

        registerBeanDefinitionDecorator("version-refresh", new VersionRefreshBeanDefinitionDecorator());

        registerBeanDefinitionParser("message-source", new ConfigMessageSourceBeanDefinitionParser());

        registerBeanDefinitionParser("jmx", new ConfigJmxBeanDefinitionParser());
    }
}
