package com.configx.demo.database;

import com.configx.client.annotation.VersionRefreshScope;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 数据源Bean Configuration
 * Created by zouzhirong on 2017/9/25.
 */
@Configuration
public class DataSourceConfiguration {

    @Bean
    @VersionRefreshScope(dependsOn = {"datasource.url", "datasource.username", "datasource.password"})
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @VersionRefreshScope(proxyMode = ScopedProxyMode.TARGET_CLASS, dependsOn = {"datasource.url", "datasource.username", "datasource.password"})
    public BasicDataSource dataSource(DataSourceProperties dataSourceProperties) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
        dataSource.setUrl(dataSourceProperties.getUrl());
        dataSource.setUsername(dataSourceProperties.getUsername());
        dataSource.setPassword(dataSourceProperties.getPassword());
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

}
