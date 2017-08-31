package com.configx.web.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zouzhirong on 2017/7/6.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommitSearchParam {

    /**
     * 应用ID
     */
    private int appId;

    /**
     * 环境ID
     */
    private int envId;

    /**
     * Profile Id
     */
    private int profileId;

    private int offset;

    private int limit;

    public void setPage(int page, int size) {
        this.offset = (page - 1) * size;
        this.limit = size;
    }


}
