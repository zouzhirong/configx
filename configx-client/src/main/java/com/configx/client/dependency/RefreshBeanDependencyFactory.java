/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.dependency;

import com.configx.client.annotation.ConfigBean;
import com.configx.client.annotation.VersionRefreshScope;
import com.configx.client.config.ConfigurationBeanFactoryMetaData;
import com.configx.client.utils.ProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可刷新Bean的属性依赖管理
 *
 * @author Zhirong Zou
 */
public class RefreshBeanDependencyFactory implements MergedBeanDefinitionPostProcessor, InstantiationAwareBeanPostProcessor, PriorityOrdered {

    /**
     * @Bean definition meta data
     */
    private ConfigurationBeanFactoryMetaData beans = new ConfigurationBeanFactoryMetaData();

    /**
     * Map between dependent property names: bean name --> Set of dependent property names
     */
    private final Map<String, Set<String>> dependentPropertyMap = new ConcurrentHashMap<>(64);

    /**
     * Map between depending property names: property name --> Set of bean names for the property's dependencies
     */
    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * Set the bean meta-data store.
     *
     * @param beans the bean meta data store
     */
    public void setBeanMetaDataStore(ConfigurationBeanFactoryMetaData beans) {
        this.beans = beans;
    }

    /**
     * Return the names of all property that the specified bean depends on, if any.
     *
     * @param beanName the name of the bean
     * @return the array of dependent property names, or an empty array if none
     */
    public String[] getDependentPropertyNames(String beanName) {
        beanName = ProxyUtils.getOriginalBeanName(beanName);
        Set<String> dependentPropertyNames = this.dependentPropertyMap.get(beanName);
        if (dependentPropertyNames == null) {
            return new String[0];
        }
        return StringUtils.toStringArray(dependentPropertyNames);
    }

    /**
     * Return the names of beans which depend on the specified property, if any.
     *
     * @param propertyName the property name
     * @return the array of bean names which depend on the property, or an empty array if none
     */
    public String[] getDependenciesForBean(String propertyName) {
        Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(propertyName);
        if (dependenciesForBean == null) {
            return new String[0];
        }
        return dependenciesForBean.toArray(new String[dependenciesForBean.size()]);
    }

    /**
     * Register a dependent property for the given bean
     *
     * @param beanName              the name of the bean
     * @param dependentPropertyName the name of the dependent property
     */
    public void registerDependentProperty(String beanName, String dependentPropertyName) {
        beanName = ProxyUtils.getOriginalBeanName(beanName);
        Set<String> dependentPropertyNames = this.dependentPropertyMap.get(beanName);
        if (dependentPropertyNames != null && dependentPropertyNames.contains(dependentPropertyName)) {
            return;
        }

        // No entry yet -> fully synchronized manipulation of the dependentBeans Set
        synchronized (this.dependentPropertyMap) {
            dependentPropertyNames = this.dependentPropertyMap.get(beanName);
            if (dependentPropertyNames == null) {
                dependentPropertyNames = new LinkedHashSet<String>(8);
                this.dependentPropertyMap.put(beanName, dependentPropertyNames);
            }
            dependentPropertyNames.add(dependentPropertyName);
        }
        synchronized (this.dependenciesForBeanMap) {
            Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(dependentPropertyName);
            if (dependenciesForBean == null) {
                dependenciesForBean = new LinkedHashSet<String>(8);
                this.dependenciesForBeanMap.put(dependentPropertyName, dependenciesForBean);
            }
            dependenciesForBean.add(beanName);
        }
    }

    /**
     * Register  dependent property names for the given bean
     *
     * @param beanName               the name of the bean
     * @param dependentPropertyNames the names of the dependent property
     */
    public void registerDependentBean(String beanName, String[] dependentPropertyNames) {
        beanName = ProxyUtils.getOriginalBeanName(beanName);
        if (dependentPropertyNames == null) {
            return;
        }
        for (String dependentPropertyName : dependentPropertyNames) {
            registerDependentProperty(beanName, dependentPropertyName);
        }
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        System.out.println();
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        // 自动为@ConfigBean的Bean注册刷新属性依赖
        resolveDependencyForConfigBean(beanClass, beanName);

        // 找到@VersionRefreshScope的类或方法，来注册Bean刷新属性依赖
        resolveDependencyForVersionRefreshScope(beanClass, beanName);

        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        // 找到RefreshBeanDependency类型的Bean，来注册Bean刷新属性依赖
        resolveDependencyForRefreshBeanDependencyBeans(bean, beanName);

        return bean;
    }

    /**
     * 自动为@ConfigBean的Bean注册刷新属性依赖
     *
     * @param beanClass
     * @param beanName
     */
    private void resolveDependencyForConfigBean(Class<?> beanClass, String beanName) {
        ConfigBean configAnnotation = AnnotationUtils.findAnnotation(beanClass, ConfigBean.class);
        if (configAnnotation != null) {
            registerDependentBean(beanName, new String[]{configAnnotation.value()});
        }
    }

    /**
     * 找到@VersionRefreshScope的类或方法，来注册Bean刷新属性依赖
     *
     * @param beanClass
     * @param beanName
     */
    private void resolveDependencyForVersionRefreshScope(Class<?> beanClass, String beanName) {
        VersionRefreshScope annotation = AnnotationUtils.findAnnotation(beanClass, VersionRefreshScope.class); // Class上的@VersionRefreshScope注解
        if (annotation != null) {
            registerDependentBean(beanName, annotation.dependsOn());
        }
        annotation = this.beans.findFactoryAnnotation(beanName, VersionRefreshScope.class); // @Configuration注解的类的方法上的@VersionRefreshScope注解
        if (annotation != null) {
            registerDependentBean(beanName, annotation.dependsOn());
        }
    }

    /**
     * 找到RefreshBeanDependency类型的Bean，来注册Bean刷新属性依赖
     *
     * @param bean
     * @param beanName
     */
    private void resolveDependencyForRefreshBeanDependencyBeans(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        if (RefreshBeanDependency.class.isAssignableFrom(beanClass)) {
            RefreshBeanDependency refreshBean = (RefreshBeanDependency) bean;
            registerDependentBean(refreshBean.getBeanName(), refreshBean.getPropertyNames());
        }
        if (RefreshBeanDependencyFactoryBean.class.isAssignableFrom(beanClass)) {
            RefreshBeanDependencyFactoryBean refreshBeanFactoryBean = (RefreshBeanDependencyFactoryBean) bean;
            registerDependentBean(refreshBeanFactoryBean.getBeanName(), refreshBeanFactoryBean.getPropertyNames());
        }
    }

}
