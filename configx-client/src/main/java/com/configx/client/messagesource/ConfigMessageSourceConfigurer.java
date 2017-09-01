/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.messagesource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.*;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * 基于配置服务的MessageSource
 *
 * @author Zhirong Zou
 */

public class ConfigMessageSourceConfigurer implements BeanDefinitionRegistryPostProcessor,
        BeanFactoryAware,
        ApplicationContextAware,
        EnvironmentAware {

    private ConfigurableListableBeanFactory beanFactory;

    private ApplicationContext applicationContext;

    private ConfigurableEnvironment environment;


    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // MessageSource bean name
        String messageSourceBeanName = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME;

        // 配置服务的MessageSource
        HierarchicalMessageSource configMessageSource = new ConfigMessageSource(applicationContext);

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
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
