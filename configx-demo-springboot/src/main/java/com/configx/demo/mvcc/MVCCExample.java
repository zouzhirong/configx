package com.configx.demo.mvcc;

import com.alibaba.fastjson.JSON;
import com.configx.client.annotation.EnableConfigService;
import com.configx.client.version.VersionContextHolder;
import com.configx.demo.bean.Students;
import com.configx.demo.bean.XmlConfigConverter;
import com.configx.demo.property.ConfigProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 *
 * 多版本控制为了保证同一个“事务”中，不管配置是否被修改，这个事务中看到的配置是一致的。
 * 比如，在一个Http请求“事务”中，会获取两个Students Bean两次，假如在第一次获取Students Bean后，students.xml发生了改变，
 * 那么第二次获取Students Bean时，获取到的是跟第一次获取到的一样的，都是更改前的配置，新的配置需要在下次事务中才会生效。
 *
 * 开启多版本控制，需要在configx.properties中添加属性：
 * configx.client.mvcc.enabled=true
 *
 * 开启了mvcc之后，需要在程序中手动清除线程的版本号信息，否则线程将一直使用第一次的版本号，不会随着配置的更新而自动更新。
 * 清除线程中的版本号，调用以下方法：
 * ConfigVersionManager.clearCurrentVersion();
 *
 * 通常在一个“事务”结束以后，需要清理线程中的版本号。
 * 常见的“事务”比如：
 * 1、一个Http请求，可以在Filter中清除，比如：
 * @Override
 * public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
 *      try {
 *          chain.doFilter(req, resp);
 *      } catch (Exception e) {
 *          ...
 *      } finally {
 *          ConfigVersionManager.clearCurrentVersion();
 *      }
 * }
 *
 * 2、在线程池中执行的任务，可以在ThreadPoolExecutor的afterExecute方法中清除，比如：
 * @Override
 * public void afterExecute(Thread t, Runnable r) {
 *      ConfigVersionManager.clearCurrentVersion();
 *      super.afterExecute(t, r);
 * }
 *
 * 3、自定义“事务”，可以使用try...finally来清除，比如：
 *    try {
 *      ...
 *    } finally {
 *          ConfigVersionManager.clearCurrentVersion();
 *    }
 *
 *    本实例演示的是3所描述的“事务”。
 *
 */
@EnableConfigService(converters = {XmlConfigConverter.class}) // 启动配置管理，并注册XmlConfigConverter
@Service
public class MVCCExample implements InitializingBean {

    @Autowired
    private Students students;

    @Autowired
    private ConfigProperties configProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动一个线程，每5秒打印students信息
        new Thread(() -> {
            while (true) {

                try {
                    doTransaction(); // 一次事务结束后，清除线程中的版本号，更改的配置必须等待下次事务才会生效。
                } finally {
                    VersionContextHolder.clearCurrentVersion();
                }

                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                }

            }
        }).start();
    }

    /**
     * 一个“事务”，每隔5秒打印注入的配置信息，总共打印5次
     */
    private void doTransaction() {
        System.out.println("=======================================================================================");
        for (int i = 0; i < 5; i++) {
            if (i > 0) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("students: \n" + JSON.toJSONString(students.getStudents(), true));
            System.out.println("ConfigProperties: \n" + JSON.toJSONString(configProperties, true));
        }
        System.out.println("=======================================================================================");
    }
}