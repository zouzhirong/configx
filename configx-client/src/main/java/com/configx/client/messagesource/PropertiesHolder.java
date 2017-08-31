package com.configx.client.messagesource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * PropertiesHolder for caching.
 */
public class PropertiesHolder {

    private static final Log logger = LogFactory.getLog("com.configx.client.messagesource");

    private static final String XML_SUFFIX = ".xml";

    private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    private Properties properties;

    /**
     * Cache to hold already generated MessageFormats per message code
     */
    private final ConcurrentMap<String, Map<Locale, MessageFormat>> cachedMessageFormats =
            new ConcurrentHashMap<>();

    public PropertiesHolder(Properties properties) {
        this.properties = properties;
    }

    public PropertiesHolder(String filename, String value) {
        this.properties = loadProperties(filename, value);
    }

    public Properties getProperties() {
        return this.properties;
    }

    public String getProperty(String code) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.getProperty(code);
    }

    public MessageFormat getMessageFormat(String code, Locale locale) {
        if (this.properties == null) {
            return null;
        }
        Map<Locale, MessageFormat> localeMap = this.cachedMessageFormats.get(code);
        if (localeMap != null) {
            MessageFormat result = localeMap.get(locale);
            if (result != null) {
                return result;
            }
        }
        String msg = this.properties.getProperty(code);
        if (msg != null) {
            if (localeMap == null) {
                localeMap = new ConcurrentHashMap<Locale, MessageFormat>();
                Map<Locale, MessageFormat> existing = this.cachedMessageFormats.putIfAbsent(code, localeMap);
                if (existing != null) {
                    localeMap = existing;
                }
            }
            MessageFormat result = createMessageFormat(msg, locale);
            localeMap.put(locale, result);
            return result;
        }
        return null;
    }

    /**
     * Create a MessageFormat for the given message and Locale.
     *
     * @param msg    the message to create a MessageFormat for
     * @param locale the Locale to create a MessageFormat for
     * @return the MessageFormat instance
     */
    protected MessageFormat createMessageFormat(String msg, Locale locale) {
        msg = escape(msg);
        return new MessageFormat((msg != null ? msg : ""), locale);
    }

    /**
     * 对消息进行转义
     *
     * @param msg
     * @return
     */
    protected String escape(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return msg;
        }
        msg = msg.replaceAll("'", "''"); // 单引号转义
        return msg;
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
        if (StringUtils.isEmpty(value)) {
            return props;
        }
        try {
            if (filename.endsWith(XML_SUFFIX)) {
                this.propertiesPersister.loadFromXml(props, new ByteArrayInputStream(value.getBytes()));
            } else {
                this.propertiesPersister.load(props, new StringReader(value));
            }
        } catch (IOException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not parse properties file [" + filename + "]", ex);
            }
        }

        return props;
    }
}
