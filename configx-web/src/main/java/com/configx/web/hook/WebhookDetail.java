package com.configx.web.hook;

import com.configx.web.model.WebhookRequestParam;
import com.configx.web.support.ApplicationContextHelper;
import com.configx.web.model.Webhook;
import com.configx.web.model.WebhookEventParam;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zouzhirong on 2017/8/9.
 */
@Data
public class WebhookDetail {

    private int id;

    private int appId;

    private String name;

    private String url;

    private ContentType contentType;

    private String secret;

    private HookEventType eventType;

    private Map<String, String> eventParams = new LinkedHashMap();

    private Map<String, String> requestParams = new LinkedHashMap<>();

    public WebhookDetail(Webhook webhook, Collection<WebhookEventParam> eventParamList, Collection<WebhookRequestParam> requestParamList) {
        this.id = webhook.getId();
        this.appId = webhook.getAppId();
        this.name = webhook.getName();
        this.url = webhook.getUrl();
        this.contentType = ContentType.fromSimpleType(webhook.getContentType());
        this.secret = webhook.getSecret();
        this.eventType = HookEventType.get(webhook.getEventType());

        List<String> eventParamNames = getHookService().getEventParamNames(eventType);
        String eventParamValue = null;
        for (String eventParamName : eventParamNames) {
            eventParamValue = getEventParam(eventParamList, eventParamName);
            eventParams.put(eventParamName, eventParamValue == null ? "" : eventParamValue);
        }

        List<String> requestParamNames = getHookService().getRequestParamNames(eventType);
        String requestParamValue = null;
        for (String requestParamName : requestParamNames) {
            requestParamValue = getRequestParam(requestParamList, requestParamName);
            requestParams.put(requestParamName, requestParamValue == null ? "" : requestParamValue);
        }

    }

    private String getEventParam(Collection<WebhookEventParam> eventParamList, String name) {
        if (CollectionUtils.isNotEmpty(eventParamList)) {
            for (WebhookEventParam eventParam : eventParamList) {
                if (eventParam.getName().equals(name)) {
                    return eventParam.getValue();
                }
            }
        }
        return null;
    }

    private String getRequestParam(Collection<WebhookRequestParam> requestParamList, String name) {
        if (CollectionUtils.isNotEmpty(requestParamList)) {
            for (WebhookRequestParam requestParam : requestParamList) {
                if (requestParam.getName().equals(name)) {
                    return requestParam.getValue();
                }
            }
        }
        return null;
    }

    private HookService getHookService() {
        return ApplicationContextHelper.getBean(HookService.class);
    }
}
