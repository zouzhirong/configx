package com.configx.client.properties;

import com.configx.client.annotation.EnableConfigService;
import com.configx.client.item.converter.CustomConverterConfigurer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class EnableConfigServiceImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(
                EnableConfigService.class.getName(), false);
        Object[] type = attributes == null ? null
                : (Object[]) attributes.getFirst("converters");
        if (type == null || type.length == 0) {
            return new String[]{};
        }
        return new String[]{ConfigConverterBeanRegistrar.class.getName()};
    }

    /**
     * {@link ImportBeanDefinitionRegistrar} for {@link com.configx.client.item.converter.ConfigConverter} register.
     */
    public static class ConfigConverterBeanRegistrar
            implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata metadata,
                                            BeanDefinitionRegistry registry) {
            MultiValueMap<String, Object> attributes = metadata
                    .getAllAnnotationAttributes(
                            EnableConfigService.class.getName(), false);
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
            String name = CustomConverterConfigurer.class.getName() + "." + EnableConfigService.class.getSimpleName();
            // converters
            List<GenericConverter> genericConverters = instantiateConverters(types);

            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                    .genericBeanDefinition(CustomConverterConfigurer.class);
            builder.addPropertyValue("genericConverters", genericConverters);
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

            registry.registerBeanDefinition(name, beanDefinition);
        }

        private List<GenericConverter> instantiateConverters(List<Class<?>> types) {
            List<GenericConverter> genericConverters = new ArrayList<>();
            for (Class<?> type : types) {
                GenericConverter genericConverter = (GenericConverter) BeanUtils.instantiate(type);
                genericConverters.add(genericConverter);
            }
            return genericConverters;
        }

    }
}