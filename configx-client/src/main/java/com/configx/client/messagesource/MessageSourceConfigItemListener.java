/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.messagesource;

import com.configx.client.item.listener.ConfigItemListener;
import com.configx.client.item.listener.ConfigItemListenerSupport;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * {@link ConfigItemListener}用于处理国际化消息的更新。
 *
 * @author Zhirong Zou
 */
public class MessageSourceConfigItemListener extends ConfigItemListenerSupport implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void onChange(Map<String, String> properties) {
        if (properties == null || properties.isEmpty()) {
            return;
        }

        // 动态修改basenames
        if (properties.containsKey(ConfigMessageManager.SPRING_MESSAGES_BASENAME)) {
            String basenames = properties.get(ConfigMessageManager.SPRING_MESSAGES_BASENAME);
            refreshBasenames(basenames);
        }

        // 刷新属性文件
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            refreshProperties(entry.getKey(), entry.getValue());
        }

    }

    /**
     * 动态修改basenames
     *
     * @param basenames
     */
    protected void refreshBasenames(String basenames) {
        ConfigMessageManager manager = applicationContext.getBean(ConfigMessageManager.class);
        manager.setBasename(basenames);
        logger.debug("ConfigItem change: spring.messages.basename=" + basenames);
    }

    /**
     * 刷新指定文件的属性值
     *
     * @param filename
     * @param value
     */
    protected void refreshProperties(String filename, String value) {
        ConfigMessageManager manager = applicationContext.getBean(ConfigMessageManager.class);

        if (manager.isLocalFile(filename)) {
            manager.refreshProperties(filename, value);
            logger.debug("ConfigItem change: Locale File=" + filename);
        }
    }

}
