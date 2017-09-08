/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.env;

import com.configx.client.item.locator.ConfigClientProperties;
import com.google.common.collect.MapMaker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 配置版本管理
 *
 * @author Zhirong Zou
 */
public class ConfigVersionManager {

    private static final Log versionLogger = LogFactory.getLog("com.configx.client.version");
    private static final Log threadLogger = LogFactory.getLog("com.configx.client.version.thread");

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
    private static final ThreadLocal<Long> threadCurrentVersion = new ThreadLocal<>();

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

    /**
     * 初始化线程的配置版本号
     *
     * @return
     */
    private static long initThreadVersion() {
        return getHeadVersion();
    }

    /**
     * 返回线程当前的配置版本号
     *
     * @return
     */
    public static ThreadLocalValue<Long> getCurrentVersion() {
        boolean initialized = false;
        Long version = threadCurrentVersion.get();
        if (version == null) {
            initialized = true;
            version = initThreadVersion();
            setCurrentVersion(version);
        }

        Thread t = Thread.currentThread();

        threadLogger.debug("Thread get current version, thread=" + t + " version=" + version
                + " headVersion=" + headVersion);

        debugThreadVersionInfo();

        return new ThreadLocalValue<>(version, initialized);
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
            threadCurrentVersion.set(version);

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
        Long version = threadCurrentVersion.get();
        threadCurrentVersion.remove();

        Thread t = Thread.currentThread();
        threadVersionMap.remove(t);

        threadLogger.debug("Thread clear current version, thread=" + t + " version=" + version
                + " headVersion=" + headVersion);

    }

    private static long lastDebugThreadTime = 0;

    /**
     * 打印所有线程使用的版本信息
     */
    private static void debugThreadVersionInfo() {
        if (threadLogger.isDebugEnabled() && (System.currentTimeMillis() - lastDebugThreadTime) > TimeUnit.SECONDS.toMillis(5)) {
            lastDebugThreadTime = System.currentTimeMillis();

            StringBuilder sb = new StringBuilder();
            sb.append("\n=========================\n");
            sb.append("Thread-Version Report:\n");
            sb.append("\tHeadVersion " + headVersion + "\n");
            sb.append("=========================\n");
            for (Map.Entry<Thread, Long> threadVersion : threadVersionMap.entrySet()) {
                sb.append("\tThread " + threadVersion.getKey().getName() + " version " + threadVersion.getValue() + "\n");
            }
            threadLogger.debug(sb.toString());
        }

    }

    /**
     * 基于版本执行逻辑
     *
     * @param callback
     * @param <T>
     * @return
     */
    public static <T> T doWithVersion(VersionCallback<T> callback) {
        ThreadLocalValue<Long> threadLocalValue = ConfigVersionManager.getCurrentVersion();
        long currentVersion = threadLocalValue.getValue();

        try {
            return (T) callback.doWithVersion(currentVersion);
        } finally {
            if (threadLocalValue.isInitialized()) {
                if (!ConfigClientProperties.isMvccEnabled()) {
                    ConfigVersionManager.clearCurrentVersion();
                }
            }
        }
    }

    /**
     * 从ThreadLocal中获取的值信息
     *
     * @param <T>
     */
    static class ThreadLocalValue<T> {

        /**
         * 从ThreadLocal中获取的值
         */
        private T value;

        /**
         * 是否刚初始化的值，否则是以前已经设置的值
         */
        private boolean initialized;

        public ThreadLocalValue(T value, boolean initialized) {
            this.value = value;
            this.initialized = initialized;
        }

        public T getValue() {
            return value;
        }

        public boolean isInitialized() {
            return initialized;
        }
    }

    /**
     * 基本Version的回调
     *
     * @param <T>
     */
    public static interface VersionCallback<T> {
        T doWithVersion(long version);
    }

    // TODO
    // Destroy and Remove not used version-refresh scope beans.

}
