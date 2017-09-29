package com.configx.demo.property;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.TimeUnit;

/**
 * 使用配置管理系统来管理文件，自动将文件映射并注册为Spring Bean
 */
public class ConfigPropertiesExample implements InitializingBean {

    private ConfigProperties configProperties;

    public void setConfigProperties(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动一个线程，每5秒打印ConfigProperties中的属性信息
        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("ConfigProperties: " + JSON.toJSONString(configProperties));
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
