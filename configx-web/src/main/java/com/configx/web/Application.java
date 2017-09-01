package com.configx.web;

import com.configx.web.mybatis.SpringBootExecutableJarVFS;
import org.apache.ibatis.io.VFS;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.WebApplicationInitializer;

@SpringBootApplication
// Mybatis Mapper Scan
@MapperScan("com.configx.web.dao")
@EnableAspectJAutoProxy
public class Application extends SpringBootServletInitializer implements WebApplicationInitializer {

    static {


        // 添加自定义VFS实现，Mybatis默认VFS实现不支持SpringBoot，导致Mybatis无法注册Type Alias
        VFS.addImplClass(SpringBootExecutableJarVFS.class);

        // 配置系统属性
        configSystemProperties();
    }

    /**
     * 配置系统属性
     */
    private static final void configSystemProperties() {
        System.setProperty("spring.config.name", "application,configx");
        System.setProperty(ConfigFileApplicationListener.CONFIG_LOCATION_PROPERTY, "file:../config/");
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

}
