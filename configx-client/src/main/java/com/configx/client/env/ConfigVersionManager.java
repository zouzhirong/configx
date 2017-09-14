/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.env;

import com.configx.client.item.locator.ConfigClientProperties;
import com.google.common.collect.MapMaker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 配置版本管理
 *
 * @author Zhirong Zou
 */
public class ConfigVersionManager {

    private static final Log versionLogger = LogFactory.getLog("com.configx.client.version");
    private static final Log threadLogger = LogFactory.getLog("com.configx.client.thread");

    /**
     * 最新版本号
     */
    public static volatile long headVersion = 0;

    /**
     * 版本号列表
     */
    private static volatile List<Long> versions = new CopyOnWriteArrayList<>();

    /**
     * 每个线程使用的配置版本号
     */
    private static final ConcurrentMap<Thread, Long> threadVersionMap = new MapMaker().weakKeys().makeMap();

    /**
     * 当前线程使用的配置版本号
     */
    private static final ThreadLocal<Long> versionHolder = new ThreadLocal<>();

    /**
     * 返回最新版本号
     *
     * @return
     */
    public static long getHeadVersion() {
        return headVersion;
    }

    /**
     * 添加版本
     *
     * @param version
     */
    public static boolean addVersion(long version) {
        if (versions.contains(version)) {
            versionLogger.warn("Add version repeatedly, version=" + version + " headVersion=" + headVersion);
            return false;
        }
        versions.add(version);

        // 旧的最新版本号
        long oldHeadVersion = headVersion;

        // 更新最新版本号
        if (version > headVersion) {
            headVersion = version;
        }

        versionLogger.info("Add version, version=" + version + " headVersion " + oldHeadVersion + "->" + headVersion);

        return true;
    }

    /**
     * 返回版本号列表
     *
     * @return
     */
    public static List<Long> getVersions() {
        return new ArrayList<>(versions);
    }

    /**
     * 返回上一个版本号
     *
     * @param version
     * @return
     */
    public static long getPreviousVersion(long version) {
        boolean found = false;
        for (int index = versions.size() - 1; index >= 0; index--) {
            if (versions.get(index) == version) {
                found = true;
            } else {
                if (found) {
                    return versions.get(index);
                }
            }
        }
        return 0;
    }

    public static Map<Thread, Long> getThreadVersionMap() {
        return threadVersionMap;
    }

    /**
     * 返回所有线程正在使用的所有的版本号
     *
     * @return
     */
    public static Set<Long> getThreadVersions() {
        Collection<Long> threadVersions = threadVersionMap.values();
        if (CollectionUtils.isEmpty(threadVersions)) {
            return Collections.emptySet();
        } else {
            return new HashSet<>(threadVersions);
        }
    }

    /**
     * 返回线程当前的配置版本号
     *
     * @return
     */
    public static Long getCurrentVersion() {
        Long version = versionHolder.get();

        Thread t = Thread.currentThread();

        threadLogger.debug("Thread get current version, thread=" + t + " version=" + version
                + " headVersion=" + headVersion);

        return version;
    }

    /**
     * 设置线程的配置版本号
     *
     * @param version
     */
    public static void setCurrentVersion(Long version) {
        if (version == null) {
            clearCurrentVersion();

        } else {
            versionHolder.set(version);

            Thread t = Thread.currentThread();
            Long previousVersion = threadVersionMap.put(t, version); // previousVersion可能为null

            threadLogger.debug("Thread set current version, thread=" + t + " version=" + version
                    + " previousVersion=" + previousVersion + " headVersion=" + headVersion);
        }
    }

    /**
     * 清除线程当前的配置版本号
     */
    public static void clearCurrentVersion() {
        Long version = versionHolder.get();
        versionHolder.remove();

        Thread t = Thread.currentThread();
        threadVersionMap.remove(t);

        threadLogger.debug("Thread clear current version, thread=" + t + " version=" + version
                + " headVersion=" + headVersion);
    }

    /**
     * 基于版本执行逻辑
     *
     * @param callback
     * @param <T>
     * @return
     */
    public static <T> T doWithVersion(VersionCallback<T> callback) {
        boolean initialized = false;
        Long version = getCurrentVersion();
        if (version == null) {
            initialized = true;
            version = getHeadVersion();
            setCurrentVersion(version);
        }

        Thread t = Thread.currentThread();

        threadLogger.debug("Thread doWithVersion, thread=" + t + " version=" + version
                + " initialized=" + initialized + " headVersion=" + headVersion);

        try {
            return callback.doWithVersion(version);
        } finally {
            // 未开启mvcc，则每次使用完后自动清理线程中的版本信息
            // 开启mvcc后，需要用户在“事务”结束后手动清除线程中的版本信息
            if (!ConfigClientProperties.isMvccEnabled()) {
                if (initialized) {
                    ConfigVersionManager.clearCurrentVersion();
                    threadLogger.debug("Thread doWithVersion clear version, thread=" + t + " version=" + version
                            + " initialized=" + initialized + " headVersion=" + headVersion);
                }
            }
        }
    }

    /**
     * 基于版本执行逻辑
     *
     * @param version
     * @param callback
     * @param <T>
     * @return
     */
    public static <T> T doWithVersion(long version, VersionCallback<T> callback) {
        Long previousVersion = versionHolder.get();
        setCurrentVersion(version);
        try {
            return callback.doWithVersion(version);
        } finally {
            setCurrentVersion(previousVersion);
        }
    }

    /**
     * 基本Version的回调
     *
     * @param <T>
     */
    public interface VersionCallback<T> {
        T doWithVersion(long version);
    }

    // TODO
    // Destroy and Remove not used version-refresh scope beans.

}
