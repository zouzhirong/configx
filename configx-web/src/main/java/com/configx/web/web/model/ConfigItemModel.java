/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.model;

import com.configx.web.support.ApplicationContextHelper;
import com.configx.web.service.app.ProfileService;
import com.configx.web.model.*;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * 配置项信息
 *
 * @author Zhirong Zou
 */
@Data
public class ConfigItemModel {

    /**
     * 配置ID
     */
    private long id;

    /**
     * 应用ID
     */
    private int appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用
     */
    private App app;

    /**
     * 环境ID
     */
    private int envId;

    /**
     * 环境名称
     */
    private String envName;

    /**
     * 环境
     */
    private Env env;

    /**
     * Profile ID
     */
    private int profileId;

    /**
     * Profile名称
     */
    private String profileName;

    /**
     * Profile
     */
    private Profile profile;

    /**
     * 配置名
     */
    private String name;

    /**
     * 配置值
     */
    private String value;

    /**
     * 配置值类型
     */
    private int valueType;

    /**
     * 标签列表
     */
    private List<Tag> tagList;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否启用
     */
    private boolean enable;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updater;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 最新修订版本号
     */
    private long revision;

    /**
     * 上次修订版本号
     */
    private long lastRevision;

    /**
     * 数据最新修改时间
     */
    private Date dataChangeLastTime;

    public ConfigItemModel(App app, Env env, Profile profile, ConfigItem configItem, List<Tag> tagList) {
        this.id = configItem.getId();
        this.appId = configItem.getAppId();
        this.appName = app.getName();
        this.app = app;
        this.envId = configItem.getEnvId();
        this.envName = env.getName();
        this.env = env;
        this.profileId = configItem.getProfileId();
        if (profile != null) {
            this.profileName = profile.getName();
        }
        this.profile = profile;
        this.name = configItem.getName();
        this.value = configItem.getValue();
        this.valueType = configItem.getValueType();
        this.tagList = tagList;
        this.description = configItem.getDescription();
        this.enable = configItem.getEnable();
        this.creator = configItem.getCreator();
        this.createTime = configItem.getCreateTime();
        this.updater = configItem.getUpdater();
        this.updateTime = configItem.getUpdateTime();
        this.revision = configItem.getRevision();
        this.lastRevision = configItem.getLastRevision();
        this.dataChangeLastTime = configItem.getDataChangeLastTime();
    }

    public List<Integer> getTagIdList() {
        List<Integer> tagIdList = new ArrayList<>();

        if (tagList == null || tagList.isEmpty()) {
            return tagIdList;
        }
        for (Tag tag : tagList) {
            tagIdList.add(tag.getId());
        }

        return tagIdList;
    }

    public static ConfigItemModel wrap(App app, Env env, ConfigItem configItem, List<Profile> profileList, List<Tag> tagList) {
        if (configItem == null) {
            return null;
        }
        Profile profile = findProfile(app.getId(), profileList, configItem);
        List<Tag> itemTagList = findTagList(tagList, configItem);
        return new ConfigItemModel(app, env, profile, configItem, itemTagList);
    }

    public static List<ConfigItemModel> wrap(List<ConfigItem> configItemList, App app, List<Env> envList, List<Profile> profileList, List<Tag> tagList) {
        List<ConfigItemModel> resultList = new ArrayList<>();

        Env env = null;
        Profile profile = null;
        List<Tag> itemTagList = null;
        for (ConfigItem configItem : configItemList) {
            env = findEnv(envList, configItem);
            profile = findProfile(app.getId(), profileList, configItem);
            if (env == null || profile == null) { // 环境或profile已删除
                continue;
            }
            itemTagList = findTagList(tagList, configItem);
            resultList.add(new ConfigItemModel(app, env, profile, configItem, itemTagList));
        }
        return resultList;
    }

    private static Env findEnv(List<Env> envList, ConfigItem configItem) {
        if (envList == null || envList.isEmpty()) {
            return null;
        }
        for (Env env : envList) {
            if (env.getId() == configItem.getEnvId()) {
                return env;
            }
        }
        return null;
    }

    private static Profile findProfile(int appId, List<Profile> profileList, ConfigItem configItem) {
        if (profileList == null || profileList.isEmpty()) {
            return null;
        }
        for (Profile profile : profileList) {
            if (profile.getId() == configItem.getProfileId()) {
                return profile;
            }
        }
        return null;
    }

    private static List<Tag> findTagList(List<Tag> tagList, ConfigItem configItem) {
        if (StringUtils.isEmpty(configItem.getTags())) {
            return Collections.emptyList();
        }
        if (tagList == null || tagList.isEmpty()) {
            return Collections.emptyList();
        }

        String[] tagIdArray = StringUtils.split(configItem.getTags(), ",");
        Set<String> tagIdSet = new HashSet<>();
        Collections.addAll(tagIdSet, tagIdArray);

        List<Tag> itemTagList = new ArrayList<>();
        for (Tag tag : tagList) {
            if (tagIdSet.contains(String.valueOf(tag.getId()))) {
                itemTagList.add(tag);
            }
        }
        return itemTagList;
    }

    private static ProfileService getProfileService() {
        return ApplicationContextHelper.getBean(ProfileService.class);
    }
}
