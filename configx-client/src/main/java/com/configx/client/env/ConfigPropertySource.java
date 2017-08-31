/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.env;

import com.configx.client.item.ConfigItem;
import com.configx.client.item.ConfigItemList;
import com.configx.client.item.ConfigItemSelector;
import com.configx.client.item.DefaultConfigItemSelector;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * {@link PropertySource} that reads property from a {@link ConfigItemList} object.
 *
 * @author Zhirong Zou
 */
public class ConfigPropertySource extends EnumerablePropertySource<ConfigItemList> {

    /**
     * A selector select item from multiple config items
     */
    private ConfigItemSelector selector = new DefaultConfigItemSelector();

    public ConfigPropertySource(String name) {
        super(name, new ConfigItemList());
    }

    public ConfigPropertySource(String name, ConfigItemList itemList) {
        super(name, itemList);
    }

    public void setItemSelector(ConfigItemSelector selector) {
        this.selector = selector;
    }

    @Override
    public String[] getPropertyNames() {
        return StringUtils.toStringArray(this.source.keys());
    }

    @Override
    public String getProperty(String name) {
        Collection<ConfigItem> itemList = this.source.get(name);
        if (itemList == null || itemList.isEmpty()) {
            return null;
        }
        return selector.select(name, itemList);
    }

}
