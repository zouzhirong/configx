package com.configx.demo.redis;

import com.configx.client.annotation.VersionRefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created by zouzhirong on 2017/9/25.
 */
@Configuration
public class RedisConfiguration {

    @Bean
    @VersionRefreshScope(dependsOn = {"redis.host", "redis.port"})
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @Bean
    @VersionRefreshScope(dependsOn = {"redis.host", "redis.port"})
    public JedisConnectionFactory jedisConnFactory(RedisProperties redisProperties) {
        JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
        jedisConnFactory.setHostName(redisProperties.getHost());
        jedisConnFactory.setPort(redisProperties.getPort());
        jedisConnFactory.setUsePool(true);
        return jedisConnFactory;
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

}
