/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release.version;

import com.configx.web.model.ReleaseVersion;

import java.util.List;

/**
 * {@link com.configx.web.service.release.version.ReleaseVersionPostProcessor} 处理
 *
 * @author Zhirong Zou
 */
public class ReleaseVersionPostProcessorDelegate {

    public static void applyPostProcessorsBeforeReleaseVersion(List<ReleaseVersionPostProcessor> releaseVersionPostProcessors, int appId, int envId, long releaseId, long rollbackId) {
        for (ReleaseVersionPostProcessor ReleaseVersionPostProcessor : releaseVersionPostProcessors) {
            ReleaseVersionPostProcessor.postProcessBeforeReleaseVersion(appId, envId, releaseId, rollbackId);
        }
    }

    public static void applyPostProcessorsAfterReleaseVersion(List<ReleaseVersionPostProcessor> releaseVersionPostProcessors, ReleaseVersion version) {
        for (ReleaseVersionPostProcessor ReleaseVersionPostProcessor : releaseVersionPostProcessors) {
            ReleaseVersionPostProcessor.postProcessAfterReleaseVersion(version);
        }
    }

}
