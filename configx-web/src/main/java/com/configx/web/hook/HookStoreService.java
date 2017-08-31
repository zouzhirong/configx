package com.configx.web.hook;

import com.alibaba.fastjson.JSON;
import com.configx.web.dao.WebhookEventParamMapper;
import com.configx.web.dao.WebhookLogMapper;
import com.configx.web.service.app.AppService;
import com.configx.web.web.dto.WebhookForm;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.configx.web.dao.WebhookMapper;
import com.configx.web.dao.WebhookRequestParamMapper;
import com.configx.web.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Hook存储服务
 * <p>
 * Created by zouzhirong on 2017/8/9.
 */
@Service
public class HookStoreService {

    @Autowired
    private AppService appService;

    @Autowired
    private WebhookMapper webhookMapper;

    @Autowired
    private WebhookRequestParamMapper webhookRequestParamMapper;

    @Autowired
    private WebhookEventParamMapper eventParamMapper;

    @Autowired
    private WebhookLogMapper webhookLogMapper;

    /**
     * 获取Webhook详细信息列表
     *
     * @param appId
     * @return
     */
    public List<WebhookDetail> getWebhookDetails(int appId) {

        List<Webhook> webhooks = getWebhookList(appId);

        List<Integer> webhookIdList = getWebhookIdList(webhooks);

        List<WebhookEventParam> eventParams = getWebhookEventParams(webhookIdList);

        List<WebhookRequestParam> requestParams = getWebhookRequestParams(webhookIdList);

        return buildWebhookDetails(webhooks, eventParams, requestParams);
    }

    /**
     * 获取指定ID的Webhook详细信息
     *
     * @param appId
     * @param webhookId
     * @return
     */
    public WebhookDetail getWebhookDetail(int appId, int webhookId) {

        Webhook webhook = getWebhook(appId, webhookId);

        List<WebhookEventParam> eventParams = getWebhookEventParams(Arrays.asList(webhookId));

        List<WebhookRequestParam> requestParams = getWebhookRequestParams(Arrays.asList(webhookId));

        return new WebhookDetail((webhook), eventParams, requestParams);
    }

    /**
     * 获取指定事件类型的所有Webhook详细信息列表
     *
     * @param appId
     * @param eventType
     * @return
     */
    public List<WebhookDetail> getWebhookDetails(int appId, HookEventType eventType) {
        List<Webhook> webhooks = webhookMapper.selectByAppAndEvent(appId, eventType.getType());

        List<Integer> webhookIdList = getWebhookIdList(webhooks);

        List<WebhookEventParam> eventParams = getWebhookEventParams(webhookIdList);

        List<WebhookRequestParam> requestParams = getWebhookRequestParams(webhookIdList);

        return buildWebhookDetails(webhooks, eventParams, requestParams);
    }

