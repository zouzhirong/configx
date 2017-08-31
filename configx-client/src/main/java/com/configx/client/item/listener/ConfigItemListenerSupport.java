package com.configx.client.item.listener;

import com.alibaba.fastjson.JSON;
import com.configx.client.item.ConfigItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置项监听器
 *
 * @author Zhirong Zou
 */
public abstract class ConfigItemListenerSupport implements ConfigItemListener {

    protected Log logger = LogFactory.getLog("com.configx.client.listener");

    @Override
    public void onApplicationEvent(ConfigItemChangeEvent event) {
        long version = event.getVersion();
        List<ConfigItem> itemList = event.getItemList();
        this.onChange(version, itemList);
    }

    /**
     * 属性值更新
     *
     * @param version
     * @param itemList
     */
    protected void onChange(long version, List<ConfigItem> itemList) {
        logger.debug("ConfigItem change: version=" + version + ", itemList:\n" + JSON.toJSONString(itemList, true));

        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }

        Map<String, String> properties = new HashMap<>();
        for (ConfigItem item : itemList) {
            properties.put(item.getName(), item.getValue());
        }

        this.onChange(properties);
    }

    /**
     * 更改的属性值
     *
     * @param properties
     */
    protected abstract void onChange(Map<String, String> properties);

}
