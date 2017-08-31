package com.configx.web.freemarker;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.TemplateHashModel;

import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

/**
 * 处理freeMarker访问静态类静态方法
 * <p>
 * Created by zouzhirong on 2017/3/22.
 */
public class FreemarkerStaticModels extends HashMap<String, Object> {

    private static FreemarkerStaticModels INSTANCE = new FreemarkerStaticModels();

    private FreemarkerStaticModels() {
    }

    public static FreemarkerStaticModels getInstance() {
        return INSTANCE;
    }

    public void setStaticModels(Properties properties) {
        if (properties != null) {
            Set<String> keys = properties.stringPropertyNames();
            for (String key : keys) {
                INSTANCE.put(key, useStaticPackage(properties.getProperty(key)));
            }
        }
    }

    public static TemplateHashModel useStaticPackage(String key) {
        try {
            BeansWrapper wrapper = new BeansWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).build();
            TemplateHashModel staticModels = wrapper.getStaticModels();
            TemplateHashModel statics = (TemplateHashModel) staticModels.get(key);
            return statics;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
