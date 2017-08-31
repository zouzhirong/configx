package com.configx.client.item.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.ConfigurableConversionService;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link BeanFactoryPostProcessor} implementation that allows for convenient
 * registration of custom {@link Converter}.
 */
public class CustomConverterConfigurer implements BeanFactoryPostProcessor, Ordered {

    protected final Log logger = LogFactory.getLog(getClass());

    private int order = Ordered.LOWEST_PRECEDENCE;  // default: same as non-Ordered

    private List<Converter<?, ?>> converters;

    private List<GenericConverter> genericConverters;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setConverters(List<Converter<?, ?>> converters) {
        this.converters = converters;
    }

    public void addConverters(Converter<?, ?> converter) {
        if (this.converters == null) {
            this.converters = new ArrayList<>();
        }
        this.converters.add(converter);
    }

    public List<Converter<?, ?>> getConverters() {
        return converters;
    }

    public void setGenericConverters(List<GenericConverter> converters) {
        this.genericConverters = converters;
    }

    public void addGenericConverter(GenericConverter converter) {
        if (this.genericConverters == null) {
            this.genericConverters = new ArrayList<>();
        }
        this.genericConverters.add(converter);
    }

    public List<GenericConverter> getGenericConverters() {
        return genericConverters;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // Initialize conversion service for this context.
        if (beanFactory.containsBean(ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME) &&
                beanFactory.isTypeMatch(ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME, ConfigurableConversionService.class)) {
            ConfigurableConversionService conversionService = beanFactory.getBean(ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME, ConfigurableConversionService.class);

            if (this.converters != null) {
                for (Converter<?, ?> converter : this.converters) {
                    conversionService.addConverter(converter);
                }
            }
            if (this.genericConverters != null) {
                for (GenericConverter genericConverter : this.genericConverters) {
                    conversionService.addConverter(genericConverter);
                }
            }
        }
    }

}
