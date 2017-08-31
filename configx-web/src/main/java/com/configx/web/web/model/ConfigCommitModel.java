package com.configx.web.web.model;

import com.configx.web.model.ConfigCommit;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zouzhirong on 2017/8/22.
 */
@Data
public class ConfigCommitModel {

    /**
     * 修订号
     */
    private long revision;

    /**
     * 应用ID
     */
    private int appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 环境ID
     */
    private int envId;

    /**
     * 环境名称
     */
    private String envName;

    /**
     * Profile ID
     */
    private int profileId;

    /**
     * Profile名称
     */
    private String profileName;

    /**
     * '提交者'
     */
    private String author;

    /**
     * '提交日期'
     */
    private Date date;

    /**
     * '提交注释'
     */
    private String message;

    /**
     * 配置历史列表
     */
    private ConfigItemHistoryModel configItemHistory;

    public ConfigCommitModel(ConfigCommit commit, ConfigItemHistoryModel configItemHistory) {
        this.revision = commit.getRevision();
        this.appId = commit.getAppId();
        this.appName = commit.getAppName();
        this.envId = commit.getEnvId();
        this.envName = commit.getEnvName();
        this.profileId = commit.getProfileId();
        this.profileName = commit.getProfileName();
        this.author = commit.getAuthor();
        this.date = commit.getDate();
        this.message = commit.getMessage();
        this.configItemHistory = configItemHistory;
    }

    public static ConfigCommitModel wrap(ConfigCommit commit, ConfigItemHistoryModel history) {
        if (history == null) {
            return null;
        }
        return new ConfigCommitModel(commit, history);
    }

    public static List<ConfigCommitModel> wrap(List<ConfigCommit> commits, List<ConfigItemHistoryModel> historyList) {
        List<ConfigCommitModel> resultList = new ArrayList<>();

        ConfigItemHistoryModel history = null;
        for (ConfigCommit commit : commits) {
            history = findHistory(historyList, commit);
            resultList.add(new ConfigCommitModel(commit, history));
        }
        return resultList;
    }

    private static ConfigItemHistoryModel findHistory(List<ConfigItemHistoryModel> historyList, ConfigCommit commit) {
        if (historyList == null || historyList.isEmpty()) {
            return null;
        }
        for (ConfigItemHistoryModel e : historyList) {
            if (e.getRevision() == commit.getRevision()) {
                return e;
            }
        }
        return null;
    }

}
