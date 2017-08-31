/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.dto;

import com.configx.web.service.app.ProfileContants;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 配置搜索表单
 *
 * @author Zhirong Zou
 */
@Data
public class ConfigSearchForm {
    /**
     * 应用ID
     */
    private int appId;

    /**
     * 环境ID
     */
    private int envId;

    /**
     * Profile Id列表
     */
    private int profileId = ProfileContants.ALL_PROFILES;

    /**
     * 标签类型
     */
    private Integer[] tagId;

    /**
     * 配置值类型
     */
    private Byte[] valueType;

    /**
     * 配置ID，多个ID之间用逗号隔开
     */
    private String configId;

    /**
     * 配置名模式，多个模式之间用逗号隔开
     */
    private String configName;

    /**
     * 配置值模式，多个模式之间用逗号隔开
     */
    private String configValue;

    /**
     * 仅显示未发布的配置
     */
    private boolean onlyUnposted;

    /**
     * 仅显示禁用的配置
     */
    private boolean onlyDisabled;

    /**
     * 返回指定搜索的配置ID列表
     *
     * @return
     */
    public List<Long> getConfigIdList() {
        if (StringUtils.isEmpty(configId)) {
            return Collections.emptyList();
        } else {
            List<Long> configIdList = new ArrayList<>();
            for (String str : StringUtils.split(this.configId)) {
                try {
                    configIdList.add(Long.valueOf(str));
                } catch (Exception e) {
                    // Ingore
                }
            }
            return configIdList;
        }
    }

}
