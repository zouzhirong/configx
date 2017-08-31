/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.servlet.listener;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Servlet Context Listener
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
public class WebContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // 解决Jetty POST请求2M数据长度限制
        // Maven Jetty Plugin 6.x
        System.setProperty("org.mortbay.jetty.Request.maxFormContentSize", "-1");
        // Maven Jetty Plugin 7.x
        System.setProperty("org.eclipse.jetty.server.Request.maxFormContentSize", "-1");

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Stopping logback-classic
        stopLogback();
    }

    /**
     * Stopping logback-classic
     */
    private void stopLogback() {
        // assume SLF4J is bound to logback-classic in the current environment
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.stop();
    }
}
