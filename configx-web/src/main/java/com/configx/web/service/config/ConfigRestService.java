/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.config;

import com.configx.web.model.*;
import com.configx.web.service.app.*;
import com.configx.web.service.build.BuildService;
import com.configx.web.service.release.version.ReleaseVersionContants;
import com.configx.web.service.release.version.ReleaseVersionService;
import com.configx.web.web.model.ConfigItemList;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 配置Rest Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class ConfigRestService {

    @Autowired
    private AppService appService;

    @Autowired
    private EnvService envService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private TagService tagService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private ReleaseVersionService releaseVersionService;

    /**
     * 获取最新配置列表
     *
     * @param appId
     * @param appKey
     * @param envName
     * @param profiles
     * @return
     */
    public ConfigItemList getHeadConfigList(int appId, String appKey, String envName, String profiles) {
        // 应用环境信息
        App app = appService.getApp(appId, appKey);
        if (app == null) {
            return new ConfigItemList(0, appId + "", envName, profiles, null);
        }

        // 重新获取App ID
        appId = app.getId();

        Env env = envService.getEnv(appId, envName);
        List<Integer> profileIdList = profileService.getOrderedProfileIdList(appId, profiles);
        // 添加默认Profile
        profileIdList.add(ProfileContants.DEFAULT_PROFILE_ID);

        // 最新发布的版本
        ReleaseVersion latestVersion = releaseVersionService.getLatestVersion(appId, env.getId());
        if (latestVersion == null) {
            return new ConfigItemList(0, appId + "", envName, profiles, null);
        }

        // 发布版本对应的构建的配置列表
        List<BuildConfigItem> buildConfigItemList = buildService.getConfigItemList(latestVersion.getBuildId());

        buildConfigItemList = filter(appId, env.getId(), profileIdList, buildConfigItemList);

        return new ConfigItemList(latestVersion.getNumber(), appId + "", envName, profiles, buildConfigItemList);
    }

    /**
     * 更新配置
     *
     * @param appId
     * @param appKey
     * @param envName
     * @param profiles
     * @param from
     * @param to
     * @return
     */
    public ConfigItemList update(int appId, String appKey, String envName, String profiles, long from, String to) {
        // 应用环境信息
        App app = appService.getApp(appId, appKey);
        if (app == null) {
            return new ConfigItemList(0, appId + "", envName, profiles, null);
        }

        // 重新获取App ID
        appId = app.getId();

        Env env = envService.getEnv(appId, envName);
        List<Integer> profileIdList = profileService.getOrderedProfileIdList(appId, profiles);
        // 添加默认Profile
        profileIdList.add(ProfileContants.DEFAULT_PROFILE_ID);

        // 最新发布的版本
        ReleaseVersion latestVersion = releaseVersionService.getLatestVersion(appId, env.getId());
        if (latestVersion == null) {
            return new ConfigItemList(0, appId + "", envName, profiles, null);
        }

        long toVersion = 0;
        if (ReleaseVersionContants.HEAD_VERSION.equals(to)) {
            toVersion = latestVersion.getNumber();
        } else {
            toVersion = Long.valueOf(to);
        }

        // 更新前的发布版本信息
        ReleaseVersion fromReleaseVersion = releaseVersionService.getVersion(appId, env.getId(), from);

        // 更新到的发布版本信息
        ReleaseVersion toReleaseVersion = releaseVersionService.getVersion(appId, env.getId(), toVersion);

        // 两个版本之间的Change List
        List<BuildConfigItem> buildConfigItemList = buildService.getChangedConfigItemList(appId, env.getId(),
                fromReleaseVersion.getBuildId(), toReleaseVersion.getBuildId(), profileIdList);

        buildConfigItemList = filter(appId, env.getId(), profileIdList, buildConfigItemList);

        return new ConfigItemList(latestVersion.getNumber(), appId + "", envName, profiles, buildConfigItemList);
    }

    /**
     * 过滤配置项列表
     *
     * @param appId
     * @param envId
     * @param profileIdList
     * @param configItemList
     * @return
     */
    private List<BuildConfigItem> filter(int appId, int envId, List<Integer> profileIdList, List<BuildConfigItem> configItemList) {
        // 获取应用的标签列表
        List<Tag> tagList = tagService.getTagList(appId);

        // 按Profile优先级覆盖，不同的Profile根据配置名覆盖，Profile的优先级顺序与指定顺序一致
        configItemList = groupByProfile(configItemList, profileIdList);

        // 替换配置项中的标签ID为标签名，标签名之间用逗号隔开
        populateTags(configItemList, tagList);

        return configItemList;
    }

    /**
     * 根据Profile分组，同名配置，根据Profile的优先级覆盖
     *
     * @param configItemList
     * @param profileIdList
     * @return
     */
    private List<BuildConfigItem> groupByProfile(List<BuildConfigItem> configItemList, List<Integer> profileIdList) {
        List<BuildConfigItem> filteredConfigItemList = new ArrayList<>();

        Table<String, Integer, List<BuildConfigItem>> configItemTable = getConfigItemTable(configItemList);

        for (String itemName : configItemTable.rowKeySet()) {
            for (int profileId : profileIdList) {
                List<BuildConfigItem> itemList = configItemTable.get(itemName, profileId);
                if (itemList != null && !itemList.isEmpty()) {
                    filteredConfigItemList.addAll(itemList);
                    break;
                }
            }
        }

        return filteredConfigItemList;
    }

    /**
     * 将配置项构造成一个二维表，[配置名称, Profile ID, 配置项]
     *
     * @param configItemList
     * @return
     */
    private Table<String, Integer, List<BuildConfigItem>> getConfigItemTable(List<BuildConfigItem> configItemList) {
        Table<String, Integer, List<BuildConfigItem>> configItemTable = HashBasedTable.create();

        List<BuildConfigItem> listByNameAndProfile = null;
        for (BuildConfigItem configItem : configItemList) {
            listByNameAndProfile = configItemTable.get(configItem.getConfigName(), configItem.getProfileId());
            if (listByNameAndProfile == null) {
                listByNameAndProfile = new ArrayList<>();
                configItemTable.put(configItem.getConfigName(), configItem.getProfileId(), listByNameAndProfile);
            }
            listByNameAndProfile.add(configItem);
        }

        return configItemTable;
    }

    /**
     * 填充配置项中的标签
     *
     * @param configItemList
     * @param tagList
     * @return
     */
    private List<BuildConfigItem> populateTags(List<BuildConfigItem> configItemList, List<Tag> tagList) {
        for (BuildConfigItem configItem : configItemList) {
            configItem.setConfigTags(replaceTags(configItem, tagList));
        }
        return configItemList;
    }

    /**
     * 将配置项中的标签引用替换成标签名称
     *
     * @param configItem
     * @param tagList
     * @return
     */
    private String replaceTags(BuildConfigItem configItem, List<Tag> tagList) {
        if (StringUtils.isEmpty(configItem.getConfigTags())) {
            return "";
        }

        List<Tag> itemTagList = findTagList(tagList, configItem);

        List<String> tagNames = new ArrayList<>();

        for (Tag tag : itemTagList) {
            tagNames.add(tag.getName());
        }

        return StringUtils.join(tagNames, ",");
    }

    /**
     * 查找指定配置项的标签信息
     *
     * @param tagList
     * @param configItem
     * @return
     */
    private static List<Tag> findTagList(List<Tag> tagList, BuildConfigItem configItem) {
        if (StringUtils.isEmpty(configItem.getConfigTags())) {
            return Collections.emptyList();
        }
        if (tagList == null || tagList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Tag> itemTagList = new ArrayList<>();
        for (Tag tag : tagList) {
            for (String tagId : StringUtils.split(configItem.getConfigTags(), ",")) {
                if (tag.getId() == Integer.valueOf(tagId)) {
                    itemTagList.add(tag);
                }
            }
        }
        return itemTagList;
    }
}
