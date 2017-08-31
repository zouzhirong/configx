/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理某个版本的Beans
 *
 * @author Zhirong Zou
 */
public class VersionBeans {
    /**
     * 版本号
     */
    private long version;

    /**
     * bean信息，BeanName -> Bean
     */
    private ConcurrentHashMap<String, VersionBean> beansMap = new ConcurrentHashMap<>();

    public VersionBeans(long version) {
        this.version = version;
    }

    /**
     * 返回版本号
     *
     * @return
     */
    public long getVersion() {
        return version;
    }

    /**
     * 返回Bean名称列表
     *
     * @return
     */
    public Collection<String> getBeanNames() {
        return beansMap.keySet();
    }

    /**
     * 返回Bean列表
     *
     * @return
     */
    public Collection<Object> getBeans() {
        List<Object> values = new ArrayList<>();
        for (VersionBean versionBean : beansMap.values()) {
            values.add(versionBean.getBean());
        }
        return values;
    }

    /**
     * 添加bean信息
     *
     * @param beanName  bean名称
     * @param bean      bean对象
     * @param inherited 是否继承自以前的版本
     */
    public Object add(String beanName, Object bean, boolean inherited) {
        VersionBean value = new VersionBean(bean, inherited);
        VersionBean result = beansMap.putIfAbsent(beanName, new VersionBean(bean, inherited));

        if (result != null) {
            return result.getBean();
        }
        return value.getBean();
    }

    /**
     * 移除bean信息
     *
     * @param beanName
     */
    public Object remove(String beanName) {
        VersionBean versionBean = beansMap.remove(beanName);
        return versionBean == null ? null : versionBean.getBean();
    }

    /**
     * 获取指定bean名称的bean
     *
     * @param beanName
     * @return
     */
    public Object get(String beanName) {
        VersionBean versionBean = beansMap.get(beanName);
        return versionBean == null ? null : versionBean.getBean();
    }
}
