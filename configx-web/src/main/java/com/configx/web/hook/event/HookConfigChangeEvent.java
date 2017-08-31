package com.configx.web.hook.event;

import com.configx.web.hook.HookEventType;
import com.configx.web.model.ConfigItem;
import com.configx.web.model.Env;
import com.configx.web.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Created by zouzhirong on 2017/8/9.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class HookConfigChangeEvent implements HookEvent {

    private int appId;

    private Env env;

    private Profile profile;

    private ConfigItem configItem;

    private String value;

    @Override
    public int getAppId() {
        return appId;
    }

    @Override
    public HookEventType getEventType() {
        return HookEventType.CONFIG_CHANGE;
    }
}
