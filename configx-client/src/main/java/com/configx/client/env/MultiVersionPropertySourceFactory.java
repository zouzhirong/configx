/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.env;

import com.configx.client.item.ConfigItemList;
import com.configx.client.listener.ConfigItemChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

/**
 * 多版本的{@link PropertySource}的工厂类，用于添加版本属性源到多版本属性源中
 *
 * @author zhirong.zou
 */
public abstract class MultiVersionPropertySourceFactory {

    public static final String MULTIVERSION_PROPERTY_SOURCE_NAME = "multiVersionProperties";

    /**
     * 创建多版本属性源
     *
     * @return
     */
    private static MultiVersionPropertySource createMultiVersionPropertySource() {
        return new MultiVersionPropertySource(MULTIVERSION_PROPERTY_SOURCE_NAME);
    }

    /**
     * 返回Environment中的多版本属性源，不存在则创建
     *
     * @param environment
     * @return
     */
    public static MultiVersionPropertySource getMultiVersionPropertySource(ConfigurableEnvironment environment) {
        PropertySource<?> multiVersionPropertySource = environment.getPropertySources().get(MULTIVERSION_PROPERTY_SOURCE_NAME);

        // 不存在多版本属性源，则创建
        if (multiVersionPropertySource == null) {
            multiVersionPropertySource = createMultiVersionPropertySource();
            environment.getPropertySources().addLast(multiVersionPropertySource);
        }

        return (MultiVersionPropertySource) multiVersionPropertySource;
    }

    /**
     * 向Environment中添加版本属性源
     *
     * @param versionPropertySource
     * @param applicationContext
     * @param environment
     */
    public static void addVersionPropertySource(VersionPropertySource<ConfigItemList> versionPropertySource,
                                                ApplicationContext applicationContext,
                                                ConfigurableEnvironment environment) {
        if (versionPropertySource == null) {
            return;
        }

        MultiVersionPropertySource multiVersionPropertySource = getMultiVersionPropertySource(environment);
        multiVersionPropertySource.addPropertySource(versionPropertySource);
    }

    /**
     * 返回Environment的多版本属性源中，指定版本的属性源
     *
     * @param version
     * @param environment
     * @return
     */
    public static VersionPropertySource<?> getVersionPropertySource(long version, ConfigurableEnvironment environment) {
        MultiVersionPropertySource multiVersionPropertySource = getMultiVersionPropertySource(environment);
        return multiVersionPropertySource.getPropertySource(version);
    }

}
