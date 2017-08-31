/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release;

import com.configx.web.model.App;
import com.configx.web.model.Env;
import com.configx.web.model.Release;
import com.configx.web.model.Build;

/**
 * 回滚上下文
 *
 * @author Zhirong Zou
 */
public class RollbackContext {
    /**
     * 应用信息
     */
    private App app;

    /**
     * 环境信息
     */
    private Env env;

    /**
     * 发布信息
     */
    private Release release;

    /**
     * 回滚到的目标发布信息
     */
    private Release rollback;

    /**
     * 构建信息
     */
    private Build build;

    public RollbackContext(App app, Env env, Release release, Release rollback) {
        this.app = app;
        this.env = env;
        this.release = release;
        this.rollback = rollback;
    }

    /**
     * 返回应用ID
     *
     * @return
     */
    public int getAppId() {
        return app.getId();
    }

    /**
     * 返回应用信息
     *
     * @return
     */
    public App getApp() {
        return app;
    }

    /**
     * 返回环境ID
     *
     * @return
     */
    public int getEnvId() {
        return env.getId();
    }

    /**
     * 返回环境信息
     *
     * @return
     */
    public Env getEnv() {
        return env;
    }

    /**
     * 返回发布ID
     *
     * @return
     */
    public long getReleaseId() {
        return release.getId();
    }

    /**
     * 返回发布信息
     *
     * @return
     */
    public Release getRelease() {
        return release;
    }

    /**
     * 设置发布信息
     *
     * @param release
     */
    public void setRelease(Release release) {
        this.release = release;
    }

    /**
     * 返回回滚到的目标发布ID
     *
     * @return
     */
    public long getRollbackId() {
        return rollback.getId();
    }

    /**
     * 返回回滚到的目标发布信息
     *
     * @return
     */
    public Release getRollback() {
        return rollback;
    }

    /**
     * 设置回滚到的目标发布信息
     *
     * @param rollback
     */
    public void setRollback(Release rollback) {
        this.rollback = rollback;
    }

    /**
     * 返回构建信息
     *
     * @return
     */
    public Build getBuild() {
        return build;
    }

    /**
     * 设置构建信息
     *
     * @param build
     */
    public void setBuild(Build build) {
        this.build = build;
    }
}
