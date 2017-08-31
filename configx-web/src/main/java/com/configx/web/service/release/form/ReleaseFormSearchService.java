/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release.form;

import com.configx.web.model.ReleaseForm;
import com.configx.web.web.dto.ReleaseFormSearchForm;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 发布单搜索 Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class ReleaseFormSearchService {

    @Autowired
    private ReleaseFormService releaseFormService;

    /**
     * 搜索表单
     *
     * @param form
     * @return
     */
    public List<ReleaseForm> search(ReleaseFormSearchForm form) {
        // 发布单
        List<ReleaseForm> releaseForms = getReleaseForms(form.getAppId(), form.getEnvId());

        // 创建日期条件限定
        releaseForms = filterByCreateDate(releaseForms, form.getDateOfCreate());

        // 发布日期条件限定
        releaseForms = filterByReleaseDate(releaseForms, form.getDateOfRelease());

        // 根据ID 排序
        releaseForms = orderById(releaseForms);

        return releaseForms;
    }

    private List<ReleaseForm> getReleaseForms(int appId, int envId) {
        return releaseFormService.getReleaseForms(appId, envId);
    }

    /**
     * 使用创建日期来过滤发布单
     *
     * @param releaseForms
     * @param createDate
     * @return
     */
    private List<ReleaseForm> filterByCreateDate(List<ReleaseForm> releaseForms, Date createDate) {
        // 未限定
        if (createDate == null) {
            return releaseForms;
        }

        List<ReleaseForm> filteredReleaseFormList = new ArrayList<>();

        for (ReleaseForm form : releaseForms) {
            if (DateUtils.isSameDay(form.getCreateTime(), createDate)) {
                filteredReleaseFormList.add(form);
            }
        }

        return filteredReleaseFormList;
    }

    /**
     * 使用发布日期来过滤发布单
     *
     * @param releaseForms
     * @param releaseDate
     * @return
     */
    private List<ReleaseForm> filterByReleaseDate(List<ReleaseForm> releaseForms, Date releaseDate) {
        // 未限定
        if (releaseDate == null) {
            return releaseForms;
        }

        List<ReleaseForm> filteredReleaseFormList = new ArrayList<>();

        for (ReleaseForm form : releaseForms) {
            if (DateUtils.isSameDay(form.getCreateTime(), releaseDate)) {
                filteredReleaseFormList.add(form);
            }
        }

        return filteredReleaseFormList;
    }

    private ReleaseFormComparator releaseFormComparator = new ReleaseFormComparator();

    /**
     * 按ID排序
     *
     * @param releaseForms
     * @return
     */
    public List<ReleaseForm> orderById(List<ReleaseForm> releaseForms) {
        // 排序
        Collections.sort(releaseForms, releaseFormComparator);

        return releaseForms;
    }
}
