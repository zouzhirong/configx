package com.configx.demo;

import com.configx.client.annotation.EnableConfigService ;
import com.configx.client.annotation.EnableMessageSource;
import com.configx.client.messagesource.ConfigMessageManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.WebApplicationInitializer;

@SpringBootApplication
// Spring Async
@EnableAsync
//Spring Scheduling
@EnableScheduling
@EnableConfigService
@EnableMessageSource
public class Application extends SpringBootServletInitializer implements WebApplicationInitializer {

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("spring.messages.basename", "basename");
        System.setProperty("spring.property.sources", "database.properties");
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Bean(name = "com.configx.demo.messagesource.ConfigMessageManager")
    public ConfigMessageManager configMessageManager() {
        ConfigMessageManager bean = new ConfigMessageManager();
        bean.setBasename("test_basename");
        return bean;
    }
}
