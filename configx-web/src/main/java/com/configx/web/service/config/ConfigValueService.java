/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.config;

import com.configx.web.dao.ConfigValueMapper;
import com.configx.web.model.ConfigValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置值 Service
 * 处理大的配置值
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class ConfigValueService {

    @Autowired
    private ConfigValueMapper configValueMapper;

    /**
     * 获取指定ID的值
     *
     * @param id
     * @return
     */
    public String getValue(long id) {
        ConfigValue configValue = configValueMapper.selectByPrimaryKey(id);
        return configValue == null ? null : configValue.getValue();
    }

    /**
     * 批量获取指定ID的值
     *
     * @param idList
     * @return
     */
    public Map<Long, String> getValueMap(List<Long> idList) {
        if (idList == null || idList.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ConfigValue> configValueList = configValueMapper.getByIdList(idList);
        if (configValueList == null || configValueList.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, String> valueMap = new HashMap<>();

        for (ConfigValue configValue : configValueList) {
            valueMap.put(configValue.getId(), configValue.getValue());
        }

        return valueMap;
    }

    /**
     * 创建值
     *
     * @param value 值
     * @return 值ID
     */
    public long createValue(String value) {
        ConfigValue configValue = newConfigValue(value);
        createConfigValue(configValue);
        return configValue.getId();
    }

    /**
     * 创建新{@link ConfigValue}项对象
     *
     * @param value
     * @return
     */
    private ConfigValue newConfigValue(String value) {
        ConfigValue configValue = new ConfigValue();

        configValue.setValue(value);

        return configValue;
    }

    /**
     * 创建新配置值
     *
     * @param configValue
     * @return
     */
    public ConfigValue createConfigValue(ConfigValue configValue) {
        configValueMapper.insert(configValue);
        return configValue;
    }

}
