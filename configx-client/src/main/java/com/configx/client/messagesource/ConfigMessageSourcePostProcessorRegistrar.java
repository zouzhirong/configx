package com.configx.client.messagesource;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link ImportBeanDefinitionRegistrar} for message source post processor.
 *
 * @author Zhirong Zou
 */
public class ConfigMessageSourcePostProcessorRegistrar implements ImportBeanDefinitionRegistrar {

    private static final String CONFIG_MESSAGE_SOURCE_CONFIGURER_BEAN_NAME = ConfigMessageSourceConfigurer.class.getName();

    private static final String MESSAGE_SOURCE_LISTENER_BEAN_NAME = MessageSourceConfigItemListener.class.getName();

    private static final String CONFIG_MESSAGE_MANAGER_BEAN_NAME = ConfigMessageManager.class.getName();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(CONFIG_MESSAGE_SOURCE_CONFIGURER_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(ConfigMessageSourceConfigurer.class);
            registry.registerBeanDefinition(CONFIG_MESSAGE_SOURCE_CONFIGURER_BEAN_NAME, meta.getBeanDefinition());
        }

        if (!registry.containsBeanDefinition(MESSAGE_SOURCE_LISTENER_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(MessageSourceConfigItemListener.class);
            registry.registerBeanDefinition(MESSAGE_SOURCE_LISTENER_BEAN_NAME, meta.getBeanDefinition());
        }

        if (!registry.containsBeanDefinition(CONFIG_MESSAGE_MANAGER_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(ConfigMessageManager.class);
            registry.registerBeanDefinition(CONFIG_MESSAGE_MANAGER_BEAN_NAME, meta.getBeanDefinition());
        }
    }

}
