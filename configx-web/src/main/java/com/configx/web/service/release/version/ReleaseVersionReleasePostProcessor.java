/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release.version;

import com.configx.web.service.release.ReleaseContext;
import com.configx.web.service.release.ReleasePostProcessor;
import com.configx.web.service.release.RollbackContext;

/**
 * 监听发布，发布后生成一个新的发布版本
 *
 * @author Zhirong Zou
 */
public class ReleaseVersionReleasePostProcessor implements ReleasePostProcessor {

    private ReleaseVersionService releaseVersionService;

    public ReleaseVersionReleasePostProcessor(ReleaseVersionService releaseVersionService) {
        this.releaseVersionService = releaseVersionService;
    }

    @Override
    public void postProcessBeforeRelease(ReleaseContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postProcessAfterRelease(ReleaseContext context) {
        releaseVersionService.create(context.getApp(), context.getEnv(), context.getRelease(), null);

    }

    @Override
    public void postProcessBeforeRollback(RollbackContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postProcessAfterRollback(RollbackContext context) {
        releaseVersionService.create(context.getApp(), context.getEnv(), context.getRelease(), context.getRollback());

    }

}
