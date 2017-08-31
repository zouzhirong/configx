/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.item.locator;

import com.alibaba.fastjson.JSON;
import com.configx.client.env.ConfigPropertySource;
import com.configx.client.env.VersionPropertySource;
import com.configx.client.item.ConfigItemList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * 配置服务的属性源Locator
 *
 * @author zhirong.zou
 */
public class ConfigServicePropertySourceLocator implements PropertySourceLocator {

    private static Log logger = LogFactory.getLog(ConfigServicePropertySourceLocator.class);

    private ConfigClientProperties clientProperties;
    private RestTemplate restTemplate;

    public ConfigServicePropertySourceLocator(ConfigClientProperties clientProperties) {
        this.clientProperties = clientProperties;
        restTemplate = new RestTemplate();
    }

    /**
     * 设置Client Properties
     *
     * @param clientProperties
     */
    public void setClientProperties(ConfigClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    /**
     * 返回Client Properties
     *
     * @return
     */
    public ConfigClientProperties getClientProperties() {
        return clientProperties;
    }

    /**
     * 设置RestTemplate
     *
     * @param restTemplate
     */
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 返回RestTemplate
     *
     * @param client
     * @return
     */
    private RestTemplate getRestTemplate(ConfigClientProperties client) {
        return this.restTemplate;
    }

    @Override
    public VersionPropertySource<ConfigItemList> update(long version) {
        // 获取RestTemplate
        RestTemplate restTemplate = this.restTemplate == null ? getRestTemplate(clientProperties) : this.restTemplate;

        Exception error = null;
        String errorBody = null;

        try {
            ConfigItemList result = post(restTemplate,
                    clientProperties.getUri(),
                    clientProperties.getApp(), clientProperties.getAppKey(),
                    clientProperties.getEnv(),
                    clientProperties.getProfiles(),
                    version + "");

            if (result != null) {
                if (result.getVersion() == version) { // 没有新的发布(result.version>version)，也没有回滚(result.version<version)
                    return null;
                }

                ConfigPropertySource propertySource = new ConfigPropertySource("configService", result);
                return new VersionPropertySource<>(result.getVersion(), propertySource);
            }

        } catch (HttpServerErrorException e) {
            error = e;
            if (MediaType.APPLICATION_JSON.includes(e.getResponseHeaders().getContentType())) {
                errorBody = e.getResponseBodyAsString();
            }

        } catch (Exception e) {
            error = e;

        }

        if (clientProperties != null && clientProperties.isFailFast()) {
            throw new IllegalStateException("Could not locate PropertySource and the fail fast property is set, failing", error);
        }

        logger.error("Could not locate remote config: " + (errorBody == null ? error == null ? "config not found" : error.getMessage() : errorBody));

        return null;
    }

    /**
     * 从配置管理中心更新配置
     *
     * @param restTemplate restTemplate
     * @param uri          配置服务器uri
     * @param app          应用标识
     * @param appKey       App Key
     * @param env          环境标识
     * @param profiles     profile列表，逗号分隔
     * @param version      本地最新版本
     * @return
     */
    private ConfigItemList post(RestTemplate restTemplate,
                                String uri,
                                String app, String appKey,
                                String env,
                                String profiles,
                                String version) {
        long startTime = System.currentTimeMillis();

        // 构造请求路径
        String path = "update?app={app}&app_key={app_key}&env={env}&from-version={version}";
        Object[] args = new String[]{app, appKey, env, version};
        if (StringUtils.hasText(profiles)) {
            args = new String[]{app, appKey, env, version, profiles};
            path = path + "&profiles={profiles}";
        }

        // 请求URL
        String url = uri + path;
        logger.debug("Config Updating\n"
                + "\turl      : " + url + "\n"
                + "\tapp      : " + app + "\n"
                + "\tappKey   : " + appKey + "\n"
                + "\tenv      : " + env + "\n"
                + "\tprofiles : " + profiles + "\n"
                + "\tversion  : " + version + "\n"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8.toString());

        // 执行请求，获取响应
        ResponseEntity<ConfigItemList> response = restTemplate.exchange(url,
                HttpMethod.GET,
                new HttpEntity<>((Void) null, headers),
                ConfigItemList.class, args);

        if (response == null || response.getStatusCode() != HttpStatus.OK) { // 错误响应
            return null;
        }

        // 从响应体中获取变更的配置列表
        ConfigItemList result = response.getBody();
        if (result != null && CollectionUtils.isNotEmpty(result.getItems())) {
            long time = System.currentTimeMillis() - startTime;
            logger.info("Config Update completed in " + time + " ms");
            if (version != null && !version.equals("0")) {
                logger.info("Config Updated[" + time + "ms]\n" + JSON.toJSONString(result, true));
            }
        }

        return result;
    }

}

