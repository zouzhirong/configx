/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 配置项列表
 *
 * @author zhirong.zou
 */
@ToString
public class ConfigItemList {
    /**
     * 版本
     */
    private long version;

    /**
     * 应用
     */
    private String app;

    /**
     * 环境
     */
    private String env;

    /**
     * profile列表，多个用逗号分隔
     */
    private String profiles;

    /**
     * 配置项列表
     */
    private List<ConfigItem> items;

    /**
     * 配置项Map，configName--> ConfigItem
     */
    private Multimap<String, ConfigItem> map = ArrayListMultimap.create();

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getProfiles() {
        return profiles;
    }

    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    public List<ConfigItem> getItems() {
        return items;
    }

    public void setItems(List<ConfigItem> items) {
        this.items = items;
        this.map.clear();
        this.put(items);
    }

    public void addItem(ConfigItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        this.map.put(item.getName(), item);
    }

    /**
     * 返回是否包含指定key的配置项
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return (get(key) != null);
    }

    /**
     * 返回所有key集合
     *
     * @return
     */
    public Set<String> keys() {
        return map.keySet();
    }

    /**
     * 获取配置项
     *
     * @param key
     * @return
     */
    public Collection<ConfigItem> get(String key) {
        return map.get(key);
    }

    /**
     * 添加配置项列表
     *
     * @param itemList
     */
    private void put(List<ConfigItem> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return;
        }
        for (ConfigItem item : itemList) {
            map.put(item.getName(), item);
        }
    }

}
