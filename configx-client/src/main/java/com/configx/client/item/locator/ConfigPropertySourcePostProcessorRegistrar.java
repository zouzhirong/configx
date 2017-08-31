package com.configx.client.item.locator;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link ImportBeanDefinitionRegistrar} for property source post processor.
 *
 * @author Zhirong Zou
 */
public class ConfigPropertySourcePostProcessorRegistrar implements ImportBeanDefinitionRegistrar {

    public static final String CONFIG_PROPERTY_SOURCE_CONFIGURER_BEAN_NAME = ConfigPropertySourceConfigurer.class.getName();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(CONFIG_PROPERTY_SOURCE_CONFIGURER_BEAN_NAME)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(ConfigPropertySourceConfigurer.class);
            registry.registerBeanDefinition(CONFIG_PROPERTY_SOURCE_CONFIGURER_BEAN_NAME, bean.getBeanDefinition());
        }
    }

}
