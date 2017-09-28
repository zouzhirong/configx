package com.configx.client.config;

import com.configx.client.dependency.RefreshBeanDependencyFactory;
import com.configx.client.properties.ConfigBeanPostProcessor;
import com.configx.client.scope.refresh.VersionRefreshScope;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * 注册Config核心相关的beans
 * <p>
 * Created by zouzhirong on 2017/9/18.
 */
public class ConfigBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    /**
     * PropertySourcesPlaceholderConfigurer bean name
     */
    public static final String PROPERTY_SOURCES_PLACEHOLDER_CONFIGURER_BEAN_NAME
            = PropertySourcesPlaceholderConfigurer.class.getName();
    /**
     * Version-refresh scope bean name
     */
    public static final String VERSION_REFRESH_SCOPE_BEAN_NAME = VersionRefreshScope.class.getName();

    /**
     * {@code @Bean} definition meta data bean name
     */
    public static final String METADATA_BEAN_NAME = ConfigurationBeanFactoryMetaData.class.getName();

    /**
     * Refresh Bean Dependency Factory bean name
     */
    public static final String REFRESH_DEPENDENCY_FACTORY_BEAN_NAME = RefreshBeanDependencyFactory.class.getName();

    /**
     * Config property source Configurer bean name
     */
    public static final String CONFIG_CONTEXT_BEAN_NAME = ConfigContext.class.getName();

    /**
     * Config Bean PostProcessor bean name
     */
    public static final String CONFIG_BEAN_POST_PROCESSOR_BEAN_NAME = ConfigBeanPostProcessor.class.getName();

    /**
     * 注册beans
     *
     * @param registry
     */
    private void registerBeanDefinitions(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(PROPERTY_SOURCES_PLACEHOLDER_CONFIGURER_BEAN_NAME)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(PropertySourcesPlaceholderConfigurer.class);
            registry.registerBeanDefinition(PROPERTY_SOURCES_PLACEHOLDER_CONFIGURER_BEAN_NAME, bean.getBeanDefinition());
        }

        if (!registry.containsBeanDefinition(VERSION_REFRESH_SCOPE_BEAN_NAME)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(VersionRefreshScope.class);
            registry.registerBeanDefinition(VERSION_REFRESH_SCOPE_BEAN_NAME, bean.getBeanDefinition());
        }

        if (!registry.containsBeanDefinition(METADATA_BEAN_NAME)) {
            BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(ConfigurationBeanFactoryMetaData.class);
            registry.registerBeanDefinition(METADATA_BEAN_NAME, meta.getBeanDefinition());
        }

        if (!registry.containsBeanDefinition(REFRESH_DEPENDENCY_FACTORY_BEAN_NAME)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(RefreshBeanDependencyFactory.class);
            bean.addPropertyReference("beanMetaDataStore", METADATA_BEAN_NAME);
            registry.registerBeanDefinition(REFRESH_DEPENDENCY_FACTORY_BEAN_NAME, bean.getBeanDefinition());
        }

        if (!registry.containsBeanDefinition(CONFIG_CONTEXT_BEAN_NAME)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(ConfigContext.class);
            bean.addPropertyReference("scope", VERSION_REFRESH_SCOPE_BEAN_NAME);
            registry.registerBeanDefinition(CONFIG_CONTEXT_BEAN_NAME, bean.getBeanDefinition());
        }

        if (!registry.containsBeanDefinition(CONFIG_BEAN_POST_PROCESSOR_BEAN_NAME)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(ConfigBeanPostProcessor.class);
            registry.registerBeanDefinition(CONFIG_BEAN_POST_PROCESSOR_BEAN_NAME, bean.getBeanDefinition());
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registerBeanDefinitions(registry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
