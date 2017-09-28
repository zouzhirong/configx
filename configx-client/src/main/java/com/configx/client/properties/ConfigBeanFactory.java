package com.configx.client.properties;

import com.configx.client.config.ConfigPropertyResolver;
import com.configx.client.converter.ConfigBeanConversionService;
import com.configx.client.converter.ConfigBeanConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.env.PropertyResolver;

/**
 * 创建配置Bean的Factory
 * <p>
 * Created by zouzhirong on 2017/9/13.
 *
 * @see {@link com.configx.client.annotation.ConfigBean}
 */
public class ConfigBeanFactory {

    public static final String FACTORY_METHOD = "createConfigBean";

    /**
     * 创建配置Bean
     *
     * @param propertyName               属性名
     * @param beanType                   配置Bean类型
     * @param converterType              转换器类型
     * @param configBeanPropertyResolver 属性解析器
     * @param conversionService          转换服务
     * @param <T>
     * @return
     */
    public static <T> T createConfigBean(String propertyName, Class<T> beanType,
                                         Class<? extends ConfigBeanConverter> converterType,
                                         ConfigPropertyResolver configBeanPropertyResolver,
                                         ConfigBeanConversionService conversionService) {

        PropertyResolver propertyResolver = configBeanPropertyResolver.getObject();
        String propertyValue = propertyResolver.getRequiredProperty(propertyName);

        if (converterType != null && !converterType.isInterface()) {
            ConfigBeanConverter converter = BeanUtils.instantiate(converterType);
            return (T) converter.convert(propertyName, propertyValue, TypeDescriptor.valueOf(beanType));

        } else {
            return (T) conversionService.convert(propertyName, propertyValue, TypeDescriptor.valueOf(beanType));
        }
    }

}
