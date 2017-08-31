/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release.version;

import com.configx.web.model.ReleaseVersion;

/**
 * 版本发布 PostProcessor
 *
 * @author Zhirong Zou
 */
public interface ReleaseVersionPostProcessor {

    /**
     * 版本发布前处理
     *
     * @param appId
     * @param envId
     * @param releaseId
     * @param rollbackId
     */
    void postProcessBeforeReleaseVersion(int appId, int envId, long releaseId, long rollbackId);

    /**
     * 版本发布后处理
     *
     * @param version
     */
    void postProcessAfterReleaseVersion(ReleaseVersion version);

}
