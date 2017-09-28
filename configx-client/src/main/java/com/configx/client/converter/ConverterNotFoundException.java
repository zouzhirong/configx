package com.configx.client.converter;

import org.springframework.core.convert.TypeDescriptor;

/**
 * Created by zouzhirong on 2017/9/28.
 */
public class ConverterNotFoundException extends RuntimeException {

    private final String propertyName;

    private final String propertyValue;

    private final TypeDescriptor targetType;

    public ConverterNotFoundException(String propertyName, String propertyValue, TypeDescriptor targetType) {
        super("No converter found capable of converting from property name [" + propertyName + "] to type [" + targetType + "]");
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
        this.targetType = targetType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public TypeDescriptor getTargetType() {
        return targetType;
    }
}
