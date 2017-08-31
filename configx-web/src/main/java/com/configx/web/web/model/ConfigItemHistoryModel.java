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
 * 配置项历史信息
 *
 * @author Zhirong Zou
 */
@Data
public class ConfigItemHistoryModel {

    /**
     * 修订号
     */
    private long revision;

    /**
     * 上一个修订版本号
     */
    private long lastRevision;

    /**
     * 应用
     */
    private App app;

    /**
     * 环境
     */
    private Env env;

    /**
     * Profile
     */
    private Profile profile;

    /**
     * 配置ID
     */
    private long configId;

    /**
     * 配置名
     */
    private String configName;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置值类型
     */
    private int valueType;

    /**
     * 标签列表
     */
    private List<Tag> tagList;

    /**
     * 创建时间
     */
    private Date createTime;

    public ConfigItemHistoryModel(App app, Env env, Profile profile, ConfigItemHistory history, List<Tag> tagList) {
        this.revision = history.getRevision();
        this.lastRevision = history.getLastRevision();
        this.app = app;
        this.env = env;
        this.profile = profile;
        this.configId = history.getConfigId();
        this.configName = history.getConfigName();
        this.configValue = history.getConfigValue();
        this.tagList = tagList;
        this.createTime = history.getCreateTime();
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

    public static ConfigItemHistoryModel wrap(App app, Env env, ConfigItemHistory history, List<Profile> profileList, List<Tag> tagList) {
        if (history == null) {
            return null;
        }
        Profile profile = findProfile(history.getAppId(), profileList, history);
        List<Tag> itemTagList = findTagList(tagList, history);
        return new ConfigItemHistoryModel(app, env, profile, history, itemTagList);
    }

    public static List<ConfigItemHistoryModel> wrap(List<ConfigItemHistory> historyList, App app, List<Env> envList, List<Profile> profileList, List<Tag> tagList) {
        List<ConfigItemHistoryModel> resultList = new ArrayList<>();

        Env env = null;
        Profile profile = null;
        List<Tag> itemTagList = null;
        for (ConfigItemHistory e : historyList) {
            env = findEnv(envList, e);
            profile = findProfile(e.getAppId(), profileList, e);
            itemTagList = findTagList(tagList, e);
            resultList.add(new ConfigItemHistoryModel(app, env, profile, e, itemTagList));
        }
        return resultList;
    }

    private static Env findEnv(List<Env> envList, ConfigItemHistory history) {
        if (envList == null || envList.isEmpty()) {
            return null;
        }
        for (Env env : envList) {
            if (env.getId() == history.getEnvId()) {
                return env;
            }
        }
        return null;
    }

    private static Profile findProfile(int appId, List<Profile> profileList, ConfigItemHistory history) {
        if (profileList == null || profileList.isEmpty()) {
            return null;
        }
        for (Profile profile : profileList) {
            if (profile.getId() == history.getProfileId()) {
                return profile;
            }
        }
        return null;
    }

    private static List<Tag> findTagList(List<Tag> tagList, ConfigItemHistory history) {
        if (StringUtils.isEmpty(history.getConfigTags())) {
            return Collections.emptyList();
        }
        if (tagList == null || tagList.isEmpty()) {
            return Collections.emptyList();
        }

        String[] tagIdArray = StringUtils.split(history.getConfigTags(), ",");
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
