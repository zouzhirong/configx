package com.configx.web.hook.event;


import com.configx.web.hook.HookEventType;

/**
 * Created by zouzhirong on 2017/8/9.
 */
public interface HookEvent {

    int getAppId();

    HookEventType getEventType();

}
