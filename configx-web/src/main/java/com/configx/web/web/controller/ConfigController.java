/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.controller;

import com.configx.web.service.app.AppService;
import com.configx.web.service.release.version.ReleaseVersionService;
import com.configx.web.model.*;
import com.configx.web.page.Page;
import com.configx.web.page.PageRequest;
import com.configx.web.service.app.EnvService;
import com.configx.web.service.app.ProfileService;
import com.configx.web.service.app.TagService;
import com.configx.web.service.config.ConfigCommitService;
import com.configx.web.service.config.ConfigSearchService;
import com.configx.web.service.config.ConfigService;
import com.configx.web.service.config.ConfigValueType;
import com.configx.web.service.privilege.PrivilegeService;
import com.configx.web.service.user.UserContext;
import com.configx.web.web.dto.ConfigSearchForm;
import com.configx.web.web.model.ConfigItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

/**
 * 管理配置项 Controller
 *
 * @author Zhirong Zou
 */
@Controller
@RequestMapping("/")
public class ConfigController {

    @Autowired
    private AppService appService;

    @Autowired
    private EnvService envService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private TagService tagService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ConfigCommitService configCommitService;

    @Autowired
    private ConfigSearchService configSearchService;

    @Autowired
    private ReleaseVersionService releaseVersionService;

    @Autowired
    private PrivilegeService privilegeService;

    /**
     * 搜索主页
     *
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/config")
    public ModelAndView getConfigList(@PathVariable("appId") int appId) {
        Env env = envService.getDefaultEnv(appId);
        return new ModelAndView(new RedirectView("/apps/" + appId + "/config/" + env.getName()));
    }

    /**
     * 搜索主页
     *
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/config/{envName}")
    public ModelAndView searchConfigList(@PathVariable("appId") int appId,
                                         @PathVariable("envName") String envName,
                                         @ModelAttribute ConfigSearchForm form,
                                         @RequestParam(name = "page", required = false, defaultValue = "1") int page, @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        App app = appService.getApp(form.getAppId());
        Env env = envService.getEnv(appId, envName);

        form.setAppId(app.getId());
        form.setEnvId(env.getId());

        Profile profile = null;
        if (form.getProfileId() > 0) {
            profile = profileService.getProfile(appId, form.getProfileId());
        } else {
            profile = profileService.defaultProfile(appId);
        }

        // 权限认证
        if (app == null || env == null
                || !privilegeService.isAppAdminOrDeveloper(form.getAppId(), UserContext.email())) {
            return new ModelAndView(new RedirectView("/apps/empty"));
        }

        List<App> appList = appService.getUserAppList(UserContext.email());
        List<Env> envList = envService.getEnvList(appId);

        List<Profile> profileList = profileService.getProfileList(appId);
        List<Tag> tagList = tagService.getTagList(appId);

        List<ConfigItem> configItemList = configSearchService.search(form);
        List<ConfigItemModel> configItemModelList = ConfigItemModel.wrap(configItemList, app, envList, profileList, tagList);

        PageRequest pageRequest = new PageRequest(page, size);
        Page<Void, ConfigItemModel> pageResponse = new Page<>(configItemModelList, pageRequest);

        // 最新发布的修订号
        long releaseRevision = releaseVersionService.getLatestReleaseRevision(appId, env.getId());

        // 最新修订版本号
        long revision = env.getRevision();
        ConfigCommit latestCommit = configCommitService.getCommit(revision);

        return new ModelAndView("config/config").addObject("form", form).
                addObject("valueTypes", ConfigValueType.values()).
                addObject("appList", appList).addObject("app", app).
                addObject("envList", envList).addObject("env", env).
                addObject("profileList", profileList).addObject("profile", profile).
                addObject("tagList", tagList).
                addObject("releaseRevision", releaseRevision).
                addObject("latestCommit", latestCommit).
                addObject("page", pageResponse);
    }

    /**
     * 查看文件
     *
     * @param appId
     * @param envName
     * @param profileName
     * @param filename
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/config/file/{envName}/{profileName}/{filename:.+}")
    public ModelAndView viewConfigFile(@PathVariable("appId") int appId,
                                       @PathVariable("envName") String envName,
                                       @PathVariable("profileName") String profileName,
                                       @PathVariable("filename") String filename) {

        App app = appService.getApp(appId);
        Env env = envService.getEnv(appId, envName);
        Profile profile = profileService.getProfile(appId, profileName);

        List<Profile> profileList = profileService.getProfileList(appId);
        List<Tag> tagList = tagService.getTagList(appId);

        ConfigItem configItem = configService.getConfigItem(appId, env.getId(), profile.getId(), filename);
        ConfigItemModel config = ConfigItemModel.wrap(app, env, configItem, profileList, tagList);

        return new ModelAndView("config/file_view")
                .addObject("appId", appId).addObject("app", app)
                .addObject("envId", env.getId()).addObject("env", env)
                .addObject("profile", profile)
                .addObject("config", config);
    }

    /**
     * 使用文件编辑器新建配置项
     *
     * @param appId
     * @param copyId 复制的配置项ID
     * @return
     */

