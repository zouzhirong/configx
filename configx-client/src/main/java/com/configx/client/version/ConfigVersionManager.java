/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.version;

import com.google.common.collect.MapMaker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 配置版本管理
 *
 * @author Zhirong Zou
 */
public class ConfigVersionManager {

    private static final Log versionLogger = LogFactory.getLog("com.configx.client.version");

    /**
     * 最新版本号
     */
    public static volatile long headVersion = 0;

    /**
     * 所有版本号列表
     */
    private static final ConcurrentSkipListSet<Long> versions = new ConcurrentSkipListSet<>();

    /**
     * 配置Bean版本号列表
     */
    private static final ConcurrentSkipListSet<Long> beanVersions = new ConcurrentSkipListSet<>();

    /**
     * 线程引用的版本号信息
     */
    private static final ConcurrentMap<Long, ThreadVersionInfo> threadVersionMap = new ConcurrentHashMap<>();

    /**
     * 版本移除监听器
     */
    private static final List<VersionRemovalListener> versionRemovalListeners = new ArrayList<>();

    /**
     * 增加新版本
     *
     * @param version
     * @return
     */
    public static synchronized boolean addVersion(long version) {
        // 前一个版本号
        long previousVersion = headVersion;

        if (versions.contains(version) || version <= previousVersion) {
            versionLogger.warn("Add version failed, version=" + version + " previousVersion=" + previousVersion);
            return false;
        }

        // 更新版本号
        versions.add(version);
        beanVersions.add(version);
        threadVersionMap.put(version, new ThreadVersionInfo(version));
        headVersion = version;

        versionLogger.info("Add version, version=" + version + " headVersion " + previousVersion + "->" + version);

        return true;
    }

    /**
     * 返回最新版本号
     *
     * @return
     */
    public static long getHeadVersion() {
        return headVersion;
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
        Long previousVersion = versions.lower(version);
        return previousVersion == null ? 0 : previousVersion;
    }

    /**
     * 返回下一个版本号
     *
     * @param version
     * @return
     */
    public static long getNextVersion(long version) {
        Long nextVersion = versions.higher(version);
        return nextVersion == null ? 0 : nextVersion;
    }

    /**
     * 返回配置Bean版本号列表
     *
     * @return
     */
    public static List<Long> getBeanVersions() {
        return new ArrayList<>(beanVersions);
    }

    /**
     * 删除指定的配置Bean版本号
     *
     * @param version
     */
    public static void removeBeanVersion(long version) {
        if (beanVersions.remove(version)) {

            // 尝试删除指定版本的beans
            tryRemoveVersionBeans(version);
        }
    }

    /**
     * 返回各个版本被线程引用的信息
     *
     * @return
     */
    public static List<ThreadVersionInfo> getThreadVersionInfos() {
        List<ThreadVersionInfo> resultList = new ArrayList<>();

        for (ThreadVersionInfo threadVersionInfo : threadVersionMap.values()) {
            if (threadVersionInfo.isEmpty()) {
                continue;
            }
            resultList.add(threadVersionInfo);
        }
        return new ArrayList<>();
    }

    /**
     * 获取指定版本的线程引用信息
     *
     * @param version
     * @return
     */
    private static ThreadVersionInfo getThreadVersionInfo(long version) {
        return threadVersionMap.get(version);
    }

    /**
     * 返回所有线程引用的版本号列表
     *
     * @return
     */
    public static Set<Long> getThreadVersions() {
        Set<Long> versions = new LinkedHashSet();

        List<ThreadVersionInfo> threadVersionInfos = getThreadVersionInfos();
        for (ThreadVersionInfo threadVersionInfo : threadVersionInfos) {
            versions.add(threadVersionInfo.getVersion());
        }

        return versions;
    }

    /**
     * 添加线程引用的版本号
     *
     * @param thread
     * @param version
     * @param isWeakReference
     */
    public static void addThreadVersion(Thread thread, long version, boolean isWeakReference) {
        if (isWeakReference) { // 线程对版本号是弱引用
            return;
        }
        ThreadVersionInfo threadReference = getThreadVersionInfo(version);
        if (threadReference != null) {
            threadReference.addThread(thread);
        }
    }

    /**
     * 删除线程引用的版本
     *
     * @param thread
     * @param version
     */
    public static void removeThreadVersion(Thread thread, long version) {
        ThreadVersionInfo threadReference = getThreadVersionInfo(version);
        if (threadReference != null) {
            Long removeVersion = threadReference.removeThread(thread);
            if (removeVersion != null) {
                // 尝试删除指定版本的beans
                tryRemoveVersionBeans(threadReference.getVersion());
            }
        }
    }

    /**
     * 添加版本移除监听器
     *
     * @param listener
     */
    public static void addVersionRemovalListeners(VersionRemovalListener listener) {
        if (listener != null) {
            versionRemovalListeners.add(listener);
        }
    }

    /**
     * 尝试删除指定版本的配置beans
     *
     * @param version
     */
    private static void tryRemoveVersionBeans(long version) {
        if (beanVersions.contains(version)) { // 这个版本的bean还不能删除
            return;
        }

        ThreadVersionInfo threadReference = getThreadVersionInfo(version);
        if (threadReference == null || (threadReference.isEmpty())) { // 没有线程引用这个版本了
            doRemoveVersionBeans(version);
        }
    }

    /**
     * 删除指定版本的beans
     *
     * @param version
     */
    private static void doRemoveVersionBeans(long version) {
        for (VersionRemovalListener listener : versionRemovalListeners) {
            listener.onRemoval(version);
        }
    }

    /**
     * 线程版本引用信息
     */
    public static class ThreadVersionInfo {
        private long version;

        private ConcurrentMap<Thread, Long> threadVersionMap = new MapMaker().weakKeys().makeMap();

        public ThreadVersionInfo(long version) {
            this.version = version;
        }

        public long getVersion() {
            return version;
        }

        public void addThread(Thread thread) {
            threadVersionMap.put(thread, version);
        }

        public Long removeThread(Thread thread) {
            return threadVersionMap.remove(thread);
        }

        public boolean isEmpty() {
            return threadVersionMap.isEmpty();
        }

        public List<String> getThreadNames() {
            List<String> threadNames = new ArrayList<>();
            for (Thread thread : threadVersionMap.keySet()) {
                threadNames.add(thread.getName());
            }
            return threadNames;
        }

        public List<Thread> getThreads() {
            return new ArrayList<>(threadVersionMap.keySet());
        }
    }

}
