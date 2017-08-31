package com.configx.client.item.listener;

import com.configx.client.item.ConfigItemList;
import com.configx.client.env.VersionPropertySource;
import com.configx.client.item.ConfigItem;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.env.EnumerablePropertySource;

import java.util.List;


public class ConfigItemChangeEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private long version;

    private List<ConfigItem> itemList;

    public ConfigItemChangeEvent(Object source, VersionPropertySource<ConfigItemList> versionPropertySource) {
        super(source);
        this.version = versionPropertySource.getVersion();

        EnumerablePropertySource<ConfigItemList> enumerablePropertySource = versionPropertySource.getSource();
        if (enumerablePropertySource != null) {
            ConfigItemList configItemList = enumerablePropertySource.getSource();
            if (configItemList != null) {
                this.itemList = configItemList.getItems();
            }
        }
    }

    public ConfigItemChangeEvent(Object source, long version, List<ConfigItem> itemList) {
        super(source);
        this.version = version;
        this.itemList = itemList;
    }

    public long getVersion() {
        return version;
    }

    public List<ConfigItem> getItemList() {
        return itemList;
    }
}

