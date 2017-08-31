package com.configx.demo.service;

import com.configx.client.messagesource.ConfigMessageManager;
import com.configx.demo.conf.LangMetas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by zouzhirong on 2017/3/15.
 */
@Service
public class DemoService {

    @Autowired
    private LangMetas langMetas;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ConfigMessageManager configMessageManager;

    @Scheduled(initialDelay = 1000, fixedDelay = 1000)    //每秒执行一次
    public void test() {
        System.out.println("langMetas=" + langMetas);
//        System.out.println("configMessageManager=" + configMessageManager);
//        System.out.println(context.getMessage("zouzhirong.name", null, Locale.getDefault()));

    }
}
