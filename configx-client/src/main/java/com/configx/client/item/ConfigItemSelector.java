package com.configx.client.item;

import java.util.Collection;

/**
 * 配置项选择器
 * 如果同一个Key，存在多个配置项，从中选择一个配置项的值
 *
 * @author Zhirong Zou
 */
public interface ConfigItemSelector {

    /**
     * 选择配置值
     *
     * @param key
     * @param itemList
     * @return
     */
    public String select(String key, Collection<ConfigItem> itemList);

}
