/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.messagesource;

import com.configx.client.env.ConfigPropertySource;
import com.configx.client.item.ConfigItemPostProcessor;
import com.configx.client.listener.ConfigItemListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * {@link ConfigItemListener}用于处理国际化消息的更新。
 *
 * @author Zhirong Zou
 */
public class MessageSourceConfigItemPostProcessor implements ConfigItemPostProcessor, ApplicationContextAware {

    private static final Log logger = LogFactory.getLog("com.configx.client.messagesource");

    private ApplicationContext applicationContext;

    private ConfigMessageSource configMessageSource;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setConfigMessageSource(ConfigMessageSource configMessageSource) {
        this.configMessageSource = configMessageSource;
    }

    @Override
    public void postProcessConfigItems(long version, ConfigPropertySource propertySource) {
        if (propertySource == null) {
            return;
        }

        // 动态修改basenames
        if (propertySource.containsProperty(ConfigMessageSource.SPRING_MESSAGES_BASENAME)) {
            String basenames = propertySource.getProperty(ConfigMessageSource.SPRING_MESSAGES_BASENAME);
            refreshBasenames(basenames);
        }

        // 刷新属性文件
        String propertyValue = null;
        for (String propertyName : propertySource.getPropertyNames()) {
            propertyValue = propertySource.getProperty(propertyName);
            if (propertyValue != null) {
                refreshProperties(propertyName, propertyValue);
            }
        }
    }

    /**
     * 动态修改basenames
     *
     * @param basenames
     */
    protected void refreshBasenames(String basenames) {
        configMessageSource.setBasename(basenames);
        logger.debug("ConfigItem change: spring.messages.basename=" + basenames);
    }

    /**
     * 刷新指定文件的属性值
     *
     * @param filename
     * @param value
     */
    protected void refreshProperties(String filename, String value) {
        if (configMessageSource.isMessageSourceFile(filename)) {
            configMessageSource.refreshProperties(filename, value);
            logger.debug("ConfigItem change: Locale File=" + filename);
        }
    }

}
