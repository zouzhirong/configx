/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.config;

import com.configx.web.dao.ConfigItemHistoryMapper;
import com.configx.web.model.ConfigItemHistory;
import com.configx.web.page.Page;
import com.configx.web.page.PageRequest;
import com.configx.web.param.HistorySearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 配置历史搜索 Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class ConfigHistorySearchService {

    @Autowired
    private ConfigItemHistoryMapper configItemHistoryMapper;

    /**
     * 搜索配置历史
     *
     * @param revision
     * @param appId
     * @param envId
     * @param profileId
     * @param configId
     * @param configName
     * @return
     */
    public Page<Void, ConfigItemHistory> search(String revision,
                                                String fromRevision,
                                                String toRevision,
                                                int appId,
                                                int envId,
                                                int profileId,
                                                String configId,
                                                String configName,
                                                PageRequest pageRequest) {
        HistorySearchParam param = new HistorySearchParam();
        param.setRevision(revision);
        param.setRevisionRange(fromRevision, toRevision);
        param.setAppId(appId);
        param.setEnvId(envId);
        param.setProfileId(profileId);
        param.setConfigId(configId);
        param.setConfigName(configName);
        param.setPage(pageRequest.getPage(), pageRequest.getSize());

        int total = configItemHistoryMapper.countForSearch(param);
        List<ConfigItemHistory> historyList = configItemHistoryMapper.search(param);

        return new Page(total, pageRequest, historyList);
    }

}
