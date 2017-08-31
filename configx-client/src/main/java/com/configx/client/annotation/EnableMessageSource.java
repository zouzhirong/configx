package com.configx.client.annotation;

import com.configx.client.messagesource.ConfigMessageSourcePostProcessorRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        ConfigMessageSourcePostProcessorRegistrar.class // 注册MessageSource处理相关bean
})
public @interface EnableMessageSource {

}
