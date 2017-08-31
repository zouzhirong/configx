/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.config;

/**
 * 配置值类型
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
public enum ConfigValueType {
    /**
     * 文件
     */
    FILE((byte) 0),

    /**
     * 文本
     */
    TEXT((byte) 1),

    /**
     * 数字
     */
    NUMBER((byte) 2),

    /**
     * 布尔
     */
    BOOLEAN((byte) 3),

    /**
     * 日期
     */
    DATE((byte) 4),

    /**
     * 时间
     */
    TIME((byte) 5),

    /**
     * 日期时间
     */
    DATETIME((byte) 6),;

    private byte type;

    private ConfigValueType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
