package com.configx.demo.message;

import com.configx.client.annotation.EnableMessageSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 使用配置管理系统来管理国际化消息
 */
@Service
public class MessageSourceExample implements ApplicationContextAware, InitializingBean {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动一个线程，每5秒打印国际化消息信息
        new Thread(() -> {
            while (true) {
                try {
                    String project = context.getMessage("project.name", null, Locale.getDefault());
                    String author = context.getMessage("author", null, null);
                    System.out.println("MessageSource project=" + project + ", author=" + author);
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
