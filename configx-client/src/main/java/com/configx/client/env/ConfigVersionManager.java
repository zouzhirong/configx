/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.env;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private static final Map<Thread, Long> threadVersionMap = new ConcurrentHashMap<>();

    /**
     * 当前线程使用的配置版本号
     */
    private static final ThreadLocal<Long> threadCurrentVersion = new ThreadLocal<Long>() {
        protected Long initialValue() {
            return initThreadVersion();
        }
    };

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
    public static long getCurrentVersion() {
        long version = threadCurrentVersion.get();
        if (version == 0) {
            version = initThreadVersion();
            setCurrentVersion(version);
        }

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
    public static void setCurrentVersion(long version) {
        threadCurrentVersion.set(version);

        Thread t = Thread.currentThread();
        Long previousVersion = threadVersionMap.put(t, version); // previousVersion可能为null

        threadLogger.debug("Thread set current version, thread=" + t + " version=" + version
                + " previousVersion=" + previousVersion + " headVersion=" + headVersion);
    }

    /**
     * 清除线程当前的配置版本号
     */
    public static void clearCurrentVersion() {
        long version = threadCurrentVersion.get();
        threadCurrentVersion.remove();

        Thread t = Thread.currentThread();
        threadVersionMap.remove(t);

        threadLogger.debug("Thread clear current version, thread=" + t + " version=" + version
                + " headVersion=" + headVersion);
    }
}
