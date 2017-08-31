/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.item;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 配置项
 *
 * @author Zhirong Zou
 */
@Data
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
