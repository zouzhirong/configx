package com.configx.demo.redis;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisExample implements InitializingBean {

    private RedisTemplate<String, String> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            while (true) {
                try {
                    redisTemplate.opsForValue().set("test", "configx");
                    String value = redisTemplate.opsForValue().get("test");

                    JedisConnectionFactory connectionFactory = ((JedisConnectionFactory) redisTemplate.getConnectionFactory());

                    System.out.println("Redis: value=" + value + ", host=" + connectionFactory.getHostName() + ", port=" + connectionFactory.getPort());

                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
