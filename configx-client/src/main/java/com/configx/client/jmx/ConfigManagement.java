package com.configx.client.jmx;

import com.configx.client.env.ConfigVersionManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zouzhirong on 2017/9/12.
 */
public class ConfigManagement {

    /**
     * 获取最新版本号
     *
     * @return
     */
    public long getHeadVersion() {
        return ConfigVersionManager.getHeadVersion();
    }

    /**
     * 获取所有版本列表
     *
     * @return
     */
    public List<Long> getVersions() {
        return ConfigVersionManager.getVersions();
    }

    /**
     * 获取所有线程引用的配置版本信息
     *
     * @return
     */
    public Map<String, Long> getThreadVersionMap() {
        Map<String, Long> threadNameVersionMap = new LinkedHashMap<>();

        Map<Thread, Long> threadLongMap = ConfigVersionManager.getThreadVersionMap();
        if (threadLongMap != null) {
            for (Map.Entry<Thread, Long> entry : threadLongMap.entrySet()) {
                threadNameVersionMap.put(entry.getKey().getName(), entry.getValue());
            }
        }

        return threadNameVersionMap;
    }
}
