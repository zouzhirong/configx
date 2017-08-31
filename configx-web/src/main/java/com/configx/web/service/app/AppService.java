/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.app;

import com.configx.web.dao.AppMapper;
import com.configx.web.model.App;
import com.configx.web.service.user.UserContext;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 应用 Service
 *
 * @author Zhirong Zou
 */
@Service
public class AppService {

    @Autowired
    private AppMapper appMapper;

    /**
     * 获取App列表
     *
     * @return
     */
    public List<App> getAppList() {
        return appMapper.selectAll();
    }

    /**
     * 获取用户是管理员角色的项目列表
     *
     * @param email
     * @return
     */
    public List<App> getAdminAppList(String email) {
        return appMapper.selectByAdmin(email);
    }

    /**
     * 获取用户是开发者角色的项目列表
     *
     * @param email
     * @return
     */
    public List<App> getDeveloperAppList(String email) {
        return appMapper.selectByDeveloper(email);
    }

    /**
     * 获取用户的项目列表
     *
     * @param email
     * @return
     */
    public List<App> getUserAppList(String email) {
        return appMapper.selectByAdminAndDeveloper(email);
    }

    /**
     * 获取用户默认的项目
     *
     * @param email
     * @return
     */
    public App getDefaultApp(String email) {
        List<App> appList = getUserAppList(email);
        return appList == null || appList.isEmpty() ? null : appList.get(0);
    }

    /**
     * 获取App
     *
     * @param appId
     * @param appKey
     * @return
     */
    public App getApp(int appId, String appKey) {
        App app = null;

        if (app == null && appId > 0) {
            app = getApp(appId);
        }

        if (app == null && StringUtils.isNotEmpty(appKey)) {
            app = getApp(appKey);
        }

        return app;
    }

    /**
     * 获取App
     *
     * @param id
     * @return
     */
    public App getApp(int id) {
        return appMapper.selectByPrimaryKey(id);
    }

    /**
     * 获取App
     *
     * @param appKey
     * @return
     */
    public App getApp(String appKey) {
        return appMapper.selectByAppKey(appKey);
    }

    /**
     * 创建新App
     *
     * @param name
     * @param description
     * @param admins
     * @param developers
     * @return
     */
    public App createApp(String name, String description, String admins, String developers) {
        App app = newApp(name, description, admins, developers);
        return createApp(app);
    }

    /**
     * 创建新App对象
     *
     * @param name
     * @param description
     * @param admins
     * @param developers
     * @return
     */
    private App newApp(String name, String description, String admins, String developers) {
        Date now = new Date();

        App app = new App();
        // 设置名称和描述
        app.setName(name);
        app.setDescription(description);
        app.setAppKey(RandomStringUtils.randomAlphanumeric(18));
        app.setAppSecret(RandomStringUtils.randomAlphanumeric(32));
        app.setAdmins(admins);
        app.setDevelopers(developers);

        app.setCreator(UserContext.name());
        app.setCreateTime(now);

        return app;
    }

    /**
     * 创建新App
     *
     * @param app
     * @return
     */
    public App createApp(App app) {
        appMapper.insertSelective(app);
        return app;
    }

    /**
     * 修改App信息
     *
     * @param appId
     * @param name
     * @param description
     * @param admins
     * @param developers
     */
    public void modifyApp(int appId, String name, String description, String admins, String developers) {
        App modifiedApp = new App();
        modifiedApp.setId(appId);
        modifiedApp.setName(name);
        modifiedApp.setDescription(description);
        modifiedApp.setAdmins(admins);
        modifiedApp.setDevelopers(developers);

        modifiedApp.setUpdater(UserContext.name());
        modifiedApp.setUpdateTime(new Date());

        updateApp(modifiedApp);
    }

    /**
     * 更新App信息
     *
     * @param app
     * @return
     */
    public App updateApp(App app) {
        appMapper.updateByPrimaryKeySelective(app);
        return app;
    }

    /**
     * 删除App
     *
     * @param id
     */
    public void delete(int id) {
        appMapper.deleteByPrimaryKey(id);
    }

    /**
     * 判断指定邮箱是否是指定App的管理员
     *
     * @param id
     * @param email
     * @return
     */
    public boolean isAdmin(int id, String email) {
        App app = getApp(id);
        String[] adminList = StringUtils.split(app.getAdmins(), ",");
        for (String admin : adminList) {
            if (admin.equals(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定邮箱是否是指定App的开发者
     *
     * @param id
     * @param email
     * @return
     */
    public boolean isDeveloper(int id, String email) {
        App app = getApp(id);
        String[] developerList = StringUtils.split(app.getDevelopers(), ",");
        for (String developer : developerList) {
            if (developer.equals(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户邮箱是否是指定应用的管理员或开发者
     *
     * @param id
     * @param email
     * @return
     */
    public boolean isAppAdminOrDeveloper(int id, String email) {
        return isAdmin(id, email) || isDeveloper(id, email);
    }
}
