package com.configx.web.hook.handler;

import com.configx.web.hook.HookEventType;
import com.configx.web.hook.WebhookDetail;
import com.configx.web.hook.event.HookConfigChangeEvent;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zouzhirong on 2017/8/9.
 */
@Component
public class HookConfigChangeEventHandler extends HookEventHandlerSupport<HookConfigChangeEvent> {

    @Override
    public boolean supports(HookEventType eventType) {
        return HookEventType.CONFIG_CHANGE.equals(eventType);
    }

    @Override
    public List<String> getEventParamNames() {
        return Arrays.asList("env", "profile", "name", "valueType");
    }

    @Override
    public List<String> getRequestParamNames() {
        return Arrays.asList("name", "value", "description");
    }

    @Override
    protected boolean shouldTrigger(WebhookDetail webhook, HookConfigChangeEvent event) {
        String env = webhook.getEventParams().get("env");
        if (StringUtils.isNotEmpty(env)) {
            if (!env.equals(event.getEnv().getName())) {
                return false;
            }
        }

        String[] profiles = StringUtils.split(webhook.getEventParams().get("profile"), ",");
        if (profiles != null) {
            List<String> profileList = new ArrayList<>();
            for (String profile : profiles) {
                profileList.add(profile.trim());
            }
            if (event.getProfile() == null) {
                if (!profileList.contains("DEFAULT_PROFILE")) {
                    return false;
                }
            } else {
                if (!profileList.contains(event.getProfile().getName())) {
                    return false;
                }
            }
        }

        String name = webhook.getEventParams().get("name");
        if (StringUtils.isNotEmpty(name)) {
            if (!name.equals(event.getConfigItem().getName())) {
                return false;
            }
        }

        String[] valueTypes = StringUtils.split(webhook.getEventParams().get("valueType"), ",");
        if (valueTypes != null) {
            List<Integer> valueTypeList = new ArrayList<>();
            for (String valueType : valueTypes) {
                valueTypeList.add(Integer.valueOf(valueType.trim()));
            }
            int configValueType = event.getConfigItem().getValueType();
            if (!valueTypeList.contains(configValueType)) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected Object buildRequestBody(WebhookDetail webhook, HookConfigChangeEvent event) {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();

        paramMap.add("name", event.getConfigItem().getName());
        paramMap.add("value", event.getConfigItem().getValue());
        paramMap.add("description", event.getConfigItem().getDescription());

        return paramMap;
    }
}
