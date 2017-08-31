package com.configx.web.freemarker;

import com.configx.web.util.StringUtils;
import com.configx.web.locale.LocaleUtils;
import com.configx.web.locale.MessageUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.Properties;

/**
 * Created by zouzhirong on 2017/3/22.
 */
@Configuration
public class FreemarkerStaticModelConfiguration {

    @Bean
    public FreemarkerStaticModels freemarkerStaticModels(FreeMarkerViewResolver freeMarkerViewResolver) {

        Properties properties = staticProperties();

        FreemarkerStaticModels.getInstance().setStaticModels(properties);

        freeMarkerViewResolver.setAttributesMap(FreemarkerStaticModels.getInstance());

        return FreemarkerStaticModels.getInstance();
    }

    private Properties staticProperties() {
        Properties properties = new Properties();

        properties.put("LocaleUtils", LocaleUtils.class.getName());
        properties.put("MessageUtils", MessageUtils.class.getName());
        properties.put("StringUtils", StringUtils.class.getName());

        return properties;
    }
}
