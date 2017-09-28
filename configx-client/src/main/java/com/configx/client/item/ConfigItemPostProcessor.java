package com.configx.client.item;

import com.configx.client.env.ConfigPropertySource;

/**
 * Created by zouzhirong on 2017/9/14.
 */
public interface ConfigItemPostProcessor {

    void postProcessConfigItems(long version, ConfigPropertySource configPropertySource);

}
