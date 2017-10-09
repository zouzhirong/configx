使用configx热修改redis
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**在configx配置管理平台中增加配置**

在configx配置管理平台中，创建一个文件类型的配置redis.properties,内容为：
::

    redis.host=localhost
    redis.port=6379

.. image:: /images/redis1.png
.. image:: /images/redis2.png

将redis.properties添加到spring.property.sources配置项中，如果没有spring.property.sources配置项，则创建一个。

.. image:: /images/redis3.png

程序代码如下：
::

    public class RedisProperties {

        @Value("${redis.host}")
        private String host;

        @Value("${redis.port}")
        private int port;

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
    }

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

    @Service
    public class RedisExample implements InitializingBean {

        // inject the actual template
        @Autowired
        private RedisTemplate<String, String> template;
    }


将redisProperties和redisTemplate两个bean的scope都设置为version-refresh,并且设置依赖属性为redis.host和redis.port，这样当有新版本的配置发布且redis.host和redis.port任一属性有更改，那么redisProperties和redisTemplate两个bean会重新创建。

新的请求会使用新创建的RedisTemplate，但是这时候旧的RedisTemplate的bean并没有destory，当没有任何线程使用旧的RedisTemplate时，configx-client会将其destory并从scope中移除，然后被gc掉，所有可能同时存在多个RedisTemplate bean实例。
这个有点像nginx重启一样，nginx先启动新的进程用于服务新的请求，但是这时候旧的nginx进程并没有关闭，继续在服务旧的请求，直接没有任何旧的请求了，再关闭旧的nginx进程。
通过这种方式，可以实现线上热修改redis到新的地址，而并不会影响正在使用旧redis地址的请求。