package com.configx.client.converter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.convert.TypeDescriptor;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 配置Bean转换服务
 * <p>
 * Created by zouzhirong on 2017/9/28.
 */
public class ConfigBeanConversionService implements BeanFactoryPostProcessor {

    private Set<ConfigBeanConverter> converters = new LinkedHashSet<>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        configurerConverters(beanFactory);
    }

    /**
     * 注册通过{@link ConfigBeanConverterConfigurer}配置的Converters
     *
     * @param beanFactory
     */
    private void configurerConverters(ConfigurableListableBeanFactory beanFactory) {
        // Find all CustomConverterConfigurer in the ApplicationContext, including ancestor contexts.
        Map<String, ConfigBeanConverterConfigurer> matchingBeans =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory, ConfigBeanConverterConfigurer.class, true, false);
        if (!matchingBeans.isEmpty()) {
            for (ConfigBeanConverterConfigurer converterConfigurer : matchingBeans.values()) {
                if (converterConfigurer.getConverters() != null) {
                    for (ConfigBeanConverter converter : converterConfigurer.getConverters()) {
                        converters.add(converter);
                    }
                }
            }
        }
    }

    /**
     * 将配置属性转换成Bean
     *
     * @param propertyName
     * @param propertyValue
     * @param targetType
     * @return
     */
    public Object convert(String propertyName, String propertyValue, TypeDescriptor targetType) {
        for (ConfigBeanConverter converter : converters) {
            if (converter.matches(propertyName, propertyValue, targetType)) {
                return converter.convert(propertyName, propertyValue, targetType);
            }
        }

        throw new ConverterNotFoundException(propertyName, propertyValue, targetType);
    }
}
