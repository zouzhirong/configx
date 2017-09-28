/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.scope.refresh;

import com.configx.client.scope.GenericScope;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import java.util.Collection;
import java.util.Set;

/**
 * Version-refresh scope
 *
 * @author zhirong.zou
 */
public class VersionRefreshScope extends GenericScope
        implements ApplicationContextAware,
        BeanDefinitionRegistryPostProcessor,
        ApplicationListener<ContextRefreshedEvent>,
        Ordered {

    /**
     * scope name
     */
    public static final String SCOPE_NAME = "version-refresh";

    private ApplicationContext context;
    private BeanDefinitionRegistry registry;
    private boolean eager = true;
    private int order = Ordered.LOWEST_PRECEDENCE - 100;

    private VersionBasedScopeCache cache = null;

    /**
     * Create a scope instance and give it the default name: "refresh".
     */
    public VersionRefreshScope() {
        super.setName(SCOPE_NAME);
        cache = new VersionBasedScopeCache();
        super.setScopeCache(cache);
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Flag to determine whether all beans in refresh scope should be instantiated eagerly
     * on startup. Default true.
     *
     * @param eager the flag to set
     */
    public void setEager(boolean eager) {
        this.eager = eager;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
            throws BeansException {
        this.registry = registry;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        start(event);
    }

    public void start(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.context) {
            if (this.eager && this.registry != null) {
                for (String name : this.context.getBeanDefinitionNames()) {
                    BeanDefinition definition = this.registry.getBeanDefinition(name);
                    if (this.getName().equals(definition.getScope())
                            && !definition.isLazyInit()) {
                        this.context.getBean(name);
                    }
                }
            }
        }
    }

    /**
     * 刷新beans
     *
     * @param version
     * @param names
     */
    public void refresh(long version, Collection<String> names) {
        cache.addRefreshBeanNames(version, names);
    }

    /**
     * 获取刷新的beans
     *
     * @param version
     * @return
     */
    public Set<String> getRefreshBeanNames(long version) {
        return cache.getRefreshBeanNames(version);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
