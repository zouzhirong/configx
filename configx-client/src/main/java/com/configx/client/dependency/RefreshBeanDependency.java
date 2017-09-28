/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.dependency;

/**
 * 可刷新Bean的属性依赖信息
 *
 * @author Zhirong Zou
 */
public class RefreshBeanDependency {

    /**
     * Bean名称
     */
    private String beanName;

    /**
     * Bean类型
     */
    private Class<?> beanType;

    /**
     * 属性名，属性名正则表达式，多个之间用逗号隔开
     */
    private String[] propertyNames;

    public RefreshBeanDependency(String beanName, Class<?> beanType, String[] propertyNames) {
        this.beanName = beanName;
        this.beanType = beanType;
        this.propertyNames = propertyNames;
    }

    /**
     * 返回 bean name
     *
     * @return
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * 设置 bean name
     *
     * @param beanName
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * 返回 bean类型
     *
     * @return
     */
    public Class<?> getBeanType() {
        return beanType;
    }

    /**
     * 设置 bean类型
     *
     * @param beanType
     */
    public void setBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }

    /**
     * 返回 bean刷新依赖的属性名称
     *
     * @return
     */
    public String[] getPropertyNames() {
        return propertyNames;
    }

    /**
     * 设置 bean刷新依赖的属性名称
     *
     * @param propertyNames
     */
    public void setPropertyNames(String[] propertyNames) {
        this.propertyNames = propertyNames;
    }
}
