/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.converter;

import org.springframework.core.convert.TypeDescriptor;

/**
 * 配置Bean转换器
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
public interface ConfigBeanConverter {

    boolean matches(String propertyName, String propertyValue, TypeDescriptor targetType);

    Object convert(String propertyName, String propertyValue, TypeDescriptor targetType);

}
