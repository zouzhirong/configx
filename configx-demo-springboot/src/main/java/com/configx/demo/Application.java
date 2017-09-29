package com.configx.demo;

import com.configx.client.annotation.EnableConfigJmx;
import com.configx.client.annotation.EnableConfigService;
import com.configx.client.annotation.EnableMessageSource;
import com.configx.demo.bean.XmlConfigConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

/**
 * Spring Boot应用
 */
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
@EnableConfigService(converters = {XmlConfigConverter.class}) // 启动配置管理，并注册XmlConfigConverter
@EnableMessageSource(fallbackToSystemLocale = false, basenames = {})    // 开启配置管理对消息国际化的支持
@EnableConfigJmx        // 开启配置管理jmx支持
public class Application {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.setWebEnvironment(false);
        springApplication.run(args);
    }
}
