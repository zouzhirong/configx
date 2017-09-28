package com.configx.client.version;

import com.configx.client.config.ConfigClientProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 基于配置版本的执行器
 * <p>
 * Created by zouzhirong on 2017/9/15.
 */
public class VersionExecutor {

    private static final Log logger = LogFactory.getLog("com.configx.client.version");

    /**
     * 基于当前版本执行
     *
     * @param callback
     * @param <T>
     * @return
     */
    public static <T> T doWithCurrentVersion(VersionCallback<T> callback) {
        // 可见的最新版本号
        long headVersion = VersionContextHolder.getVisibleVersion();

        boolean initialized = false;
        Long version = VersionContextHolder.getCurrentVersion();
        if (version == null) {
            initialized = true;
            version = headVersion;
            VersionContextHolder.setCurrentVersion(version, false);
        }

        Thread t = Thread.currentThread();

        logger.debug("Thread doWithVersion, thread=" + t + " version=" + version + " headVersion=" + headVersion
                + " initialized=" + initialized);

        try {
            return callback.doWithVersion(version);
        } finally {
            // 未开启mvcc，则每次使用完后自动清理线程中的版本信息
            // 开启mvcc后，需要用户在“事务”结束后手动清除线程中的版本信息
            if (!ConfigClientProperties.isMvccEnabled()) {
                if (initialized) {
                    VersionContextHolder.clearCurrentVersion();
                    logger.debug("Thread doWithVersion clear version, thread=" + t + " version=" + version
                            + " initialized=" + initialized + " headVersion=" + headVersion);
                }
            }
        }
    }

    /**
     * 基于指定版本执行
     *
     * @param version
     * @param callback
     * @param <T>
     * @return
     */
    public static <T> T doWithVersion(long version, VersionCallback<T> callback) {
        Long previousVersion = VersionContextHolder.getCurrentVersion();
        VersionContextHolder.setCurrentVersion(version, true);
        try {
            return callback.doWithVersion(version);
        } finally {
            VersionContextHolder.setCurrentVersion(previousVersion, true);
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

}
