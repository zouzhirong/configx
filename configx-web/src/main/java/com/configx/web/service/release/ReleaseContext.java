/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release;

import com.configx.web.model.App;
import com.configx.web.model.Env;
import com.configx.web.model.Build;
import com.configx.web.model.Release;

/**
 * 发布上下文
 *
 * @author Zhirong Zou
 */
public class ReleaseContext {
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
     * 构建信息
     */
    private Build build;

    public ReleaseContext(App app, Env env, Release release) {
        this.app = app;
        this.env = env;
        this.release = release;
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
