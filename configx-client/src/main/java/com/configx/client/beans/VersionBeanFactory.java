/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理各个版本的Bean的Factory
 *
 * @author Zhirong Zou
 */
public class VersionBeanFactory {

    private static final Log logger = LogFactory.getLog("com.configx.client.versionbean.factory");

    /**
     * Map between version beans: version--> Set of beans
     */
    private ConcurrentHashMap<Long, VersionBeans> versionBeansMap = new ConcurrentHashMap<>();

    /**
     * clear all version beans
     *
     * @return
     */
    public void clear() {
        versionBeansMap.clear();
        logger.info("VersionBeanFactory clear all version beans, version");
    }

    /**
     * clear the specified version beans
     *
     * @return
     */
    public Collection<Object> clear(long version) {
        VersionBeans versionBeans = versionBeansMap.remove(version);
        Collection<Object> values = new ArrayList<Object>(versionBeans.getBeans());

        logger.info("VersionBeanFactory clear version beans, version=" + version);

        return values;
    }

    /**
     * return the bean of specified version and specified name
     *
     * @param version
     * @param name
     * @return
     */
    public Object get(long version, String name) {
        VersionBeans versionBeans = versionBeansMap.get(version);
        return versionBeans == null ? null : versionBeans.get(name);
    }

    /**
     * put the bean of specified version and specified name
     *
     * @param version
     * @param beanName
     * @param bean
     * @param inherited
     * @return
     */
    public Object put(long version, String beanName, Object bean, boolean inherited) {
        VersionBeans versionBeans = versionBeansMap.get(version);
        if (versionBeans == null) {
            versionBeans = new VersionBeans(version);
            VersionBeans result = versionBeansMap.putIfAbsent(version, versionBeans);
            if (result != null) {
                versionBeans = result;
            }
        }

        Object versionBean = versionBeans.add(beanName, bean, inherited);

        logger.info("VersionBeanFactory put version bean, version=" + version + " beanName=" + beanName
                + " " + (versionBean != null) + " inherited=" + inherited);

        return versionBean;
    }

    /**
     * remove the bean of specified version and specified name
     *
     * @param version
     * @param name
     * @return
     */
    public Object remove(long version, String name) {
        VersionBeans versionBeans = versionBeansMap.get(version);
        Object versionBean = versionBeans == null ? null : versionBeans.remove(name);

        logger.info("VersionBeanFactory remove version bean, version=" + version + " beanName=" + name
                + " " + (versionBean != null));

        return versionBean;
    }

}
