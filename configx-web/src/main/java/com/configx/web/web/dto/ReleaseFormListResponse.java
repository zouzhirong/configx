/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.dto;

import com.configx.web.model.Release;
import com.configx.web.model.ReleaseForm;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public ReleaseFormListResponse(List<ReleaseForm> releaseFormList, List<Release> releaseList) {
        this.releaseFormList = releaseFormList;
        this.releaseList = releaseList;
    }

    public List<ReleaseFormDto> getReleaseFormDtoList() {
        Map<Long, Release> releaseMap = new HashMap<>();
        if (releaseList != null) {
            for (Release release : releaseList) {
                releaseMap.put(release.getId(), release);
            }
        }

        List<ReleaseFormDto> dtoList = new ArrayList<>();
        Release release = null;
        for (ReleaseForm form : releaseFormList) {
            release = releaseMap.get(form.getReleaseId());
            dtoList.add(new ReleaseFormDto(form, release));
        }
        return dtoList;
    }
}
