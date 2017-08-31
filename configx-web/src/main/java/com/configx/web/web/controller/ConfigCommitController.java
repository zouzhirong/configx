/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.controller;

import com.configx.web.page.Page;
import com.configx.web.page.PageRequest;
import com.configx.web.service.app.AppService;
import com.configx.web.service.app.EnvService;
import com.configx.web.service.config.ConfigHistorySearchService;
import com.configx.web.service.config.ConfigItemHistoryService;
import com.configx.web.web.dto.ConfigCommitSearchForm;
import com.configx.web.service.config.ConfigCommitService;
import com.configx.web.service.privilege.PrivilegeService;
import com.configx.web.web.model.ConfigCommitModel;
import com.google.common.base.Function;
import com.configx.web.model.*;
import com.configx.web.service.app.ProfileService;
import com.configx.web.service.app.TagService;
import com.configx.web.service.user.UserContext;
import com.configx.web.web.model.ConfigItemHistoryModel;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

/**
 * 管理配置项Commits Controller
 *
 * @author Zhirong Zou
 */
@Controller
@RequestMapping("/")
public class ConfigCommitController {

    @Autowired
    private AppService appService;

    @Autowired
    private EnvService envService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private TagService tagService;

    @Autowired
    private ConfigCommitService configCommitService;

    @Autowired
    private ConfigItemHistoryService configItemHistoryService;

    @Autowired
    private ConfigHistorySearchService configHistorySearchService;

    @Autowired
    private PrivilegeService privilegeService;

    /**
     * 查看提交日志
     *
     * @param appId
     * @param envName
     * @param form
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/commits/{envName}")
    public ModelAndView searchConfigCommits(@PathVariable("appId") int appId,
                                            @PathVariable("envName") String envName,
                                            @ModelAttribute ConfigCommitSearchForm form,
                                            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        App app = appService.getApp(appId);
        Env env = envService.getEnv(appId, envName);

        form.setAppId(app.getId());
        form.setEnvId(env.getId());

        // 权限认证
        if (!privilegeService.isAppAdminOrDeveloper(form.getAppId(), UserContext.email())) {
            return new ModelAndView(new RedirectView("/apps/empty"));
        }

        List<App> appList = appService.getUserAppList(UserContext.email());
        List<Env> envList = envService.getEnvList(appId);

        List<Profile> profileList = profileService.getProfileList(appId);
        List<Tag> tagList = tagService.getTagList(appId);

        PageRequest pageRequest = new PageRequest(page, size);

        // 配置历史
        Page<Void, ConfigItemHistory> historyPage = configHistorySearchService.search(form.getRevision(), form.getFromRevision(), form.getToRevision(), form.getAppId(), form.getEnvId(), form.getProfileId(), null, form.getConfigName(), pageRequest);
        List<ConfigItemHistoryModel> configItemModelList = ConfigItemHistoryModel.wrap(historyPage.getContent(), app, envList, profileList, tagList);


        // 配置提交
        List<Long> revisionList = configItemHistoryService.getHistoryRevisionList(historyPage.getContent());
        List<ConfigCommit> commitList = configCommitService.getCommitList(revisionList);
        List<ConfigCommitModel> configCommitModelList = ConfigCommitModel.wrap(commitList, configItemModelList);

        Function<ConfigCommitModel, String> dateGroupFunction = new Function<ConfigCommitModel, String>() {
            @Override
            public String apply(ConfigCommitModel input) {
                return DateFormatUtils.format(input.getDate(), "yyyy-MM-dd");
            }
        };
        Page<String, ConfigCommitModel> groupPageResponse = new Page<String, ConfigCommitModel>(historyPage.getTotalElements(), pageRequest, configCommitModelList).group(dateGroupFunction);

        return new ModelAndView("config/config_commits")
                .addObject("form", form)
                .addObject("appList", appList).addObject("app", app)
                .addObject("envList", envList).addObject("env", env)
                .addObject("profileList", profileList)
                .addObject("tagList", tagList)
                .addObject("page", groupPageResponse);
    }

    /**
     * 查看某一次提交
     *
     * @param appId
     * @param revision
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/commit/{revision}")
    public String getConfigCommit(@PathVariable("appId") int appId,
                                  @PathVariable("revision") long revision) {
        ConfigItemHistory configItemHistory = configItemHistoryService.getRevisionHistory(appId, revision);
        return "redirect:/apps/" + configItemHistory.getAppId() + "/config/compare/" + configItemHistory.getLastRevision() + ":" + configItemHistory.getRevision();
    }

    /**
     * 比较两个版本
     *
     * @param appId
     * @param revision1
     * @param revision2
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/config/compare/{revision1}:{revision2}")
    public ModelAndView compare(@PathVariable("appId") int appId,
                                @PathVariable("revision1") long revision1, @PathVariable("revision2") long revision2) {

        App app = appService.getApp(appId);

        ConfigItemHistory left = configItemHistoryService.getRevisionHistory(appId, revision1);
        ConfigItemHistory right = configItemHistoryService.getRevisionHistory(appId, revision2);

        return new ModelAndView("config/file-diff")
                .addObject("app", app)
                .addObject("left", left).addObject("right", right)
                .addObject("revision1", revision1).addObject("revision2", revision2);
    }


    /**
     * 获取历史配置
     *
     * @param appId
     * @param revision
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/config/history/{revision}", method = RequestMethod.GET)
    @ResponseBody
    public Object getConfigItemHistory(@PathVariable("appId") int appId,
                                       @PathVariable("revision") long revision) {
        ConfigItemHistory configItemHistory = configItemHistoryService.getRevisionHistory(appId, revision);
        return configItemHistory;
    }
}
