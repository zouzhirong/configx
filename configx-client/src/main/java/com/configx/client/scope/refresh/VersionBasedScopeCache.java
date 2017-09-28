/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.scope.refresh;

import com.configx.client.beans.VersionBeanFactory;
import com.configx.client.scope.ScopeCache;
import com.configx.client.version.ConfigVersionManager;
import com.configx.client.version.VersionExecutor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 基于版本的Scope Cache
 *
 * @author Zhirong Zou
 */
public class VersionBasedScopeCache implements ScopeCache {

    /**
     * Version bean factory
     */
    private VersionBeanFactory versionBeanFactory = new VersionBeanFactory();

    /**
     * Refresh bean names for versions
     */
    private final ConcurrentMap<Long, Set<String>> versionRefreshBeanNames = new ConcurrentHashMap<>();

    @Override
    public Object remove(String name) {
        return VersionExecutor.doWithCurrentVersion(version ->
                versionBeanFactory.remove(version, name)
        );
    }

    @Override
    public Collection<Object> clear() {
        return VersionExecutor.doWithCurrentVersion((version ->
                versionBeanFactory.clear(version)
        ));
    }

    @Override
    public Object get(String name) {
        return VersionExecutor.doWithCurrentVersion((version ->
                versionBeanFactory.get(version, name)
        ));
    }

    @Override
    public Object put(String name, Object value) {
        return VersionExecutor.doWithCurrentVersion((version ->
                putIfAbsent(version, name, value)
        ));
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
        // 当前版本中，存在需要的bean
        Object bean = versionBeanFactory.get(version, name);
        if (bean != null) {
            return bean;
        }

        // 当前版本中，不存在需要的bean，则创建
        return put(version, name, value);
    }

    /**
     * 放入新的bean或bean的创建工厂(创建bean的factory)
     *
     * @param version
     * @param name
     * @param value
     * @return
     */
    private Object put(long version, String name, Object value) {
        // 当前版本的指定名称的bean的最终的值
        Object finalValue = resolve(version, name, value);

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
     * @param name
     * @param value
     * @return
     */
    private Object resolve(long version, String name, Object value) {
        // 上一个版本号
        long previousVersion = ConfigVersionManager.getPreviousVersion(version);

        // 当前版本是首个版本，需要创建
        if (previousVersion <= 0) {
            return value;
        }
        if (shouldRefresh(version, name)) {// 当前版本Bean依赖的属性变化，需要刷新
            return value;
        }

        // Bean不需要创建，则尝试获取上一个版本的bean，来继承
        return getPreviousVersionBean(version, previousVersion, name, value);
    }

    /**
     * 获取上一个版本的bean
     *
     * @param version
     * @param previousVersion
     * @param name
     * @param value
     * @return
     */
    private Object getPreviousVersionBean(long version, long previousVersion, String name, Object value) {
        // 上一个版本的bean可能未初始化,触发初始化
        putIfAbsent(previousVersion, name, value);

        // 获取上一个版本的bean
        Object previousBean = versionBeanFactory.get(previousVersion, name);

        return previousBean;
    }

    /**
     * 判断指定版本指定的bean是否应该刷新
     *
     * @param version
     * @param beanNmae
     * @return
     */
    public boolean shouldRefresh(long version, String beanNmae) {
        Set<String> list = versionRefreshBeanNames.get(version);
        return list != null && list.contains(beanNmae);
    }

    /**
     * 添加需要刷新的beans
     *
     * @param version
     * @param beanNames
     */
    public void addRefreshBeanNames(long version, Collection<String> beanNames) {
        Set<String> set = versionRefreshBeanNames.get(version);
        if (set == null) {
            set = new ConcurrentSkipListSet<>();
            Set<String> oldSet = versionRefreshBeanNames.putIfAbsent(version, set);
            if (oldSet != null) {
                set = oldSet;
            }
        }
        set.addAll(beanNames);
    }

    /**
     * 获取需要刷新的beans
     *
     * @param version
     * @return
     */
    public Set<String> getRefreshBeanNames(long version) {
        Set<String> set = versionRefreshBeanNames.get(version);
        return set == null ? Collections.emptySet() : new HashSet<>(set);
    }
}
