package com.configx.client.dependency;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * bean依赖属性信息的bean定义工具类
 * <p>
 * Created by zouzhirong on 2017/9/19.
 */
public class DependencyConfigUtils {

    public static void registerDependency(BeanDefinitionRegistry registry, String beanName, String[] propertyNames) {
        if (ArrayUtils.isEmpty(propertyNames)) {
            return;
        }
        String dependencyBeanName = beanName + ".dependency";
        BeanDefinitionBuilder meta = BeanDefinitionBuilder.genericBeanDefinition(RefreshBeanDependencyFactoryBean.class);
        meta.addPropertyValue("beanName", beanName);
        meta.addPropertyValue("propertyNames", propertyNames);
        registry.registerBeanDefinition(dependencyBeanName, meta.getBeanDefinition());
    }
}
