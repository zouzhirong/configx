/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.annotation;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.lang.annotation.*;

/**
 * Refresh Scope meta-annotated with @Scope
 *
 * @author Zhirong Zou
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Scope(com.configx.client.scope.refresh.VersionRefreshScope.SCOPE_NAME)
@Documented
public @interface VersionRefreshScope {
    /**
     * @see Scope#proxyMode()
     */
    ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;

    /**
     * Properties on which the current bean depends.
     */
    String[] dependsOn() default {};

}
