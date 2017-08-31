/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.scope.refresh;

import com.configx.client.scope.GenericScope;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

/**
 * Version-refresh scope
 *
 * @author zhirong.zou
 */
public class VersionRefreshScope extends GenericScope implements ApplicationContextAware {
    /**
     * scope name
     */
    public static final String SCOPE_NAME = "version-refresh";

    private ConfigurableApplicationContext applicationContext;

    /**
     * Create a scope instance and give it the default name: "refresh".
     */
    public VersionRefreshScope() {
        super();
        super.setName(SCOPE_NAME);
        super.setScopeCache(new VersionBasedScopeCache(this));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Assert.state(applicationContext instanceof ConfigurableApplicationContext, "ApplicationContext does not implement ConfigurableApplicationContext");
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    /**
     * 返回ConfigurableApplicationContext
     *
     * @return
     */
    public ConfigurableApplicationContext getContext() {
        return applicationContext;
    }
}
