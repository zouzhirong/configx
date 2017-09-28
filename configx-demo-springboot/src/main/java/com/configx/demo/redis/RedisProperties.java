package com.configx.demo.redis;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by zouzhirong on 2017/9/26.
 */
public class RedisProperties {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
