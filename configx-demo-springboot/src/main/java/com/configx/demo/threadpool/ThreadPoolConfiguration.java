package com.configx.demo.threadpool;

import com.configx.client.annotation.VersionRefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池Bean Configuration
 * Created by zouzhirong on 2017/9/25.
 */
@Configuration
public class ThreadPoolConfiguration {

    @Bean
    @VersionRefreshScope(dependsOn = {"threadpool.corePoolSize"})
    public ThreadPoolProperties threadPoolProperties() {
        return new ThreadPoolProperties();
    }

    @Bean
    @VersionRefreshScope(dependsOn = {"threadpool.corePoolSize"})
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolProperties threadPoolProperties) {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)
                Executors.newFixedThreadPool(threadPoolProperties.getCorePoolSize());
        return threadPoolExecutor;
    }

}
