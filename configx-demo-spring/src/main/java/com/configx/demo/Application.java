package com.configx.demo;

import com.configx.demo.property.ConfigPropertiesExample;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring应用
 */
public class Application {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring.xml",
                "spring-messagesource.xml",
                "spring-configbean.xml",
                "spring-database.xml",
                "spring-redis.xml");

        context.getBean(ConfigPropertiesExample.class);
    }

}
