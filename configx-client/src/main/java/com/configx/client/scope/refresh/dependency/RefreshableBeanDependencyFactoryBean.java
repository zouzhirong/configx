/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.scope.refresh.dependency;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.ClassUtils;

/**
 * {@link com.configx.client.scope.refresh.dependency.RefreshableBeanDependency} FactoryBean
 *
 * @author Zhirong Zou
 */
public class RefreshableBeanDependencyFactoryBean extends AbstractFactoryBean<RefreshableBeanDependency> {

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    /**
     * Bean名称
     */
    private String beanName;

    /**
     * Bean类型
     */
    private String beanClass;

    /**
     * 属性名，属性名正则表达式，多个之间用逗号隔开
     */
    private String[] propertyNames;

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
     * 返回 bean类型名称
     *
     * @return
     */
    public String getBeanClass() {
        return beanClass;
    }

    /**
     * 设置 bean类型名称
     *
     * @param beanClass
     */
    public void setBeanClass(String beanClass) {
        this.beanClass = beanClass;
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

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        super.setBeanClassLoader(classLoader);
        this.beanClassLoader = classLoader;
    }

    @Override
    public Class<?> getObjectType() {
        return RefreshableBeanDependency.class;
    }

    @Override
    protected RefreshableBeanDependency createInstance() throws Exception {
        String className = getBeanClass();
        Class<?> resolvedClass = null;
        if (className != null) {
            resolvedClass = ClassUtils.forName(className, beanClassLoader);
        }

        return new RefreshableBeanDependency(className, resolvedClass, propertyNames);
    }
}
