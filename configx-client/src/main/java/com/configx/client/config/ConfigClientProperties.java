/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.config;

import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;

import java.util.concurrent.TimeUnit;

/**
 * 配置客户端属性
 *
 * @author Zhirong Zou
 */
public class ConfigClientProperties {

    public static final String PREFIX = "configx.client";

    public static final long DEFAULT_UPDATE_INITIAL_DELAY = TimeUnit.MILLISECONDS.toMillis(30 * 1000);
    public static final long DEFAULT_UPDATE_INTERVAL = TimeUnit.MILLISECONDS.toMillis(1 * 1000);

    /**
     * 是否开启多版本控制
     */
    private static volatile boolean mvccEnabled = false;

    /**
     * 应用ID
     */
    private String app;

    /**
     * App Key
     */
    private String appKey;

    /**
     * 环境
     */
    private String env;

    /**
     * Profiles
     */
    private String profiles;

    /**
     * 应用密钥
     */
    private String secret;

    /**
     * The URI of the remote server
     */
    private String uri;

    /**
     * Flag to indicate that failure to connect to the server is fatal (default false).
     */
    private boolean failFast = false;

    /**
     * 更新初始延迟，默认30秒
     */
    private long updateInitialDelay = DEFAULT_UPDATE_INITIAL_DELAY;

    /**
     * 更新间隔，默认1秒
     */
    private long updateInterval = DEFAULT_UPDATE_INTERVAL;

    /**
     * 是否开启了多版本控制，否则总是读取最新版本的配置
     *
     * @return
     */
    public static boolean isMvccEnabled() {
        return mvccEnabled;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getProfiles() {
        return profiles;
    }

    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String url) {
        this.uri = url;
    }

    public boolean isFailFast() {
        return failFast;
    }

    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    public long getUpdateInitialDelay() {
        return updateInitialDelay;
    }

    public void setUpdateInitialDelay(long updateInitialDelay) {
        this.updateInitialDelay = updateInitialDelay;
    }

    public long getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    public ConfigClientProperties override(Environment environment) {
        ConfigClientProperties override = new ConfigClientProperties();
        BeanUtils.copyProperties(this, override);

        // mvcc enabled
        if (environment.containsProperty(ConfigClientProperties.PREFIX + ".mvcc.enabled")) {
            mvccEnabled = environment.getProperty(ConfigClientProperties.PREFIX + ".mvcc.enabled", Boolean.class);
        }

        if (environment.containsProperty(ConfigClientProperties.PREFIX + ".app")) {
            override.setApp(environment.getProperty(ConfigClientProperties.PREFIX + ".app"));
        }
        if (environment.containsProperty(ConfigClientProperties.PREFIX + ".appKey")) {
            override.setAppKey(environment.getProperty(ConfigClientProperties.PREFIX + ".appKey"));
        }
        if (environment.containsProperty(ConfigClientProperties.PREFIX + ".env")) {
            override.setEnv(environment.getProperty(ConfigClientProperties.PREFIX + ".env"));
        }
        if (environment.containsProperty(ConfigClientProperties.PREFIX + ".profiles")) {
            override.setProfiles(environment.getProperty(ConfigClientProperties.PREFIX + ".profiles"));
        }
        if (environment.containsProperty(ConfigClientProperties.PREFIX + ".secret")) {
            override.setSecret(environment.getProperty(ConfigClientProperties.PREFIX + ".secret"));
        }
        if (environment.containsProperty(ConfigClientProperties.PREFIX + ".uri")) {
            override.setUri(environment.getProperty(ConfigClientProperties.PREFIX + ".uri"));
        }
        if (environment.containsProperty(ConfigClientProperties.PREFIX + ".failFast")) {
            override.setFailFast(environment.getProperty(ConfigClientProperties.PREFIX + ".failFast",
                    boolean.class, false));
        }
        if (environment.containsProperty(ConfigClientProperties.PREFIX + ".updateInitialDelay")) {
            override.setUpdateInitialDelay(environment.getProperty(ConfigClientProperties.PREFIX + ".updateInitialDelay",
                    long.class, DEFAULT_UPDATE_INITIAL_DELAY));
        }
        if (environment.containsProperty(ConfigClientProperties.PREFIX + ".updateInterval")) {
            override.setUpdateInterval(environment.getProperty(ConfigClientProperties.PREFIX + ".updateInterval",
                    long.class, DEFAULT_UPDATE_INTERVAL));
        }
        return override;
    }

    @Override
    public String toString() {
        return "ConfigClientProperties [app=" + app + ", env=" + env + ", profiles=" + (profiles == null ? "" : profiles) + ", secret=" + secret + ", uri=" + uri + "]";
    }
}
