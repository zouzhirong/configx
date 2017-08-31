/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.app;

import com.configx.web.model.ReleaseForm;
import com.configx.web.model.ConfigItem;
import com.configx.web.model.Env;
import com.configx.web.service.release.form.ReleaseFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自动发布组件
 *
 * @author Zhirong Zou
 */
@Component
public class AutoReleaseComponent {

    @Autowired
    private EnvService envService;

    @Autowired
    private ReleaseFormService releaseFormService;

    /**
     * 自动发布
     *
     * @param configItem
     */
    public void release(ConfigItem configItem) {
        // 只有在自动发布模式下才能自动发布
        Env env = envService.getEnv(configItem.getEnvId());
        if (!env.getAutoRelease()) {
            return;
        }

        // 自动创建发布单
        ReleaseForm form = releaseFormService.createForm(configItem.getAppId(), configItem.getEnvId(), generateFormName(configItem), generateFormRemark(configItem), null);

        // 自动提交审核
        releaseFormService.submit(configItem.getAppId(), form.getId());

        // 自动审核通过
        releaseFormService.audit(form.getId(), true);

        // 自动发布
        releaseFormService.release(configItem.getAppId(), form.getId());
    }

    /**
     * 生成自动发布单名称
     *
     * @param configItem
     * @return
     */
    private String generateFormName(ConfigItem configItem) {
        return "auto-release-" + configItem.getName();
    }

    /**
     * 生成自动发布单备注
     *
     * @param configItem
     * @return
     */
    private String generateFormRemark(ConfigItem configItem) {
        return "auto release " + configItem.getName();
    }

}
