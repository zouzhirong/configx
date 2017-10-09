package com.configx.demo.redis;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis样例
 * 支持热修改Redis地址
 */
@Service
public class RedisExample implements InitializingBean {

    // inject the actual template
    @Autowired
    private RedisTemplate<String, String> template;

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            while (true) {
                try {
                    template.opsForValue().set("test", "configx");
                    String value = template.opsForValue().get("test");

                    JedisConnectionFactory connectionFactory = ((JedisConnectionFactory) template.getConnectionFactory());

                    System.out.println("Redis: value=" + value + ", host=" + connectionFactory.getHostName() + ", port=" + connectionFactory.getPort());

                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
