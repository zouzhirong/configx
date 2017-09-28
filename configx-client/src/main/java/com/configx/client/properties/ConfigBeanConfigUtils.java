package com.configx.client.properties;

import com.configx.client.config.ConfigPropertyResolver;
import com.configx.client.converter.ConfigBeanConversionService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * 配置bean定义修改工具类
 * <p>
 * Created by zouzhirong on 2017/9/19.
 */
public class ConfigBeanConfigUtils {

    /**
     * ConfigBeanPropertyResolver Bean Name
     */
    public static final String CONFIG_BEAN_PROPERTY_RESOLVER_BEAN_NAME = ConfigPropertyResolver.BEAN_NAME;

    /**
     * ConfigBeanConversionService Bean Name
     */
    public static final String CONFIG_BEAN_CONVERSION_SERVICE_BEAN_NAME = ConfigBeanConversionService.class.getName();

    /**
     * 注册ConfigBeanPropertyResolver
     *
     * @param registry
     */
    public static void registerConfigBeanPropertyResolver(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(CONFIG_BEAN_PROPERTY_RESOLVER_BEAN_NAME)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(ConfigPropertyResolver.class);
            registry.registerBeanDefinition(CONFIG_BEAN_PROPERTY_RESOLVER_BEAN_NAME, bean.getBeanDefinition());
        }
    }

    /**
     * 注册ConfigBeanConversionService
     *
     * @param registry
     */
    public static void registerConfigBeanConversionService(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(CONFIG_BEAN_CONVERSION_SERVICE_BEAN_NAME)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(ConfigBeanConversionService.class);
            registry.registerBeanDefinition(CONFIG_BEAN_CONVERSION_SERVICE_BEAN_NAME, bean.getBeanDefinition());
        }
    }

    /**
     * 修改配置Bean定义，使用静态方法{@link ConfigBeanFactory#FACTORY_METHOD}来实例化。
     *
     * @param registry
     * @param beanName
     * @param beanDefinition
     * @param propertyName
     * @param converterType
     */
    public static void setConfigBeanFactoryMethod(BeanDefinitionRegistry registry,
                                                  String beanName, BeanDefinition beanDefinition,
                                                  String propertyName, Class<?> converterType) {

        // 注册ConfigBeanPropertyResolver
        registerConfigBeanPropertyResolver(registry);

        // 注册ConfigBeanConversionService
        registerConfigBeanConversionService(registry);

        String beanClassName = beanDefinition.getBeanClassName();

        beanDefinition.setBeanClassName(ConfigBeanFactory.class.getName());
        beanDefinition.setFactoryMethodName(ConfigBeanFactory.FACTORY_METHOD);
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addGenericArgumentValue(propertyName, String.class.getName());
        constructorArgumentValues.addGenericArgumentValue(beanClassName, Class.class.getName());
        constructorArgumentValues.addGenericArgumentValue(converterType);
        constructorArgumentValues.addGenericArgumentValue(new RuntimeBeanReference(CONFIG_BEAN_PROPERTY_RESOLVER_BEAN_NAME));
        constructorArgumentValues.addGenericArgumentValue(new RuntimeBeanReference(CONFIG_BEAN_CONVERSION_SERVICE_BEAN_NAME));
        beanDefinition.getConstructorArgumentValues().addArgumentValues(constructorArgumentValues);
    }
}
