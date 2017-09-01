/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.item;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 配置项
 *
 * @author Zhirong Zou
 */
public class ConfigItem {

    /**
     * 配置名
     */
    private String name;

    /**
     * 配置值
     */
    private String value;

    /**
     * 标签列表，多个标签之间用逗号隔开
     */
    private String tags;

    /**
     * 描述
     */
    private String description;

    public ConfigItem() {

    }

    public ConfigItem(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 返回标签列表
     *
     * @return
     */
    public List<String> getTagList() {
        if (StringUtils.isEmpty(tags)) {
            return Collections.emptyList();
        }
        String[] tagArray = StringUtils.split(tags, ",");
        return Arrays.asList(tagArray);
    }
}
