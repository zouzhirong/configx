/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release;

/**
 * 发布状态
 *
 * @author Zhirong Zou
 */
public class ReleaseStatus {

    /**
     * 发布中
     */
    public static final byte RELEASE_STARTED = (byte) 1;

    /**
     * 发布成功
     */
    public static final byte RELEASE_FINISHED = (byte) 2;

    /**
     * 发布失败
     */
    public static final byte RELEASE_FAILED = (byte) 3;

    /**
     * 回滚中
     */
    public static final byte ROLLBACK_STARTED = (byte) 4;

    /**
     * 回滚成功
     */
    public static final byte ROLLBACK_FINISHED = (byte) 5;

    /**
     * 回滚失败
     */
    public static final byte ROLLBACK_FAILED = (byte) 6;

}
