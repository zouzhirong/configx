package com.configx.web.hook.handler;

import com.configx.web.hook.ContentType;
import com.configx.web.hook.HookStoreService;
import com.configx.web.hook.WebhookDetail;
import com.configx.web.support.ApplicationContextHelper;
import com.configx.web.hook.WebhookRequestResponse;
import com.configx.web.hook.event.HookEvent;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by zouzhirong on 2017/8/9.
 */
public abstract class HookEventHandlerSupport<T extends HookEvent> implements HookEventHandler<T> {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void handleEvent(HookEvent event) {
        T t = (T) event;
        this.doHandleEvent(t);
    }

    /**
     * 处理事件
     *
     * @param event
     */
    protected void doHandleEvent(T event) {
        List<WebhookDetail> webhooks = getHookStore().getWebhookDetails(event.getAppId(), event.getEventType());
        for (WebhookDetail webhook : webhooks) {
            handleWebhook(webhook, event);
        }
    }

    /**
     * 处理一个事件
     *
     * @param webhook
     * @param event
     */
    protected void handleWebhook(WebhookDetail webhook, T event) {
        boolean shouldTrigger = shouldTrigger(webhook, event);
        if (shouldTrigger) {
            doHandleWebhook(webhook, event);
        }
    }

    /**
     * 判断事件是否应该触发
     *
     * @param webhook
     * @param event
     * @return
     */
    protected abstract boolean shouldTrigger(WebhookDetail webhook, T event);

    /**
     * 处理一个事件
     *
     * @param webhook
     * @param event
     */
    protected void doHandleWebhook(WebhookDetail webhook, T event) {

        // 发送Post请求
        WebhookRequestResponse requestResponse = post(webhook, event);
        // 处理Webhook结果
        handleWebhookResult(webhook, event, requestResponse);
    }

    /**
     * 处理Webhook结果
     *
     * @param webhook
     * @param event
     * @param requestResponse
     */
    protected void handleWebhookResult(WebhookDetail webhook, T event, WebhookRequestResponse requestResponse) {
        getHookStore().createWebhookLog(webhook, requestResponse);
    }

    /**
     * 请求Webhook
     *
     * @param webhook
     * @param event
     * @return
     */
    protected WebhookRequestResponse post(WebhookDetail webhook, T event) {

        // 请求Content-Type
        String contentType = webhook.getContentType().getFullType();

        // 请求体
        Object requestBody = buildRequestBody(webhook, event);

        // 替换请求参数名
        requestBody = replaceRequestParamNames(webhook, requestBody);

        return doPost(webhook.getUrl(), contentType, requestBody);
    }

    /**
     * 替换请求参数名
     *
     * @param webhook
     * @param requestBody
     * @return
     */
    private Object replaceRequestParamNames(WebhookDetail webhook, Object requestBody) {
        if (ContentType.FORM.equals(webhook.getContentType())) {

            // 替换前的参数Map
            MultiValueMap<String, Object> sourceParamMap = (MultiValueMap<String, Object>) requestBody;

            // 替换后的参数Map
            MultiValueMap<String, Object> targetParamMap = new LinkedMultiValueMap<>();

            // 参数名映射
            Map<String, String> paramMapping = webhook.getRequestParams();

            String targetParamName = null;
            List<Object> paramValue = null;
            for (String sourceParamName : sourceParamMap.keySet()) {
                targetParamName = paramMapping.get(sourceParamName);
                if (StringUtils.isEmpty(targetParamName)) {
                    targetParamName = sourceParamName;
                }
                paramValue = sourceParamMap.get(sourceParamName);
                targetParamMap.put(targetParamName, paramValue);
            }

            return targetParamMap;

        } else {
            return requestBody;
        }
    }

    /**
     * 执行POST请求
     *
     * @param url
     * @param contentType
     * @param requestBody
     * @return
     */
    protected WebhookRequestResponse doPost(String url, String contentType, Object requestBody) {

        // 请求头
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.parseMediaType(contentType));

        HttpEntity<?> requestEntity = new HttpEntity(requestBody, requestHeaders);

        try {
            // 执行请求
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
            // 返回响应结果
            return new WebhookRequestResponse(requestHeaders, requestBody, responseEntity);

        } catch (Exception e) {
            return new WebhookRequestResponse(requestHeaders, requestBody, e);
        }

    }

    /**
     * 构造请求对象
     *
     * @param webhook
     * @param event
     * @return
     */
    protected abstract Object buildRequestBody(WebhookDetail webhook, T event);

    protected HookStoreService getHookStore() {
        return ApplicationContextHelper.getBean(HookStoreService.class);
    }

}
