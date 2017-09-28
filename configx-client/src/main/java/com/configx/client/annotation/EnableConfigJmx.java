package com.configx.client.annotation;

import com.configx.client.jmx.EnableConfigJmxImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启配置管理JMX支持
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableConfigJmxImportSelector.class)
public @interface EnableConfigJmx {

}
