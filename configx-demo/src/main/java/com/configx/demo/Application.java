package com.configx.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.web.WebApplicationInitializer;

/**
 * Spring Boot应用
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer implements WebApplicationInitializer {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

}
