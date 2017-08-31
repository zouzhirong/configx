/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.dto;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * 发布单表单
 *
 * @author Zhirong Zou
 */
@Data
public class ReleaseFormRequest {
    /**
     * 应用ID
     */
    private int appId;

    /**
     * 环境ID
     */
    private int envId;

    /**
     * 发布单名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 计划发布时间
     */
    private String planPubTime;

    public Date getPlanPubDate() throws ParseException {
        if (StringUtils.isEmpty(planPubTime)) {
            return null;
        }
        return DateUtils.parseDate(this.planPubTime, new String[]{"yyyy-MM-dd HH:mm:ss"});
    }

}
