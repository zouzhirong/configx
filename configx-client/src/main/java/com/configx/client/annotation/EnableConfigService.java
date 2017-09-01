package com.configx.client.annotation;

import com.configx.client.item.converter.ConfigConverter;
import com.configx.client.item.locator.ConfigPropertySourcePostProcessorRegistrar;
import com.configx.client.properties.ConfigPropertiesPostProcessorRegistrar;
import com.configx.client.properties.EnableConfigServiceImportSelector;
import com.configx.client.properties.MetaDataPostProcessorRegistrar;
import com.configx.client.scope.refresh.ConfigRefreshPostProcessorRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        EnableConfigServiceImportSelector.class,            // 注册ConfigConverter
        MetaDataPostProcessorRegistrar.class,               // 注册元数据处理相关bean
        ConfigPropertySourcePostProcessorRegistrar.class,   // 注册配置属性源处理相关bean
        ConfigPropertiesPostProcessorRegistrar.class,       // 注册配置Bean属性注入相关bean
        ConfigRefreshPostProcessorRegistrar.class,          // 注册配置Bean刷新处理相关bean
})
public @interface EnableConfigService {


    /**
     * 自动注册配置项的转换器
     */
    Class<? extends ConfigConverter>[] converters() default {};

}
