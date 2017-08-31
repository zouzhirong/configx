/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.config;

import com.configx.web.dao.ConfigItemMapper;
import com.configx.web.support.BeanUtils;
import com.configx.web.model.ConfigItem;
import com.configx.web.service.user.UserContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 配置 Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class ConfigService {

    @Autowired
    private ConfigItemMapper configItemMapper;

    @Autowired
    private ConfigValueService valueService;

    /**
     * 交换出配置项中的配置值
     *
     * @param configItem
     * @return
     */
    private void swapOutValue(ConfigItem configItem) {
        // 插入配置值
        long valueId = valueService.createValue(configItem.getValue());
        configItem.setValueId(valueId);
    }

    /**
     * 交换回配置项中的配置值
     *
     * @param configItem
     * @return
     */
    private ConfigItem swapInValue(ConfigItem configItem) {
        if (configItem == null) {
            return configItem;
        }
        long valueId = configItem.getValueId();
        if (valueId == 0) {
            return configItem;
        }
        String value = valueService.getValue(valueId);
        configItem.setValue(value);
        return configItem;
    }

    /**
     * 交换回配置项中的配置值
     *
     * @param configItemList
     * @return
     */
    private List<ConfigItem> swapInValue(List<ConfigItem> configItemList) {
        if (configItemList == null || configItemList.isEmpty()) {
            return configItemList;
        }

        // 配置值ID列表
        List<Long> valueIdList = new ArrayList<>();

        // Map: config item value id --> config item
        Map<Long, ConfigItem> valueIdConfigItemMap = new HashMap<>();

        long valueId = 0;
        for (ConfigItem configItem : configItemList) {
            valueId = configItem.getValueId();
            if (valueId > 0) {
                valueIdList.add(valueId);
                valueIdConfigItemMap.put(valueId, configItem);
            }
        }

        // 配置值Map: value id --> value string
        Map<Long, String> valueMap = valueService.getValueMap(valueIdList);

        String value = null;
        for (Map.Entry<Long, ConfigItem> entry : valueIdConfigItemMap.entrySet()) {
            value = valueMap.get(entry.getKey());
            entry.getValue().setValue(value);
        }

        return configItemList;
    }

    /**
     * 获取配置项列表
     *
     * @param appId
     * @return
     */
    public List<ConfigItem> getConfigItemList(int appId) {
        List<ConfigItem> configItemList = configItemMapper.selectByApp(appId);
        return swapInValue(configItemList);
    }

    /**
     * 获取配置项列表
     *
     * @param appId
     * @param envId
     * @return
     */
    public List<ConfigItem> getConfigItemList(int appId, int envId) {
        List<ConfigItem> configItemList = configItemMapper.selectByAppEnv(appId, envId);
        return swapInValue(configItemList);
    }

    /**
     * 获取配置项列表
     *
     * @param appId
     * @param envId
     * @param profileIdList
     * @return
     */
    public List<ConfigItem> getConfigItemList(int appId, int envId, List<Integer> profileIdList) {
        List<ConfigItem> configItemList = configItemMapper.selectByAppEnvProfileList(appId, envId, profileIdList);
        return swapInValue(configItemList);
    }

    /**
     * 获取配置项
     *
     * @param id
     * @return
     */
    public ConfigItem getConfigItem(long id) {
        ConfigItem configItem = configItemMapper.selectByPrimaryKey(id);
        return swapInValue(configItem);
    }

    /**
     * 获取配置项
     *
     * @param appId
     * @param id
     * @return
     */
    public ConfigItem getConfigItem(int appId, long id) {
        ConfigItem configItem = getConfigItem(id);
        if (configItem == null || configItem.getAppId() != appId) {
            return null;
        } else {
            return configItem;
        }
    }

    /**
     * 获取配置项，根据名称
     *
     * @param appId
     * @param envId
     * @param profileId
     * @param name
     * @return
     */
    public ConfigItem getConfigItem(int appId, int envId, int profileId, String name) {
        List<ConfigItem> configItemList = getConfigItemList(appId, envId, Arrays.asList(profileId));
        if (CollectionUtils.isEmpty(configItemList)) {
            return null;
        }

        for (ConfigItem configItem : configItemList) {
            if (name.equals(configItem.getName())) {
                return configItem;
            }
        }

        return null;
    }

    /**
     * 批量获取配置项
     *
     * @param idList
     * @return
     */
    public List<ConfigItem> getConfigItemList(List<Long> idList) {
        List<ConfigItem> configItemList = configItemMapper.selectByIds(idList);
        return swapInValue(configItemList);
    }

    /**
     * 创建配置项
     *
     * @param appId
     * @param envId
     * @param profileId
     * @param name
     * @param value
     * @param valueType
     * @param tagIdList
     * @param description
     * @return
     */
    public ConfigItem createConfigItem(int appId, int envId, int profileId, String name, String value, byte valueType, List<Integer> tagIdList, String description) {
        ConfigItem configItem = newConfigItem(appId, envId, profileId, name, value, valueType, tagIdList, description);
        createConfigItem(configItem);

        return configItem;
    }

    /**
     * 创建新{@link ConfigItem}项对象
     *
     * @param appId
     * @param envId
     * @param profileId
     * @param name
     * @param value
     * @param valueType
     * @param tagIdList
     * @param description
     * @return
     */
    private ConfigItem newConfigItem(int appId, int envId, int profileId, String name, String value, byte valueType, List<Integer> tagIdList, String description) {
        Date now = new Date();

        ConfigItem configItem = new ConfigItem();
        configItem.setAppId(appId);
        configItem.setEnvId(envId);
        configItem.setProfileId(profileId);
        configItem.setName(name);
        configItem.setValue(value);
        configItem.setValueType(valueType);
        configItem.setTags(StringUtils.join(tagIdList, ","));
        configItem.setDescription(description);

        configItem.setEnable(true);

        configItem.setCreator(UserContext.name());
        configItem.setCreateTime(now);
        return configItem;
    }

    /**
     * 创建新配置项
     *
     * @param configItem
     * @return
     */
    public ConfigItem createConfigItem(ConfigItem configItem) {
        // 交换出配置值
        if (configItem.getValue() != null) {
            if (configItem.getValueType() == ConfigValueType.FILE.getType()) {
                swapOutValue(configItem);
            }
        }

        configItemMapper.insertSelective(configItem);
        return configItem;
    }

    /**
     * 启用配置值
     *
     * @param id
     */
    public ConfigItem enableConfigItem(long id) {
        ConfigItem modifier = new ConfigItem();
        modifier.setId(id);
        modifier.setEnable(true);

        ConfigItem configItem = updateConfigItem(modifier);
        return configItem;
    }

    /**
     * 禁用配置值
     *
     * @param id
     */
    public ConfigItem disableConfigItem(long id) {
        ConfigItem modifier = new ConfigItem();
        modifier.setId(id);
        modifier.setEnable(false);

        ConfigItem configItem = updateConfigItem(modifier);
        return configItem;
    }

    /**
     * 修改配置项
     *
     * @param id
     * @param name
     * @param value
     * @param tagList
     * @param description
     * @return
     */
    public ConfigItem modifyConfigItem(long id, String name, String value, List<Integer> tagList, String description) {
        ConfigItem modifier = new ConfigItem();
        modifier.setId(id);
        modifier.setName(name);
        modifier.setValue(value);
        modifier.setTags(StringUtils.join(tagList, ","));
        modifier.setDescription(description);
        modifier.setUpdater(UserContext.name());
        modifier.setUpdateTime(new Date());

        ConfigItem configItem = updateConfigItem(modifier);
        return configItem;
    }

    /**
     * 更新配置项
     *
     * @param modifier 修改器
     * @return 更新后的配置项
     */
    public ConfigItem updateConfigItem(ConfigItem modifier) {
        // 更新之前的配置项
        ConfigItem configItem = getConfigItem(modifier.getId());
        byte valueType = configItem.getValueType();

        // 交换出配置值
        if (modifier.getValue() != null) {
            if (valueType == ConfigValueType.FILE.getType()) {
                swapOutValue(modifier);
            }
        }

        // 更新数据库
        configItemMapper.updateByPrimaryKeySelective(modifier);

        // 将更新到数据库的字段拷贝到bean中
        BeanUtils.copyPropertiesIgnoreNull(modifier, configItem);

        return configItem;
    }

    /**
     * 删除配置项
     *
     * @param appId
     * @param id
     * @return
     */
    public ConfigItem delete(int appId, long id) {
        ConfigItem configItem = getConfigItem(appId, id);
        if (configItem != null) {
            configItemMapper.deleteByPrimaryKey(id);
        }
        return configItem;
    }

    /**
     * 数据更新时，更新配置项信息中的相关信息
     *
     * @param configId
     * @param revision
     */
    public void onDataChange(long configId, long revision) {
        configItemMapper.updateRevisionInfo(configId, revision, new Date());
    }

}
