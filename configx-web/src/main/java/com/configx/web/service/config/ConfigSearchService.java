/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.config;

import com.configx.web.service.app.ProfileContants;
import com.configx.web.service.release.version.ReleaseVersionService;
import com.configx.web.model.ConfigItem;
import com.configx.web.web.dto.ConfigSearchForm;
import name.fraser.neil.plaintext.diff_match_patch;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.php.internal.ui.util.SearchPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 配置搜索 Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class ConfigSearchService {

    private diff_match_patch dmp = new diff_match_patch();

    @Autowired
    private ConfigService configService;

    @Autowired
    private ReleaseVersionService releaseVersionService;

    public ConfigSearchService() {
        dmp.Match_Distance = Integer.MAX_VALUE; // Loose location.
        dmp.Match_Threshold = 0.3f;
    }

    /**
     * 搜索配置
     *
     * @param form
     * @return
     */
    public List<ConfigItem> search(ConfigSearchForm form) {
        // 必须指定应用、环境
        if (form.getAppId() == 0 || form.getEnvId() == 0) {
            return Collections.emptyList();
        }

        // 如果指定了ID，直接查找
        if (CollectionUtils.isNotEmpty(form.getConfigIdList())) {
            List<ConfigItem> itemList = configService.getConfigItemList(form.getConfigIdList());
            if (itemList == null) {
                return Collections.emptyList();
            } else {
                return itemList;
            }
        }

        // 获取应用的所有配置项列表
        List<ConfigItem> configItemList = configService.getConfigItemList(form.getAppId(), form.getEnvId());

        // Profile条件限定
        configItemList = filterByProfile(configItemList, asList(form.getProfileId()));

        // 标签条件限定
        configItemList = filterByTag(configItemList, asList(form.getTagId()));

        // 配置值类型条件限定
        configItemList = filterByValueType(configItemList, asList(form.getValueType()));

        // 配置名条件限定
        configItemList = filterByConfigName(configItemList, form.getConfigName());

        // 配置值条件限定
        configItemList = filterByConfigValue(configItemList, form.getConfigValue());

        // 仅显示未发布
        configItemList = filterByRelease(form.getAppId(), form.getEnvId(), configItemList, form.isOnlyUnposted());

        // 仅显示禁用的配置
        configItemList = filterByEnable(configItemList, form.isOnlyDisabled());

        // 根据ID 排序
        configItemList = order(configItemList);

        return configItemList;
    }

    @SafeVarargs
    private static <T> List<T> asList(T... a) {
        if (a == null) {
            return null;
        } else {
            return Arrays.asList(a);
        }
    }

    /**
     * 使用Profile来过滤配置项
     *
     * @param configItemList
     * @param profileIdList
     * @return
     */
    private List<ConfigItem> filterByProfile(List<ConfigItem> configItemList, List<Integer> profileIdList) {
        // 未限定
        if (profileIdList == null || profileIdList.isEmpty()) {
            return configItemList;
        }

        if (profileIdList.contains(ProfileContants.ALL_PROFILES)) {
            return configItemList;
        }

        List<ConfigItem> filteredConfigItemList = new ArrayList<>();

        for (ConfigItem configItem : configItemList) {
            if (profileIdList.contains(configItem.getProfileId())) {
                filteredConfigItemList.add(configItem);
            }
        }

        return filteredConfigItemList;
    }

    /**
     * 使用标签来过滤配置项
     *
     * @param configItemList
     * @param tagIdList
     * @return
     */
    private List<ConfigItem> filterByTag(List<ConfigItem> configItemList, List<Integer> tagIdList) {
        // 未限定
        if (tagIdList == null || tagIdList.isEmpty()) {
            return configItemList;
        }

        List<ConfigItem> filteredConfigItemList = new ArrayList<>();

        for (ConfigItem configItem : configItemList) {
            if (StringUtils.isNotEmpty(configItem.getTags())) {
                boolean tagged = false;
                for (String tagId : StringUtils.split(configItem.getTags(), ",")) {
                    if (tagIdList.contains(Integer.valueOf(tagId))) {
                        tagged = true;
                        break;
                    }
                }
                if (tagged) {
                    filteredConfigItemList.add(configItem);
                }
            }
        }

        return filteredConfigItemList;
    }

    /**
     * 使用配置值类型来过滤配置项
     *
     * @param configItemList
     * @param valueTypeList
     * @return
     */
    private List<ConfigItem> filterByValueType(List<ConfigItem> configItemList, List<Byte> valueTypeList) {
        // 未限定
        if (valueTypeList == null || valueTypeList.isEmpty()) {
            return configItemList;
        }

        List<ConfigItem> filteredConfigItemList = new ArrayList<>();

        for (ConfigItem configItem : configItemList) {
            if (valueTypeList.contains(configItem.getValueType())) {
                filteredConfigItemList.add(configItem);
            }
        }

        return filteredConfigItemList;
    }

    /**
     * 匹配配置名
     *
     * @param configItemList
     * @param configNamePattern
     * @return
     */
    public List<ConfigItem> filterByConfigName(List<ConfigItem> configItemList, String configNamePattern) {
        if (StringUtils.isEmpty(configNamePattern)) {
            return configItemList;
        }
        List<ConfigItem> matchedList = new ArrayList<>();

        for (ConfigItem configItem : configItemList) {
            if (SearchPattern.match(configNamePattern, configItem.getName(), false, true)) {
                matchedList.add(configItem);
            }
        }

        return matchedList;
    }

    /**
     * 匹配配置值
     *
     * @param configItemList
     * @param configValuePattern
     * @return
     */
    public List<ConfigItem> filterByConfigValue(List<ConfigItem> configItemList, String configValuePattern) {
        if (StringUtils.isEmpty(configValuePattern)) {
            return configItemList;
        }
        List<ConfigItem> matchedList = new ArrayList<>();

        for (ConfigItem configItem : configItemList) {
            if (configItem.getValue() != null) {
                long time = System.currentTimeMillis();
                int matchLoc = dmp.match_main(configItem.getValue(), configValuePattern, 0);
                System.out.println("Match time: " + (System.currentTimeMillis() - time));
                if (matchLoc != -1) {
                    matchedList.add(configItem);
                }
            }
        }

        return matchedList;
    }

    /**
     * 使用配置值是否已发布来过滤配置项
     *
     * @param appId
     * @param envId
     * @param configItemList
     * @param onlyUnposted
     * @return
     */
    private List<ConfigItem> filterByRelease(int appId, int envId, List<ConfigItem> configItemList, boolean onlyUnposted) {
        // 未限定
        if (!onlyUnposted) {
            return configItemList;
        }

        // 最新发布版本的修订号
        long latestReleaseRevision = releaseVersionService.getLatestReleaseRevision(appId, envId);

        List<ConfigItem> filteredConfigItemList = new ArrayList<>();

        for (ConfigItem configItem : configItemList) {
            if (configItem.getRevision() > latestReleaseRevision) {
                filteredConfigItemList.add(configItem);
            }
        }

        return filteredConfigItemList;
    }

    /**
     * 使用配置值启用|禁用来过滤配置项
     *
     * @param configItemList
     * @param onlyDisabled
     * @return
     */
    private List<ConfigItem> filterByEnable(List<ConfigItem> configItemList, boolean onlyDisabled) {
        // 未限定
        if (!onlyDisabled) {
            return configItemList;
        }

        List<ConfigItem> filteredConfigItemList = new ArrayList<>();

        for (ConfigItem configItem : configItemList) {
            if (!configItem.getEnable()) {
                filteredConfigItemList.add(configItem);
            }
        }

        return filteredConfigItemList;
    }

    private ConfigItemComparator configItemComparator = new ConfigItemComparator();

    /**
     * 按ID排序
     *
     * @param configItemList
     * @return
     */
    public List<ConfigItem> order(List<ConfigItem> configItemList) {
        Collections.sort(configItemList, configItemComparator);
        return configItemList;
    }
}
