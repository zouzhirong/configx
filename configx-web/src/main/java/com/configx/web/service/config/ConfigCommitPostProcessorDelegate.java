/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.config;

import com.configx.web.support.ApplicationContextHelper;
import com.configx.web.event.ConfigChangeEvent;
import com.configx.web.model.ConfigItem;

import java.util.List;

/**
 * {@link com.configx.web.model.ConfigItem} 处理
 *
 * @author Zhirong Zou
 */
public class ConfigCommitPostProcessorDelegate {

    public static void applyListenersOnAdded(List<ConfigCommitPostProcessor> listeners, long revision, ConfigItem configItem) {
        for (ConfigCommitPostProcessor listener : listeners) {
            listener.onConfigItemAdded(revision, configItem);
        }

        // 发布事件
        ApplicationContextHelper.getContext().publishEvent(new ConfigChangeEvent(configItem, configItem));
    }

    public static void applyListenersOnModified(List<ConfigCommitPostProcessor> listeners, long revision, ConfigItem configItem, String newValue) {
        for (ConfigCommitPostProcessor listener : listeners) {
            listener.onConfigItemModified(revision, configItem, newValue);
        }

        // 发布事件
        ApplicationContextHelper.getContext().publishEvent(new ConfigChangeEvent(configItem, configItem));
    }

    public static void applyListenersOnDeleted(List<ConfigCommitPostProcessor> listeners, long revision, ConfigItem configItem) {
        for (ConfigCommitPostProcessor listener : listeners) {
            listener.onConfigItemDeleted(revision, configItem);
        }

        // 发布事件
        ApplicationContextHelper.getContext().publishEvent(new ConfigChangeEvent(configItem, configItem));
    }
}
