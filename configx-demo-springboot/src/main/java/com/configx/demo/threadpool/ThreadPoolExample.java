package com.configx.demo.threadpool;

import com.configx.client.item.ConfigItem;
import com.configx.client.listener.ConfigItemChangeEvent;
import com.configx.client.listener.ConfigItemListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池样例
 * 支持热修改线程池参数
 */
@Service
public class ThreadPoolExample implements ConfigItemListener, InitializingBean {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void onApplicationEvent(ConfigItemChangeEvent event) {
        if (event.getItemList() != null) {
            for (ConfigItem configItem : event.getItemList()) {
                if ("threadpool.corePoolSize".equals(configItem.getName())) {

                    // threadpool.corePoolSize属性修改，需要更新ThreadPoolExecutor的corePoolSize
                    int corePoolSize = Integer.valueOf(configItem.getValue());
                    threadPoolExecutor.setCorePoolSize(corePoolSize);

                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            while (true) {
                try {
                    int corePoolSize = threadPoolExecutor.getCorePoolSize();

                    System.out.println("ThreadPool: corePoolSize=" + corePoolSize);

                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
