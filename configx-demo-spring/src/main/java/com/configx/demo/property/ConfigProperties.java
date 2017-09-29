package com.configx.demo.property;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by zouzhirong on 2017/3/15.
 */
public class ConfigProperties implements InitializingBean, DisposableBean {

    @Value("${timeout}")
    private long timeout;

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Initializing " + this);
    }

    @Override
    public void destroy() {
        System.out.println("Destroy " + this);
    }
}
