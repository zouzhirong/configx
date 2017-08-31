package com.configx.client.item.locator;

import com.configx.client.item.ConfigItemList;
import com.configx.client.env.VersionPropertySource;
import com.configx.client.item.ConfigItem;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

/**
 * 属性文件属性源处理器
 */
public class ConfigPropertiesPropertySourceProcessor {

    private static final String SPRING_PROPERTY_SOURCES = "spring.property.sources";
    private static final String DEFAULT_PROPERTY_SOURCES = "application";
    private static final String DELIMITERS = ",; \t";

    private static final String XML_SUFFIX = ".xml";
    private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    private Environment environment;
    private String propertySourceName;

    public ConfigPropertiesPropertySourceProcessor(Environment environment) {
        this.environment = environment;
    }

    /**
     * 处理配置属性文件中的属性源
     *
     * @param versionPropertySource
     */
    public void processPropertySources(VersionPropertySource<ConfigItemList> versionPropertySource) {
        if (versionPropertySource == null) { // 当前版本无属性
            return;
        }

        // 初始化propertySourceName
        if (propertySourceName == null) {
            if (environment.containsProperty(SPRING_PROPERTY_SOURCES)) {
                propertySourceName = environment.getProperty(SPRING_PROPERTY_SOURCES);
            } else {
                propertySourceName = DEFAULT_PROPERTY_SOURCES;
            }
        }

        // 从配置中心动态更新propertySourceName
        if (versionPropertySource.containsProperty(SPRING_PROPERTY_SOURCES)) {
            propertySourceName = (String) versionPropertySource.getProperty(SPRING_PROPERTY_SOURCES);
        }

        String[] propertySourceNames = StringUtils.tokenizeToStringArray(propertySourceName, DELIMITERS);
        if (propertySourceNames != null) {
            for (String name : propertySourceNames) {
                if (StringUtils.isEmpty(name)) {
                    continue;
                }
                name = name.trim();
                if (versionPropertySource.containsProperty(name)) {
                    processPropertySource(versionPropertySource, name);
                }
            }
        }
    }

    /**
     * 处理配置属性文件中的属性源
     *
     * @param versionPropertySource
     * @param propertySourceName
     */
    private void processPropertySource(VersionPropertySource<ConfigItemList> versionPropertySource, String propertySourceName) {
        // 属性文件内容
        if (!versionPropertySource.containsProperty(propertySourceName)) {
            return;
        }
        String content = versionPropertySource.getProperty(propertySourceName).toString();
        if (StringUtils.isEmpty(content)) {
            return;
        }

        ConfigItemList configItemList = null;
        EnumerablePropertySource<ConfigItemList> enumerablePropertySource = versionPropertySource.getSource();
        if (enumerablePropertySource != null) {
            configItemList = enumerablePropertySource.getSource();
        }
        if (configItemList == null) {
            return;
        }

        Properties properties = loadProperties(propertySourceName, content);
//      PropertiesPropertySource pps = new PropertiesPropertySource(propertySourceName, properties);
        if (properties != null && !properties.isEmpty()) {
            for (Map.Entry<?, ?> entry : properties.entrySet()) {
                if (entry.getKey() != null) {
                    configItemList.addItem(new ConfigItem(entry.getKey().toString(), entry.getValue().toString()));
                }
            }
        }
    }

    /**
     * 加载属性文件内容
     *
     * @param filename
     * @param value
     * @return
     */
    protected Properties loadProperties(String filename, String value) {
        Properties props = new Properties();
        if (org.apache.commons.lang.StringUtils.isEmpty(value)) {
            return props;
        }
        try {
            if (filename.endsWith(XML_SUFFIX)) {
                this.propertiesPersister.loadFromXml(props, new ByteArrayInputStream(value.getBytes()));
            } else {
                this.propertiesPersister.load(props, new StringReader(value));
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not parse properties file [" + filename + "]" + e.getMessage());
        }

        return props;
    }
}
