package com.configx.web.web.dto;

import com.configx.web.model.Release;
import com.configx.web.model.ReleaseForm;
import lombok.Data;

import java.util.Date;

/**
 * 发布单发布信息
 * <p>
 * Created by zouzhirong on 2016/11/7.
 */
@Data
public class ReleaseFormDto {

    private Long id;

    private Long releaseId;

    private Integer appId;

    private String appName;

    private Integer envId;

    private String envName;

    private String name;

    private String remark;

    private Date planPubTime;

    private String creator;

    private Date createTime;

    private String updater;

    private Date updateTime;

    private String auditor;

    private Byte auditStatus;

    private Date auditTime;


    private Byte releaseStatus;

    private String releaseUserCode;

    private String releaseIp;

    private Long releaseBuildId;

    private Date releaseTime;

    private String rollbackUserCode;

    private String rollbackIp;

    private Long rollbackBuildId;

    private Date rollbackTime;

    private String releaseMessage;

    private String rollbackMessage;

    public ReleaseFormDto(ReleaseForm form, Release release) {
        this.id = form.getId();
        this.releaseId = form.getReleaseId();
        this.appId = form.getAppId();
        this.appName = form.getAppName();
        this.envId = form.getEnvId();
        this.envName = form.getEnvName();
        this.name = form.getName();
        this.remark = form.getRemark();
        this.planPubTime = form.getPlanPubTime();
        this.creator = form.getCreator();
        this.createTime = form.getCreateTime();
        this.updater = form.getUpdater();
        this.updateTime = form.getUpdateTime();
        this.auditor = form.getAuditor();
        this.auditStatus = form.getAuditStatus();
        this.auditTime = form.getAuditTime();

        if (release != null) {
            this.releaseStatus = release.getReleaseStatus();
            this.releaseUserCode = release.getReleaseUserCode();
            this.releaseIp = release.getReleaseIp();
            this.releaseBuildId = release.getReleaseBuildId();
            this.releaseTime = release.getReleaseTime();
            this.rollbackUserCode = release.getRollbackUserCode();
            this.rollbackIp = release.getRollbackIp();
            this.rollbackBuildId = release.getRollbackBuildId();
            this.rollbackTime = release.getRollbackTime();
            this.releaseMessage = release.getReleaseMessage();
            this.rollbackMessage = release.getRollbackMessage();
        }
    }
}