    @RequestMapping(value = "/apps/{appId}/config/file/new/{envName}/{profileName}")
    public ModelAndView newConfigFile(@PathVariable("appId") int appId,
                                      @PathVariable("envName") String envName,
                                      @PathVariable("profileName") String profileName,
                                      @RequestParam(name = "copyId", required = false, defaultValue = "0") long copyId) {
        App app = appService.getApp(appId);
        Env env = envService.getEnv(appId, envName);
        Profile profile = profileService.getProfile(appId, profileName);

        List<Env> envList = envService.getEnvList(appId);
        List<Profile> profileList = profileService.getProfileList(appId);
        List<Tag> tagList = tagService.getTagList(appId);

        ConfigItem copy = copyId > 0 ? configService.getConfigItem(appId, copyId) : null;

        ModelAndView mv = new ModelAndView("config/file_new")
                .addObject("appId", appId).addObject("app", app)
                .addObject("envList", envList).addObject("env", env)
                .addObject("profileList", profileList).addObject("profile", profile)
                .addObject("tagList", tagList);
        if (copy != null) {
            ConfigItemModel config = ConfigItemModel.wrap(app, env, copy, profileList, tagList);
            mv.addObject("copy", config);
        }
        return mv;
    }

    /**
     * 使用文件编辑器编辑配置项
     *
     * @param appId
     * @param envName
     * @param profileName
     * @param filename
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/config//file/edit/{envName}/{profileName}/{filename:.+}")
    public ModelAndView editConfigFile(@PathVariable("appId") int appId,
                                       @PathVariable("envName") String envName,
                                       @PathVariable("profileName") String profileName,
                                       @PathVariable("filename") String filename) {

        App app = appService.getApp(appId);
        Env env = envService.getEnv(appId, envName);
        Profile profile = profileService.getProfile(appId, profileName);

        List<Env> envList = envService.getEnvList(appId);
        List<Profile> profileList = profileService.getProfileList(appId);
        List<Tag> tagList = tagService.getTagList(appId);

        ConfigItem configItem = configService.getConfigItem(appId, env.getId(), profile.getId(), filename);
        ConfigItemModel config = ConfigItemModel.wrap(app, env, configItem, profileList, tagList);

        return new ModelAndView("config/file_edit")
                .addObject("appId", appId).addObject("app", app)
                .addObject("env", env).addObject("envList", envList)
                .addObject("profileList", profileList).addObject("profile", profile)
                .addObject("tagList", tagList).addObject("config", config);
    }


    /**
     * 发布配置diff
     *
     * @param configId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/config/{configId}/diff/release")
    public String getChange(@PathVariable("appId") int appId,
                            @PathVariable("configId") long configId) {
        ConfigItem configItem = configService.getConfigItem(appId, configId);
        long revision1 = releaseVersionService.getLatestReleaseRevision(configItem.getAppId(), configItem.getEnvId(), configId);
        long revision2 = configItem.getRevision();

        return "redirect:/apps/" + configItem.getAppId() + "/config/compare/" + revision1 + ":" + revision2;
    }

    /**
     * 获取配置
     *
     * @param configId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/configitem/{configId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getConfigItem(@PathVariable("appId") int appId,
                                @PathVariable("configId") long configId) {
        ConfigItem configItem = configService.getConfigItem(appId, configId);

        App app = appService.getApp(configItem.getAppId());
        Env env = envService.getEnv(configItem.getAppId(), configItem.getEnvId());
        List<Profile> profileList = profileService.getProfileList(configItem.getAppId());
        List<Tag> tagList = tagService.getTagList(configItem.getAppId());

        ConfigItemModel config = ConfigItemModel.wrap(app, env, configItem, profileList, tagList);
        return config;
    }

    /**
     * 创建配置
     *
     * @param appId       应用ID
     * @param envId       环境ID
     * @param profileId   ProfileId，0表示未指定
     * @param name        配置名
     * @param value       配置值
     * @param valueType   配置值类型
     * @param tagIdList   标签
     * @param description 配置描述
     * @param message     注释
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/config", method = RequestMethod.POST)
    @ResponseBody
    public Object createConfigItem(@PathVariable("appId") int appId,
                                   @RequestParam("envId") int envId, @RequestParam("profileId") int profileId,
                                   @RequestParam("name") String name, @RequestParam(value = "value", required = false, defaultValue = "") String value,
                                   @RequestParam(value = "valueType", required = false) Byte valueType,
                                   @RequestParam(value = "tagId", required = false) List<Integer> tagIdList,
                                   @RequestParam(value = "description", required = false, defaultValue = "") String description,
                                   @RequestParam(value = "message", required = false, defaultValue = "") String message) {

        // 默认配置值类型是复杂文本
        if (valueType == null) {
            valueType = ConfigValueType.FILE.getType();
        }
        ConfigItem configItem = configCommitService.createConfigItem(appId, envId, profileId, name, value, valueType, tagIdList, description, message);
        configItem = configService.getConfigItem(appId, configItem.getId());

        App app = appService.getApp(configItem.getAppId());
        Env env = envService.getEnv(configItem.getAppId(), configItem.getEnvId());
        List<Profile> profileList = profileService.getProfileList(configItem.getAppId());
        List<Tag> tagList = tagService.getTagList(configItem.getAppId());

        ConfigItemModel config = ConfigItemModel.wrap(app, env, configItem, profileList, tagList);

        return config;
    }

    /**
     * 更改配置
     *
     * @param appId       应用ID
     * @param configId    配置项ID
     * @param name        配置名
     * @param value       配置值
     * @param tagIdList   标签
     * @param description 配置描述
     * @param message     注释
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/configitem/{configId}", method = RequestMethod.PUT)
    @ResponseBody
    public Object modifyConfigItem(@PathVariable("appId") int appId,
                                   @PathVariable("configId") long configId,
                                   @RequestParam("name") String name,
                                   @RequestParam(value = "value", required = false, defaultValue = "") String value,
                                   @RequestParam(value = "tagId", required = false) List<Integer> tagIdList,
                                   @RequestParam(value = "description", required = false, defaultValue = "") String description,
                                   @RequestParam(value = "message", required = false, defaultValue = "") String message) {

        configCommitService.modifyConfigItem(appId, configId, name, value, tagIdList, description, message);
        ConfigItem configItem = configService.getConfigItem(appId, configId);

        App app = appService.getApp(configItem.getAppId());
        Env env = envService.getEnv(configItem.getAppId(), configItem.getEnvId());
        List<Profile> profileList = profileService.getProfileList(configItem.getAppId());
        List<Tag> tagList = tagService.getTagList(configItem.getAppId());

        ConfigItemModel config = ConfigItemModel.wrap(app, env, configItem, profileList, tagList);

        return config;
    }

    /**
     * 启用/禁用配置项
     *
     * @param appId    应用ID
     * @param configId 配置项ID
     * @param enable   true启动，false禁用
     * @param message  注释
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/configitem/{configId}/enable", method = RequestMethod.PUT)
    @ResponseBody
    public Object enableConfigItem(@PathVariable("appId") int appId,
                                   @PathVariable("configId") long configId, @RequestParam("enable") boolean enable,
                                   @RequestParam(value = "message", required = false, defaultValue = "") String message) {
        if (enable) {
            configCommitService.enableConfigItem(appId, configId, message);
        } else {
            configCommitService.disableConfigItem(appId, configId, message);
        }
        return true;
    }

    /**
     * 删除配置
     *
     * @param appId    应用ID
     * @param configId 配置项ID
     * @param message  注释
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/configitem/{configId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteConfigItem(@PathVariable("appId") int appId,
                                   @PathVariable("configId") long configId,
                                   @RequestParam("message") String message) {

        ConfigItem configItem = configService.getConfigItem(appId, configId);
        App app = appService.getApp(configItem.getAppId());
        Env env = envService.getEnv(configItem.getAppId(), configItem.getEnvId());
        List<Profile> profileList = profileService.getProfileList(configItem.getAppId());
        List<Tag> tagList = tagService.getTagList(configItem.getAppId());

        ConfigItemModel config = ConfigItemModel.wrap(app, env, configItem, profileList, tagList);

        configCommitService.delete(appId, configId, message);

        return config;
    }
}
