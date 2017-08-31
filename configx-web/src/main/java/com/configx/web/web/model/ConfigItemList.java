/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.model;

import com.configx.web.model.BuildConfigItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置项列表
 *
 * @author zhirong.zou
 */
@Data
public class ConfigItemList {

    private long version;

    private String app;

    private String env;

    private String profiles;

    private List<ConfigItem> items;

    public ConfigItemList(long version, String app, String env, String profiles, List<BuildConfigItem> items) {
        this.version = version;
        this.app = app;
        this.env = env;
        this.profiles = profiles;
        this.items = transform(items);
    }

    private List<ConfigItem> transform(List<BuildConfigItem> buildConfigItems) {
        List<ConfigItem> list = new ArrayList<>();
        if (buildConfigItems == null) {
            return list;
        }
        for (BuildConfigItem item : buildConfigItems) {
            list.add(new ConfigItem(item));
        }
        return list;
    }

    @Data
    private static class ConfigItem {
        /**
         * Profile ID
         */
        private int profileId;

        /**
         * 修订版本号
         */
        private long revision;

        /**
         * 配置ID
         */
        private long id;

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

        public ConfigItem(BuildConfigItem item) {
            this.profileId = item.getProfileId();
            this.revision = item.getRevision();
            this.id = item.getConfigId();
            this.name = item.getConfigName();
            this.value = item.getConfigValue();
            this.tags = item.getConfigTags();
            this.description = "";
        }
    }

}
