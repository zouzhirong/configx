/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.dto;

import lombok.Data;

/**
 * 配置提交搜索表单
 *
 * @author Zhirong Zou
 */
@Data
public class ConfigCommitSearchForm {
    /**
     * 修订号
     */
    private String revision;

    /**
     * 从修订号
     */
    private String fromRevision;

    /**
     * 到修订号
     */
    private String toRevision;

    /**
     * 应用ID
     */
    private int appId;

    /**
     * 环境ID
     */
    private int envId;

    /**
     * Profile Id
     */
    private int profileId;

    /**
     * 配置项名称
     */
    private String configName;
}
