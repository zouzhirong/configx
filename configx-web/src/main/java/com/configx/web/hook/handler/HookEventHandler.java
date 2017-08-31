package com.configx.web.hook.handler;

import com.configx.web.hook.HookEventType;
import com.configx.web.hook.event.HookEvent;

import java.util.List;

/**
 * Created by zouzhirong on 2017/8/9.
 */
public interface HookEventHandler<T extends HookEvent> {

    /**
     * 是否支持指定事件类型
     *
     * @param eventType
     * @return
     */
    boolean supports(HookEventType eventType);

    /**
     * 获取事件参数名
     *
     * @return
     */
    List<String> getEventParamNames();

    /**
     * 获取Webhook参数名
     *
     * @return
     */
    List<String> getRequestParamNames();

    /**
     * 处理事件
     *
     * @param event
     */
    void handleEvent(HookEvent event);

}
