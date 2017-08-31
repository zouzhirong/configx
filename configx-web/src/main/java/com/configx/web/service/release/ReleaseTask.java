/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release;

import com.configx.web.support.ApplicationContextHelper;

/**
 * 发布Task
 *
 * @author Zhirong Zou
 */
public class ReleaseTask implements Runnable {

    /**
     * 发布ID
     */
    private long releaseId;

    public ReleaseTask(long releaseId) {
        this.releaseId = releaseId;
    }

    @Override
    public void run() {
        getReleaseService().release(releaseId);
    }

    private ReleaseService getReleaseService() {
        return ApplicationContextHelper.getBean(ReleaseService.class);
    }

}
