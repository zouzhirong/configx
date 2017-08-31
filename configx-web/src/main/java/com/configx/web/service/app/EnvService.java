/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.app;

import com.configx.web.model.Env;
import com.configx.web.dao.EnvMapper;
import com.configx.web.service.user.UserContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 环境 Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class EnvService {

    @Autowired
    private EnvMapper envMapper;

    /**
     * 获取Env
     *
     * @param id
     * @return
     */
    public Env getEnv(int id) {
        return envMapper.selectByPrimaryKey(id);
    }

    /**
     * 获取Env
     *
     * @param appId
     * @param id
     * @return
     */
    public Env getEnv(int appId, int id) {
        Env env = getEnv(id);
        if (env == null || env.getAppId() != appId) {
            return null;
        } else {
            return env;
        }
    }

    /**
     * 获取Env，根据环境名称
     *
     * @param appId
     * @param name
     * @return
     */
    public Env getEnv(int appId, String name) {
        List<Env> envList = getEnvList(appId);
        if (envList == null || envList.isEmpty()) {
            return null;
        }
        for (Env env : envList) {
            if (env.getName().equalsIgnoreCase(name) || isAlias(env, name)) {
                return env;
            }
        }
        return null;
    }

    /**
     * 判断名字是否是别名
     *
     * @param env
     * @param name
     * @return
     */
    private boolean isAlias(Env env, String name) {
        if (StringUtils.isEmpty(env.getAlias())) {
            return false;
        }
        String[] aliases = StringUtils.split(env.getAlias(), ",");
        for (String alias : aliases) {
            if (alias != null && alias.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取Env列表
     *
     * @param appId
     * @return
     */
    public List<Env> getEnvList(int appId) {
        return envMapper.selectByAppId(appId);
    }

    /**
     * 获取默认的环境
     *
     * @param appId
     * @return
     */
    public Env getDefaultEnv(int appId) {
        List<Env> envList = getEnvList(appId);
        return envList == null || envList.isEmpty() ? null : envList.get(0);
    }

    /**
     * 创建新Env
     *
     * @param appId
     * @param name
     * @param alias
     * @param description
     * @param order
     * @return
     */
    public Env createEnv(int appId, String name, String alias, String description, int order) {
        Env env = newEnv(appId, name, alias, description, order);
        return createEnv(env);
    }

    /**
     * 创建新Env对象
     *
     * @param appId
     * @param name
     * @param alias
     * @param description
     * @param order
     * @return
     */
    private Env newEnv(int appId, String name, String alias, String description, int order) {
        Date now = new Date();

        Env env = new Env();
        env.setAppId(appId);
        env.setName(name);
        env.setAlias(alias);
        env.setDescription(description);
        env.setOrder(order);

        env.setCreator(UserContext.name());
        env.setCreateTime(now);
        return env;
    }

    /**
     * 创建新Env
     *
     * @param env
     * @return
     */
    public Env createEnv(Env env) {
        envMapper.insertSelective(env);
        return env;
    }

    /**
     * 修改Env信息
     *
     * @param id
     * @param name
     * @param alias
     * @param autoRelease
     * @param description
     * @param order
     */
    public void modifyEnv(int id, String name, String alias, boolean autoRelease, String description, int order) {
        Env env = getEnv(id);

        Env newEnv = new Env();
        newEnv.setId(env.getId());
        newEnv.setName(name);
        newEnv.setAlias(alias);
        newEnv.setAutoRelease(autoRelease);
        newEnv.setDescription(description);
        newEnv.setOrder(order);
        newEnv.setUpdater(UserContext.name());
        newEnv.setUpdateTime(new Date());

        updateEnv(newEnv);
    }

    /**
     * 更新Env信息
     *
     * @param env
     * @return
     */
    public Env updateEnv(Env env) {
        envMapper.updateByPrimaryKeySelective(env);
        return env;
    }

    /**
     * 删除Env
     *
     * @param appId
     * @param id
     */
    public void delete(int appId, int id) {
        Env env = getEnv(appId, id);
        if (env != null) {
            envMapper.deleteByPrimaryKey(id);
        }
    }

    /**
     * 数据更新后，更新环境信息中的相关信息
     *
     * @param envId
     * @param revision
     */
    public void updateRevisionInfo(int envId, long revision) {
        Env modifiedEnv = new Env();
        modifiedEnv.setId(envId);
        modifiedEnv.setRevision(revision);
        modifiedEnv.setDataChangeLastTime(new Date());

        updateEnv(modifiedEnv);
    }

    /**
     * 构建后，更新环境信息中的相关信息
     *
     * @param envId
     * @param buildId
     * @param buildTime
     */
    public void updateBuildInfo(int envId, long buildId, Date buildTime) {
        Env modifiedEnv = new Env();
        modifiedEnv.setId(envId);
        modifiedEnv.setBuildId(buildId);
        modifiedEnv.setBuildTime(buildTime);

        updateEnv(modifiedEnv);
    }

    /**
     * 版本发布后，更新环境信息中的相关信息
     *
     * @param envId
     * @param releaseVersion
     * @param releaseTime
     */
    public void updateReleaseVersion(int envId, long releaseVersion, Date releaseTime) {
        Env modifiedEnv = new Env();
        modifiedEnv.setId(envId);
        modifiedEnv.setReleaseVersion(releaseVersion);
        modifiedEnv.setReleaseTime(releaseTime);

        updateEnv(modifiedEnv);
    }
}
