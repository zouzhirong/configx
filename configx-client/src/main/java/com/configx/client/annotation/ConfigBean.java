/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.annotation;

import com.configx.client.item.converter.ConfigConverter;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 标记一个Bean是配置Bean
 *
 * @author zouzhirong
 * @version 1.0
 * @since 1.0
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
    Class<? extends ConfigConverter> converter() default ConfigConverter.class;

}
