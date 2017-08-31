/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.controller;

import com.configx.web.model.App;
import com.configx.web.model.Profile;
import com.configx.web.service.app.AppService;
import com.configx.web.service.privilege.PrivilegeService;
import com.configx.web.service.app.ProfileService;
import com.configx.web.service.user.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 管理项目的环境的Profile Controller
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping("/")
public class ProfileController {

    @Autowired
    private AppService appService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private PrivilegeService privilegeService;

    /**
     * 列出指定环境下的Profile列表
     *
     * @param appId 应用ID
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/profiles", method = RequestMethod.GET)
    public ModelAndView getProfileList(@PathVariable("appId") int appId) {

        // 权限认证
        if (!privilegeService.isAppAdmin(appId, UserContext.email())) {
            return new ModelAndView("privilege/access_denied");
        }

        App app = appService.getApp(appId);

        List<Profile> profileList = profileService.getProfileList(appId);

        return new ModelAndView("app/profile").addObject("appId", appId).addObject("app", app).addObject("profileList", profileList);
    }

    /**
     * 获取Profile
     *
     * @param appId
     * @param profileId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/profiles/{profileId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getProfile(@PathVariable("appId") int appId, @PathVariable("profileId") int profileId) {

        // 权限认证
        if (!privilegeService.isAdministrator(UserContext.email())) {
            return false;
        }

        return profileService.getProfile(appId, profileId);
    }

    /**
     * 创建Profile
     *
     * @param appId       应用ID
     * @param name        profile名称
     * @param description profile描述
     * @param order       顺序
     * @param color       颜色
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/profiles", method = RequestMethod.POST)
    @ResponseBody
    public Object createProfile(@PathVariable("appId") int appId,
                                @RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam(value = "order", required = false, defaultValue = "0") int order,
                                @RequestParam(value = "color", required = false, defaultValue = "") String color) {

        // 权限认证
        if (!privilegeService.isAppAdmin(appId, UserContext.email())) {
            return false;
        }

        profileService.createProfile(appId, name, description, order, color);
        return true;
    }

    /**
     * 更改Profile
     *
     * @param appId       应用ID
     * @param profileId   profile id
     * @param name        profile名称
     * @param description profile描述
     * @param order       顺序
     * @param color       颜色
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/profiles/{profileId}", method = RequestMethod.PUT)
    @ResponseBody
    public Object modifyProfile(@PathVariable("appId") int appId,
                                @PathVariable("profileId") int profileId,
                                @RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam(value = "order", required = false, defaultValue = "0") int order,
                                @RequestParam(value = "color", required = false, defaultValue = "") String color) {

        // 权限认证
        if (!privilegeService.isAppAdmin(appId, UserContext.email())) {
            return false;
        }

        profileService.modifyProfile(appId, profileId, name, description, order, color);
        return true;
    }

    /**
     * 删除Profile
     *
     * @param appId     应用ID
     * @param profileId profile id
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/profiles/{profileId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteProfile(@PathVariable("appId") int appId, @PathVariable("profileId") int profileId) {

        // 权限认证
        if (!privilegeService.isAppAdmin(appId, UserContext.email())) {
            return false;
        }

        profileService.delete(appId, profileId);
        return true;
    }
}
