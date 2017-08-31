/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.controller;

import com.alibaba.fastjson.JSON;
import com.configx.web.service.config.ConfigRestService;
import com.configx.web.service.config.ConfigService;
import com.configx.web.service.config.ConfigValueType;
import com.configx.web.service.release.version.ReleaseVersionContants;
import com.configx.web.webmvc.LoginIgnored;
import com.configx.web.service.config.ConfigCommitService;
import com.configx.web.model.*;
import com.configx.web.service.app.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * REST API
 *
 * @author Zhirong Zou
 */
@LoginIgnored
@Controller
@RequestMapping("/v1/")
public class RestController {

    private Logger logger = LoggerFactory.getLogger(getClass());

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
    private ConfigRestService configRestService;

    /**
     * 获取配置列表，必须指定应用、环境，可以指定Profile
     *
     * @param appId    应用ID
     * @param envName  环境名称
     * @param profiles Profile列表
     * @return
     */
    @RequestMapping(value = "/config/list", method = RequestMethod.GET)
    @ResponseBody
    public Object getConfigItemList(@RequestParam(value = "app", required = false, defaultValue = "0") int appId,
                                    @RequestParam(value = "app_key", required = false, defaultValue = "") String appKey,
                                    @RequestParam("env") String envName,
                                    @RequestParam(value = "profiles", required = false, defaultValue = "") String profiles) {

        return configRestService.getHeadConfigList(appId, appKey, envName, profiles);

    }

    /**
     * 获取配置列表，必须指定应用、环境，可以指定Profile
     *
     * @param appId    应用ID
     * @param appKey   应用KEY
     * @param envName  环境名称
     * @param profiles Profile列表
     * @param from     起始版本号
     * @param to       截止版本号
     * @return
     */
    @RequestMapping(value = "/config/update", method = RequestMethod.GET)
    @ResponseBody
    public Object getConfigItemList(@RequestParam(value = "app", required = false, defaultValue = "0") int appId,
                                    @RequestParam(value = "app_key", required = false, defaultValue = "") String appKey,
                                    @RequestParam("env") String envName,
                                    @RequestParam(value = "profiles", required = false, defaultValue = "") String profiles,
                                    @RequestParam(value = "from-version", required = false, defaultValue = "0") long from,
                                    @RequestParam(value = "to-version", required = false) String to) {

        if (StringUtils.isEmpty(to)) {
            to = ReleaseVersionContants.HEAD_VERSION;
        }
        if (from == 0 && ReleaseVersionContants.HEAD_VERSION.equals(to)) {
            return configRestService.getHeadConfigList(appId, appKey, envName, profiles);
        } else {
            return configRestService.update(appId, appKey, envName, profiles, from, to);
        }
    }


    /**
     * 获取配置
     *
     * @param appId
     * @param appKey
     * @param envName
     * @param profileName
     * @param name
     * @return
     */
    @RequestMapping(value = "/config/getItem", method = RequestMethod.GET)
    @ResponseBody
    public Object getConfigItem(@RequestParam(value = "app", required = false, defaultValue = "0") int appId,
                                @RequestParam(value = "app_key", required = false, defaultValue = "") String appKey,
                                @RequestParam("env") String envName,
                                @RequestParam("profile") String profileName,
                                @RequestParam("name") String name) {


        // 应用信息
        App app = appService.getApp(appId, appKey);
        if (app == null) {
            logger.error("Can not find app, appId=" + appId + ", appKey=" + appKey);
            return null;
        }
        // 重新获取App ID
        appId = app.getId();

        // 环境信息
        Env env = envService.getEnv(app.getId(), envName);
        if (env == null) {
            logger.error("Can not find env, appId=" + appId + ", appKey=" + appKey + ", env=" + envName);
            return null;
        }

        // Profile信息
        if (StringUtils.isEmpty(profileName)) {
            logger.error("Profile Name can not be empty, profileName=" + profileName);
            return null;
        }
        Profile profile = profileService.createIfAbsent(app.getId(), profileName, "", 0, "");


        // 配置信息
        if (StringUtils.isEmpty(name)) {
            logger.error("Config Name can not be empty, name=" + name);
            return null;
        }
        ConfigItem configItem = configService.getConfigItem(app.getId(), env.getId(), profile.getId(), name);
        return configItem;
    }

    /**
     * 创建配置
     *
     * @param appId
     * @param appKey
     * @param envName
     * @param profileName
     * @param name
     * @param value
     * @param valueType
     * @param tagNameList
     * @param description
     * @param message
     * @return
     */
    @RequestMapping(value = "/config/create", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object createConfigItem(@RequestParam(value = "app", required = false, defaultValue = "0") int appId,
                                   @RequestParam(value = "app_key", required = false, defaultValue = "") String appKey,
                                   @RequestParam("env") String envName,
                                   @RequestParam("profile") String profileName,
                                   @RequestParam("name") String name,
                                   @RequestParam("value") String value,
                                   @RequestParam(value = "valueType", required = false) Byte valueType,
                                   @RequestParam(value = "tag", required = false) List<String> tagNameList,
                                   @RequestParam(value = "description", required = false, defaultValue = "") String description,
                                   @RequestParam(value = "message", required = false, defaultValue = "") String message) {

        // 默认配置值类型是复杂文本
        if (valueType == null) {
            valueType = ConfigValueType.FILE.getType();
        }

        // 应用信息
        App app = appService.getApp(appId, appKey);
        if (app == null) {
            logger.error("Can not find app, appId=" + appId + ", appKey=" + appKey);
            return null;
        }
        // 重新获取App ID
        appId = app.getId();

        // 环境信息
        Env env = envService.getEnv(app.getId(), envName);
        if (env == null) {
            logger.error("Can not find env, appId=" + appId + ", appKey=" + appKey + ", env=" + envName);
            return null;
        }

        int profileId = ProfileContants.DEFAULT_PROFILE_ID;
        // Profile信息
        if (StringUtils.isNotEmpty(profileName)) {
            Profile profile = profileService.createIfAbsent(app.getId(), profileName, "", 0, "");
            profileId = profile.getId();
        }

        // Tag列表
        List<Tag> tagList = tagService.createIfAbsent(app.getId(), tagNameList);
        List<Integer> tagIdList = tagService.getTagIdList(tagList);


        // 配置信息
        if (StringUtils.isEmpty(name)) {
            logger.error("Config Name can not be empty, name=" + name);
            return null;
        }
        ConfigItem configItem = configService.getConfigItem(app.getId(), env.getId(), profileId, name);
        if (configItem == null) {
            configItem = configCommitService.createConfigItem(appId, env.getId(), profileId, name, value, valueType, tagIdList, description, message);
            logger.info("Config Item Created\n" + JSON.toJSONString(configItem, true));
            return configItem;

        } else {
            configItem = configCommitService.modifyConfigItem(appId, configItem.getId(), name, value, tagIdList, description, message);
            logger.info("Config Item Updated\n" + JSON.toJSONString(configItem, true));
            return configItem;

        }

    }

}
