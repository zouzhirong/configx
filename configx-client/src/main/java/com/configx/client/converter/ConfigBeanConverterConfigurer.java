package com.configx.client.converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Config bean converters configurer.
 */
public class ConfigBeanConverterConfigurer {

    private List<ConfigBeanConverter> converters;

    public void setConverters(List<ConfigBeanConverter> converters) {
        this.converters = converters;
    }

    public void addConverters(ConfigBeanConverter converter) {
        if (this.converters == null) {
            this.converters = new ArrayList<>();
        }
        this.converters.add(converter);
    }

    public List<ConfigBeanConverter> getConverters() {
        return converters;
    }

}
