/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release.form;

/**
 * 发布单审核状态
 *
 * @author Zhirong Zou
 */
public class ReleaseFormAuditStatus {

    /**
     * 编辑中
     */
    public static final byte EDIT = (byte) 1;

    /**
     * 待审核
     */
    public static final byte AUDIT_PENDING = (byte) 2;

    /**
     * 通过
     */
    public static final byte AUDIT_PASSED = (byte) 3;

    /**
     * 驳回
     */
    public static final byte AUDIT_DISMISSED = (byte) 4;

}
