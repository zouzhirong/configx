/**
 * $Id$
 * Copyright(C) 2012-2016 qiyun.com. All rights reserved.
 */
package com.configx.demo.bean;

import com.configx.client.converter.ConfigBeanConverter;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;

/**
 * 自定义配置转换器，将XML文件转换成Bean
 */
public class XmlConfigConverter implements ConfigBeanConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlConfigConverter.class);

    @Override
    public boolean matches(String propertyName, String propertyValue, TypeDescriptor targetType) {
        return propertyName != null && propertyName.endsWith(".xml");
    }

    @Override
    public Object convert(String propertyName, String propertyValue, TypeDescriptor targetType) {
        Class<?> targetClass = targetType.getType();
        Serializer serializer = new Persister(new AnnotationStrategy());
        try {
            return serializer.read(targetClass, propertyValue, false);
        } catch (Exception e) {
            LOGGER.error("Convert xml to " + targetClass + " error", e);
        }
        return null;
    }

}
