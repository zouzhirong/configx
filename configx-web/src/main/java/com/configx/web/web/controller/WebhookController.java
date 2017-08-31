/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.controller;

import com.configx.web.model.WebhookLog;
import com.configx.web.service.app.AppService;
import com.configx.web.web.dto.WebhookForm;
import com.configx.web.hook.*;
import com.configx.web.model.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


/**
 * Webhook Controller
 *
 * @author Zhirong Zou
 */
@Controller
@RequestMapping(value = "/")
public class WebhookController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AppService appService;

    @Autowired
    private HookStoreService hookStoreService;

    @Autowired
    private HookService hookService;

    /**
     * 列出Webhooks
     *
     * @param appId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/hooks")
    public ModelAndView getHooks(@PathVariable("appId") int appId) {

        App app = appService.getApp(appId);
        List<WebhookDetail> hooks = hookStoreService.getWebhookDetails(appId);
        return new ModelAndView("webhook/hooks").addObject("app", app).addObject("hooks", hooks);
    }

    /**
     * 获取添加Webhook视图
     *
     * @param appId
     * @param eventType
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/hooks/new")
    public ModelAndView getNewWebhookView(@PathVariable("appId") int appId, @RequestParam(value = "eventType", required = false, defaultValue = "0") int eventType) {
        HookEventType hookEventType = eventType == 0 ? HookEventType.defaultType() : HookEventType.get(eventType);

        App app = appService.getApp(appId);
        List<String> eventParamNames = hookService.getEventParamNames(hookEventType);
        List<String> requestParamNames = hookService.getRequestParamNames(hookEventType);

        return new ModelAndView("webhook/hooks_new")
                .addObject("app", app)
                .addObject("eventTypes", HookEventType.values())
                .addObject("contentTypes", ContentType.values())
                .addObject("eventType", hookEventType)
                .addObject("eventParamNames", eventParamNames)
                .addObject("requestParamNames", requestParamNames);
    }

    /**
     * 添加Webhook
     *
     * @param appId
     * @param form
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/hooks/add")
    public String addWebhook(@PathVariable("appId") int appId, @ModelAttribute WebhookForm form) {

        hookStoreService.addWebhook(appId, form);
        return "redirect:/apps/" + appId + "/hooks";
    }

    /**
     * 获取修改Webhook视图
     *
     * @param appId
     * @param webhookId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/hooks/{webhookId}")
    public ModelAndView getEditWebhookView(@PathVariable("appId") int appId, @PathVariable("webhookId") int webhookId) {

        App app = appService.getApp(appId);
        WebhookDetail webhook = hookStoreService.getWebhookDetail(appId, webhookId);

        return new ModelAndView("webhook/hooks_edit")
                .addObject("app", app)
                .addObject("webhook", webhook)
                .addObject("eventTypes", HookEventType.values())
                .addObject("contentTypes", ContentType.values());
    }

    /**
     * 修改Webhook
     *
     * @param appId
     * @param webhookId
     * @param form
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/hooks/edit/{webhookId}")
    public String editWebhook(@PathVariable("appId") int appId,
                              @PathVariable("webhookId") int webhookId,
                              @ModelAttribute WebhookForm form) {

        hookStoreService.editWebhookDetail(appId, webhookId, form);
        return "redirect:/apps/" + appId + "/hooks";
    }

    /**
     * 删除Webhook
     *
     * @param appId
     * @param webhookId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/hooks/delete/{webhookId}")
    public String deleteWebhook(@PathVariable("appId") int appId, @PathVariable("webhookId") int webhookId) {

        hookStoreService.deleteWebhookDetail(appId, webhookId);
        return "redirect:/apps/" + appId + "/hooks";
    }

    /**
     * 列出Webhook日志
     *
     * @param appId
     * @param webhookId
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/hooks/log")
    public ModelAndView getWebhookLogs(@PathVariable("appId") int appId, @RequestParam(value = "webhookId", required = false, defaultValue = "0") int webhookId) {

        App app = appService.getApp(appId);
        List<WebhookLog> webhookLogs = null;
        if (webhookId > 0) {
            webhookLogs = hookStoreService.getWebhookLogList(webhookId);
        } else {
            webhookLogs = hookStoreService.getAppWebhookLogList(appId);
        }

        return new ModelAndView("webhook/hooks_log").addObject("app", app).addObject("webhookLogs", webhookLogs);
    }

    /**
     * 查看Webhook日志
     *
     * @return
     */
    @RequestMapping(value = "/apps/{appId}/hooks/log/{logId}")
    public ModelAndView getWebhookLog(@PathVariable("appId") int appId, @PathVariable("logId") int logId) {

        App app = appService.getApp(appId);
        WebhookLog webhookLog = hookStoreService.getWebhookLog(logId);

        return new ModelAndView("webhook/hooks_log_detail").addObject("app", app).addObject("webhookLog", webhookLog);
    }

}
