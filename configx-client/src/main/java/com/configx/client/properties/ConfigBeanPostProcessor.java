/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.properties;

import com.configx.client.annotation.ConfigBean;
import com.configx.client.item.converter.ConfigConverter;
import com.configx.client.item.converter.CustomConverterConfigurer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.*;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * {@link BeanPostProcessor}用于处理{@link com.configx.client.annotation.ConfigBean}注解的Bean，直接使用{@link PropertySources}中的指定属性来创建Bean。
 *
 * @author Zhirong Zou
 */
public class ConfigBeanPostProcessor
        implements InstantiationAwareBeanPostProcessor, BeanFactoryAware,
        EnvironmentAware, ApplicationContextAware, InitializingBean, DisposableBean,
        PriorityOrdered {

    private PropertySources propertySources;

    private ConfigurableConversionService conversionService;

    private DefaultConversionService defaultConversionService;

    private BeanFactory beanFactory;

    private Environment environment = new StandardEnvironment();

    private ApplicationContext applicationContext;

    private List<Converter<?, ?>> converters = Collections.emptyList();

    private List<GenericConverter> genericConverters = Collections.emptyList();

    private int order = Ordered.HIGHEST_PRECEDENCE + 1;

    private ConfigurablePropertyResolver propertyResolver;

    /**
     * A list of custom converters (in addition to the defaults) to use when converting
     * properties for binding.
     *
     * @param converters the converters to set
     */
    @Autowired(required = false)
    public void setConverters(List<Converter<?, ?>> converters) {
        this.converters = converters;
    }

    /**
     * A list of custom converters (in addition to the defaults) to use when converting
     * properties for binding.
     *
     * @param converters the converters to set
     */
    @Autowired(required = false)
    public void setGenericConverters(List<GenericConverter> converters) {
        this.genericConverters = converters;
    }

    /**
     * Set the order of the bean.
     *
     * @param order the order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Return the order of the bean.
     *
     * @return the order
     */
    @Override
    public int getOrder() {
        return this.order;
    }

    /**
     * Set the property sources to bind.
     *
     * @param propertySources the property sources
     */
    public void setPropertySources(PropertySources propertySources) {
        this.propertySources = propertySources;
    }

    /**
     * Set the conversion service used to convert property values.
     *
     * @param conversionService the conversion service
     */
    public void setConversionService(ConfigurableConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.propertySources == null) {
            this.propertySources = deducePropertySources();
        }

        if (this.conversionService == null) {
            this.conversionService = getOptionalBean(
                    ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME,
                    ConfigurableConversionService.class);
        }

        // If no explicit conversion service is provided we add one so that (at least)
        // comma-separated arrays of convertibles can be bound automatically
        if (this.conversionService == null) {
            this.conversionService = getDefaultConversionService();
        }

        this.propertyResolver = new PropertySourcesPropertyResolver(this.propertySources);
        this.propertyResolver.setConversionService(this.conversionService);
    }

    @Override
    public void destroy() throws Exception {
    }

    private PropertySources deducePropertySources() {
        PropertySourcesPlaceholderConfigurer configurer = getSinglePropertySourcesPlaceholderConfigurer();
        if (configurer != null) {
            // Flatten the sources into a single list so they can be iterated
            return new FlatPropertySources(configurer.getAppliedPropertySources());
        }
        if (this.environment instanceof ConfigurableEnvironment) {
            MutablePropertySources propertySources = ((ConfigurableEnvironment) this.environment)
                    .getPropertySources();
            return new FlatPropertySources(propertySources);
        }
        // empty, so not very useful, but fulfils the contract
        return new MutablePropertySources();
    }

    private PropertySourcesPlaceholderConfigurer getSinglePropertySourcesPlaceholderConfigurer() {
        // Take care not to cause early instantiation of all FactoryBeans
        if (this.beanFactory instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = (ListableBeanFactory) this.beanFactory;
            Map<String, PropertySourcesPlaceholderConfigurer> beans = listableBeanFactory
                    .getBeansOfType(PropertySourcesPlaceholderConfigurer.class, false,
                            false);
            if (beans.size() == 1) {
                return beans.values().iterator().next();
            }
        }
        return null;
    }

    private <T> T getOptionalBean(String name, Class<T> type) {
        try {
            return this.beanFactory.getBean(name, type);
        } catch (NoSuchBeanDefinitionException ex) {
            return null;
        }
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        ConfigBean config = AnnotationUtils.findAnnotation(beanClass, ConfigBean.class);
        if (config == null) {
            return null;
        }

        // 注册@ConfigBean注解中的Converter
        Class<? extends ConfigConverter> converterType = config.converter();
        if (converterType != null && !converterType.isInterface()) {
            ConfigConverter converter = BeanUtils.instantiate(converterType);
            String value = propertyResolver.getRequiredProperty(config.value());
            return converter.convert(value, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(beanClass));

        } else {
            return propertyResolver.getRequiredProperty(config.value(), beanClass);
        }
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * Initialize default ConversionService
     *
     * @return
     */
    private ConfigurableConversionService getDefaultConversionService() {
        if (this.defaultConversionService == null) {
            DefaultConversionService conversionService = new DefaultConversionService();
            this.applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
            for (Converter<?, ?> converter : this.converters) {
                conversionService.addConverter(converter);
            }
            for (GenericConverter genericConverter : this.genericConverters) {
                conversionService.addConverter(genericConverter);
            }

            // Add Custom Converters registered by CustomConverterConfigurer
            addCustomConverters(conversionService);
            // Add default config converters
            addDefaultConfigConverters(conversionService);

            this.defaultConversionService = conversionService;
        }
        return this.defaultConversionService;
    }

    /**
     * Add converters registered by CustomConverterConfigurer
     *
     * @param converterRegistry
     */
    private void addCustomConverters(ConverterRegistry converterRegistry) {
        // Find all CustomConverterConfigurer in the ApplicationContext, including ancestor contexts.
        Map<String, CustomConverterConfigurer> matchingBeans =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, CustomConverterConfigurer.class, true, false);
        if (!matchingBeans.isEmpty()) {
            for (CustomConverterConfigurer converterConfigurer : matchingBeans.values()) {
                if (converterConfigurer.getConverters() != null) {
                    for (Converter<?, ?> converter : converterConfigurer.getConverters()) {
                        converterRegistry.addConverter(converter);
                    }
                }
                if (converterConfigurer.getGenericConverters() != null) {
                    for (GenericConverter genericConverter : converterConfigurer.getGenericConverters()) {
                        converterRegistry.addConverter(genericConverter);
                    }
                }
            }
        }
    }

    /**
     * Add default config converters
     *
     * @param converterRegistry
     */
    private void addDefaultConfigConverters(ConverterRegistry converterRegistry) {
        // TODO
    }

    /**
     * Convenience class to flatten out a tree of property sources without losing the
     * reference to the backing data (which can therefore be updated in the background).
     */
    private static class FlatPropertySources implements PropertySources {

        private PropertySources propertySources;

        FlatPropertySources(PropertySources propertySources) {
            this.propertySources = propertySources;
        }

        @Override
        public Iterator<PropertySource<?>> iterator() {
            MutablePropertySources result = getFlattened();
            return result.iterator();
        }

        @Override
        public boolean contains(String name) {
            return get(name) != null;
        }

        @Override
        public PropertySource<?> get(String name) {
            return getFlattened().get(name);
        }

        private MutablePropertySources getFlattened() {
            MutablePropertySources result = new MutablePropertySources();
            for (PropertySource<?> propertySource : this.propertySources) {
                flattenPropertySources(propertySource, result);
            }
            return result;
        }

        private void flattenPropertySources(PropertySource<?> propertySource,
                                            MutablePropertySources result) {
            Object source = propertySource.getSource();
            if (source instanceof ConfigurableEnvironment) {
                ConfigurableEnvironment environment = (ConfigurableEnvironment) source;
                for (PropertySource<?> childSource : environment.getPropertySources()) {
                    flattenPropertySources(childSource, result);
                }
            } else {
                result.addLast(propertySource);
            }
        }

    }

}
