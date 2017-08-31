/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.controller;

import com.configx.web.model.*;
import com.configx.web.page.Page;
import com.configx.web.page.PageRequest;
import com.configx.web.service.app.AppService;
import com.configx.web.service.app.EnvService;
import com.configx.web.service.app.ProfileService;
import com.configx.web.service.privilege.PrivilegeService;
import com.configx.web.service.release.ReleaseService;
import com.configx.web.service.release.form.ReleaseFormSearchService;
import com.configx.web.service.release.form.ReleaseFormService;
import com.configx.web.service.user.UserContext;
import com.configx.web.web.dto.ReleaseFormDto;
import com.configx.web.web.dto.ReleaseFormListResponse;
import com.configx.web.web.dto.ReleaseFormRequest;
import com.configx.web.web.dto.ReleaseFormSearchForm;
import com.configx.web.web.util.Pair;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 发布单 Controller
 *
 * @author Zhirong Zou
 */
@Controller
@RequestMapping("/")
public class ReleaseFormController {

    @Autowired
    private AppService appService;

    @Autowired
    private EnvService envService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ReleaseFormService releaseFormService;

    @Autowired
    private ReleaseService releaseService;

    @Autowired
    private ReleaseFormSearchService releaseFormSearchService;

    @Autowired
    private PrivilegeService privilegeService;

