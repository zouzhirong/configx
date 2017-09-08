/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release.version;

import com.configx.web.dao.ReleaseVersionMapper;
import com.configx.web.model.*;
import com.configx.web.service.app.AppService;
import com.configx.web.service.app.EnvService;
import com.configx.web.service.build.BuildService;
import com.configx.web.service.release.ReleaseService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 发布的版本 Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class ReleaseVersionService {

    /**
     * 构建后处理器
     */
    private final List<ReleaseVersionPostProcessor> releaseVersionPostProcessors = new ArrayList<>();

    @Autowired
    private ReleaseVersionMapper releaseVersionMapper;

    @Autowired
    private AppService appService;

    @Autowired
    private EnvService envService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private ReleaseService releaseService;

    @PostConstruct
    public void init() {
        releaseService.addPostProcessor(new ReleaseVersionReleasePostProcessor(this));
    }

    /**
     * 添加后处理器
     *
     * @param releaseVersionPostProcessor
     */
    public void addPostProcessor(ReleaseVersionPostProcessor releaseVersionPostProcessor) {
        this.releaseVersionPostProcessors.add(releaseVersionPostProcessor);
    }

    /**
     * 返回后处理器
     *
     * @return
     */
    public List<ReleaseVersionPostProcessor> getReleaseVersionPostProcessors() {
        return this.releaseVersionPostProcessors;
    }

    /**
     * 创建新的发布版本
     *
     * @param appId
     * @param envId
     * @param releaseId
     * @param rollbackId
     * @return
     */
    public ReleaseVersion create(int appId, int envId, long releaseId, long rollbackId) {
        App app = appService.getApp(appId);
        Env env = envService.getEnv(envId);
        Release release = releaseService.getRelease(releaseId);
        Release rollback = releaseService.getRelease(rollbackId);

        return this.create(app, env, release, rollback);
    }

    /**
     * 创建新的发布版本
     *
     * @param app
     * @param env
     * @param release  发布信息
     * @param rollback 回滚到的目标发布信息，如果是新发布，则为null，如果是回滚一次发布，则为回滚到的目标发布信息
     * @return
     */
    public ReleaseVersion create(App app, Env env, Release release, Release rollback) {
        long releaseId = release == null ? 0 : release.getId();
        long rollbackId = rollback == null ? 0 : rollback.getId();

        // 应用 ReleaseVersionPostProcessors
        ReleaseVersionPostProcessorDelegate.applyPostProcessorsBeforeReleaseVersion(getReleaseVersionPostProcessors(), app.getId(), env.getId(), releaseId, rollbackId);

        ReleaseVersion version = newReleaseVersion(app, env, release, rollback);
        version = insert(version);

        // 应用 ReleaseVersionPostProcessors
        ReleaseVersionPostProcessorDelegate.applyPostProcessorsAfterReleaseVersion(getReleaseVersionPostProcessors(), version);

        return version;
    }

    /**
     * 创建新的{@link com.configx.web.model.Release}对象
     *
     * @param app
     * @param env
     * @param release
     * @param rollback
     * @return
     */
    private ReleaseVersion newReleaseVersion(App app, Env env, Release release, Release rollback) {
        Date now = new Date();

        ReleaseVersion version = new ReleaseVersion();

        version.setAppId(app.getId());
        version.setAppName(app.getName());
        version.setEnvId(env.getId());
        version.setEnvName(env.getName());

        if (rollback == null) { // 新的发布
            version.setReleaseId(release.getId());
            version.setBuildId(release.getReleaseBuildId());
        } else { // 回滚
            version.setReleaseId(release.getId());
            version.setRollbackId(rollback.getId());
            version.setBuildId(rollback.getReleaseBuildId());
        }

        version.setCreateTime(now);

        return version;
    }

    /**
     * 插入发布版本
     *
     * @param version
     * @return
     */
    private ReleaseVersion insert(ReleaseVersion version) {
        releaseVersionMapper.insertSelective(version);
        return version;
    }

    /**
     * 返回指定应用指定环境的指定版本
     *
     * @param appId
     * @param envId
     * @param version
     * @return
     */
    public ReleaseVersion getVersion(int appId, int envId, long version) {
        ReleaseVersion releaseVersion = releaseVersionMapper.selectByPrimaryKey(version);
        if (releaseVersion != null && releaseVersion.getAppId() == appId && releaseVersion.getEnvId() == envId) {
            return releaseVersion;
        } else {
            return null;
        }
    }

    /**
     * 获取两个版本之间的版本列表，包含这两个版本
     *
     * @param appId
     * @param envId
     * @param fromVerion
     * @param toVersion
     * @return
     */
    public List<ReleaseVersion> getVersionsBetween(int appId, int envId, long fromVerion, long toVersion) {
        return releaseVersionMapper.getVersionsBetween(appId, envId, fromVerion, toVersion);
    }

    /**
     * 返回指定应用指定环境的发布版本
     *
     * @param appId
     * @param envId
     * @return
     */
    public List<ReleaseVersion> getVersions(int appId, int envId) {
        return releaseVersionMapper.getVersions(appId, envId);
    }

    /**
     * 根据发布ID列表，获取发布版本列表
     *
     * @param releaseIdList
     * @return
     */
    public List<ReleaseVersion> getVersionsByReleaseIds(List<Long> releaseIdList) {
        if (CollectionUtils.isEmpty(releaseIdList)) {
            return Collections.emptyList();
        }
        return releaseVersionMapper.getVersionsByReleaseIdList(releaseIdList);
    }

    /**
     * 获取发布的信息
     *
     * @param releaseList
     * @return
     */
    public List<ReleaseVersion> getVersionsByReleases(List<Release> releaseList) {
        if (CollectionUtils.isEmpty(releaseList)) {
            return Collections.emptyList();
        }

        List<Long> idList = new ArrayList<>();
        for (Release release : releaseList) {
            idList.add(release.getId());
        }

        List<ReleaseVersion> releaseVersionList = getVersionsByReleaseIds(idList);
        return releaseVersionList;
    }

    /**
     * 返回指定应用指定环境的最新发布版本
     *
     * @param appId
     * @param envId
     * @return
     */
    public ReleaseVersion getLatestVersion(int appId, int envId) {
        return releaseVersionMapper.getLatestVersion(appId, envId);
    }

    /**
     * 返回指定应用指定环境的最新发布版本号
     *
     * @param appId
     * @param envId
     * @return
     */
    public long getLatestReleaseVersion(int appId, int envId) {
        ReleaseVersion latestVersion = getLatestVersion(appId, envId);
        if (latestVersion == null) {
            return 0;
        } else {
            return latestVersion.getNumber();
        }
    }

    /**
     * 返回指定应用指定环境的最新发布版本的修订号
     *
     * @param appId
     * @param envId
     * @return
     */
    public long getLatestReleaseRevision(int appId, int envId) {
        ReleaseVersion latestVersion = getLatestVersion(appId, envId);
        if (latestVersion == null) {
            return 0;
        }

        Build build = buildService.getBuild(latestVersion.getBuildId());
        return build == null ? 0 : build.getRevision();
    }

    /**
     * 返回指定应用指定环境的最新发布版本中，指定的配置项的修订号
     *
     * @param appId
     * @param envId
     * @param configId
     * @return
     */
    public long getLatestReleaseRevision(int appId, int envId, long configId) {
        ReleaseVersion latestVersion = getLatestVersion(appId, envId);
        if (latestVersion == null) {
            return 0;
        }

        BuildConfigItem buildConfigItem = buildService.getConfigItem(latestVersion.getBuildId(), configId);
        return buildConfigItem == null ? 0 : buildConfigItem.getRevision();
    }
}
