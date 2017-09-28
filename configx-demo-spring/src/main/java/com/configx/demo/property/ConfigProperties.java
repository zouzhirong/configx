package com.configx.demo.property;

import com.configx.client.version.VersionContextHolder;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by zouzhirong on 2017/3/15.
 */
public class ConfigProperties {

    @Value("${timeout}")
    private long timeout;

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @PostConstruct
    public void init() {
        System.out.println();
    }

    @PreDestroy
    public void destroy() {
        long version = VersionContextHolder.getCurrentVersion();
        System.out.println("ConfigProperties destroy, version=" + version);
    }
}
