package com.configx.client.config;

/**
 * 配置监听器
 * <p>
 * Created by zouzhirong on 2017/9/14.
 */
public interface ConfigWatcher {

    /**
     * 开始监听
     *
     * @param clientProperties
     * @param callback
     */
    void start(ConfigClientProperties clientProperties, Callback callback);

    /**
     * 回调类
     */
    interface Callback {
        /**
         * 监听到配置变更时，执行该方法
         */
        void onChange();
    }
}
