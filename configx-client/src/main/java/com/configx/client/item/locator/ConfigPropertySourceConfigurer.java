/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.item.locator;

import com.configx.client.env.ConfigVersionManager;
import com.configx.client.env.MultiVersionPropertySourceFactory;
import com.configx.client.env.VersionPropertySource;
import com.configx.client.item.ConfigItemList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 属性源配置
 *
 * @author Zhirong Zou
 */
public class ConfigPropertySourceConfigurer implements BeanDefinitionRegistryPostProcessor,
        ApplicationContextAware, EnvironmentAware, InitializingBean {

    private final Log logger = LogFactory.getLog("com.configx.client.propertysource");

    private ApplicationContext applicationContext;

    private ConfigurableEnvironment environment;

    private ConfigClientProperties clientProperties;

    private PropertySourceLocator propertySourceLocator;

    private ConfigPropertiesPropertySourceProcessor propertiesPropertySourceProcessor;

    private ConfigUpdater configUpdater = null;

    private final ScheduledExecutorService configUpdateExecutor = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.clientProperties = initClientProperties();
        this.propertySourceLocator = new ConfigServicePropertySourceLocator(clientProperties);
        this.propertiesPropertySourceProcessor = new ConfigPropertiesPropertySourceProcessor(environment);
    }

    /**
     * 初始化配置客户端属性
     *
     * @return
     */
    private ConfigClientProperties initClientProperties() {
        // 从configx.properties文件读取属性
        loadProperties();

        ConfigClientProperties defaults = new ConfigClientProperties();
        ConfigClientProperties clientProperties = defaults.override(environment);
        return clientProperties;
    }

    /**
     * 从configx.properties文件读取属性
     */
    private void loadProperties() {
        Resource configxClientProperties = new ClassPathResource("configx.properties");
        if (configxClientProperties.exists()) {
            try {
                Properties defaultProperties = PropertiesLoaderUtils.loadProperties(configxClientProperties);
                PropertySource<?> defaultPropertySource = new PropertiesPropertySource("configxClientProperties", defaultProperties);
                this.environment.getPropertySources().addLast(defaultPropertySource);
            } catch (IOException e) {
                logger.error("Failed to load configx.properties", e);
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // TODO Auto-generated method stub
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 启动配置更新
        configUpdaterStarted();
    }

    /**
     * 更新配置属性源
     *
     * @return
     */
    private synchronized VersionPropertySource<ConfigItemList> updateConfigPropertySource() {
        VersionPropertySource<ConfigItemList> versionPropertySource = propertySourceLocator.update(ConfigVersionManager.getHeadVersion());

        // 处理配置文件中的属性源
        if (versionPropertySource != null) {
            propertiesPropertySourceProcessor.processPropertySources(versionPropertySource);
        }

        return versionPropertySource;
    }

    /**
     * 启动配置更新
     */
    public void configUpdaterStarted() {
        configUpdater = new ConfigUpdater();
        configUpdater.initialize();

        logger.debug("ConfigUpdate background threads started (as scheduler was started).");
    }

    /////////////////////////////////////////////////////////////////////////////
    //
    // ConfigUpdate Thread
    //
    /////////////////////////////////////////////////////////////////////////////

    class ConfigUpdater extends Thread {

        ConfigUpdater() {
            this.setPriority(Thread.NORM_PRIORITY + 2);
            this.setName("config-updater");
            this.setDaemon(true);
        }

        public void initialize() {
            this.update();
            configUpdateExecutor.scheduleWithFixedDelay(ConfigUpdater.this,
                    clientProperties.getUpdateInitialDelay(), clientProperties.getUpdateInterval(), TimeUnit.MILLISECONDS);
        }

        private void update() {
            try {
                VersionPropertySource<ConfigItemList> versionPropertySource = updateConfigPropertySource();
                MultiVersionPropertySourceFactory.addVersionPropertySource(versionPropertySource, applicationContext, environment);
            } catch (Exception e) {
                logger.error("Config Update Failed", e);
            }
        }

        @Override
        public void run() {
            update();
        }
    }

}
