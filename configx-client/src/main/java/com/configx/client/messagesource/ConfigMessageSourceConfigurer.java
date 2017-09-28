/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.messagesource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * 基于配置服务的MessageSource
 *
 * @author Zhirong Zou
 */

public class ConfigMessageSourceConfigurer implements BeanDefinitionRegistryPostProcessor,
        BeanFactoryAware {

    public static final String CONFIG_MESSAGE_SOURCE_BEAN_NAME = ConfigMessageSource.class.getName();

    public static final String MESSAGE_SOURCE_POST_PROCESSOR_BEAN_NAME = MessageSourceConfigItemPostProcessor.class.getName();

    private ConfigurableListableBeanFactory beanFactory;

    private boolean fallbackToSystemLocale = false;
    private String[] basenames = null;

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }

    public void setBasenames(String[] basenames) {
        this.basenames = basenames;
    }

    /**
     * 注册beans
     *
     * @param registry
     */
    private void registerBeanDefinitions(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(CONFIG_MESSAGE_SOURCE_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(ConfigMessageSource.class);
            meta.addPropertyValue("fallbackToSystemLocale", fallbackToSystemLocale);
            meta.addPropertyValue("basenames", basenames);
            registry.registerBeanDefinition(CONFIG_MESSAGE_SOURCE_BEAN_NAME, meta.getBeanDefinition());
        }

        if (!registry.containsBeanDefinition(MESSAGE_SOURCE_POST_PROCESSOR_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(MessageSourceConfigItemPostProcessor.class);
            meta.addPropertyReference("configMessageSource", CONFIG_MESSAGE_SOURCE_BEAN_NAME);
            registry.registerBeanDefinition(MESSAGE_SOURCE_POST_PROCESSOR_BEAN_NAME, meta.getBeanDefinition());
        }
    }

    /**
     * 配置基于配置的MessageSource
     */
    private void configureMessageSource() {
        // MessageSource bean name
        String messageSourceBeanName = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME;

        // Config MessageSource bean name
        String configMessageSourceBeanName = CONFIG_MESSAGE_SOURCE_BEAN_NAME;

        // 基于配置服务的MessageSource
        ConfigMessageSource configMessageSource = beanFactory.getBean(configMessageSourceBeanName, ConfigMessageSource.class);

        // 如果已经存在MessageSource bean,则将ConfigMessageSource设置为messageSource的parent,将messageSource原来的parent设置为ConfigMessageSource的parent
        if (beanFactory.containsLocalBean(messageSourceBeanName)) {
            MessageSource messageSource = beanFactory.getBean(messageSourceBeanName, MessageSource.class);
            if (messageSource instanceof HierarchicalMessageSource) {
                HierarchicalMessageSource hms = (HierarchicalMessageSource) messageSource;
                MessageSource pms = hms.getParentMessageSource();

                hms.setParentMessageSource(configMessageSource);
                configMessageSource.setParentMessageSource(pms);
            }
        } else {
            beanFactory.registerSingleton(messageSourceBeanName, configMessageSource);
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registerBeanDefinitions(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        configureMessageSource();
    }
}
