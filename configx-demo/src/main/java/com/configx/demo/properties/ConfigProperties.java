package com.configx.demo.properties;

import com.configx.client.annotation.VersionRefreshScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by zouzhirong on 2017/3/15.
 */
// @VersionRefreshScope注解用来标记这是一个可刷新的Bean，当配置更改时，这个Bean会重新创建，使用最新的配置。
// dependsOn设置哪些配置更改时，需要重新创建这个Bean。
@VersionRefreshScope(dependsOn = {
        "database.url",
        "database.max-active",
        "date",
        "time",
        "datetime"
})
@Service
public class ConfigProperties {

    @Value("${database.url}")
    private String databaseUrl;

    @Value("${database.max-active}")
    private int databaseMaxActive;

    @Value("${date}")
    private String date;

    @Value("${time}")
    private String time;

    @Value("${datetime}")
    private String datetime;

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public int getDatabaseMaxActive() {
        return databaseMaxActive;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDatetime() {
        return datetime;
    }
}
