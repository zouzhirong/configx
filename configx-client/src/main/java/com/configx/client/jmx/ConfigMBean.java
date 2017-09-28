package com.configx.client.jmx;

import com.configx.client.version.ConfigVersionManager;
import com.configx.client.version.ConfigVersionManager.ThreadVersionInfo;
import com.configx.client.version.VersionContextHolder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Jmx MBean
 * <p>
 * Created by zouzhirong on 2017/9/12.
 */
public class ConfigMBean {

    /**
     * 获取最新版本号
     *
     * @return
     */
    public long getHeadVersion() {
        return ConfigVersionManager.getHeadVersion();
    }

    /**
     * 获取线程可见的最新版本号
     *
     * @return
     */
    public long getVisibleVersion() {
        return VersionContextHolder.getVisibleVersion();
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
     * 获取配置Bean版本号列表
     *
     * @return
     */
    public List<Long> getBeanVersions() {
        return ConfigVersionManager.getBeanVersions();
    }

    /**
     * 获取线程引用的版本号信息
     *
     * @return
     */
    public Map<Long, Collection<String>> getThreadVersions() {
        Map<Long, Collection<String>> versionThreadNameMap = new LinkedHashMap<>();

        List<ThreadVersionInfo> threadVersionInfos = ConfigVersionManager.getThreadVersionInfos();
        if (threadVersionInfos != null) {
            for (ThreadVersionInfo threadVersionInfo : threadVersionInfos) {
                versionThreadNameMap.put(threadVersionInfo.getVersion(), threadVersionInfo.getThreadNames());
            }
        }

        return versionThreadNameMap;
    }

}
