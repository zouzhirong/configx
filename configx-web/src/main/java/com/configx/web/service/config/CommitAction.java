/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.config;

/**
 * 提交动作
 *
 * @author Zhirong Zou
 */
public enum CommitAction {

    /**
     * 添加
     */
    Added(1, "A"),

    /**
     * 修改
     */
    Modified(0, "M"),

    /**
     * 删除
     */
    Deleted(-1, "D"),;

    private int id;

    private String name;

    private CommitAction(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
