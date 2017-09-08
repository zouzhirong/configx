/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.dto;

import com.configx.web.model.Release;
import com.configx.web.model.ReleaseForm;
import com.configx.web.model.ReleaseVersion;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;

import java.util.*;

/**
 * 发布单响应信息
 *
 * @author Zhirong Zou
 */
@Data
public class ReleaseFormListResponse {
    /**
     * 发布单列表
     */
    private List<ReleaseForm> releaseFormList;

    /**
     * 发布列表
     */
    private List<Release> releaseList;

    /**
     * 发布版本列表
     */
    private List<ReleaseVersion> releaseVersionList;

    public ReleaseFormListResponse(List<ReleaseForm> releaseFormList, List<Release> releaseList, List<ReleaseVersion> releaseVersionList) {
        this.releaseFormList = releaseFormList;
        this.releaseList = releaseList;
        this.releaseVersionList = releaseVersionList;
    }

    public List<ReleaseFormDto> getReleaseFormDtoList() {
        Map<Long, Release> releaseMap = new HashMap<>();
        if (releaseList != null) {
            for (Release release : releaseList) {
                releaseMap.put(release.getId(), release);
            }
        }

        Multimap<Long, ReleaseVersion> releaseVersionMultimap = ArrayListMultimap.create();
        if (releaseVersionList != null) {
            for (ReleaseVersion releaseVersion : releaseVersionList) {
                releaseVersionMultimap.put(releaseVersion.getReleaseId(), releaseVersion);
            }
        }

        List<ReleaseFormDto> dtoList = new ArrayList<>();
        Release release = null;
        Collection<ReleaseVersion> releaseVersions = null;
        for (ReleaseForm form : releaseFormList) {
            release = releaseMap.get(form.getReleaseId());
            releaseVersions = releaseVersionMultimap.get(form.getReleaseId());
            dtoList.add(new ReleaseFormDto(form, release, releaseVersions));
        }
        return dtoList;
    }
}