    /**
     * 列出发布单
     *
     * @param appId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseforms", method = RequestMethod.GET)
    public ModelAndView getReleaseFormList(@PathVariable("appId") int appId) {
        Env env = envService.getDefaultEnv(appId);
        if (env == null) {
            return new ModelAndView(new RedirectView("/apps/" + appId + "/envs/empty"));
        }
        return new ModelAndView(new RedirectView("/apps/" + appId + "/releaseforms/" + env.getName()));
    }

    /**
     * 列出发布单
     *
     * @param appId
     * @param envName
     * @param form
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseforms/{envName}", method = RequestMethod.GET)
    public ModelAndView getReleaseFormList(@PathVariable("appId") int appId,
                                           @PathVariable("envName") String envName,
                                           @ModelAttribute ReleaseFormSearchForm form,
                                           @RequestParam(name = "page", required = false, defaultValue = "1") int page, @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        App app = appService.getApp(appId);
        Env env = envService.getEnv(appId, envName);

        form.setAppId(app.getId());
        form.setEnvId(env.getId());

        // 权限认证
        if (!privilegeService.isAppAdminOrDeveloper(form.getAppId(), UserContext.email())) {
            return new ModelAndView(new RedirectView("/apps/empty"));
        }

        List<Env> envList = envService.getEnvList(app.getId());

        List<ReleaseForm> releaseFormList = releaseFormSearchService.search(form);
        List<Release> releaseList = releaseService.getReleaseList(releaseFormList);
        List<ReleaseFormDto> releaseFormDtoList = new ReleaseFormListResponse(releaseFormList, releaseList).getReleaseFormDtoList();

        PageRequest pageRequest = new PageRequest(page, size);
        Page<Void, ReleaseFormDto> pageResponse = new Page<>(releaseFormDtoList, pageRequest);

        return new ModelAndView("release/release_form")
                .addObject("form", form)
                .addObject("app", app)
                .addObject("envList", envList).addObject("env", env)
                .addObject("page", pageResponse);
    }

    /**
     * 查看发布单
     *
     * @param formId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{formId}/detail", method = RequestMethod.GET)
    public ModelAndView viewReleaseForm(@PathVariable("appId") int appId,
                                        @PathVariable("formId") long formId) {

        App app = appService.getApp(appId);

        // 发布单
        ReleaseForm releaseForm = releaseFormService.getReleaseForm(appId, formId);

        // 发布信息
        Release release = releaseService.getRelease(releaseForm.getReleaseId());

        // 是否是应用管理员
        boolean isAppAdmin = appService.isAdmin(releaseForm.getAppId(), UserContext.email());

        return new ModelAndView("release/release_form_view")
                .addObject("app", app)
                .addObject("releaseForm", releaseForm)
                .addObject("release", release)
                .addObject("isAppAdmin", isAppAdmin);
    }

    /**
     * 添加发布单
     *
     * @param appId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/new", method = RequestMethod.GET)
    public ModelAndView addReleaseForm(@PathVariable("appId") int appId) {

        App app = appService.getApp(appId);
        List<Env> envList = envService.getEnvList(appId);

        return new ModelAndView("release/release_form_add")
                .addObject("app", app)
                .addObject("envList", envList);
    }

    /**
     * 修改发布单
     *
     * @param appId
     * @param formId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{formId}/edit", method = RequestMethod.GET)
    public ModelAndView editReleaseForm(@PathVariable("appId") int appId,
                                        @PathVariable("formId") long formId) {

        App app = appService.getApp(appId);

        ReleaseForm releaseForm = releaseFormService.getReleaseForm(appId, formId);
        return new ModelAndView("release/release_form_edit")
                .addObject("app", app)
                .addObject("releaseForm", releaseForm);
    }

    /**
     * 查看发布单的Change List
     *
     * @param appId
     * @param formId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{formId}/changelist", method = RequestMethod.GET)
    public ModelAndView getChangeList(@PathVariable("appId") int appId,
                                      @PathVariable("formId") long formId) {

        ReleaseForm releaseForm = releaseFormService.getReleaseForm(appId, formId);
        List<BuildConfigChange> changeList = releaseFormService.getConfigChangeList(formId);
        return new ModelAndView("release/release_changelist").addObject("releaseForm", releaseForm).addObject("changeList", changeList);
    }

    /**
     * 查看发布单的指定配置项的change信息
     *
     * @param formId
     * @param configId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{formId}/compare/{configId}", method = RequestMethod.GET)
    public String compare(@PathVariable("appId") int appId,
                          @PathVariable("formId") long formId,
                          @PathVariable("configId") long configId) {

        ReleaseForm form = releaseFormService.getReleaseForm(appId, formId);

        Pair<BuildConfigItem> change = releaseFormService.getChangeBetween(formId, configId);
        long revision1 = 0;
        long revision2 = 0;
        if (change.getLeft() != null) {
            revision1 = change.getLeft().getRevision();
        }
        if (change.getRight() != null) {
            revision2 = change.getRight().getRevision();
        }

        App app = appService.getApp(form.getAppId());
        Env env = envService.getEnv(form.getAppId(), form.getEnvId());
        Profile profile = profileService.getProfile(appId, change.getLeft().getProfileId());

        return "redirect:/apps/" + app.getId() + "/config/compare/" + env.getName() + "/" + profile.getName() + "/" + change.getLeft().getConfigName() + "/" + revision1 + ":" + revision2;
    }

    /**
     * 查看发布单的commits
     *
     * @param appId
     * @param formId
     * @param configId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{formId}/commits/{configId}", method = RequestMethod.GET)
    public String getCommits(@PathVariable("appId") int appId,
                             @PathVariable("formId") long formId,
                             @PathVariable("configId") long configId) {

        ReleaseForm form = releaseFormService.getReleaseForm(appId, formId);

        Pair<BuildConfigItem> change = releaseFormService.getChangeBetween(formId, configId);
        long revision1 = 0;
        long revision2 = 0;
        if (change.getLeft() != null) {
            revision1 = change.getLeft().getRevision();
        }
        if (change.getRight() != null) {
            revision2 = change.getRight().getRevision();
        }

        App app = appService.getApp(form.getAppId());
        Env env = envService.getEnv(form.getAppId(), form.getEnvId());

        return "redirect:/apps/" + app.getId() + "/commits/" + env.getName() + "?fromRevision=" + (revision1 + 1) + "&toRevision=" + revision2 + "&configName=" + change.getLeft().getConfigName();
    }

    /**
     * 获取发布单
     *
     * @param appId
     * @param formId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{formId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getReleaseForm(@PathVariable("appId") int appId, @PathVariable("formId") long formId) {
        return releaseFormService.getReleaseForm(appId, formId);
    }

    /**
     * 创建发布单
     *
     * @param appId
     * @param form
     * @return
     * @throws ParseException
     */
    @RequestMapping(value = "/apps/{appId}/releaseform", method = RequestMethod.POST)
    @ResponseBody
    public Object createReleaseForm(@PathVariable("appId") int appId, @ModelAttribute ReleaseFormRequest form) throws ParseException {
        form.setAppId(appId);
        ReleaseForm releaseForm = releaseFormService.createForm(form.getAppId(), form.getEnvId(), form.getName(), form.getRemark(), form.getPlanPubDate());
        return releaseForm;
    }

