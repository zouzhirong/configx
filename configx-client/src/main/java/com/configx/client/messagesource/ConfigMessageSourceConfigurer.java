/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.messagesource;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * 基于配置服务的MessageSource
 *
 * @author Zhirong Zou
 */

public class ConfigMessageSourceConfigurer implements
        BeanFactoryAware,
        EnvironmentAware,
        ApplicationListener<ContextRefreshedEvent> {

    private ConfigurableListableBeanFactory beanFactory;

    private ConfigurableEnvironment environment;

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // MessageSource bean name
        String messageSourceBeanName = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME;

        // 配置服务的MessageSource
        HierarchicalMessageSource configMessageSource = new ConfigMessageSource(event.getApplicationContext());

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
}
