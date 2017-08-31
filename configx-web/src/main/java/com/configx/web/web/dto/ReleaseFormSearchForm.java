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
 * 发布单搜索表单
 *
 * @author Zhirong Zou
 */
@Data
public class ReleaseFormSearchForm {
    /**
     * 应用ID
     */
    private int appId;

    /**
     * 环境ID
     */
    private int envId;

    /**
     * 创建日期
     */
    private String createDate;

    /**
     * 发布日期
     */
    private String releaseDate;

    public Date getDateOfCreate() {
        if (StringUtils.isEmpty(createDate)) {
            return null;
        }
        try {
            return DateUtils.parseDate(this.createDate, new String[]{"yyyy-MM-dd"});
        } catch (ParseException e) {
            return null;
        }
    }

    public Date getDateOfRelease() {
        if (StringUtils.isEmpty(releaseDate)) {
            return null;
        }
        try {
            return DateUtils.parseDate(this.releaseDate, new String[]{"yyyy-MM-dd"});
        } catch (ParseException e) {
            return null;
        }
    }

}
