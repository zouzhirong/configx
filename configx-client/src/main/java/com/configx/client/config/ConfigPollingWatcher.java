package com.configx.client.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于轮询的配置监听器
 * <p>
 * Created by zouzhirong on 2017/9/14.
 */
public class ConfigPollingWatcher implements ConfigWatcher {

    private final Log logger = LogFactory.getLog("com.configx.client.config.watcher");

    /**
     * 轮询初始延迟
     */
    private long updateInitialDelay;

    /**
     * 轮询间隔
     */
    private long updateInterval;

    /**
     * 监听回调
     */
    private ConfigWatcher.Callback callback;

    /**
     * 轮询线程
     */
    private PollingThread pollingThread = null;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * 设置轮询初始延迟，单位毫秒
     *
     * @param updateInitialDelay
     */
    public void setUpdateInitialDelay(long updateInitialDelay) {
        this.updateInitialDelay = updateInitialDelay;
    }

    /**
     * 设置轮询间隔，单位毫秒
     *
     * @param updateInterval
     */
    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    @Override
    public void start(ConfigClientProperties clientProperties, ConfigWatcher.Callback callback) {
        this.callback = callback;

        this.pollingThread = new PollingThread();
        this.pollingThread.initialize();

        this.logger.debug("ConfigUpdate background threads started (as scheduler was started).");
    }

    /////////////////////////////////////////////////////////////////////////////
    //
    // ConfigUpdate Thread
    //
    /////////////////////////////////////////////////////////////////////////////

    class PollingThread extends Thread {

        PollingThread() {
            this.setPriority(Thread.NORM_PRIORITY + 2);
            this.setName("config-polling-watcher");
            this.setDaemon(true);
        }

        public void initialize() {
            this.update();
            executorService.scheduleWithFixedDelay(PollingThread.this, updateInitialDelay, updateInterval, TimeUnit.MILLISECONDS);
        }

        private void update() {
            try {
                callback.onChange();
            } catch (Exception e) {
                logger.error("Config Update Failed", e);
            }
        }

        @Override
        public void run() {
            update();
        }
    }

}
