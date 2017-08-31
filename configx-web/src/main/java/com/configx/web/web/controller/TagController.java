/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.controller;

import com.configx.web.model.App;
import com.configx.web.model.Tag;
import com.configx.web.service.app.AppService;
import com.configx.web.service.privilege.PrivilegeService;
import com.configx.web.service.app.TagService;
import com.configx.web.service.user.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 管理标签Tag Controller
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping("/")
public class TagController {

    @Autowired
    private AppService appService;

    @Autowired
    private TagService tagService;

    @Autowired
    private PrivilegeService privilegeService;

    /**
     * 列出指定环境下的Tag列表
     *
     * @param appId 应用ID
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/tags", method = RequestMethod.GET)
    public ModelAndView getTagList(@PathVariable("appId") int appId) {

        // 权限认证
        if (!privilegeService.isAppAdmin(appId, UserContext.email())) {
            return new ModelAndView("privilege/access_denied");
        }

        App app = appService.getApp(appId);
        List<Tag> tagList = tagService.getTagList(appId);
        return new ModelAndView("app/tag").addObject("appId", appId).addObject("app", app).addObject("tagList", tagList);
    }

    /**
     * 获取Profile
     *
     * @param appId
     * @param tagId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/tags/{tagId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getTag(@PathVariable("appId") int appId, @PathVariable("tagId") int tagId) {

        // 权限认证
        if (!privilegeService.isAdministrator(UserContext.email())) {
            return false;
        }

        return tagService.getTag(appId, tagId);
    }

    /**
     * 创建Tag
     *
     * @param appId       应用ID
     * @param name        tag名称
     * @param description tag描述
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/tags", method = RequestMethod.POST)
    @ResponseBody
    public Object createTag(@PathVariable("appId") int appId, @RequestParam("name") String name, @RequestParam("description") String description) {

        // 权限认证
        if (!privilegeService.isAppAdmin(appId, UserContext.email())) {
            return false;
        }

        tagService.createTag(appId, name, description);
        return true;
    }

    /**
     * 更改Tag
     *
     * @param appId       应用ID
     * @param tagId       tag id
     * @param name        tag名称
     * @param description tag描述
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/tags/{tagId}", method = RequestMethod.PUT)
    @ResponseBody
    public Object modifyTag(@PathVariable("appId") int appId, @PathVariable("tagId") int tagId, @RequestParam("name") String name, @RequestParam("description") String description) {

        // 权限认证
        if (!privilegeService.isAppAdmin(appId, UserContext.email())) {
            return false;
        }

        tagService.modifyTag(tagId, name, description);
        return true;
    }

    /**
     * 删除Tag
     *
     * @param appId 应用ID
     * @param tagId tag id
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/tags/{tagId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteTag(@PathVariable("appId") int appId, @PathVariable("tagId") int tagId) {

        // 权限认证
        if (!privilegeService.isAppAdmin(appId, UserContext.email())) {
            return false;
        }

        tagService.delete(appId, tagId);
        return true;
    }
}
