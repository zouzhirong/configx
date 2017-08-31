/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.config;

import com.configx.web.dao.ConfigItemHistoryMapper;
import com.configx.web.service.app.EnvService;
import com.configx.web.service.app.AppService;
import com.configx.web.service.app.ProfileService;
import com.configx.web.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 配置项版本历史Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class ConfigItemHistoryService {
    @Autowired
    private ConfigItemHistoryMapper configItemHistoryMapper;

    @Autowired
    private AppService appService;

    @Autowired
    private EnvService envService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ConfigValueService valueService;

    /**
     * 交换回配置项中的配置值
     *
     * @param configItemHistory
     * @return
     */
    private ConfigItemHistory swapInValue(ConfigItemHistory configItemHistory) {
        if (configItemHistory == null) {
            return configItemHistory;
        }
        long valueId = configItemHistory.getConfigValueId();
        if (valueId == 0) {
            return configItemHistory;
        }
        String value = valueService.getValue(valueId);
        configItemHistory.setConfigValue(value);
        return configItemHistory;
    }

    /**
     * 交换回配置项中的配置值
     *
     * @param configItemHistoryList
     * @return
     */
    private List<ConfigItemHistory> swapInValue(List<ConfigItemHistory> configItemHistoryList) {
        if (configItemHistoryList == null || configItemHistoryList.isEmpty()) {
            return configItemHistoryList;
        }

        // 配置值ID列表
        List<Long> valueIdList = new ArrayList<>();

        // Map: config item history value id --> config item history
        Map<Long, ConfigItemHistory> valueIdConfigItemHistoryMap = new HashMap<>();

        long valueId = 0;
        for (ConfigItemHistory configItemHistory : configItemHistoryList) {
            valueId = configItemHistory.getConfigValueId();
            if (valueId > 0) {
                valueIdList.add(valueId);
                valueIdConfigItemHistoryMap.put(valueId, configItemHistory);
            }
        }

        // 配置值Map: value id --> value string
        Map<Long, String> valueMap = valueService.getValueMap(valueIdList);

        String value = null;
        for (Map.Entry<Long, ConfigItemHistory> entry : valueIdConfigItemHistoryMap.entrySet()) {
            value = valueMap.get(entry.getKey());
            entry.getValue().setConfigValue(value);
        }

        return configItemHistoryList;
    }

    /**
     * 获取指定配置项的修订版本信息
     *
     * @param configId
     * @param revision
     * @return
     */
    public ConfigItemHistory getHistory(long configId, long revision) {
        ConfigItemHistory bean = configItemHistoryMapper.getByConfigRevision(configId, revision);
        return swapInValue(bean);
    }

    /**
     * 获取指定配置项的修订版本信息
     *
     * @param configName
     * @param revision
     * @return
     */
    public ConfigItemHistory getHistory(String configName, long revision) {
        ConfigItemHistory bean = configItemHistoryMapper.getByConfigNameRevision(configName, revision);
        return swapInValue(bean);
    }

    /**
     * 获取指定修订版本的信息
     *
     * @param appId
     * @param revision
     * @return
     */
    public ConfigItemHistory getRevisionHistory(int appId, long revision) {
        List<ConfigItemHistory> list = configItemHistoryMapper.getByRevision(revision);
        list = swapInValue(list);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            ConfigItemHistory configItemHistory = list.get(0);
            if (configItemHistory == null || configItemHistory.getAppId() != appId) {
                return null;
            } else {
                return configItemHistory;
            }
        }
    }

    /**
     * 获取指定修订版本的信息
     *
     * @param revision
     * @return
     */
    public List<ConfigItemHistory> getHistoryOfRevision(long revision) {
        List<ConfigItemHistory> list = configItemHistoryMapper.getByRevision(revision);
        return swapInValue(list);
    }

    /**
     * 获取指定的多个修订
     *
     * @param revisions
     * @return
     */
    public List<ConfigItemHistory> getHistorys(List<Long> revisions) {
        if (revisions == null || revisions.isEmpty()) {
            return Collections.emptyList();
        }
        List<ConfigItemHistory> list = configItemHistoryMapper.getByRevisions(revisions);
        return swapInValue(list);
    }

    /**
     * 获取指定应用的所有修订信息
     *
     * @param appId
     * @return
     */
    public List<ConfigItemHistory> getHistorys(int appId) {
        List<ConfigItemHistory> list = configItemHistoryMapper.getByAppId(appId);
        return swapInValue(list);
    }

    /**
     * 获取指定应用和环境的所有修订信息
     *
     * @param appId
     * @param envId
     * @return
     */
    public List<ConfigItemHistory> getHistorys(int appId, int envId) {
        List<ConfigItemHistory> list = configItemHistoryMapper.getByAppAndEnv(appId, envId);
        return swapInValue(list);
    }

    /**
     * 获取指定配置项的修订信息
     *
     * @param configId
     * @return
     */
    public List<ConfigItemHistory> getHistorys(long configId) {
        List<ConfigItemHistory> list = configItemHistoryMapper.getByConfigId(configId);
        return swapInValue(list);
    }

    /**
     * 获取指定配置名的修订信息
     *
     * @param configName
     * @return
     */
    public List<ConfigItemHistory> getHistorys(String configName) {
        List<ConfigItemHistory> list = configItemHistoryMapper.getByConfigName(configName);
        return swapInValue(list);
    }

    /**
     * 从配置历史列表中获取版本列表
     *
     * @param list
     * @return
     */
    public List<Long> getHistoryRevisionList(List<ConfigItemHistory> list) {
        List<Long> revisionList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return revisionList;
        }
        for (ConfigItemHistory history : list) {
            revisionList.add(history.getRevision());
        }
        return revisionList;
    }

    /**
     * 当添加配置项时
     *
     * @param revision
     * @param configItem
     */
    public void onConfigItemAdded(long revision, ConfigItem configItem) {
        // 环境信息
        Env env = envService.getEnv(configItem.getEnvId());

        // 应用信息
        App app = appService.getApp(configItem.getAppId());

        // 配置项的Profile信息
        Profile profile = configItem.getProfileId() == 0 ? null : profileService.getProfile(configItem.getProfileId());

        // 创建配置项历史
        createConfigItemHistory(revision, app, env, profile, configItem, configItem.getValue());
    }

    /**
     * 当修改配置项时
     *
     * @param revision
     * @param configItem
     * @param newValue
     */
    public void onConfigItemModified(long revision, ConfigItem configItem, String newValue) {
        // 环境信息
        Env env = envService.getEnv(configItem.getEnvId());

        // 应用信息
        App app = appService.getApp(configItem.getAppId());

        // 配置项的Profile信息
        Profile profile = configItem.getProfileId() == 0 ? null : profileService.getProfile(configItem.getProfileId());

        // 创建配置项历史
        createConfigItemHistory(revision, app, env, profile, configItem, newValue);

    }

    /**
     * 当删除配置项时
     *
     * @param revision
     * @param configItem
     */
    public void onConfigItemDeleted(long revision, ConfigItem configItem) {
        // 环境信息
        Env env = envService.getEnv(configItem.getEnvId());

        // 应用信息
        App app = appService.getApp(configItem.getAppId());

        // 配置项的Profile信息
        Profile profile = configItem.getProfileId() == 0 ? null : profileService.getProfile(configItem.getProfileId());

        // 创建配置项历史
        createConfigItemHistory(revision, app, env, profile, configItem, "");

    }

    /**
     * 创建配置项历史信息
     *
     * @param revision
     * @param app
     * @param env
     * @param profile
     * @param configItem
     * @param value
     * @return
     */
    public ConfigItemHistory createConfigItemHistory(long revision, App app, Env env, Profile profile, ConfigItem configItem, String value) {
        ConfigItemHistory revisionInfo = newConfigItemHistory(revision, app, env, profile, configItem, value);
        createConfigItemHistory(revisionInfo);

        configService.onDataChange(configItem.getId(), revisionInfo.getRevision());
        envService.updateRevisionInfo(env.getId(), revisionInfo.getRevision());
        return revisionInfo;
    }

    /**
     * 创建新的{@link com.configx.web.model.ConfigItemHistory}对象
     *
     * @param revision
     * @param app
     * @param env
     * @param profile
     * @param configItem
     * @param value
     * @return
     */
    public ConfigItemHistory newConfigItemHistory(long revision, App app, Env env, Profile profile, ConfigItem configItem, String value) {
        ConfigItemHistory revisionInfo = new ConfigItemHistory();

        revisionInfo.setRevision(revision);
        revisionInfo.setLastRevision(configItem.getRevision());

        revisionInfo.setAppId(app.getId());
        revisionInfo.setAppName(app.getName());

        revisionInfo.setEnvId(env.getId());
        revisionInfo.setEnvName(env.getName());

        if (profile != null) {
            revisionInfo.setProfileId(profile.getId());
            revisionInfo.setProfileName(profile.getName());
        } else {
            revisionInfo.setProfileId(0);
            revisionInfo.setProfileName("");
        }

        revisionInfo.setConfigId(configItem.getId());
        revisionInfo.setConfigName(configItem.getName());
        revisionInfo.setConfigValue(value);
        revisionInfo.setConfigValueId(configItem.getValueId());
        revisionInfo.setConfigValueType(configItem.getValueType());
        revisionInfo.setConfigTags(configItem.getTags());

        revisionInfo.setCreateTime(new Date());

        return revisionInfo;
    }

    /**
     * 创建配置项历史
     *
     * @param revisionInfo
     */
    private void createConfigItemHistory(ConfigItemHistory revisionInfo) {
        configItemHistoryMapper.insertSelective(revisionInfo);
    }

    /**
     * 获取两个修订版之间的差异
     *
     * @param appId
     * @param envId
     * @param fromRevision
     * @param toRevision
     * @return
     */
    public List<ConfigItemHistory> getChangeList(int appId, int envId, long fromRevision, long toRevision) {
        List<ConfigItemHistory> list = configItemHistoryMapper.getByRevisionRange(appId, envId, fromRevision, toRevision);
        return swapInValue(list);
    }
}
