package com.configx.client.version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 保存每个线程使用的版本号
 * <p>
 * Created by zouzhirong on 2017/9/14.
 */
public class VersionContextHolder {

    private static final Log logger = LogFactory.getLog("com.configx.client.version");

    /**
     * 线程可见的最新版本号
     */
    public static volatile long visibleVersion = 0;

    /**
     * 当前线程使用的配置版本号
     */
    private static final ThreadLocal<Long> versionHolder = new ThreadLocal<>();

    /**
     * 设置线程可见的最新版本号
     *
     * @param visibleVersion
     */
    public static void setVisibleVersion(long visibleVersion) {
        VersionContextHolder.visibleVersion = visibleVersion;
    }

    /**
     * 返回线程可见的最新版本号
     *
     * @return
     */
    public static long getVisibleVersion() {
        return visibleVersion;
    }

    /**
     * 返回线程当前的配置版本号
     *
     * @return
     */
    public static Long getCurrentVersion() {
        Long version = versionHolder.get();

        Thread t = Thread.currentThread();

        logger.debug("Thread get current version, thread=" + t + " version=" + version
                + " headVersion=" + visibleVersion);

        return version;
    }

    /**
     * 设置线程的配置版本号
     *
     * @param version
     * @param isWeakReference 线程对版本号的引用是否是弱引用
     */
    public static void setCurrentVersion(Long version, boolean isWeakReference) {
        if (version == null) {
            clearCurrentVersion();

        } else {
            versionHolder.set(version);

            Thread t = Thread.currentThread();
            Long previousVersion = versionHolder.get(); // previousVersion可能为null
            ConfigVersionManager.addThreadVersion(t, version, isWeakReference);

            logger.debug("Thread set current version, thread=" + t + " version=" + version
                    + " previousVersion=" + previousVersion + " headVersion=" + visibleVersion);
        }
    }

    /**
     * 清除线程当前的配置版本号
     */
    public static void clearCurrentVersion() {
        Long version = versionHolder.get();
        if (version == null) {
            return;
        }
        versionHolder.remove();

        Thread t = Thread.currentThread();
        ConfigVersionManager.removeThreadVersion(t, version);

        logger.debug("Thread clear current version, thread=" + t + " version=" + version
                + " headVersion=" + visibleVersion);
    }

}
