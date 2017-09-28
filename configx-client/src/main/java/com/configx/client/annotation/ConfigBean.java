/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.annotation;

import com.configx.client.converter.ConfigBeanConverter;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 配置Bean注解，标记这个bean由属性转换而来。
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ConfigBean {
    /**
     * 配置名
     */
    String value();

    /**
     * 配置项的转换器
     */
    Class<? extends ConfigBeanConverter> converter() default ConfigBeanConverter.class;

}
