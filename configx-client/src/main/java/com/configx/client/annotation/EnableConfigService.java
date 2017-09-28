package com.configx.client.annotation;

import com.configx.client.config.EnableConfigServiceImportSelector;
import com.configx.client.converter.ConfigBeanConverter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启配置管理服务
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableConfigServiceImportSelector.class)
public @interface EnableConfigService {

    /**
     * 自动注册配置项的转换器
     */
    Class<? extends ConfigBeanConverter>[] converters() default {};

}
