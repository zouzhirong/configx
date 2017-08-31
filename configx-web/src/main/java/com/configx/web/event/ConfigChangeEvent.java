package com.configx.web.event;

import com.configx.web.model.ConfigItem;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 配置更改事件
 * <p>
 * Created by zouzhirong on 2017/8/2.
 */
@Getter
public class ConfigChangeEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private ConfigItem configItem;

    public ConfigChangeEvent(Object source, ConfigItem configItem) {
        super(source);
        this.configItem = configItem;
    }
}