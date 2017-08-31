/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.build;

import com.configx.web.model.Build;

import java.util.List;

/**
 * {@link com.configx.web.service.build.BuildPostProcessor} 处理
 *
 * @author Zhirong Zou
 */
public class BuildPostProcessorDelegate {

    /**
     * 预构建阶段的后处理
     *
     * @param buildPostProcessors
     * @param build
     */
    public static void applyPostProcessorsBeforeBuild(List<BuildPostProcessor> buildPostProcessors, Build build) {
        for (BuildPostProcessor BuildPostProcessor : buildPostProcessors) {
            BuildPostProcessor.postProcessBeforeBuild(build);
        }
    }

    /**
     * 构建完成阶段的后处理
     *
     * @param BuildPostProcessors
     * @param build
     */
    public static void applyPostProcessorsAfterBuild(List<BuildPostProcessor> BuildPostProcessors, Build build) {
        for (BuildPostProcessor BuildPostProcessor : BuildPostProcessors) {
            BuildPostProcessor.postProcessAfterBuild(build);
        }
    }

}
