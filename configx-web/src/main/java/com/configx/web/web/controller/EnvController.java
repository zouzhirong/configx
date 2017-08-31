/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.controller;

import com.configx.web.service.app.EnvService;
import com.configx.web.model.App;
import com.configx.web.model.Env;
import com.configx.web.service.app.AppService;
import com.configx.web.service.privilege.PrivilegeService;
import com.configx.web.service.user.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 管理项目的环境(Env) Controller
 *
 * @author Zhirong Zou
 */
@Controller
@RequestMapping("/")
public class EnvController {

    @Autowired
    private AppService appService;

    @Autowired
    private EnvService envService;

    @Autowired
    private PrivilegeService privilegeService;

    /**
     * 列出项目的环境列表
     *
     * @param appId 应用ID
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/envs", method = RequestMethod.GET)
    public ModelAndView getEnvList(@PathVariable("appId") int appId) {

        // 权限认证
        if (!privilegeService.isAppAdmin(appId, UserContext.email())) {
            return new ModelAndView("privilege/access_denied");
        }

        App app = appService.getApp(appId);
        List<Env> envList = envService.getEnvList(appId);
        return new ModelAndView("app/env").addObject("appId", appId).addObject("app", app).addObject("envList", envList);
    }

    /**
     * 获取环境
     *
     * @param appId
     * @param envId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/envs/{envId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getEnv(@PathVariable("appId") int appId, @PathVariable("envId") int envId) {

        // 权限认证
        if (!privilegeService.isAdministrator(UserContext.email())) {
            return false;
        }

        return envService.getEnv(appId, envId);
    }

    /**
     * 创建环境
     *
     * @param appId       应用ID
     * @param name        环境名称
     * @param alias       环境别名
     * @param description 环境描述
     *                    * @param order       顺序
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/envs", method = RequestMethod.POST)
    @ResponseBody
    public Object createEnv(@PathVariable("appId") int appId,
                            @RequestParam("name") String name,
                            @RequestParam("alias") String alias,
                            @RequestParam("description") String description,
                            @RequestParam(value = "order", required = false, defaultValue = "0") int order) {

        // 权限认证
        if (!privilegeService.isAppAdmin(appId, UserContext.email())) {
            return false;
        }

        envService.createEnv(appId, name, alias, description, order);
        return true;
    }

    /**
     * 更改环境
     *
     * @param appId       应用ID
     * @param envId       环境ID
     * @param name        环境名称
     * @param alias       环境别名
     * @param autoRelease 自动发布
     * @param description 环境描述
     * @param order       顺序
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/envs/{envId}", method = RequestMethod.PUT)
    @ResponseBody
    public Object modifyEnv(@PathVariable("appId") int appId,
                            @PathVariable("envId") int envId,
                            @RequestParam("name") String name,
                            @RequestParam("alias") String alias,
                            @RequestParam(name = "autoRelease", required = false, defaultValue = "false") boolean autoRelease,
                            @RequestParam("description") String description,
                            @RequestParam(value = "order", required = false, defaultValue = "0") int order) {

        // 权限认证
        if (!privilegeService.isAppAdmin(appId, UserContext.email())) {
            return false;
        }

        envService.modifyEnv(envId, name, alias, autoRelease, description, order);
        return true;
    }

    /**
     * 删除环境
     *
     * @param appId 应用ID
     * @param envId 环境ID
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/envs/{envId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteEnv(@PathVariable("appId") int appId, @PathVariable("envId") int envId) {

        // 权限认证
        if (!privilegeService.isAppAdmin(appId, UserContext.email())) {
            return false;
        }

        envService.delete(appId, envId);
        return true;
    }

}
