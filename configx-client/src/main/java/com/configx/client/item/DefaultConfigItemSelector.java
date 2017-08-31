/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.item;

import java.util.Collection;

/**
 * 默认配置项选择器
 *
 * @author Zhirong Zou
 */
public class DefaultConfigItemSelector implements ConfigItemSelector {

    @Override
    public String select(String key, Collection<ConfigItem> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return null;
        }
        if (itemList.size() == 1) {
            return itemList.iterator().next().getValue();
        } else {
            return itemList.iterator().next().getValue();
        }
    }

}
