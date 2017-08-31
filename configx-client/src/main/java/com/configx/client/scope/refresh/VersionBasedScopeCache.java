/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.scope.refresh;

import com.configx.client.beans.VersionBeanFactory;
import com.configx.client.env.ConfigVersionManager;
import com.configx.client.env.MultiVersionPropertySourceFactory;
import com.configx.client.scope.ScopeCache;
import com.configx.client.scope.refresh.dependency.RefreshableBeanDependencyManagement;
import com.configx.client.env.VersionPropertySource;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * 基于版本的Scope Cache
 *
 * @author Zhirong Zou
 */
public class VersionBasedScopeCache implements ScopeCache {
    /**
     * Scope
     */
    private VersionRefreshScope scope;

    /**
     * Version bean factory
     */
    private VersionBeanFactory versionBeanFactory;

    public VersionBasedScopeCache(VersionRefreshScope scope) {
        this.scope = scope;
        this.versionBeanFactory = new VersionBeanFactory();
    }

    @Override
    public Object remove(String name) {
        // 当前线程使用的配置版本号
        long version = ConfigVersionManager.getCurrentVersion();

        return versionBeanFactory.remove(version, name);
    }

    @Override
    public Collection<Object> clear() {
        // 当前线程使用的配置版本号
        long version = ConfigVersionManager.getCurrentVersion();

        return versionBeanFactory.clear(version);
    }

    @Override
    public Object get(String name) {
        // 当前线程使用的配置版本号
        long version = ConfigVersionManager.getCurrentVersion();

        return versionBeanFactory.get(version, name);
    }

    @Override
    public Object put(String name, Object value) {
        // 当前线程使用的配置版本号
        long version = ConfigVersionManager.getCurrentVersion();

        return putIfAbsent(version, name, value);
    }

    /**
     * 如果当前版本中存在需要的bean，则直接返回存在的bean，不存在，则放入新的bean或bean的创建工厂。
     *
     * @param version
     * @param name
     * @param value
     * @return
     */
    private Object putIfAbsent(long version, String name, Object value) {
        // 上一个版本号
        long previousVersion = ConfigVersionManager.getPreviousVersion(version);

        // 当前版本中，存在需要的bean
        Object bean = versionBeanFactory.get(version, name);
        if (bean != null) {
            return bean;
        }

        // 当前版本中，不存在需要的bean，则创建
        return put(version, previousVersion, name, value);
    }

    /**
     * 放入新的bean或bean的创建工厂(创建bean的factory)
     *
     * @param version
     * @param previousVersion
     * @param name
     * @param value
     * @return
     */
    private synchronized Object put(long version, long previousVersion, String name, Object value) {
        // 当前版本的指定名称的bean的最终的值
        Object finalValue = resolve(version, previousVersion, name, value);

        // 是否继承以前版本的bean
        boolean inherited = (finalValue != value);
        // 将当前版本的当前bean放入 VersionBeanFactory
        versionBeanFactory.put(version, name, finalValue, inherited);

        return finalValue;
    }

    /**
     * 为了防止bean重复创建，创建bean时先判断bean的属性是否变化，变化则创建，没有变化则读取以前版本创建的bean，首个版本必须创建。
     *
     * @param version
     * @param previousVersion
     * @param name
     * @param value
     * @return
     */
    private Object resolve(long version, long previousVersion, String name, Object value) {
        // 当前版本是首个版本，需要创建
        if (previousVersion <= 0) {
            return value;
        }
        if (requiresRefresh(version, name)) {// 当前版本Bean依赖的属性变化，需要刷新
            return value;
        }

        // Bean不需要创建，则尝试获取上一个版本的bean，来继承
        return getPreviousVersionBean(version, previousVersion, name);
    }

    /**
     * 判断指定版本的bean是否需要刷新
     *
     * @param version
     * @param beanName
     * @return
     */
    private boolean requiresRefresh(long version, String beanName) {
        // 这个版本的属性未变化，不用刷新任何Bean
        VersionPropertySource<?> versionPropertySource = getVersionPropertySource(version);
        if (versionPropertySource == null) {
            return false;
        }

        // 这个版本变化的属性
        String[] changedProperties = versionPropertySource.getPropertyNames();

        // 这个版本的属性有变化,判断这个bean是否跟这些变化的属性有关,有就刷新,没有则不刷新
        RefreshableBeanDependencyManagement dependencyManagement = getRefreshableBeanDependencyManagement();
        String[] dependentPropertyNames = dependencyManagement.getDependentPropertyNames(beanName);

        for (String propertyName : changedProperties) {
            for (String dependentPropertyName : dependentPropertyNames) {
                if (Pattern.matches(dependentPropertyName, propertyName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取上一个版本的bean
     *
     * @param version
     * @param previousVersion
     * @param name
     * @return
     */
    private Object getPreviousVersionBean(long version, long previousVersion, String name) {
        // 设置当前线程使用的配置版本号为上一版本
        ConfigVersionManager.setCurrentVersion(previousVersion);

        // 上一个版本的bean可能未初始化,触发初始化
        getContext().getBean(name);

        // 恢复当前线程使用的配置版本号为当前版本
        ConfigVersionManager.setCurrentVersion(version);

        // 获取上一个版本的bean
        Object previousBean = versionBeanFactory.get(previousVersion, name);

        return previousBean;
    }

    /**
     * 返回ConfigurableApplicationContext
     *
     * @return
     */
    public ConfigurableApplicationContext getContext() {
        return scope.getContext();
    }

    /**
     * 返回Environment
     *
     * @return
     */
    public ConfigurableEnvironment getEnvironment() {
        return getContext().getEnvironment();
    }

    /**
     * 获取指定版本号的属性源
     *
     * @param version
     * @return
     */
    public VersionPropertySource<?> getVersionPropertySource(long version) {
        return MultiVersionPropertySourceFactory.getVersionPropertySource(version, getEnvironment());
    }

    /**
     * 返回RefreshableBeanDependencyManagement Bean
     *
     * @return
     */
    public RefreshableBeanDependencyManagement getRefreshableBeanDependencyManagement() {
        String name = ConfigRefreshPostProcessorRegistrar.REFRESH_DEPENDENCY_MANAGEMENT_BEAN_NAME;
        return getContext().getBean(name, RefreshableBeanDependencyManagement.class);
    }
}