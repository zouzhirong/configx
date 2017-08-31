/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.config;

import com.configx.web.dao.ConfigCommitMapper;
import com.configx.web.service.app.AppService;
import com.configx.web.service.app.EnvService;
import com.configx.web.service.app.ProfileService;
import com.configx.web.service.user.UserContext;
import com.configx.web.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 配置提交 Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class ConfigCommitService {

    /**
     * 配置项监听器
     */
    private List<ConfigCommitPostProcessor> listeners = new ArrayList<>();

    @Autowired
    private ConfigCommitMapper configCommitMapper;

    @Autowired
    private AppService appService;

    @Autowired
    private EnvService envService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ConfigItemHistoryService configItemHistoryService;

    /**
     * 添加配置项监听器
     *
     * @param listener
     */
    public void addListener(ConfigCommitPostProcessor listener) {
        this.listeners.add(listener);
    }

    /**
     * 返回配置项监听器列表
     *
     * @return
     */
    public List<ConfigCommitPostProcessor> getListeners() {
        return listeners;
    }

    /**
     * 获取提交日志
     *
     * @param appId
     * @param envId
     * @return
     */
    public List<ConfigCommit> getCommits(int appId, int envId) {
        return configCommitMapper.selectByAppEnv(appId, envId);
    }

    /**
     * 获取指定的提交日志
     *
     * @param revision
     * @return
     */
    public ConfigCommit getCommit(long revision) {
        return configCommitMapper.selectByPrimaryKey(revision);
    }

    /**
     * 获取指定版本的提交日志列表
     *
     * @param revisionList
     * @return
     */
    public List<ConfigCommit> getCommitList(List<Long> revisionList) {
        if (CollectionUtils.isEmpty(revisionList)) {
            return Collections.emptyList();
        }
        return configCommitMapper.selectByIds(revisionList);
    }

    /**
     * 提交配置项添加动作
     *
     * @param configItem
     * @param message
     */
    public void commitConfigItemAdded(ConfigItem configItem, String message) {
        // 环境信息
        Env env = envService.getEnv(configItem.getEnvId());

        // 应用信息
        App app = appService.getApp(configItem.getAppId());

        // 配置项的Profile信息
        Profile profile = configItem.getProfileId() == 0 ? null : profileService.getProfile(configItem.getProfileId());

        // 创建提交日志
        ConfigCommit commitLog = createCommitLog(app, env, profile, UserContext.name(), new Date(), message);

        // 创建配置项历史
        configItemHistoryService.onConfigItemAdded(commitLog.getRevision(), configItem);

        // 应用 ConfigItemListeners
        ConfigCommitPostProcessorDelegate.applyListenersOnAdded(getListeners(), commitLog.getRevision(), configItem);
    }

    /**
     * 提交配置项修改动作
     *
     * @param configItem
     * @param newValue
     * @param message
     */
    public void commitConfigItemModified(ConfigItem configItem, String newValue, String message) {
        // 环境信息
        Env env = envService.getEnv(configItem.getEnvId());

        // 应用信息
        App app = appService.getApp(configItem.getAppId());

        // 配置项的Profile信息
        Profile profile = configItem.getProfileId() == 0 ? null : profileService.getProfile(configItem.getProfileId());

        // 创建提交日志
        ConfigCommit commitLog = createCommitLog(app, env, profile, UserContext.name(), new Date(), message);

        // 创建配置项历史
        configItemHistoryService.onConfigItemModified(commitLog.getRevision(), configItem, newValue);

        // 应用 ConfigItemListeners
        ConfigCommitPostProcessorDelegate.applyListenersOnModified(getListeners(), commitLog.getRevision(), configItem, newValue);
    }

    /**
     * 提交配置项删除动作
     *
     * @param configItem
     * @param message
     */
    public void commitConfigItemDeleted(ConfigItem configItem, String message) {
        // 环境信息
        Env env = envService.getEnv(configItem.getEnvId());

        // 应用信息
        App app = appService.getApp(configItem.getAppId());

        // 配置项的Profile信息
        Profile profile = configItem.getProfileId() == 0 ? null : profileService.getProfile(configItem.getProfileId());

        // 创建提交日志
        ConfigCommit commitLog = createCommitLog(app, env, profile, UserContext.name(), new Date(), message);

        // 创建配置项历史
        configItemHistoryService.onConfigItemDeleted(commitLog.getRevision(), configItem);

        // 应用 ConfigItemListeners
        ConfigCommitPostProcessorDelegate.applyListenersOnDeleted(getListeners(), commitLog.getRevision(), configItem);
    }

    /**
     * 创建提交日志
     *
     * @param app
     * @param env
     * @param profile
     * @param author
     * @param date
     * @param message
     * @return
     */
    public ConfigCommit createCommitLog(App app, Env env, Profile profile, String author, Date date, String message) {
        ConfigCommit commitLog = newCommitLog(app, env, profile, author, date, message);
        createConfigCommitLog(commitLog);
        return commitLog;
    }

    /**
     * 创建新{@link ConfigCommit}项对象
     *
     * @param app
     * @param env
     * @param profile
     * @param author
     * @param date
     * @param message
     * @return
     */
    private ConfigCommit newCommitLog(App app, Env env, Profile profile, String author, Date date, String message) {
        ConfigCommit commitLog = new ConfigCommit();
        commitLog.setAppId(app.getId());
        commitLog.setAppName(app.getName());
        commitLog.setEnvId(env.getId());
        commitLog.setEnvName(env.getName());

        if (profile != null) {
            commitLog.setProfileId(profile.getId());
            commitLog.setProfileName(profile.getName());
        } else {
            commitLog.setProfileId(0);
            commitLog.setProfileName("");
        }

        commitLog.setAuthor(author);
        commitLog.setDate(date);
        commitLog.setMessage(message);
        return commitLog;
    }

    /**
     * 创建提交日志
     *
     * @param commitLog
     * @return
     */
    public ConfigCommit createConfigCommitLog(ConfigCommit commitLog) {
        configCommitMapper.insert(commitLog);
        return commitLog;
    }

    /**
     * 创建配置项
     *
     * @param appId
     * @param envId
     * @param profileId
     * @param name
     * @param value
     * @param valueType
     * @param tagIdList
     * @param description
     * @return
     */
    public ConfigItem createConfigItem(int appId, int envId, int profileId, String name, String value, byte valueType, List<Integer> tagIdList, String description
            , String message) {
        ConfigItem configItem = configService.createConfigItem(appId, envId, profileId, name, value, valueType, tagIdList, description);

        commitConfigItemAdded(configItem, message);

        return configItem;
    }

    /**
     * 启用配置项
     *
     * @param appId
     * @param id
     * @param message
     */
    public ConfigItem enableConfigItem(int appId, long id, String message) {
        ConfigItem configItem = configService.getConfigItem(appId, id);
        if (configItem == null) {
            return configItem;
        }
        configItem = configService.enableConfigItem(id);
        commitConfigItemAdded(configItem, message);
        return configItem;
    }

    /**
     * 禁用配置项
     *
     * @param appId
     * @param id
     * @param message
     */
    public ConfigItem disableConfigItem(int appId, long id, String message) {
        ConfigItem configItem = configService.getConfigItem(appId, id);
        if (configItem == null) {
            return configItem;
        }
        configItem = configService.disableConfigItem(id);
        commitConfigItemDeleted(configItem, message);
        return configItem;
    }

    /**
     * 修改配置项
     *
     * @param appId
     * @param id
     * @param name
     * @param value
     * @param tagList
     * @param description
     * @param message
     * @return
     */
    public ConfigItem modifyConfigItem(int appId, long id, String name, String value, List<Integer> tagList, String description, String message) {
        ConfigItem configItem = configService.getConfigItem(appId, id);
        if (configItem == null) {
            return configItem;
        }
        configItem = configService.modifyConfigItem(id, name, value, tagList, description);
        commitConfigItemModified(configItem, value, message);
        return configItem;
    }

    /**
     * 删除配置项
     *
     * @param appId
     * @param id
     * @param message
     */
    public void delete(int appId, long id, String message) {
        ConfigItem configItem = configService.delete(appId, id);
        commitConfigItemDeleted(configItem, message);
    }

}
