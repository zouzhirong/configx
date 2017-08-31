package com.configx.web.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

/**
 * Created by zouzhirong on 2017/7/6.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorySearchParam {

    /**
     * 修订号
     */
    private Long revision;

    /**
     * 从修订号
     */
    private Long fromRevision;

    /**
     * 到修订号
     */
    private Long toRevision;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 环境ID
     */
    private Integer envId;

    /**
     * Profile Id列表
     */
    private Integer profileId;

    /**
     * 配置项ID
     */
    private Long configId;

    /**
     * 配置项名称
     */
    private String configName;

    private int offset;

    private int limit;

    public void setRevision(String revision) {
        if (StringUtils.isNotEmpty(revision)) {
            this.revision = Long.valueOf(revision);
        }
    }

    public void setRevisionRange(String fromRevision, String toRevision) {
        if (StringUtils.isNotEmpty(fromRevision)) {
            this.fromRevision = Long.valueOf(fromRevision);
        }
        if (StringUtils.isNotEmpty(toRevision)) {
            this.toRevision = Long.valueOf(toRevision);
        }
    }

    public void setConfigId(String configId) {
        if (StringUtils.isNotEmpty(configId)) {
            this.configId = Long.valueOf(configId);
        }
    }

    public void setConfigName(String configName) {
        if (StringUtils.isNotEmpty(configName)) {
            this.configName = configName;
        }
    }

    public void setPage(int page, int size) {
        this.offset = (page - 1) * size;
        this.limit = size;
    }


}
