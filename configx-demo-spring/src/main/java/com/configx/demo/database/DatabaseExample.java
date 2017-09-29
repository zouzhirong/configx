package com.configx.demo.database;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 数据库样例
 * 支持热修改数据源地址
 */
public class DatabaseExample implements InitializingBean {

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            while (true) {
                try {
                    int value = 0;
                    try {
                        value = jdbcTemplate.queryForObject("select 1", int.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    BasicDataSource dataSource = ((BasicDataSource) jdbcTemplate.getDataSource());

                    System.out.println("Database: value=" + value + ", url=" + dataSource.getUrl());

                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
