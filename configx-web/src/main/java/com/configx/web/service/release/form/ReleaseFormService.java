/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release.form;

import com.configx.web.dao.ReleaseFormMapper;
import com.configx.web.service.app.AppService;
import com.configx.web.service.app.EnvService;
import com.configx.web.service.release.ReleaseService;
import com.configx.web.service.user.UserContext;
import com.configx.web.model.*;
import com.configx.web.service.release.ReleaseStatus;
import com.configx.web.web.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 发布单 Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class ReleaseFormService {

    @Autowired
    private ReleaseFormMapper releaseFormMapper;

    @Autowired
    private AppService appService;

    @Autowired
    private EnvService envService;

    @Autowired
    private ReleaseService releaseService;

    /**
     * 获取发布单列表
     *
     * @return
     */
    public List<ReleaseForm> getReleaseFormList() {
        return releaseFormMapper.selectByLowLimitId(0, Integer.MAX_VALUE);
    }

    /**
     * 获取指定应用的发布单列表
     *
     * @param appId
     * @return
     */
    public List<ReleaseForm> getReleaseForms(int appId) {
        return releaseFormMapper.selectByAppId(appId);
    }

    /**
     * 获取指定环境的发布单列表
     *
     * @param appId
     * @param envId
     * @return
     */
    public List<ReleaseForm> getReleaseForms(int appId, int envId) {
        return releaseFormMapper.selectByEnvId(appId, envId);
    }

    /**
     * 获取指定的发布单
     *
     * @param appId
     * @param formId
     * @return
     */
    public ReleaseForm getReleaseForm(int appId, long formId) {
        ReleaseForm form = getReleaseForm(formId);
        if (form == null || form.getAppId() != appId) {
            return null;
        } else {
            return form;
        }
    }

    /**
     * 获取指定的发布单
     *
     * @param formId
     * @return
     */
    public ReleaseForm getReleaseForm(long formId) {
        return releaseFormMapper.selectByPrimaryKey(formId);
    }

    /**
     * 创建发布单
     *
     * @param appId
     * @param envId
     * @param name
     * @param remark
     * @param planPubTime
     * @return
     */
    public ReleaseForm createForm(int appId, int envId, String name, String remark, Date planPubTime) {
        App app = appService.getApp(appId);
        Env env = envService.getEnv(envId);
        ReleaseForm form = newReleaseForm(app, env, name, remark, planPubTime);
        form = insertReleaseForm(form);

        return form;
    }

    /**
     * 创建新的{@link com.configx.web.model.ReleaseForm} 对象
     *
     * @param app
     * @param env
     * @param name
     * @param remark
     * @param planPubTime
     * @return
     */
    private ReleaseForm newReleaseForm(App app, Env env, String name, String remark, Date planPubTime) {
        Date now = new Date();

        ReleaseForm form = new ReleaseForm();

        form.setAppId(app.getId());
        form.setAppName(app.getName());
        form.setEnvId(env.getId());
        form.setEnvName(env.getName());

        form.setName(name);
        form.setRemark(remark);
        if (planPubTime != null) {
            form.setPlanPubTime(planPubTime);
        }

        form.setAuditStatus(ReleaseFormAuditStatus.EDIT);

        form.setCreator(UserContext.name());
        form.setCreateTime(now);

        return form;
    }

    /**
     * 插入发布单
     *
     * @param form
     * @return
     */
    private ReleaseForm insertReleaseForm(ReleaseForm form) {
        releaseFormMapper.insertSelective(form);
        return form;
    }

    /**
     * 修改发布单
     *
     * @param appId
     * @param formId
     * @param name
     * @param remark
     * @param planPubTime
     */
    public void modifyForm(int appId, long formId, String name, String remark, Date planPubTime) {
        if (getReleaseForm(appId, formId) == null) {
            return;
        }
        ReleaseForm form = new ReleaseForm();
        form.setId(formId);
        form.setName(name);
        form.setRemark(remark);
        form.setPlanPubTime(planPubTime);

        form.setUpdater(UserContext.name());
        form.setUpdateTime(new Date());

        releaseFormMapper.updateByPrimaryKeySelective(form);
    }

    /**
     * 删除发布单
     *
     * @param appId
     * @param formId
     */
    public void delete(int appId, long formId) {
        ReleaseForm form = getReleaseForm(appId, formId);
        if (form != null) {
            releaseFormMapper.deleteByPrimaryKey(formId);
        }
    }

    /**
     * 提交审核
     *
     * @param appId
     * @param formId
     */
    public void submit(int appId, long formId) {
        ReleaseForm form = getReleaseForm(appId, formId);
        if (form == null) {
            return;
        }
        if (form.getAuditStatus() != ReleaseFormAuditStatus.EDIT) {
            return;
        }

        ReleaseForm updateRecord = new ReleaseForm();
        updateRecord.setId(formId);
        updateRecord.setAuditStatus(ReleaseFormAuditStatus.AUDIT_PENDING);
        releaseFormMapper.updateByPrimaryKeySelective(updateRecord);
    }

    /**
     * 审核
     *
     * @param formId
     * @param passed
     */
    public void audit(long formId, boolean passed) {
        ReleaseForm form = releaseFormMapper.selectByPrimaryKey(formId);
        if (form == null) {
            return;
        }
        if (form.getAuditStatus() != ReleaseFormAuditStatus.AUDIT_PENDING) {
            return;
        }

        ReleaseForm updateRecord = new ReleaseForm();
        updateRecord.setId(formId);
        updateRecord.setAuditor("");
        updateRecord.setAuditStatus(passed ? ReleaseFormAuditStatus.AUDIT_PASSED : ReleaseFormAuditStatus.AUDIT_DISMISSED);
        updateRecord.setAuditTime(new Date());
        releaseFormMapper.updateByPrimaryKeySelective(updateRecord);
    }

    /**
     * 发布
     *
     * @param appId
     * @param formId 发布单ID
     */
    public void release(int appId, long formId) {
        synchronized ("form-release-" + formId) { // 发布同步锁

            ReleaseForm form = getReleaseForm(appId, formId);
            if (form == null) { // 发布单不存在
                return;
            }

            if (form.getReleaseId() > 0) { // 已经发布过
                return;
            }

            if (form.getAuditStatus() != ReleaseFormAuditStatus.AUDIT_PASSED) { // 未审核通过
                return;
            }

            // 发布
            long releaseId = releaseService.asyncRelease(form.getAppId(), form.getEnvId());
            // 更新发布单中的发布ID
            updateReleaseId(formId, releaseId);
        }
    }

    /**
     * 更新发布单的发布ID
     *
     * @param formId
     * @param releaseId
     */
    private void updateReleaseId(long formId, long releaseId) {
        ReleaseForm moidfied = new ReleaseForm();
        moidfied.setId(formId);
        moidfied.setReleaseId(releaseId);
        releaseFormMapper.updateByPrimaryKeySelective(moidfied);
    }

    /**
     * 回滚到上一个发布单
     *
     * @param appId
     * @param formId
     */
    public void rollback(int appId, long formId) {
        ReleaseForm form = getReleaseForm(appId, formId);
        if (form == null) { // 发布单不存在
            return;
        }

        ReleaseForm lastForm = releaseFormMapper.getLastForm(form.getAppId(), form.getEnvId(), formId);
        if (lastForm == null) { // 没有以前的发布单
            return;
        }

        rollbackTo(formId, lastForm.getId());
    }

    /**
     * 回滚到指定的发布单
     *
     * @param formId
     * @param targetFormId
     */
    public void rollbackTo(long formId, long targetFormId) {
        ReleaseForm form = getReleaseForm(formId);
        if (form == null) { // 发布单不存在
            return;
        }

        Release release = releaseService.getRelease(form.getReleaseId());
        if (release == null) { // 没有发布过
            return;
        }

        if (release.getReleaseStatus() != ReleaseStatus.RELEASE_FINISHED) { // 只有成功发布后，才能回滚
            return;
        }

        ReleaseForm targetForm = getReleaseForm(targetFormId);
        releaseService.asyncRollback(form.getReleaseId(), targetForm.getReleaseId());
    }

    /**
     * 返回发布单的配置Change List
     *
     * @param formId
     * @return
     */
    public List<BuildConfigChange> getConfigChangeList(long formId) {
        ReleaseForm form = getReleaseForm(formId);
        if (form == null) {
            return Collections.emptyList();
        }
        return releaseService.getConfigChangeList(form.getReleaseId());
    }

    public Pair<BuildConfigItem> getChangeBetween(long formId, long configId) {
        ReleaseForm form = getReleaseForm(formId);
        if (form == null) {
            return null;
        }
        return releaseService.getChangeBetween(form.getReleaseId(), configId);
    }
}
