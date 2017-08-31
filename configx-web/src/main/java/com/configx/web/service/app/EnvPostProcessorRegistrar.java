/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.app;

import com.configx.web.model.Build;
import com.configx.web.model.ConfigItem;
import com.configx.web.model.ReleaseVersion;
import com.configx.web.service.build.BuildPostProcessor;
import com.configx.web.service.build.BuildService;
import com.configx.web.service.config.ConfigCommitPostProcessor;
import com.configx.web.service.release.version.ReleaseVersionPostProcessor;
import com.configx.web.service.release.version.ReleaseVersionService;
import com.configx.web.service.config.ConfigCommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 环境 Helper
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Component
public class EnvPostProcessorRegistrar {

    @Autowired
    private EnvService envService;

    @Autowired
    private ConfigCommitService configCommitService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private ReleaseVersionService releaseVersionService;

    @Autowired
    private AutoReleaseComponent autoReleaseComponent;

    @PostConstruct
    public void init() {
        // 监听配置项变化
        configCommitService.addListener(new EnvConfigItemListener());

        // 开启自动发布时，配置项变化立刻自动发布
        configCommitService.addListener(new AutoReleaseListener());

        // 监听构建
        buildService.addPostProcessor(new EnvBuildPostProcessor());

        // 监听版本发布
        releaseVersionService.addPostProcessor(new EnvReleaseVersionPostProcessor());
    }

    /**
     * 监听环境下的配置项的变化
     */
    private class EnvConfigItemListener implements ConfigCommitPostProcessor {

        @Override
        public void onConfigItemAdded(long revision, ConfigItem configItem) {
            envService.updateRevisionInfo(configItem.getEnvId(), revision);
        }

        @Override
        public void onConfigItemModified(long revision, ConfigItem configItem, String newValue) {
            envService.updateRevisionInfo(configItem.getEnvId(), revision);
        }

        @Override
        public void onConfigItemDeleted(long revision, ConfigItem configItem) {
            envService.updateRevisionInfo(configItem.getEnvId(), revision);
        }

    }

    /**
     * 自动发布
     */
    private class AutoReleaseListener implements ConfigCommitPostProcessor {

        @Override
        public void onConfigItemAdded(long revision, ConfigItem configItem) {
            autoReleaseComponent.release(configItem);
        }

        @Override
        public void onConfigItemModified(long revision, ConfigItem configItem, String newValue) {
            autoReleaseComponent.release(configItem);
        }

        @Override
        public void onConfigItemDeleted(long revision, ConfigItem configItem) {
            autoReleaseComponent.release(configItem);
        }

    }

    /**
     * 监听环境的构建
     */
    private class EnvBuildPostProcessor implements BuildPostProcessor {
        @Override
        public void postProcessBeforeBuild(Build build) {
        }

        @Override
        public void postProcessAfterBuild(Build build) {
            envService.updateBuildInfo(build.getEnvId(), build.getId(), build.getBuildTime());
        }
    }

    /**
     * 监听环境的版本发布
     */
    private class EnvReleaseVersionPostProcessor implements ReleaseVersionPostProcessor {
        @Override
        public void postProcessBeforeReleaseVersion(int appId, int envId, long releaseId, long rollbackId) {
        }

        @Override
        public void postProcessAfterReleaseVersion(ReleaseVersion version) {
            envService.updateReleaseVersion(version.getEnvId(), version.getNumber(), version.getCreateTime());
        }
    }
}
