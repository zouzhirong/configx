/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.item.converter;

import com.configx.client.annotation.ConfigBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.Collections;
import java.util.Set;

/**
 * {@link com.configx.client.item.converter.ConfigConverter}的抽象基类实现
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
public abstract class ConfigConverterSupport implements ConfigConverter {

    @Override
    public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, Object.class));
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.getType().equals(targetType.getType())) {
            // no conversion required
            return false;
        }
        ConfigBean configBean = getConfigAnnotation(targetType.getType());
        if (configBean == null) {
            return false;
        }
        return this.matches(configBean, sourceType, targetType);
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        Class<?> targetClass = targetType.getType();
        try {
            return convert((String) source, targetClass);
        } catch (Throwable ex) {
            throw new ConversionFailedException(sourceType, targetType, source, ex);
        }
    }

    /**
     * 是否匹配，留给子类实现
     *
     * @param annotation
     * @param sourceType
     * @param targetType
     * @return
     */
    protected abstract boolean matches(ConfigBean annotation, TypeDescriptor sourceType, TypeDescriptor targetType);

    /**
     * String转目标Class
     *
     * @param source
     * @param targetClass
     * @return
     */
    protected abstract Object convert(String source, Class<?> targetClass);

    /**
     * 返回目标Class上的{@link com.configx.client.annotation.ConfigBean}注解
     *
     * @param targetClass
     * @return
     */
    private static ConfigBean getConfigAnnotation(Class<?> targetClass) {
        ConfigBean config = AnnotationUtils.findAnnotation(targetClass, ConfigBean.class);
        return config;
    }

}
