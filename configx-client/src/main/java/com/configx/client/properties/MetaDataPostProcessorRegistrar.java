package com.configx.client.properties;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link ImportBeanDefinitionRegistrar} for {@code @Bean} definition meta data post processor.
 *
 * @author Zhirong Zou
 */
public class MetaDataPostProcessorRegistrar implements ImportBeanDefinitionRegistrar {

    public static final String METADATA_BEAN_NAME = ConfigurationBeanFactoryMetaData.class.getName();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(METADATA_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(ConfigurationBeanFactoryMetaData.class);
            registry.registerBeanDefinition(METADATA_BEAN_NAME, meta.getBeanDefinition());
        }
    }

}
