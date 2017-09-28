/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.properties;

import com.configx.client.annotation.ConfigBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.PropertySources;
import org.springframework.util.ClassUtils;

/**
 * {@link BeanPostProcessor}用于处理{@link ConfigBean}注解的Bean，直接使用{@link PropertySources}中的指定属性来创建Bean。
 *
 * @author Zhirong Zou
 */
public class ConfigBeanPostProcessor implements
        BeanDefinitionRegistryPostProcessor,
        BeanFactoryAware {

    private ConfigurableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("Not running in a ConfigurableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registerFactoryMethodForConfigBeans(registry);
    }

    /**
     * 为@ConfigBean的bean注册它的Factory Method，通过静态Factory Method来创建@ConfigBean实例
     *
     * @param registry
     */
    private void registerFactoryMethodForConfigBeans(BeanDefinitionRegistry registry) {
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            registerFactoryMethodForConfigBean(registry, beanName, beanDefinition);
        }
    }

    /**
     * 为@ConfigBean的bean注册它的Factory Method，通过静态Factory Method来创建@ConfigBean实例
     *
     * @param registry
     * @param beanName
     * @param beanDefinition
     */
    private void registerFactoryMethodForConfigBean(BeanDefinitionRegistry registry, String beanName, BeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        if (beanClassName == null) { // 通过注解@Bean声明的bean，beanClassName=null
            return;
        }

        Class<?> beanClass = ClassUtils.resolveClassName(beanClassName, beanFactory.getBeanClassLoader());
        ConfigBean config = AnnotationUtils.findAnnotation(beanClass, ConfigBean.class);
        if (config == null) {
            return;
        }

        // 为配置bean设置factory method
        String propertyName = config.value();
        ConfigBeanConfigUtils.setConfigBeanFactoryMethod(registry,
                beanName, beanDefinition, propertyName, config.converter());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
