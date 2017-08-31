/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.beans;

/**
 * 基于版本的bean信息
 *
 * @author Zhirong Zou
 */
public class VersionBean {

    /**
     * bean对象
     */
    private Object bean;

    /**
     * 是否继承以前的版本
     */
    private boolean inherited;

    public VersionBean(Object bean, boolean inherited) {
        this.bean = bean;
        this.inherited = inherited;
    }

    /**
     * 返回bean
     *
     * @return
     */
    public Object getBean() {
        return bean;
    }

    /**
     * 返回bean是否继承以前版本的bean
     *
     * @return
     */
    public boolean isInherited() {
        return inherited;
    }
}
