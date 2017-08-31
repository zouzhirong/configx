package com.configx.web.hook;

/**
 * Created by zouzhirong on 2017/8/10.
 */
public enum ContentType {

    FORM("form", "application/x-www-form-urlencoded"),

    JSON("json", "application/json"),;

    /**
     * 简单类型，例如json、form
     */
    private String simpleType;

    /**
     * 完整类型，例如：application/json、application/x-www-form-urlencoded
     */
    private String fullType;

    ContentType(String simpleType, String fullType) {
        this.simpleType = simpleType;
        this.fullType = fullType;
    }

    public String getSimpleType() {
        return simpleType;
    }

    public String getFullType() {
        return fullType;
    }

    public static ContentType fromSimpleType(String simpleType) {
        for (ContentType contentType : values()) {
            if (contentType.getSimpleType().equalsIgnoreCase(simpleType)) {
                return contentType;
            }
        }
        return null;
    }
}
