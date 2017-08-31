/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.messagesource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * 基于Config的MessageSource
 *
 * @author Zhirong Zou
 */
public class ConfigMessageSource extends AbstractMessageSource {

    private ApplicationContext context;

    public ConfigMessageSource(ApplicationContext context) {
        this.context = context;
    }

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        ConfigMessageManager manager = context.getBean(ConfigMessageManager.class);
        return manager.resolveCodeWithoutArguments(code, locale);
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        ConfigMessageManager manager = context.getBean(ConfigMessageManager.class);
        return manager.resolveCode(code, locale);
    }

}
