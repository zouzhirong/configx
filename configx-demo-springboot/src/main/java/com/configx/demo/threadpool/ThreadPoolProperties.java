package com.configx.demo.threadpool;

import org.springframework.beans.factory.annotation.Value;

/**
 * 线程池属性
 * Created by zouzhirong on 2017/9/26.
 */
public class ThreadPoolProperties {

    @Value("${threadpool.corePoolSize}")
    private int corePoolSize;

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

}
