package com.configx.web.hook;

import com.configx.web.event.ConfigChangeEvent;
import com.configx.web.hook.event.HookConfigChangeEvent;
import com.configx.web.model.ConfigItem;
import com.configx.web.model.Env;
import com.configx.web.model.Profile;
import com.configx.web.service.app.AppService;
import com.configx.web.service.app.EnvService;
import com.configx.web.service.app.ProfileService;
import com.configx.web.service.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by zouzhirong on 2017/8/10.
 */
@Component
public class HookEventPublisher {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AppService appService;

    @Autowired
    private EnvService envService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ConfigService configService;

    @Async
    @EventListener
    public void onConfigChangeEvent(ConfigChangeEvent event) {

        String value = null;
        ConfigItem configItem = configService.getConfigItem(event.getConfigItem().getId());
        if (configItem != null) {
            value = configItem.getValue();
        }

        Env env = envService.getEnv(event.getConfigItem().getEnvId());
        Profile profile = profileService.getProfile(event.getConfigItem().getProfileId());

        applicationContext.publishEvent(new HookConfigChangeEvent(event.getConfigItem().getAppId(), env, profile, event.getConfigItem(), value));
    }

}