    /**
     * 更改发布单
     *
     * @param appId
     * @param formId      发布单ID
     * @param name        发布单名称
     * @param remark      发布单备注
     * @param planPubTime 计划发布时间
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{formId}", method = RequestMethod.PUT)
    @ResponseBody
    public Object modifyReleaseForm(@PathVariable("appId") int appId,
                                    @PathVariable("formId") long formId,
                                    @RequestParam("name") String name,
                                    @RequestParam("remark") String remark, @RequestParam("planPubTime") String planPubTime) throws ParseException {

        Date planPubDate = null;
        if (StringUtils.isNotEmpty(planPubTime)) {
            planPubDate = DateUtils.parseDate(planPubTime, new String[]{"yyyy-MM-dd HH:mm:ss"});
        }

        releaseFormService.modifyForm(appId, formId, name, remark, planPubDate);
        return releaseFormService.getReleaseForm(formId);
    }

    /**
     * 删除
     *
     * @param appId
     * @param formId 发布单ID
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{formId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteReleaseForm(@PathVariable("appId") int appId,
                                    @PathVariable("formId") long formId) {

        ReleaseForm releaseForm = releaseFormService.getReleaseForm(appId, formId);
        releaseFormService.delete(appId, formId);
        return releaseForm;
    }

    /**
     * 发布单提交审核
     *
     * @param
     * @param releaseFormId 发布单
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{releaseFormId}/submit", method = RequestMethod.POST)
    @ResponseBody
    public Object submitReleaseForm(@PathVariable("appId") int appId,
                                    @PathVariable("releaseFormId") long releaseFormId) {
        releaseFormService.submit(appId, releaseFormId);
        return releaseFormService.getReleaseForm(releaseFormId);
    }

    /**
     * 审核发布单
     *
     * @param appId
     * @param releaseFormId 发布单
     * @param passed        是否通过
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{releaseFormId}/audit", method = RequestMethod.POST)
    @ResponseBody
    public Object auditReleaseForm(@PathVariable("appId") int appId,
                                   @PathVariable("releaseFormId") long releaseFormId,
                                   @RequestParam("passed") boolean passed) {

        ReleaseForm form = releaseFormService.getReleaseForm(appId, releaseFormId);

        // 权限认证
        if (!privilegeService.isAppAdmin(form.getAppId(), UserContext.email())) {
            return false;
        }

        releaseFormService.audit(releaseFormId, passed);
        return releaseFormService.getReleaseForm(releaseFormId);
    }

    /**
     * 发布发布单
     *
     * @param appId
     * @param releaseFormId 发布单
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{releaseFormId}/release", method = RequestMethod.POST)
    @ResponseBody
    public Object releaseForm(@PathVariable("appId") int appId,
                              @PathVariable("releaseFormId") long releaseFormId) {
        releaseFormService.release(appId, releaseFormId);
        return releaseFormService.getReleaseForm(releaseFormId);
    }

    /**
     * 回滚发布单
     *
     * @param appId
     * @param releaseFormId 发布单
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{releaseFormId}/rollback", method = RequestMethod.POST)
    @ResponseBody
    public Object rollbackForm(@PathVariable("appId") int appId,
                               @PathVariable("releaseFormId") long releaseFormId) {
        releaseFormService.rollback(appId, releaseFormId);
        return releaseFormService.getReleaseForm(releaseFormId);
    }

    /**
     * 查询发布单状态
     *
     * @param appId
     * @param releaseFormId 发布单
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/releaseform/{releaseFormId}/release-status", method = RequestMethod.GET)
    @ResponseBody
    public Object queryReleaseStatus(@PathVariable("appId") int appId,
                                     @PathVariable("releaseFormId") long releaseFormId) {
        ReleaseForm releaseForm = releaseFormService.getReleaseForm(appId, releaseFormId);

        if (releaseForm.getReleaseId() == 0) {
            return 0;
        } else {
            Release release = releaseService.getRelease(releaseForm.getReleaseId());
            return release == null ? 0 : release.getReleaseStatus();
        }
    }
}
