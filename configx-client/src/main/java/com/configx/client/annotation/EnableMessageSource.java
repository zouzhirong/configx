package com.configx.client.annotation;

import com.configx.client.messagesource.EnableMessageSourceImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启配置管理对MessageSource支持
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableMessageSourceImportSelector.class)
public @interface EnableMessageSource {

    boolean fallbackToSystemLocale() default true;

    String[] basenames() default {};
}
