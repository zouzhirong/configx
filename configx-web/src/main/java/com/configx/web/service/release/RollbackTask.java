/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release;

import com.configx.web.support.ApplicationContextHelper;

/**
 * 回滚Task
 *
 * @author Zhirong Zou
 */
public class RollbackTask implements Runnable {

    /**
     * 回滚的发布ID
     */
    private long releaseId;

    /**
     * 回滚到的目标发布ID
     */
    private long targetReleaseId;

    public RollbackTask(long releaseId, long targetReleaseId) {
        this.releaseId = releaseId;
        this.targetReleaseId = targetReleaseId;
    }

    @Override
    public void run() {
        getReleaseService().rollback(releaseId, targetReleaseId);
    }

    private ReleaseService getReleaseService() {
        return ApplicationContextHelper.getBean(ReleaseService.class);
    }

}
