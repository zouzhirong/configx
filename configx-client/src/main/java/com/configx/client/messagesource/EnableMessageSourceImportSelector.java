package com.configx.client.messagesource;

import com.configx.client.annotation.EnableMessageSource;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

/**
 * {@link com.configx.client.annotation.EnableMessageSource}注解相关的导入
 */
public class EnableMessageSourceImportSelector implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(EnableMessageSource.class.getName(), false);
        boolean fallbackToSystemLocale = (boolean) (attributes != null ? attributes.getFirst("fallbackToSystemLocale") : false);
        String[] basenames = (String[]) (attributes != null ? attributes.getFirst("basenames") : null);

        BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(ConfigMessageSourceConfigurer.class);
        bean.addPropertyValue("fallbackToSystemLocale", fallbackToSystemLocale);
        if (basenames != null && basenames.length > 0) {
            bean.addPropertyValue("basenames", basenames);
        }
        registry.registerBeanDefinition(ConfigMessageSourceConfigurer.class.getName(), bean.getBeanDefinition());
    }

}