/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.config;

import com.configx.web.model.ConfigItem;

/**
 * 配置项监听器
 *
 * @author Zhirong Zou
 */
public interface ConfigCommitPostProcessor {
    /**
     * 配置项添加时
     *
     * @param revision
     * @param configItem
     */
    public void onConfigItemAdded(long revision, ConfigItem configItem);

    /**
     * 配置项修改时
     *
     * @param revision
     * @param configItem
     * @param newValue
     */
    public void onConfigItemModified(long revision, ConfigItem configItem, String newValue);

    /**
     * 配置项删除时
     *
     * @param revision
     * @param configItem
     */
    public void onConfigItemDeleted(long revision, ConfigItem configItem);
}
