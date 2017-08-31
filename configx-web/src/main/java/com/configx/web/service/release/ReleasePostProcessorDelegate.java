/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release;

import java.util.List;

/**
 * {@link com.configx.web.service.release.ReleasePostProcessor} 处理
 *
 * @author Zhirong Zou
 */
public class ReleasePostProcessorDelegate {

    public static void applyPostProcessorsBeforeRelease(List<ReleasePostProcessor> releasePostProcessors, ReleaseContext context) {
        for (ReleasePostProcessor releasePostProcessor : releasePostProcessors) {
            releasePostProcessor.postProcessBeforeRelease(context);
        }
    }

    public static void applyPostProcessorsAfterRelease(List<ReleasePostProcessor> releasePostProcessors, ReleaseContext context) {
        for (ReleasePostProcessor releasePostProcessor : releasePostProcessors) {
            releasePostProcessor.postProcessAfterRelease(context);
        }
    }

    public static void applyPostProcessorsBeforeRollback(List<ReleasePostProcessor> releasePostProcessors, RollbackContext context) {
        for (ReleasePostProcessor releasePostProcessor : releasePostProcessors) {
            releasePostProcessor.postProcessBeforeRollback(context);
        }
    }

    public static void applyPostProcessorsAfterRollback(List<ReleasePostProcessor> releasePostProcessors, RollbackContext context) {
        for (ReleasePostProcessor releasePostProcessor : releasePostProcessors) {
            releasePostProcessor.postProcessAfterRollback(context);
        }
    }
}
