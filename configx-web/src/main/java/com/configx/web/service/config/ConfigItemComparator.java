/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.config;

import com.configx.web.model.ConfigItem;

import java.util.Comparator;

/**
 * 配置项排序
 *
 * @author Zhirong Zou
 */
public class ConfigItemComparator implements Comparator<ConfigItem> {

    @Override
    public int compare(ConfigItem o1, ConfigItem o2) {
        long time1 = Math.max(o1.getCreateTime().getTime(), o1.getUpdateTime().getTime());
        long time2 = Math.max(o2.getCreateTime().getTime(), o2.getUpdateTime().getTime());
        return Long.valueOf(time2).compareTo(time1);
    }

}
