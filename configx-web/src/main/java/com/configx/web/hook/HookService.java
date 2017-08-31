package com.configx.web.hook;

import com.configx.web.hook.event.HookEvent;
import com.configx.web.hook.handler.HookEventHandlerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zouzhirong on 2017/8/9.
 */
@Service
public class HookService {

    @Autowired
    private HookEventHandlerDelegate eventHandlerDelegate;

    public List<String> getEventParamNames(HookEventType eventType) {
        return eventHandlerDelegate.getEventParamNames(eventType);
    }

    public List<String> getRequestParamNames(HookEventType eventType) {
        return eventHandlerDelegate.getRequestParamNames(eventType);
    }

    @EventListener
    public void processHookEvent(HookEvent event) {
        eventHandlerDelegate.handleEvent(event);
    }

}
