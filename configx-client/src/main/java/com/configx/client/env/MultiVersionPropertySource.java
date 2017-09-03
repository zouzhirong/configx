/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.env;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 多版本的{@link PropertySource}，每个版本保存更新的属性，新版本从旧版本继承属性，新版本覆盖旧版本的属性
 *
 * @author zhirong.zou
 */
public class MultiVersionPropertySource extends EnumerablePropertySource<Object> {

    private final List<VersionPropertySource<?>> propertySources = new CopyOnWriteArrayList<>();

    public MultiVersionPropertySource(String name) {
        super(name);
    }

    /**
     * 返回指定版本的属性源
     *
     * @param version
     * @return
     */
    public VersionPropertySource<?> getPropertySource(long version) {
        for (VersionPropertySource<?> propertySource : this.propertySources) {
            if (propertySource.getVersion() == version) {
                return propertySource;
            }
        }
        return null;
    }

    @Override
    public Object getProperty(String name) {
        return ConfigVersionManager.doWithVersion(version -> {
            VersionPropertySource<?> propertySource = null;
            for (int index = this.propertySources.size() - 1; index >= 0; index--) {
                propertySource = this.propertySources.get(index);

                if (propertySource.getVersion() <= version) {
                    Object candidate = propertySource.getProperty(name);
                    if (candidate != null) {
                        return candidate;
                    }
                }
            }

            return null;
        });
    }

    @Override
    public boolean containsProperty(String name) {
        return ConfigVersionManager.doWithVersion(version -> {
            VersionPropertySource<?> propertySource = null;
            for (int index = this.propertySources.size() - 1; index >= 0; index--) {
                propertySource = this.propertySources.get(index);

                if (propertySource.getVersion() <= version) {
                    if (propertySource.containsProperty(name)) {
                        return true;
                    }
                }
            }

            return false;
        });
    }

    @Override
    public String[] getPropertyNames() {
        return ConfigVersionManager.doWithVersion(version -> {
            Set<String> names = new LinkedHashSet<String>();

            VersionPropertySource<?> propertySource = null;
            for (int index = this.propertySources.size() - 1; index >= 0; index--) {
                propertySource = this.propertySources.get(index);

                if (propertySource.getVersion() <= version) {
                    names.addAll(Arrays.asList(propertySource.getPropertyNames()));
                }
            }

            return StringUtils.toStringArray(names);
        });
    }

    /**
     * Add the given {@link PropertySource} to the end of the chain.
     *
     * @param versionPropertySource the version PropertySource to add
     */
    public void addPropertySource(VersionPropertySource<?> versionPropertySource) {
        if (ConfigVersionManager.addVersion(versionPropertySource.getVersion())) {
            this.propertySources.add(versionPropertySource);
        }
    }

    /**
     * Return all property sources that this composite source holds.
     *
     * @since 4.1.1
     */
    public Collection<VersionPropertySource<?>> getPropertySources() {
        return this.propertySources;
    }

    @Override
    public String toString() {
        return String.format("%s [name='%s', propertySources=%s]", getClass().getSimpleName(), this.name, this.propertySources);
    }

}
