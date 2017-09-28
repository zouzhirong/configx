package com.configx.client.config;

import com.configx.client.dependency.RefreshBeanDependencyFactory;
import com.configx.client.env.ConfigPropertySource;
import com.configx.client.env.MultiVersionPropertySourceFactory;
import com.configx.client.env.VersionPropertySource;
import com.configx.client.item.ConfigItemList;
import com.configx.client.item.ConfigItemPostProcessor;
import com.configx.client.listener.ConfigItemChangeEvent;
import com.configx.client.scope.refresh.VersionRefreshScope;
import com.configx.client.version.ConfigVersionManager;
import com.configx.client.version.VersionContextHolder;
import com.configx.client.version.VersionExecutor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 配置管理Context
 * <p>
 * Created by zouzhirong on 2017/9/14.
 */
public class ConfigContext implements BeanDefinitionRegistryPostProcessor,
        ApplicationListener<ContextRefreshedEvent>,
        ApplicationContextAware, EnvironmentAware,
        InitializingBean {

    private final Log logger = LogFactory.getLog("com.configx.client.context");

    private ApplicationContext applicationContext;

    private ConfigurableEnvironment environment;

    /**
     * 基于版本刷新的scope
     */
    private VersionRefreshScope scope;

    /**
     * 配置客户端属性
     */
    private ConfigClientProperties clientProperties;

    /**
     * 配置属性源Locator，用于从远程配置管理中心加载配置
     */
    private PropertySourceLocator propertySourceLocator;

    /**
     * 可刷新的bean names
     */
    private final List<String> refreshScopeBeanNames = new ArrayList<>();

    /**
     * 配置项预处理器
     */
    private final List<ConfigItemPostProcessor> configItemPostProcessors = new ArrayList<>();

    /**
     * 配置监听器
     */
    private final List<ConfigWatcher> configWatchers = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    /**
     * 设置Scope
     *
     * @param scope
     */
    public void setScope(VersionRefreshScope scope) {
        this.scope = scope;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.clientProperties = initClientProperties();
        this.propertySourceLocator = new ConfigServicePropertySourceLocator(clientProperties);

        setDestructionCallback((name) ->
                scope.destroy(name)
        );
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registerBeanDefinitions(registry);
        registerConfigItemPostProcessors();
        initRefreshScopeBeanNames(registry);
        refresh(false);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            registerConfigWatchers();
            watch();
        }
    }

    /**
     * 设置销毁回调
     *
     * @param destructionCallback
     */
    public void setDestructionCallback(DestructionCallback destructionCallback) {
        DestructionCallbackBindingListener destructionCallbackListener = new DestructionCallbackBindingListener(this, destructionCallback);
        ConfigVersionManager.addVersionRemovalListeners(destructionCallbackListener);
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

    /**
     * 注册beans
     *
     * @param registry
     */
    private void registerBeanDefinitions(BeanDefinitionRegistry registry) {
        String beanName = PropertiesFileConfigItemPostProcessor.class.getName();
        if (!registry.containsBeanDefinition(beanName)) {
            BeanDefinitionBuilder bean = BeanDefinitionBuilder.genericBeanDefinition(PropertiesFileConfigItemPostProcessor.class);
            registry.registerBeanDefinition(beanName, bean.getBeanDefinition());
        }
    }

    /**
     * 注册所有可刷新的beans
     *
     * @param registry
     */
    private void initRefreshScopeBeanNames(BeanDefinitionRegistry registry) {
        List<String> list = getRefreshScopeBeans(registry);
        this.refreshScopeBeanNames.addAll(list);
    }

    /**
     * 获取所有可刷新的beans
     *
     * @param registry
     */
    private List<String> getRefreshScopeBeans(BeanDefinitionRegistry registry) {
        List<String> beanNames = new ArrayList<>();
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (VersionRefreshScope.SCOPE_NAME.equalsIgnoreCase(beanDefinition.getScope())) {
                beanNames.add(beanName);
            }
        }
        return beanNames;
    }

    /**
     * 注册配置项预处理器
     *
     * @return
     */
    private void registerConfigItemPostProcessors() {
        List<ConfigItemPostProcessor> configItemPostProcessors = new ArrayList<>();
        configItemPostProcessors.addAll(applicationContext.getBeansOfType(ConfigItemPostProcessor.class).values());
        AnnotationAwareOrderComparator.sort(configItemPostProcessors);

        this.configItemPostProcessors.addAll(configItemPostProcessors);
    }

    /**
     * 注册配置监听器
     */
    private void registerConfigWatchers() {
        List<ConfigWatcher> watchers = new ArrayList<>();
        watchers.addAll(applicationContext.getBeansOfType(ConfigWatcher.class).values());
        AnnotationAwareOrderComparator.sort(watchers);

        // 如果没有配置任何监听器，则启动一个默认的轮询监听器
        if (CollectionUtils.isEmpty(watchers)) {
            ConfigWatcher defaultConfigWatcher = createDefaultConfigWatcher();
            watchers.add(defaultConfigWatcher);
        }

        this.configWatchers.addAll(watchers);
    }

    /**
     * 创建一个默认的配置监听器
     *
     * @return
     */
    private ConfigWatcher createDefaultConfigWatcher() {
        ConfigPollingWatcher configWatcher = new ConfigPollingWatcher();
        configWatcher.setUpdateInitialDelay(clientProperties.getUpdateInitialDelay());
        configWatcher.setUpdateInterval(clientProperties.getUpdateInterval());
        return configWatcher;
    }

    /**
     * 启动监听
     */
    public void watch() {
        List<ConfigWatcher> watchers = this.configWatchers;

        // 监听回调
        ConfigWatcher.Callback callback = new ConfigWatcher.Callback() {
            @Override
            public void onChange() {
                refresh(true);
            }
        };

        for (ConfigWatcher watcher : new LinkedHashSet<>(watchers)) {
            watcher.start(clientProperties, callback);
        }
    }

    /**
     * 刷新
     *
     * @param isEagerInit 是否立即初始化可刷新的beans
     */
    public synchronized void refresh(boolean isEagerInit) {
        try {
            // 从配置管理中心更新配置
            VersionPropertySource<ConfigItemList> versionPropertySource = updateConfig();
            if (versionPropertySource == null) {
                return;
            }

            // 调用配置项预处理器处理配置
            invokeConfigItemPostProcessors(versionPropertySource);

            // 添加新版本配置到环境属性源中
            addVersionPropertySource(versionPropertySource);

            // 刷新beans
            refreshVersionBeans(versionPropertySource);

            // 预先初始化新版本的beans，首次加载配置时，Spring未启动完，不进行预先初始化
            if (isEagerInit) {
                preInstantiateRefreshScopeBeans(versionPropertySource);
            }

            // 升级版本号，新版本号可见
            exposeVersion(versionPropertySource.getVersion());

            // 完成刷新
            finishRefresh(versionPropertySource);

        } catch (Exception e) {
            logger.error("Refresh config context error", e);
        }
    }

    /**
     * 从远程配置管理中心更新配置
     *
     * @return
     */
    private VersionPropertySource<ConfigItemList> updateConfig() {
        return propertySourceLocator.update(ConfigVersionManager.getHeadVersion());
    }

    /**
     * 调用所有的{@link ConfigItemPostProcessor}进行配置项预处理
     *
     * @param versionPropertySource
     */
    private void invokeConfigItemPostProcessors(VersionPropertySource<ConfigItemList> versionPropertySource) {
        if (versionPropertySource == null) {
            return;
        }
        List<ConfigItemPostProcessor> configItemPostProcessors = this.configItemPostProcessors;
        if (CollectionUtils.isEmpty(configItemPostProcessors)) {
            return;
        }

        long version = versionPropertySource.getVersion();
        EnumerablePropertySource<?> propertySource = versionPropertySource.getSource();
        if (propertySource == null) {
            return;
        }
        if (!(propertySource instanceof ConfigPropertySource)) {
            return;
        }

        ConfigPropertySource configPropertySource = (ConfigPropertySource) propertySource;

        for (ConfigItemPostProcessor postProcessor : configItemPostProcessors) {
            postProcessor.postProcessConfigItems(version, configPropertySource);
        }
    }

    /**
     * 添加新版本属性源
     *
     * @param versionPropertySource
     */
    private void addVersionPropertySource(VersionPropertySource<ConfigItemList> versionPropertySource) {
        if (versionPropertySource == null) {
            return;
        }
        // 将更新的配置添加到环境属性源中
        if (ConfigVersionManager.addVersion(versionPropertySource.getVersion())) {
            MultiVersionPropertySourceFactory.addVersionPropertySource(versionPropertySource, applicationContext, environment);
        }
    }

    /**
     * 获取指定版本需要刷新的beans
     *
     * @param version
     * @return
     */
    public Set<String> getRefreshBeanNames(long version) {
        return scope.getRefreshBeanNames(version);
    }

    /**
     * 刷新beans
     *
     * @param versionPropertySource
     */
    private void refreshVersionBeans(VersionPropertySource<?> versionPropertySource) {
        if (versionPropertySource == null) {
            return;
        }

        long version = versionPropertySource.getVersion();
        Set<String> needRefreshBeanNames = getNeedRefreshBeanNames(versionPropertySource);
        scope.refresh(version, needRefreshBeanNames);
    }

    /**
     * 获取这个版本属性源需要刷新的bean names
     *
     * @param versionPropertySource
     * @return
     */
    private Set<String> getNeedRefreshBeanNames(VersionPropertySource<?> versionPropertySource) {
        if (versionPropertySource == null) {
            return Collections.emptySet();
        }

        // 这个版本的属性有变化,判断这个bean是否跟这些变化的属性有关,有就刷新,没有则不刷新
        RefreshBeanDependencyFactory dependencyFactory = getRefreshBeanDependencyFactory();

        Set<String> refreshBeanNames = new HashSet<>();
        for (String beanName : this.refreshScopeBeanNames) {
            if (shouldRefresh(versionPropertySource, beanName, dependencyFactory)) {
                refreshBeanNames.add(beanName);
            }
        }

        return refreshBeanNames;
    }

    /**
     * 判断指定版本的指定的bean是否需要刷新
     *
     * @param versionPropertySource
     * @param beanName
     * @param dependencyFactory
     * @return
     */
    private boolean shouldRefresh(VersionPropertySource<?> versionPropertySource, String beanName,
                                  RefreshBeanDependencyFactory dependencyFactory) {

        // 这个版本的属性未变化，不用刷新任何Bean
        if (versionPropertySource == null) {
            return false;
        }

        // 这个版本变化的属性名
        String[] changedPropertyNames = versionPropertySource.getPropertyNames();
        if (ArrayUtils.isEmpty(changedPropertyNames)) {
            return false;
        }

        String[] dependentPropertyNames = dependencyFactory.getDependentPropertyNames(beanName);

        for (String propertyName : changedPropertyNames) {
            for (String dependentPropertyName : dependentPropertyNames) {
                if (Pattern.matches(dependentPropertyName, propertyName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回{@link RefreshBeanDependencyFactory} Bean
     *
     * @return
     */
    private RefreshBeanDependencyFactory getRefreshBeanDependencyFactory() {
        String name = ConfigBeanDefinitionRegistryPostProcessor.REFRESH_DEPENDENCY_FACTORY_BEAN_NAME;
        return applicationContext.getBean(name, RefreshBeanDependencyFactory.class);
    }

    /**
     * 预先初始化新版本的beans
     *
     * @param versionPropertySource
     */
    protected void preInstantiateRefreshScopeBeans(VersionPropertySource<ConfigItemList> versionPropertySource) {
        if (versionPropertySource == null) {
            return;
        }
        long version = versionPropertySource.getVersion();
        List<String> beanNames = new ArrayList<>(this.refreshScopeBeanNames);

        VersionExecutor.doWithVersion(version, (v) -> {
            for (String beanName : beanNames) {
                applicationContext.getBean(beanName);
            }
            return null;
        });
    }

    /**
     * 升级版本号，新版本号可见
     *
     * @param version
     * @return
     */
    protected void exposeVersion(long version) {
        VersionContextHolder.setVisibleVersion(version);

        // 移除前一个版本的引用，使其被销毁
        long previousVersion = ConfigVersionManager.getPreviousVersion(version);
        ConfigVersionManager.removeBeanVersion(previousVersion);
    }

    /**
     * 完成刷新
     *
     * @param versionPropertySource
     */
    protected void finishRefresh(VersionPropertySource<ConfigItemList> versionPropertySource) {
        // 发布事件
        applicationContext.publishEvent(new ConfigItemChangeEvent(this, versionPropertySource));
    }

}

