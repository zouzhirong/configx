package com.configx.web.hook;

/**
 * 事件类型
 * <p>
 * Created by zouzhirong on 2017/8/8.
 */
public enum HookEventType {

    /**
     * 配置变化
     */
    CONFIG_CHANGE(1002, "config_change"),;

    /**
     * 类型
     */
    private int type;

    /**
     * 名称
     */
    private String typeName;

    HookEventType(int type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    public int getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public static HookEventType defaultType() {
        return CONFIG_CHANGE;
    }

    public static HookEventType get(int type) {
        for (HookEventType eventType : HookEventType.values()) {
            if (eventType.getType() == type) {
                return eventType;
            }
        }
        return null;
    }
}
