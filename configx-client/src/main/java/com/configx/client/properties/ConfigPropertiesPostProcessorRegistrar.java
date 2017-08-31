package com.configx.client.properties;

import com.configx.client.annotation.ConfigBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link ImportBeanDefinitionRegistrar} for binding application properties to {@link ConfigBean} beans.
 *
 * @author Dave Syer
 * @author Phillip Webb
 */
public class ConfigPropertiesPostProcessorRegistrar implements ImportBeanDefinitionRegistrar {

    public static final String CONFIG_BEAN_POST_PROCESSOR_BEAN_NAME = ConfigBeanPostProcessor.class.getName();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(CONFIG_BEAN_POST_PROCESSOR_BEAN_NAME)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(ConfigBeanPostProcessor.class);
            registry.registerBeanDefinition(CONFIG_BEAN_POST_PROCESSOR_BEAN_NAME, bean.getBeanDefinition());
        }
    }

}
