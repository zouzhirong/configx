/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility class to memorize {@code @Bean} definition meta data during initialization of
 * the bean factory.
 *
 * @author Zhirong Zou
 */
public class ConfigurationBeanFactoryMetaData implements BeanFactoryPostProcessor {

    private ConfigurableListableBeanFactory beanFactory;

    private Map<String, MetaData> beans = new HashMap<String, MetaData>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        for (String name : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition definition = beanFactory.getBeanDefinition(name);
            String method = definition.getFactoryMethodName();
            String bean = definition.getFactoryBeanName();
            if (method != null && bean != null) {
                this.beans.put(name, new MetaData(bean, method));
            }
        }
    }

    public <A extends Annotation> Map<String, Object> getBeansWithFactoryAnnotation(Class<A> type) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String name : this.beans.keySet()) {
            if (findFactoryAnnotation(name, type) != null) {
                result.put(name, this.beanFactory.getBean(name));
            }
        }
        return result;
    }

    public <A extends Annotation> A findFactoryAnnotation(String beanName, Class<A> type) {
        Method method = findFactoryMethod(beanName);
        return (method == null ? null : AnnotationUtils.findAnnotation(method, type));
    }

    private Method findFactoryMethod(String beanName) {
        if (!this.beans.containsKey(beanName)) {
            return null;
        }
        final AtomicReference<Method> found = new AtomicReference<Method>(null);
        MetaData meta = this.beans.get(beanName);
        final String factory = meta.getMethod();
        Class<?> type = this.beanFactory.getType(meta.getBean());
        ReflectionUtils.doWithMethods(type, new MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                if (method.getName().equals(factory)) {
                    found.compareAndSet(null, method);
                }
            }
        });
        return found.get();
    }

    private static class MetaData {

        private String bean;

        private String method;

        MetaData(String bean, String method) {
            this.bean = bean;
            this.method = method;
        }

        public String getBean() {
            return this.bean;
        }

        public String getMethod() {
            return this.method;
        }

    }

}
