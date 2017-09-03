/**
 * $Id$
 * Copyright(C) 2012-2016 qiyun.com. All rights reserved.
 */
package com.configx.demo.bean;

import com.configx.client.annotation.ConfigBean;
import com.configx.client.item.converter.ConfigConverterSupport;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;

/**
 * 自定义配置转换器，将XML文件转换成Bean
 */
public class XmlConfigConverter extends ConfigConverterSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlConfigConverter.class);

    @Override
    protected boolean matches(ConfigBean annotation, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return annotation.value().endsWith(".xml");
    }

    @Override
    protected Object convert(String source, Class<?> targetClass) {
        Serializer serializer = new Persister(new AnnotationStrategy());
        try {
            return serializer.read(targetClass, source, false);
        } catch (Exception e) {
            LOGGER.error("Convert xml to " + targetClass + " error", e);
        }
        return null;
    }

}
