/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.env;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

/**
 * 带版本号的{@link PropertySource}
 *
 * @author zhirong.zou
 */
public class VersionPropertySource<T> extends EnumerablePropertySource<EnumerablePropertySource<T>> {

    /**
     * 版本号
     */
    private final long version;

    public VersionPropertySource(long version, EnumerablePropertySource<T> propertySource) {
        super(generateVersionPropertySourceName(version, propertySource), propertySource);
        this.version = version;
    }

    /**
     * 生成带版本号的属性源的名称
     *
     * @param version
     * @param propertySource
     * @return
     */
    private static String generateVersionPropertySourceName(long version, PropertySource<?> propertySource) {
        return propertySource.getName() + "-v" + version;
    }

    /**
     * 返回PropertySource的版本号
     *
     * @return
     */
    public long getVersion() {
        return version;
    }

    @Override
    public Object getProperty(String name) {
        return getSource().getProperty(name);
    }

    @Override
    public boolean containsProperty(String name) {
        return getSource().containsProperty(name);
    }

    @Override
    public String toString() {
        return String.format("%s [name='%s', version='%d', propertySource=%s]", getClass().getSimpleName(), this.name, this.version, this.source);
    }

    @Override
    public String[] getPropertyNames() {
        return source.getPropertyNames();
    }

}
