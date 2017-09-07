/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.build;

import com.configx.web.dao.BuildConfigChangeMapper;
import com.configx.web.dao.BuildConfigItemMapper;
import com.configx.web.dao.BuildMapper;
import com.configx.web.model.*;
import com.configx.web.service.app.AppService;
import com.configx.web.service.app.EnvService;
import com.configx.web.service.app.ProfileContants;
import com.configx.web.service.config.ConfigItemHistoryService;
import com.configx.web.service.config.ConfigService;
import com.configx.web.service.config.ConfigValueService;
import com.configx.web.web.util.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 构建 Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class BuildService {

    /**
     * 构建后处理器
     */
    private final List<BuildPostProcessor> buildPostProcessors = new ArrayList<>();

    @Autowired
    private BuildMapper buildMapper;

    @Autowired
    private BuildConfigItemMapper buildConfigItemMapper;

    @Autowired
    private BuildConfigChangeMapper buildChangeMapper;

    @Autowired
    private AppService appService;

    @Autowired
    private EnvService envService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ConfigValueService valueService;

    @Autowired
    private ConfigItemHistoryService configItemHistoryService;

    /**
     * 添加后处理器
     *
     * @param BuildPostProcessor
     */
    public void addPostProcessor(BuildPostProcessor BuildPostProcessor) {
        this.buildPostProcessors.add(BuildPostProcessor);
    }

    /**
     * 返回后处理器
     *
     * @return
     */
    public List<BuildPostProcessor> getBuildPostProcessors() {
        return this.buildPostProcessors;
    }

    /**
     * 获取指定的构建
     *
     * @param buildId
     * @return
     */
    public Build getBuild(long buildId) {
        return buildMapper.selectByPrimaryKey(buildId);
    }

    /**
     * 交换回配置项中的配置值
     *
     * @param buildConfigItem
     * @return
     */
    private BuildConfigItem swapInValue(BuildConfigItem buildConfigItem) {
        if (buildConfigItem == null) {
            return buildConfigItem;
        }
        long valueId = buildConfigItem.getConfigValueId();
        if (valueId == 0) {
            return buildConfigItem;
        }
        String value = valueService.getValue(valueId);
        buildConfigItem.setConfigValue(value);
        return buildConfigItem;
    }

    /**
     * 交换回配置项中的配置值
     *
     * @param buildConfigItemList
     * @return
     */
    private List<BuildConfigItem> swapInValue(List<BuildConfigItem> buildConfigItemList) {
        if (buildConfigItemList == null || buildConfigItemList.isEmpty()) {
            return buildConfigItemList;
        }

        // 配置值ID列表
        List<Long> valueIdList = new ArrayList<>();

        // Map: build config item value id --> build config item
        Map<Long, BuildConfigItem> valueIdBuildConfigItemMap = new HashMap<>();

        long valueId = 0;
        for (BuildConfigItem buildConfigItem : buildConfigItemList) {
            valueId = buildConfigItem.getConfigValueId();
            if (valueId > 0) {
                valueIdList.add(valueId);
                valueIdBuildConfigItemMap.put(valueId, buildConfigItem);
            }
        }

        // 配置值Map: value id --> value string
        Map<Long, String> valueMap = valueService.getValueMap(valueIdList);

        String value = null;
        for (Map.Entry<Long, BuildConfigItem> entry : valueIdBuildConfigItemMap.entrySet()) {
            value = valueMap.get(entry.getKey());
            entry.getValue().setConfigValue(value);
        }

        return buildConfigItemList;
    }

    /**
     * 获取指定构建ID的构建的配置列表
     *
     * @param buildId
     * @return
     */
    public List<BuildConfigItem> getConfigItemList(long buildId) {
        List<BuildConfigItem> list = buildConfigItemMapper.selectByBuildId(buildId);
        return swapInValue(list);
    }

    /**
     * 获取指定构建ID的指定配置项
     *
     * @param buildId
     * @param configId
     * @return
     */
    public BuildConfigItem getConfigItem(long buildId, long configId) {
        BuildConfigItem bean = buildConfigItemMapper.selectByConfigId(buildId, configId);
        return swapInValue(bean);
    }

    /**
     * 获取指定构建中，指定的配置的信息
     *
     * @param appId
     * @param envId
     * @param buildId
     * @param profileIdList
     * @param changedConfigNames
     * @return
     */
    public List<BuildConfigItem> getConfigItemList(int appId, int envId, long buildId, List<Integer> profileIdList, Collection<String> changedConfigNames) {
        if (CollectionUtils.isEmpty(changedConfigNames)) {
            return Collections.emptyList();
        }
        List<String> changedConfigNameList = new ArrayList<>(changedConfigNames);
        List<BuildConfigItem> buildConfigItems = buildConfigItemMapper.selectByBuildIdAndConfigNames(buildId, changedConfigNameList);

        List<BuildConfigItem> filteredBuildConfigItems = new ArrayList<>();
        for (BuildConfigItem buildConfigItem : buildConfigItems) {
            if (profileIdList != null && profileIdList.contains(buildConfigItem.getProfileId())) {
                filteredBuildConfigItems.add(buildConfigItem);
            }
        }
        return swapInValue(filteredBuildConfigItems);
    }

    /**
     * 获取两次构建之间的Change List
     *
     * @param appId
     * @param envId
     * @param lastBuildId
     * @param buildId
     * @param profileIdList
     * @return
     */
    public List<BuildConfigItem> getChangedConfigItemList(int appId, int envId, long lastBuildId, long buildId, List<Integer> profileIdList) {
        // 获取两次构建之间的配置更改信息
        List<BuildConfigChange> changes = getChangedConfigBetween(appId, envId, lastBuildId, buildId, profileIdList);

        // 更改的配置名称列表
        Set<String> changedConfigNames = new LinkedHashSet<>();
        for (BuildConfigChange change : changes) {
            changedConfigNames.add(change.getConfigName());
        }

        // 获取当前构建时，那些更改了的配置名称对应的配置列表
        List<BuildConfigItem> changeConfigItemList = getConfigItemList(appId, envId, buildId, profileIdList, changedConfigNames);
        Map<String, BuildConfigItem> changeConfigItemMap = new HashMap<>();
        for (BuildConfigItem item : changeConfigItemList) {
            changeConfigItemMap.put(item.getConfigName(), item);
        }

        // 如果已经更改的配置名称在当前构建时没有配置信息，则说明删除了，构造一个已删除的配置信息
        for (String changedConfigName : changedConfigNames) {
            if (!changeConfigItemMap.containsKey(changedConfigName)) {
                changeConfigItemList.add(generateDeletedConfigItem(appId, envId, buildId, changedConfigName));
            }
        }
        return changeConfigItemList;
    }

    /**
     * 返回两次构建之间的Change List
     *
     * @param appId
     * @param envId
     * @param lastBuildId
     * @param buildId
     * @param profileIdList 限定Profile ID列表
     * @return
     */
    public List<BuildConfigChange> getChangedConfigBetween(int appId, int envId, long lastBuildId, long buildId, List<Integer> profileIdList) {
        // 如果是正常发布，则buildId比lastBuildId大；如果是回滚，则buildId比lastBuildId小
        long minId = Math.min(lastBuildId, buildId);
        long maxId = Math.max(lastBuildId, buildId);
        List<BuildConfigChange> buildConfigChanges = buildChangeMapper.selectChanges(appId, envId, minId, maxId);

        List<BuildConfigChange> filteredBuildConfigChanges = new ArrayList<>();
        for (BuildConfigChange buildConfigChange : buildConfigChanges) {
            if (profileIdList != null && profileIdList.contains(buildConfigChange.getProfileId())) {
                filteredBuildConfigChanges.add(buildConfigChange);
            }
        }
        return filteredBuildConfigChanges;
    }

    /**
     * 返回指定构建的Change List
     *
     * @param appId
     * @param envId
     * @param buildId
     * @return
     */
    public List<BuildConfigChange> getChangedConfigList(int appId, int envId, long buildId) {
        return buildChangeMapper.selectChangesByBuildId(buildId);
    }

    private BuildConfigItem generateDeletedConfigItem(int appId, int envId, long buildId, String configName) {
        BuildConfigItem item = new BuildConfigItem();

        item.setBuildId(buildId);
        item.setAppId(appId);
        item.setEnvId(envId);
        item.setProfileId(ProfileContants.DEFAULT_PROFILE_ID);
        item.setRevision(0L);
        item.setConfigId(0L);
        item.setConfigName(configName);
        item.setConfigValue(null);
        item.setConfigValueId(0L);
        item.setConfigTags("");

        return item;
    }

    /**
     * 构建
     *
     * @param appId
     * @param envId
     * @return
     */
    public Build build(int appId, int envId) {
        App app = appService.getApp(appId);
        Env env = envService.getEnv(envId);
        // 创建构建
        Build build = createBuild(app, env);

        // 应用 BuildPostProcessors
        BuildPostProcessorDelegate.applyPostProcessorsBeforeBuild(getBuildPostProcessors(), build);

        // 处理配置项信息
        processConfigItem(build, app, env);

        // 处理变更记录Change List
        processChangeList(build, app, env);

        // 应用 BuildPostProcessors
        BuildPostProcessorDelegate.applyPostProcessorsAfterBuild(getBuildPostProcessors(), build);

        return build;
    }

    /**
     * 创建新的构建
     *
     * @param app
     * @param env
     * @return
     */
    public Build createBuild(App app, Env env) {
        // 上次构建ID
        long lastBuildId = env.getBuildId();
        Build build = newBuild(lastBuildId, app.getId(), app.getName(), env.getId(), env.getName(), env.getRevision());
        build = insertBuild(build);
        return build;
    }

    /**
     * 创建新{@link com.configx.web.model.Build}对象
     *
     * @param lastBuildId
     * @param appId
     * @param appName
     * @param envId
     * @param envName
     * @param revision
     * @return
     */
    private Build newBuild(long lastBuildId, int appId, String appName, int envId, String envName, long revision) {
        Build build = new Build();

        build.setLastId(lastBuildId);
        build.setAppId(appId);
        build.setAppName(appName);
        build.setEnvId(envId);
        build.setEnvName(envName);
        build.setRevision(revision);
        build.setBuildTime(new Date());

        return build;
    }

    /**
     * 插入新的构建
     *
     * @param build
     * @return
     */
    public Build insertBuild(Build build) {
        buildMapper.insert(build);
        return build;
    }

    /**
     * 处理配置项
     *
     * @param build
     * @param app
     * @param env
     */
    public void processConfigItem(Build build, App app, Env env) {
        // 获取当前应用当前环境当前修订版的配置信息列表
        List<ConfigItem> configItemList = configService.getConfigItemList(app.getId(), env.getId());

        List<BuildConfigItem> buildConfigItemList = new ArrayList<>();
        BuildConfigItem buildConfigItem = null;
        for (ConfigItem configItem : configItemList) {
            if (configItem.getEnable()) {
                buildConfigItem = newBuildConfigItem(build.getId(), app, env, configItem);
                buildConfigItemList.add(buildConfigItem);
            }

        }

        insertBuildConfigItem(buildConfigItemList);
    }

    /**
     * 创建新的{@link com.configx.web.model.BuildConfigItem}对象
     *
     * @param buildId
     * @param app
     * @param env
     * @param configItem
     * @return
     */
    private BuildConfigItem newBuildConfigItem(long buildId, App app, Env env, ConfigItem configItem) {
        BuildConfigItem buildConfigItem = new BuildConfigItem();

        buildConfigItem.setBuildId(buildId);

        buildConfigItem.setAppId(app.getId());
        buildConfigItem.setEnvId(env.getId());
        buildConfigItem.setProfileId(configItem.getProfileId());

        buildConfigItem.setRevision(configItem.getRevision());
        buildConfigItem.setConfigId(configItem.getId());
        buildConfigItem.setConfigName(configItem.getName());
        buildConfigItem.setConfigValue(configItem.getValue());
        buildConfigItem.setConfigValueId(configItem.getValueId());
        buildConfigItem.setConfigTags(configItem.getTags());

        return buildConfigItem;
    }

    /**
     * 插入配置项
     *
     * @param buildConfigItemList
     */
    public void insertBuildConfigItem(List<BuildConfigItem> buildConfigItemList) {
        if (buildConfigItemList == null || buildConfigItemList.isEmpty()) {
            return;
        }
        for (BuildConfigItem buildConfigItem : buildConfigItemList) {
            buildConfigItemMapper.insert(buildConfigItem);
        }
    }

    /**
     * 处理构建的变更记录(Change List)
     *
     * @param build
     * @param app
     * @param env
     */
    public void processChangeList(Build build, App app, Env env) {
        // 上次构建信息
        Build lastBuild = getBuild(env.getBuildId());

        // 上次构建时的修订版本号
        long lastRevision = lastBuild == null ? 0 : lastBuild.getRevision();

        // 上次构建->此次构建之间的修订历史
        List<ConfigItemHistory> changes = configItemHistoryService.getChangeList(app.getId(), env.getId(), lastRevision, env.getRevision());

        // 上次构建->此次构建之间更改了的配置ID集合
        Set<Long> changedConfigIdSet = new HashSet<>();
        if (changes != null) {
            for (ConfigItemHistory change : changes) {
                if (!changedConfigIdSet.contains(change.getConfigId())) {
                    changedConfigIdSet.add(change.getConfigId());
                    insertChange(build.getId(), change);
                }
            }
        }

    }

    /**
     * 插入变更记录
     *
     * @param buildId
     * @param cvr
     */
    private void insertChange(long buildId, ConfigItemHistory cvr) {
        buildChangeMapper.insert(newBuildConfigChange(buildId, cvr));
    }

    /**
     * 创建新的{@link com.configx.web.model.BuildConfigChange}对象
     *
     * @param buildId
     * @param cvr
     * @return
     */
    private BuildConfigChange newBuildConfigChange(long buildId, ConfigItemHistory cvr) {
        BuildConfigChange buildChange = new BuildConfigChange();
        buildChange.setBuildId(buildId);
        buildChange.setAppId(cvr.getAppId());
        buildChange.setAppName(cvr.getAppName());
        buildChange.setEnvId(cvr.getEnvId());
        buildChange.setEnvName(cvr.getEnvName());
        buildChange.setProfileId(cvr.getProfileId());
        buildChange.setProfileName(cvr.getProfileName());

        buildChange.setConfigId(cvr.getConfigId());
        buildChange.setConfigName(cvr.getConfigName());
        return buildChange;
    }

    /**
     * @param buildId
     * @param configId
     */
    public Pair<BuildConfigItem> getChangeBetween(long buildId, long configId) {
        Build build = getBuild(buildId);

        // 上次构建ID
        long lastBuildId = build.getLastId();

        BuildConfigItem lastBuildConfigItem = getConfigItem(lastBuildId, configId);
        BuildConfigItem buildConfigItem = getConfigItem(buildId, configId);

        return new Pair<BuildConfigItem>(lastBuildConfigItem, buildConfigItem);
    }
}
