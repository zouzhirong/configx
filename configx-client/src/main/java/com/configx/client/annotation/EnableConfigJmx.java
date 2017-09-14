package com.configx.client.annotation;

import com.configx.client.jmx.ConfigJmxRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        ConfigJmxRegistrar.class // 注册JMX处理相关bean
})
public @interface EnableConfigJmx {

}
