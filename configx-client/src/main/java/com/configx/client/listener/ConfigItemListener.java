package com.configx.client.listener;

import org.springframework.context.ApplicationListener;

/**
 * 配置项监听器
 *
 * @author Zhirong Zou
 */
public interface ConfigItemListener extends ApplicationListener<ConfigItemChangeEvent> {

}
