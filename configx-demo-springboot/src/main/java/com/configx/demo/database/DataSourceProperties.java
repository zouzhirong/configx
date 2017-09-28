package com.configx.demo.database;

import org.springframework.beans.factory.annotation.Value;

/**
 * 数据源属性
 * <p>
 * Created by zouzhirong on 2017/9/26.
 */
public class DataSourceProperties {

    @Value("${datasource.driverClassName}")
    private String driverClassName;

    @Value("${datasource.url}")
    private String url;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