    /**
     * 从Webhook列表中获取Webhook ID列表
     *
     * @param webhooks
     * @return
     */
    private List<Integer> getWebhookIdList(List<Webhook> webhooks) {
        List<Integer> webhookIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(webhooks)) {
            for (Webhook Webhook : webhooks) {
                webhookIdList.add(Webhook.getId());
            }
        }
        return webhookIdList;
    }

    /***
     * 构建WebhookDetail信息
     *
     * @param webhooks
     * @param eventParams
     * @param requestParams
     * @return
     */
    private List<WebhookDetail> buildWebhookDetails(List<Webhook> webhooks, List<WebhookEventParam> eventParams, List<WebhookRequestParam> requestParams) {
        Multimap<Integer, WebhookEventParam> eventParamMultimap = ArrayListMultimap.create();
        Multimap<Integer, WebhookRequestParam> requestParamMultimap = ArrayListMultimap.create();

        if (CollectionUtils.isNotEmpty(eventParams)) {
            for (WebhookEventParam eventParam : eventParams) {
                eventParamMultimap.put(eventParam.getWebhookId(), eventParam);
            }
        }
        if (CollectionUtils.isNotEmpty(requestParams)) {
            for (WebhookRequestParam requestParam : requestParams) {
                requestParamMultimap.put(requestParam.getWebhookId(), requestParam);
            }
        }

        List<WebhookDetail> webhookDetails = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(webhooks)) {
            for (Webhook webhook : webhooks) {
                Collection<WebhookEventParam> webhookEventParams = eventParamMultimap.get(webhook.getId());
                Collection<WebhookRequestParam> webhookRequestParams = requestParamMultimap.get(webhook.getId());
                WebhookDetail webhookDetail = new WebhookDetail(webhook, webhookEventParams, webhookRequestParams);
                webhookDetails.add(webhookDetail);
            }
        }
        return webhookDetails;
    }

    /**
     * 添加Webhook
     *
     * @param appId
     * @param form
     */
    public void addWebhook(int appId, WebhookForm form) {
        // 创建Webhook
        Webhook webhook = createWebhook(appId, form.getName(), form.getUrl(), form.getContentType(), form.getSecret(), form.getEventType());

        // 重新创建Webhook事件参数
        recreateEventParams(webhook.getId(), webhook.getEventType(), form.getEventParams());

        // 重新创建Webhook请求参数
        recreateRequestParams(webhook.getId(), form.getRequestParams());
    }

    /**
     * 编辑WebhookDetail
     *
     * @param appId
     * @param webhookId
     * @param form
     */
    public void editWebhookDetail(int appId, int webhookId, WebhookForm form) {

        // 更新Webhook
        Webhook webhook = updateWebhook(webhookId, appId, form.getName(), form.getUrl(), form.getContentType(), form.getSecret(), form.getEventType());

        // 重新创建Webhook事件参数
        recreateEventParams(webhookId, webhook.getEventType(), form.getEventParams());

        // 重新创建Webhook请求参数
        recreateRequestParams(webhookId, form.getRequestParams());
    }


    /**
     * 删除WebhookDetail
     *
     * @param webhookId
     */
    public void deleteWebhookDetail(int appId, int webhookId) {

        deleteWebhook(appId, webhookId);

        deleteEventParams(webhookId);

        deleteRequestParams(webhookId);
    }

    //---------------------------------------------------------------------
    // Webhook
    //---------------------------------------------------------------------

    /**
     * 获取应用的Webhook列表
     *
     * @param appId
     * @return
     */
    public List<Webhook> getWebhookList(int appId) {
        return webhookMapper.selectByAppId(appId);
    }

    /**
     * 获取Webhook
     *
     * @param appId
     * @param webhookId
     * @return
     */
    public Webhook getWebhook(int appId, int webhookId) {
        Webhook webhook = webhookMapper.selectByPrimaryKey(webhookId);
        if (webhook == null || webhook.getAppId() != appId) {
            return null;
        } else {
            return webhook;
        }
    }

    /**
     * 创建Webhook
     *
     * @param appId
     * @param name
     * @param url
     * @param contentType
     * @param secret
     * @param eventType
     * @return
     */
    public Webhook createWebhook(int appId, String name, String url, String contentType, String secret, int eventType) {
        Webhook webhook = newWebhook(appId, name, url, contentType, secret, eventType);
        webhookMapper.insertSelective(webhook);
        return webhook;
    }

    private Webhook newWebhook(int appId, String name, String url, String contentType, String secret, int eventType) {
        Webhook bean = new Webhook();
        bean.setAppId(appId);
        bean.setName(name);
        bean.setUrl(url);
        bean.setContentType(contentType);
        bean.setSecret(secret);
        bean.setEventType(eventType);
        return bean;
    }

    /**
     * 更新Webhook
     *
     * @param webhookId
     * @param appId
     * @param name
     * @param url
     * @param contentType
     * @param secret
     * @param eventType
     * @return
     */
    public Webhook updateWebhook(int webhookId, int appId, String name, String url, String contentType, String secret, int eventType) {
        Webhook webhook = getWebhook(appId, webhookId);

//      webhook.setAppId(appId); // appId不能修改
        webhook.setName(name);
        webhook.setUrl(url);
        webhook.setContentType(contentType);
        webhook.setSecret(secret);
//      webhook.setEventType(eventType); // eventType不能修改

        webhookMapper.updateByPrimaryKeySelective(webhook);

        return webhook;
    }

    /**
     * 删除Webhook
     *
     * @param appId
     * @param webhookId
     */
    public void deleteWebhook(int appId, int webhookId) {
        Webhook webhook = getWebhook(appId, webhookId);
        webhookRequestParamMapper.deleteByWebhookId(webhookId);
        eventParamMapper.deleteByWebhookId(webhookId);
        webhookMapper.deleteByPrimaryKey(webhookId);
    }


    //---------------------------------------------------------------------
    // Webhook事件参数
    //---------------------------------------------------------------------

    /**
     * 获取多个Webhook的事件参数信息
     *
     * @param webhookIdList
     * @return
     */
    public List<WebhookEventParam> getWebhookEventParams(List<Integer> webhookIdList) {
        if (CollectionUtils.isEmpty(webhookIdList)) {
            return Collections.emptyList();
        }
        return eventParamMapper.selectByWebhookIdList(webhookIdList);
    }

    /**
     * 重新创建Webhook事件参数
     *
     * @param webhookId
     * @param eventType
     * @param paramMap
     * @return
     */
    public List<WebhookEventParam> recreateEventParams(int webhookId, int eventType, Map<String, String> paramMap) {

        // 删除
        deleteEventParams(webhookId);

        // 创建
        return createEventParams(webhookId, eventType, paramMap);
    }

    /**
     * 创建Webhook事件参数
     *
     * @param webhookId
     * @param eventType
     * @param paramMap
     * @return
     */
    public List<WebhookEventParam> createEventParams(int webhookId, int eventType, Map<String, String> paramMap) {
        if (MapUtils.isEmpty(paramMap)) {
            return Collections.emptyList();
        }

        List<WebhookEventParam> webhookEventParams = new ArrayList<>();
        WebhookEventParam webhookEventParam = null;
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            webhookEventParam = newWebhookEventParam(webhookId, eventType, entry.getKey(), entry.getValue());
            webhookEventParams.add(webhookEventParam);
        }
        if (CollectionUtils.isNotEmpty(webhookEventParams)) {
            eventParamMapper.batchInsert(webhookEventParams);
        }
        return webhookEventParams;
    }

    private WebhookEventParam newWebhookEventParam(int webhookId, int eventType, String name, String value) {
        WebhookEventParam bean = new WebhookEventParam();
        bean.setWebhookId(webhookId);
        bean.setEventType(eventType);
        bean.setName(name);
        bean.setValue(value);
        return bean;
    }

    /**
     * 删除Webhook事件参数
     *
     * @param webhookId
     * @return
     */
    public int deleteEventParams(int webhookId) {
        return eventParamMapper.deleteByWebhookId(webhookId);
    }


    //---------------------------------------------------------------------
    // Webhook请求参数
    //---------------------------------------------------------------------

    /**
     * 获取多个Webhook的参数信息
     *
     * @param webhookIdList
     * @return
     */
    public List<WebhookRequestParam> getWebhookRequestParams(List<Integer> webhookIdList) {
        if (CollectionUtils.isEmpty(webhookIdList)) {
            return Collections.emptyList();
        }
        return webhookRequestParamMapper.selectByWebhookIdList(webhookIdList);
    }

    /**
     * 重新创建Webhook请求参数
     *
     * @param webhookId
     * @param paramMap
     * @return
     */
    public List<WebhookRequestParam> recreateRequestParams(int webhookId, Map<String, String> paramMap) {

        // 删除
        deleteRequestParams(webhookId);

        // 创建
        return createRequestParams(webhookId, paramMap);
    }

    /**
     * 创建Webhook请求参数
     *
     * @param webhookId
     * @param paramMap
     * @return
     */
    public List<WebhookRequestParam> createRequestParams(int webhookId, Map<String, String> paramMap) {
        if (MapUtils.isEmpty(paramMap)) {
            return Collections.emptyList();
        }

        List<WebhookRequestParam> webhookRequestParams = new ArrayList<>();
        WebhookRequestParam webhookRequestParam = null;
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            webhookRequestParam = newWebhookRequestParam(webhookId, entry.getKey(), entry.getValue());
            webhookRequestParams.add(webhookRequestParam);
        }
        if (CollectionUtils.isNotEmpty(webhookRequestParams)) {
            webhookRequestParamMapper.batchInsert(webhookRequestParams);
        }
        return webhookRequestParams;
    }

    private WebhookRequestParam newWebhookRequestParam(int webhookId, String name, String value) {
        WebhookRequestParam bean = new WebhookRequestParam();
        bean.setWebhookId(webhookId);
        bean.setName(name);
        bean.setValue(value);
        return bean;
    }

    /**
     * 删除Webhook请求参数
     *
     * @param webhookId
     * @return
     */
    public int deleteRequestParams(int webhookId) {
        return webhookRequestParamMapper.deleteByWebhookId(webhookId);
    }


    //---------------------------------------------------------------------
    // Webhook请求参数
    //---------------------------------------------------------------------

    /**
     * 获取应用的所有Webhook日志
     *
     * @param appId
     * @return
     */
    public List<WebhookLog> getAppWebhookLogList(int appId) {
        return webhookLogMapper.selectByAppId(appId);
    }

    /**
     * 获取指定Webhook的所有日志
     *
     * @param webhookId
     * @return
     */
    public List<WebhookLog> getWebhookLogList(int webhookId) {
        return webhookLogMapper.selectByWebhookId(webhookId);
    }

    /**
     * 获取指定ID的Webhook日志
     *
     * @param logId
     * @return
     */
    public WebhookLog getWebhookLog(int logId) {
        return webhookLogMapper.selectByPrimaryKey(logId);
    }

    /**
     * 创建Webhook日志
     *
     * @param webhook
     * @param requestResponse
     * @return
     */
    public WebhookLog createWebhookLog(WebhookDetail webhook, WebhookRequestResponse requestResponse) {
        App app = appService.getApp(webhook.getAppId());
        WebhookLog log = newWebhookLog(app, webhook, requestResponse);
        webhookLogMapper.insertSelective(log);
        return log;
    }

    private WebhookLog newWebhookLog(App app, WebhookDetail webhook, WebhookRequestResponse requestResponse) {
        WebhookLog bean = new WebhookLog();
        bean.setAppId(webhook.getAppId());
        bean.setAppName(app.getName());
        bean.setWebhookId(webhook.getId());
        bean.setName(webhook.getName());
        bean.setUrl(webhook.getUrl());
        bean.setContentType(webhook.getContentType().getFullType());
        bean.setSecret(webhook.getSecret());
        bean.setEventType(webhook.getEventType().getType());
        bean.setEventName(webhook.getEventType().getTypeName());
        bean.setCreateTime(new Date());
        bean.setEventParams(JSON.toJSONString(webhook.getEventParams()));
        bean.setRequestParams(JSON.toJSONString(webhook.getRequestParams()));

        // HTTP请求、响应信息
        if (requestResponse.getRequestHeaders() != null) {
            bean.setRequestHeaders(JSON.toJSONString(requestResponse.getRequestHeaders()));
        } else {
            bean.setRequestHeaders("");
        }
        if (requestResponse.getRequestBody() != null) {
            bean.setRequestBody(JSON.toJSONString(requestResponse.getRequestBody()));
        } else {
            bean.setRequestBody("");
        }

        bean.setStatusCode(requestResponse.getStatusCode());
        if (requestResponse.getResponseHeaders() != null) {
            bean.setResponseHeaders(JSON.toJSONString(requestResponse.getResponseHeaders()));
        } else {
            bean.setResponseHeaders("");
        }
        if (requestResponse.getResponseBody() != null) {
            bean.setResponseBody(JSON.toJSONString(requestResponse.getResponseBody()));
        } else {
            bean.setResponseBody("");
        }

        // 错误信息
        if (requestResponse.getException() != null) {
            String errorMsg = getErrorMsg(requestResponse.getException());
            bean.setErrorMsg(errorMsg);
        } else {
            bean.setErrorMsg("");
        }

        return bean;
    }

    private String getErrorMsg(Exception e) {
        return ExceptionUtils.getFullStackTrace(e);
    }
}
