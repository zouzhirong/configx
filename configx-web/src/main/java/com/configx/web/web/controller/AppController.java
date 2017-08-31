/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.controller;

import com.configx.web.page.Page;
import com.configx.web.model.App;
import com.configx.web.service.app.AppService;
import com.configx.web.service.privilege.PrivilegeService;
import com.configx.web.page.PageRequest;
import com.configx.web.service.user.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 应用管理Controller
 *
 * @author Zhirong Zou
 */
@Controller
@RequestMapping("/")
public class AppController {

    @Autowired
    private AppService appService;

    @Autowired
    private PrivilegeService privilegeService;

    /**
     * 列出项目列表
     *
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/apps", method = RequestMethod.GET)
    public ModelAndView getAppList(@RequestParam(name = "page", required = false, defaultValue = "1") int page, @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        List<App> appList = null;
        if (privilegeService.isAdministrator(UserContext.email())) {
            appList = appService.getAppList();
        } else {
            appList = appService.getAdminAppList(UserContext.email());
        }

        PageRequest pageRequest = new PageRequest(page, size);
        Page<Void, App> pageResponse = new Page<>(appList, pageRequest);
        return new ModelAndView("app/app").addObject("page", pageResponse);
    }

    /**
     * 项目列表为空的提示
     *
     * @return
     */
    @RequestMapping(value = "/apps/empty", method = RequestMethod.GET)
    public ModelAndView emptyList() {
        return new ModelAndView("app/app_empty");
    }

    /**
     * 获取项目
     *
     * @param appId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getApp(@PathVariable("appId") int appId) {

        // 权限认证
        if (!privilegeService.isAdministrator(UserContext.email())) {
            return false;
        }

        return appService.getApp(appId);
    }

    /**
     * 创建项目
     *
     * @param name        应用名称
     * @param description 应用描述
     * @param admins      管理员邮箱
     * @param developers  开发者邮箱
     * @return
     */
    @RequestMapping(value = "/apps", method = RequestMethod.POST)
    @ResponseBody
    public Object createApp(@RequestParam("name") String name,
                            @RequestParam(value = "description", required = false, defaultValue = "") String description,
                            @RequestParam(value = "admins", required = false, defaultValue = "") String admins,
                            @RequestParam(value = "developers", required = false, defaultValue = "") String developers) {

        // 权限认证
        if (!privilegeService.isAdministrator(UserContext.email())) {
            return false;
        }

        appService.createApp(name, description, admins, developers);
        return true;
    }

    /**
     * 更改项目
     *
     * @param appId       应用ID
     * @param name        应用名称
     * @param description 应用描述
     * @param admins      管理员邮箱
     * @param developers  开发者邮箱
     * @return
     */
    @RequestMapping(value = "/apps/{appId}", method = RequestMethod.PUT)
    @ResponseBody
    public Object modifyApp(@PathVariable("appId") int appId,
                            @RequestParam("name") String name,
                            @RequestParam(value = "description", required = false, defaultValue = "") String description,
                            @RequestParam(value = "admins", required = false, defaultValue = "") String admins,
                            @RequestParam(value = "developers", required = false, defaultValue = "") String developers) {

        // 权限认证
        if (!privilegeService.isAdministrator(UserContext.email())
                && !privilegeService.isAppAdmin(appId, UserContext.email())) {
            return false;
        }

        appService.modifyApp(appId, name, description, admins, developers);
        return true;
    }

    /**
     * 删除项目
     *
     * @param appId 应用ID
     * @return
     */
    @RequestMapping(value = "/apps/{appId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteApp(@PathVariable("appId") int appId) {

        // 权限认证
        if (!privilegeService.isAdministrator(UserContext.email())
                && !privilegeService.isAppAdmin(appId, UserContext.email())) {
            return false;
        }

        appService.delete(appId);
        return true;
    }
}
