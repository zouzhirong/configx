package com.configx.client.scope.refresh;

import com.configx.client.properties.MetaDataPostProcessorRegistrar;
import com.configx.client.scope.refresh.dependency.RefreshableBeanDependencyManagement;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link ImportBeanDefinitionRegistrar} for bean refresh post processor.
 *
 * @author Zhirong Zou
 */
public class ConfigRefreshPostProcessorRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * Version-refresh scope bean name
     */
    public static final String VERSION_REFRESH_SCOPE_BEAN_NAME = VersionRefreshScope.class.getName();

    /**
     * Refreshable bean dependency management bean name
     */
    public static final String REFRESH_DEPENDENCY_MANAGEMENT_BEAN_NAME = RefreshableBeanDependencyManagement.class.getName();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        if (!registry.containsBeanDefinition(VERSION_REFRESH_SCOPE_BEAN_NAME)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(VersionRefreshScope.class);
            registry.registerBeanDefinition(VERSION_REFRESH_SCOPE_BEAN_NAME, bean.getBeanDefinition());
        }

        if (!registry.containsBeanDefinition(REFRESH_DEPENDENCY_MANAGEMENT_BEAN_NAME)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(RefreshableBeanDependencyManagement.class);

            // org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessorRegistrar.registerBeanDefinitions()
            // org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration.configurationPropertiesBeans()
            bean.addPropertyReference("beanMetaDataStore", MetaDataPostProcessorRegistrar.METADATA_BEAN_NAME);

            registry.registerBeanDefinition(REFRESH_DEPENDENCY_MANAGEMENT_BEAN_NAME, bean.getBeanDefinition());
        }

    }

}
