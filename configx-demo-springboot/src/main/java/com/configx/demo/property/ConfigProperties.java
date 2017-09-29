package com.configx.demo.property;

import com.configx.client.annotation.VersionRefreshScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by zouzhirong on 2017/3/15.
 */
// @VersionRefreshScope注解用来标记这是一个可刷新的Bean，当配置更改时，这个Bean会重新创建，使用最新的配置。
// dependsOn设置哪些配置更改时，需要重新创建这个Bean。
@VersionRefreshScope(dependsOn = {"timeout"})
@Service
public class ConfigProperties {

    @Value("${timeout}")
    private long timeout;

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    @PostConstruct
    public void init() {
        System.out.println("Initializing " + this);
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Destroy " + this);
    }
}
