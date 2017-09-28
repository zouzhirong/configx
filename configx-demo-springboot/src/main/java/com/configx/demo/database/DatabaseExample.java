package com.configx.demo.database;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 数据库样例
 * 支持热修改数据源地址
 */
@Service
public class DatabaseExample implements InitializingBean {

    // inject the actual template
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            while (true) {
                try {
                    int value = jdbcTemplate.queryForObject("select 1", int.class);

                    System.out.println("Database: value=" + value);

                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
