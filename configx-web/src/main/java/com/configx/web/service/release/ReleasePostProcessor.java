/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release;


/**
 * 发布后处理器
 *
 * @author Zhirong Zou
 */
public interface ReleasePostProcessor {

    /**
     * 预发布阶段的后处理
     *
     * @param context
     */
    void postProcessBeforeRelease(ReleaseContext context);

    /**
     * 发布完成阶段的后处理
     *
     * @param context
     */
    void postProcessAfterRelease(ReleaseContext context);

    /**
     * 预回滚阶段的后处理
     *
     * @param context
     */
    void postProcessBeforeRollback(RollbackContext context);

    /**
     * 回滚完成阶段的后处理
     *
     * @param context
     */
    void postProcessAfterRollback(RollbackContext context);
}
