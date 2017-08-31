/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.release;

import com.configx.web.dao.ReleaseMapper;
import com.configx.web.service.app.AppService;
import com.configx.web.service.app.EnvService;
import com.configx.web.support.BeanUtils;
import com.configx.web.model.*;
import com.configx.web.service.build.BuildService;
import com.configx.web.service.user.UserContext;
import com.configx.web.web.util.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 发布 Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class ReleaseService {

    /**
     * 发布的executor
     */
    private ExecutorService executor;

    /**
     * 发布后处理器
     */
    private final List<ReleasePostProcessor> releasePostProcessors = new ArrayList<>();

    @Autowired
    private ReleaseMapper releaseMapper;

    @Autowired
    private AppService appService;

    @Autowired
    private EnvService envService;

    @Autowired
    private BuildService buildService;

    @PostConstruct
    public void init() {
        executor = Executors.newFixedThreadPool(5);
    }

    @PreDestroy
    public void destroy() {
        executor.shutdown();
        try {
            executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加后处理器
     *
     * @param releasePostProcessor
     */
    public void addPostProcessor(ReleasePostProcessor releasePostProcessor) {
        this.releasePostProcessors.add(releasePostProcessor);
    }

    /**
     * 返回后处理器
     *
     * @return
     */
    public List<ReleasePostProcessor> getReleasePostProcessors() {
        return this.releasePostProcessors;
    }

    /**
     * 获取指定的发布
     *
     * @param id
     * @return
     */
    public Release getRelease(long id) {
        return releaseMapper.selectByPrimaryKey(id);
    }

    /**
     * 获取多个发布信息
     *
     * @param idList
     * @return
     */
    public List<Release> getReleases(List<Long> idList) {
        return releaseMapper.selectByIdList(idList);
    }

    /**
     * 获取发布单的发布信息
     *
     * @param releaseFormList
     * @return
     */
    public List<Release> getReleaseList(List<ReleaseForm> releaseFormList) {
        if (CollectionUtils.isEmpty(releaseFormList)) {
            return Collections.emptyList();
        }

        List<Long> idList = new ArrayList<>();
        for (ReleaseForm form : releaseFormList) {
            idList.add(form.getReleaseId());
        }

        List<Release> releaseList = getReleases(idList);
        return releaseList;
    }

    /**
     * 获取指定的发布的状态
     *
     * @param id
     * @return
     */
    public int getReleaseStatus(long id) {
        Release release = releaseMapper.selectByPrimaryKey(id);
        return release == null ? 0 : release.getReleaseStatus();
    }

    /**
     * 异步发布
     *
     * @param appId 应用ID
     * @param envId 环境ID
     * @return 发布ID
     */
    public long asyncRelease(int appId, int envId) {
        Release release = createRelease(appId, envId);
        executor.submit(new ReleaseTask(release.getId()));
        return release.getId();
    }

    /**
     * 同步发布
     *
     * @param appId 应用ID
     * @param envId 环境ID
     * @return 发布信息
     */
    public Release release(int appId, int envId) {
        Release release = createRelease(appId, envId);
        return release(release.getId());
    }

    /**
     * 创建一次发布
     *
     * @param appId
     * @param envId
     * @return
     */
    private Release createRelease(int appId, int envId) {
        App app = appService.getApp(appId);
        Env env = envService.getEnv(envId);

        Release release = newRelease(app, env);
        release = insertRelease(release);
        return release;
    }

    /**
     * 创建新的{@link com.configx.web.model.Release}对象
     *
     * @param app
     * @param env
     * @return
     */
    private Release newRelease(App app, Env env) {
        Release release = new Release();

        release.setAppId(app.getId());
        release.setAppName(app.getName());
        release.setEnvId(env.getId());
        release.setEnvName(env.getName());

        // 发布操作人
        release.setReleaseUserCode(UserContext.name());
        // 发布操作人的机器IP
        release.setReleaseIp("");
        // 状态为：开始发布
        release.setReleaseStatus(ReleaseStatus.RELEASE_STARTED);

        return release;
    }

    /**
     * 插入发布信息
     *
     * @param release
     * @return
     */
    private Release insertRelease(Release release) {
        releaseMapper.insertSelective(release);
        return release;
    }

    /**
     * 开始发布
     */
    public Release release(long releaseId) {
        Release release = getRelease(releaseId);
        if (release == null) { // 不存在的发布ID
            return release;
        }

        App app = appService.getApp(release.getAppId());
        Env env = envService.getEnv(release.getEnvId());

        // 发布上下文
        ReleaseContext context = new ReleaseContext(app, env, release);

        // 预发布
        prepareRelease(context);

        Throwable ex = null;
        try {
            // 发布
            doRelease(context);
        } catch (Exception e) {
            e.printStackTrace();
            ex = e;
        }

        // 发布后
        if (ex == null) {
            postRelease(context);
        } else {
            postFailedRelease(context, ex);
        }

        return context.getRelease();
    }

    /**
     * 预发布
     *
     * @param context
     */
    private void prepareRelease(ReleaseContext context) {
        // 应用 ReleasePostProcessors
        ReleasePostProcessorDelegate.applyPostProcessorsBeforeRelease(getReleasePostProcessors(), context);
    }

    /**
     * 执行发布逻辑
     *
     * @param context
     */
    private void doRelease(ReleaseContext context) {
        // 构建
        Build build = buildService.build(context.getAppId(), context.getEnvId());
        context.setBuild(build);
    }

    /**
     * 发布成功的后处理
     *
     * @param context
     */
    private void postRelease(ReleaseContext context) {
        // 设置状态：发布完成
        // 发布时间
        updateReleaseResult(context.getRelease(), context.getBuild(), ReleaseStatus.RELEASE_FINISHED, new Date(), null);

        // 应用 ReleasePostProcessors
        ReleasePostProcessorDelegate.applyPostProcessorsAfterRelease(getReleasePostProcessors(), context);
    }

    /**
     * 发布失败的后处理
     *
     * @param context
     * @param e
     */
    private void postFailedRelease(ReleaseContext context, Throwable e) {
        // 设置状态：发布失败
        // 发布时间
        // 发布异常信息
        updateReleaseResult(context.getRelease(), context.getBuild(), ReleaseStatus.RELEASE_FAILED, new Date(), e.getMessage());
    }

    /**
     * 更新发布结果
     *
     * @param release
     * @param build
     * @param status
     * @param releaseTime
     * @param releaseMessage
     */
    private void updateReleaseResult(Release release, Build build, byte status, Date releaseTime, String releaseMessage) {
        Release modified = new Release();

        // 发布ID
        modified.setId(release.getId());

        // 发布状态
        modified.setReleaseStatus(status);

        if (build != null) {
            modified.setReleaseBuildId(build.getId());
        }

        modified.setReleaseTime(releaseTime);
        modified.setReleaseMessage(releaseMessage);

        // 更新DB
        releaseMapper.updateByPrimaryKeySelective(modified);

        // 将更新到数据库的字段拷贝到bean中
        BeanUtils.copyPropertiesIgnoreNull(modified, release);
    }

    /**
     * 异步回滾
     *
     * @param releaseId       回滚的发布ID
     * @param targetReleaseId 回滚到的目标发布ID
     */
    public void asyncRollback(long releaseId, long targetReleaseId) {
        executor.submit(new RollbackTask(releaseId, targetReleaseId));
    }

    /**
     * 回滚
     *
     * @param releaseId       回滚的发布ID
     * @param targetReleaseId 回滚到的目标发布ID
     * @return
     */
    public Release rollback(long releaseId, long targetReleaseId) {
        Release release = getRelease(releaseId);
        Release targetRelease = getRelease(targetReleaseId);

        App app = appService.getApp(release.getAppId());
        Env env = envService.getEnv(release.getEnvId());

        RollbackContext context = new RollbackContext(app, env, release, targetRelease);
        prepareRollback(context);

        Throwable ex = null;
        try {
            doRollback(context);
        } catch (Exception e) {
            e.printStackTrace();
            ex = e;
        }

        if (ex == null) {
            postRollback(context);
        } else {
            postFailedRollback(context, ex);
        }

        return release;
    }

    /**
     * 预回滚
     *
     * @param context
     */
    private void prepareRollback(RollbackContext context) {
        Release modified = new Release();
        // 回滚操作人
        modified.setRollbackUserCode(UserContext.name());
        // 回滚操作人的机器IP
        modified.setRollbackIp("");
        modified.setReleaseStatus(ReleaseStatus.ROLLBACK_STARTED);

        // 更新DB
        releaseMapper.updateByPrimaryKeySelective(modified);

        // 将更新到数据库的字段拷贝到bean中
        BeanUtils.copyPropertiesIgnoreNull(modified, context.getRelease());

        // 应用 ReleasePostProcessors
        ReleasePostProcessorDelegate.applyPostProcessorsBeforeRollback(getReleasePostProcessors(), context);
    }

    /**
     * 执行回滚
     *
     * @param context
     */
    private void doRollback(RollbackContext context) {
        // 回滚到的构建
        Build rollbackBuild = buildService.getBuild(context.getRollbackId());
        context.setBuild(rollbackBuild);
    }

    /**
     * 回滚成功的后处理
     *
     * @param context
     */
    private void postRollback(RollbackContext context) {
        // 设置状态：回滚成功
        // 设置回滚时间
        updateRollbackResult(context.getRelease(), context.getBuild(), ReleaseStatus.ROLLBACK_FINISHED, new Date(), null);

        // 应用 ReleasePostProcessors
        ReleasePostProcessorDelegate.applyPostProcessorsAfterRollback(getReleasePostProcessors(), context);
    }

    /**
     * 回滚失败的后处理
     *
     * @param context
     * @param e
     */
    private void postFailedRollback(RollbackContext context, Throwable e) {
        // 设置状态：回滚失败
        // 设置回滚时间
        // 设置回滚失败异常信息
        updateRollbackResult(context.getRelease(), context.getBuild(), ReleaseStatus.ROLLBACK_FAILED, new Date(), e.getMessage());
    }

    /**
     * 更新回滚结果
     *
     * @param release
     * @param build
     * @param status
     * @param rollbackTime
     * @param rollbackMessage
     */
    private void updateRollbackResult(Release release, Build build, byte status, Date rollbackTime, String rollbackMessage) {
        Release modified = new Release();

        // 发布ID
        modified.setId(release.getId());

        // 发布状态
        modified.setReleaseStatus(status);

        if (build != null) {
            modified.setRollbackBuildId(build.getId());
        }

        modified.setRollbackTime(rollbackTime);
        modified.setRollbackMessage(rollbackMessage);

        // 更新DB
        releaseMapper.updateByPrimaryKeySelective(modified);

        // 将更新到数据库的字段拷贝到bean中
        BeanUtils.copyPropertiesIgnoreNull(modified, release);
    }

    /**
     * 返回发布单的配置Change List
     *
     * @param releaseId
     * @return
     */
    public List<BuildConfigChange> getConfigChangeList(long releaseId) {
        Release release = getRelease(releaseId);
        if (release == null) {
            return Collections.emptyList();
        }
        return buildService.getChangedConfigList(release.getAppId(), release.getEnvId(), release.getReleaseBuildId());
    }

    public Pair<BuildConfigItem> getChangeBetween(long releaseId, long configId) {
        Release release = getRelease(releaseId);
        if (release == null) {
            return null;
        }
        return buildService.getChangeBetween(release.getReleaseBuildId(), configId);
    }
}
