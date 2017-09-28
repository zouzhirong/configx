package com.configx.client.config;

import com.configx.client.annotation.EnableConfigService;
import com.configx.client.converter.ConfigBeanConverter;
import com.configx.client.converter.ConfigBeanConverterConfigurer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link com.configx.client.annotation.EnableConfigService}注解相关的导入
 */
public class EnableConfigServiceImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        return new String[]{
                ConfigImportBeanDefinitionRegistrar.class.getName(),
                ConfigConverterBeanRegistrar.class.getName()
        };
    }

    /**
     * 注册{@link ConfigBeanDefinitionRegistryPostProcessor} Bean
     */
    public static class ConfigImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(ConfigBeanDefinitionRegistryPostProcessor.class);
            registry.registerBeanDefinition(ConfigBeanDefinitionRegistryPostProcessor.class.getName(), bean.getBeanDefinition());
        }
    }

    /**
     * 注册{@link ConfigBeanConverter} Beans
     */
    public static class ConfigConverterBeanRegistrar
            implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata,
                                            BeanDefinitionRegistry registry) {
            MultiValueMap<String, Object> attributes = metadata
                    .getAllAnnotationAttributes(
                            EnableConfigService.class.getName(), false);
            Object[] type = attributes == null ? null
                    : (Object[]) attributes.getFirst("converters");
            if (type == null || type.length == 0) {
                return;
            }

            List<Class<?>> types = collectClasses(attributes.get("converters"));
//            for (Class<?> type : types) {
//                String name = type.getName();
//                if (!registry.containsBeanDefinition(name)) {
//                    registerBeanDefinition(registry, type, name);
//                }
//            }

            registerConverters(registry, types);
        }

        private List<Class<?>> collectClasses(List<Object> list) {
            ArrayList<Class<?>> result = new ArrayList<Class<?>>();
            for (Object object : list) {
                for (Object value : (Object[]) object) {
                    if (value instanceof Class && value != void.class) {
                        result.add((Class<?>) value);
                    }
                }
            }
            return result;
        }

        private void registerBeanDefinition(BeanDefinitionRegistry registry,
                                            Class<?> type, String name) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                    .genericBeanDefinition(type);
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

            registry.registerBeanDefinition(name, beanDefinition);


        }

        private void registerConverters(BeanDefinitionRegistry registry, List<Class<?>> types) {

            // Register ConfigConverter by CustomConverterConfigurer
            String name = ConfigBeanConverterConfigurer.class.getName() + "." + EnableConfigService.class.getSimpleName();
            // converters
            List<ConfigBeanConverter> converters = instantiateConverters(types);

            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                    .genericBeanDefinition(ConfigBeanConverterConfigurer.class);
            builder.addPropertyValue("converters", converters);
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

            registry.registerBeanDefinition(name, beanDefinition);
        }

        private List<ConfigBeanConverter> instantiateConverters(List<Class<?>> types) {
            List<ConfigBeanConverter> converters = new ArrayList<>();
            for (Class<?> type : types) {
                ConfigBeanConverter converter = (ConfigBeanConverter) BeanUtils.instantiate(type);
                converters.add(converter);
            }
            return converters;
        }

    }

}