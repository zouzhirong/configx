package com.configx.web.hook.handler;

import com.configx.web.hook.HookEventType;
import com.configx.web.hook.event.HookEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HookEventHandlerDelegate {

    @Autowired(required = false)
    private List<HookEventHandler> handlers;

    private final Map<HookEventType, HookEventHandler> handlerCache = new ConcurrentHashMap<>(256);

    @PostConstruct
    public void init() {
        if (this.handlers == null) {
            this.handlers = new ArrayList<>();
        }
    }

    /**
     * 返回handler列表
     *
     * @return
     */
    public List<HookEventHandler> getHandlers() {
        return Collections.unmodifiableList(this.handlers);
    }

    /**
     * 清除配置的handler列表
     */
    public void clear() {
        this.handlers.clear();
    }

    /**
     * 查找HookEventHandler
     *
     * @param eventType
     * @return
     */
    private HookEventHandler getHookEventHandler(HookEventType eventType) {
        HookEventHandler result = this.handlerCache.get(eventType);
        if (result == null) {
            for (HookEventHandler handler : this.handlers) {
                if (handler.supports(eventType)) {
                    result = handler;
                    this.handlerCache.put(eventType, result);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 添加 {@link HookEventHandler}
     *
     * @param handler
     * @return
     */
    public HookEventHandlerDelegate addHandler(HookEventHandler handler) {
        this.handlers.add(handler);
        return this;
    }

    /**
     * 添加 {@link HookEventHandler}s.
     *
     * @param handlers
     * @return
     */
    public HookEventHandlerDelegate addHandlers(List<? extends HookEventHandler> handlers) {
        if (handlers != null) {
            for (HookEventHandler handler : handlers) {
                this.handlers.add(handler);
            }
        }
        return this;
    }

    public List<String> getEventParamNames(HookEventType eventType) {
        HookEventHandler handler = getHookEventHandler(eventType);
        Assert.notNull(handler, "Unknown event type [" + eventType + "]");
        return handler.getEventParamNames();
    }

    public List<String> getRequestParamNames(HookEventType eventType) {
        HookEventHandler handler = getHookEventHandler(eventType);
        Assert.notNull(handler, "Unknown event type [" + eventType + "]");
        return handler.getRequestParamNames();
    }

    public void handleEvent(HookEvent event) {
        HookEventHandler handler = getHookEventHandler(event.getEventType());
        Assert.notNull(handler, "Unknown event type [" + event.getEventType() + "]");
        handler.handleEvent(event);
    }

}
