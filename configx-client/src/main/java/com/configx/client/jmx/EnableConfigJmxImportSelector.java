package com.configx.client.jmx;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link com.configx.client.annotation.EnableConfigJmx}注解相关的导入
 */
public class EnableConfigJmxImportSelector implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(ConfigJmxBeanDefinitionRegistryPostProcessor.class);
        registry.registerBeanDefinition(ConfigJmxBeanDefinitionRegistryPostProcessor.class.getName(), bean.getBeanDefinition());
    }

}