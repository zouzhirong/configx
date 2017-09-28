package com.configx.client.config;

import com.configx.client.version.ConfigVersionManager;
import com.configx.client.version.VersionContextHolder;
import com.configx.client.version.VersionExecutor;
import com.configx.client.version.VersionRemovalListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 对未使用的版本的bean进行销毁，首先会触发spring bean的destory，然后再remove。
 * <p>
 * 销毁工作如何触发？
 * 1、通过注册版本移除监听器，当有新的版本或旧的版本没有线程再引用时，会触发销毁旧版本的beans。
 * 2、清理线程每分钟会进行一次完整的清理工作。
 * <p>
 * Created by zouzhirong on 2017/9/11.
 */
public class DestructionCallbackBindingListener implements VersionRemovalListener {

    private static final Log logger = LogFactory.getLog("com.configx.client.beans");

    private ConfigContext configContext;

    /**
     * 已经销毁的配置Bean版本号列表
     */
    private static final ConcurrentSkipListSet<Long> destroyedBeanVersions = new ConcurrentSkipListSet<>();

    /**
     * 销毁回调
     */
    private DestructionCallback destructionCallback;

    public DestructionCallbackBindingListener(ConfigContext configContext, DestructionCallback destructionCallback) {
        this.configContext = configContext;
        this.destructionCallback = destructionCallback;

        // 开始后台的清理线程
        startCleanThread();
    }

    @Override
    public void onRemoval(long version) {
        asyncCheck(version);
    }

    /**
     * 异步检查指定版本
     *
     * @param version
     */
    public void asyncCheck(long version) {
        executor.execute(() ->
                check(version)
        );
    }

    /**
     * 检查所有版本
     */
    public void check() {
        // 版本号列表
        List<Long> versions = ConfigVersionManager.getVersions();

        for (long version : versions) {
            check(version);
        }
    }

    /**
     * 检查指定版本
     *
     * @param version
     */
    public void check(long version) {
        // 最新版本号
        long headVersion = VersionContextHolder.getVisibleVersion();

        // 这个版本的下个版本
        long nextVersion = ConfigVersionManager.getNextVersion(version);

        // 所有线程正在使用的所有版本号
        Set<Long> threadVersions = ConfigVersionManager.getThreadVersions();

        // 最新版本号的bean不能销毁
        if (headVersion == 0 || version >= headVersion) {
            return;
        }

        if (nextVersion == 0 || version >= nextVersion) {
            return;
        }

        // 如果有线程正在使用这个版本的bean，则不能销毁
        if (threadVersions.contains(version)) {
            return;
        }

        // 销毁
        destroy(version, nextVersion);
    }

    /**
     * 销毁scope中指定版本的beans
     *
     * @param version
     * @param nextVersion
     */
    private synchronized void destroy(long version, long nextVersion) {
        if (destroyedBeanVersions.contains(version)) { // 这个版本已经销毁了
            return;
        }

        Set<String> beenNames = getVersionDestroyBeanNames(version, nextVersion);
        destroy(version, beenNames);

        // 标记这个版本已经销毁
        destroyedBeanVersions.add(version);
    }

    /**
     * 销毁scope中指定版本的beans
     *
     * @param version
     * @param beenNames
     */
    private void destroy(long version, Collection<String> beenNames) {
        if (CollectionUtils.isEmpty(beenNames)) {
            return;
        }

        VersionExecutor.doWithVersion(version, (v) -> {
                    for (String beenName : beenNames) {
                        boolean result = destructionCallback.destroy(beenName);
                        logger.info("Destroy bean, version=" + version + ", beenName=" + beenName + ", result=" + result);
                    }
                    return null;
                }
        );
    }

    /**
     * 获取这个版本需要销毁的beans，也就是下个版本刷新的beans
     *
     * @param version
     * @param nextVersion
     * @return
     */
    private Set<String> getVersionDestroyBeanNames(long version, long nextVersion) {
        return configContext.getRefreshBeanNames(nextVersion);
    }

    /////////////////////////////////////////////////////////////////////////////
    //
    // VersionBeanCleanThread Thread
    //
    /////////////////////////////////////////////////////////////////////////////

    private VersionBeanCleanThread cleanThread = null;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public void startCleanThread() {
        cleanThread = new VersionBeanCleanThread();
        cleanThread.initialize();

        logger.debug("DestructionManager background threads started (as scheduler was started).");
    }

    /**
     * 版本Bean清理线程
     */
    class VersionBeanCleanThread extends Thread {

        VersionBeanCleanThread() {
            this.setPriority(Thread.NORM_PRIORITY + 2);
            this.setName("version-bean-clean-thread");
            this.setDaemon(true);
        }

        public void initialize() {
            executor.scheduleWithFixedDelay(this, 1, 1, TimeUnit.MINUTES);
        }

        @Override
        public void run() {
            try {
                check();
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }
}
