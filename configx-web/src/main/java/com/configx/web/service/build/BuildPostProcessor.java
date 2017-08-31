/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.build;

import com.configx.web.model.Build;

/**
 * 构建后处理器
 *
 * @author Zhirong Zou
 */
public interface BuildPostProcessor {

    /**
     * 预构建阶段的后处理
     *
     * @param build
     */
    void postProcessBeforeBuild(Build build);

    /**
     * 构建完成阶段的后处理
     *
     * @param context
     */
    void postProcessAfterBuild(Build build);

}
