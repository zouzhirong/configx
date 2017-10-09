使用configx热修改数据源
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**在configx配置管理平台中增加配置**

在configx配置管理平台中，创建一个文件类型的配置database.properties,内容为：
::

    datasource.driverClassName=com.mysql.jdbc.Driver
    datasource.url=jdbc:mysql://192.168.1.199:3306/configx
    datasource.username=root
    datasource.password=test

.. image:: /images/database1.png
.. image:: /images/database2.png

将database.properties添加到spring.property.sources配置项中，如果没有spring.property.sources配置项，则创建一个。

.. image:: /images/database3.png

程序代码如下：

::

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

    /**
     * 数据源Bean Configuration
     * Created by zouzhirong on 2017/9/25.
     */
    @Configuration
    public class DataSourceConfiguration {

        @Bean
        @VersionRefreshScope(dependsOn = {"datasource.url"})
        public DataSourceProperties dataSourceProperties() {
            return new DataSourceProperties();
        }

        @Bean
        @VersionRefreshScope(proxyMode = ScopedProxyMode.TARGET_CLASS, dependsOn = {"datasource.url"})
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

    /**
     * 数据库样例
     * 支持热修改数据源地址
     */
    @Service
    public class DatabaseExample implements InitializingBean {

        // inject the actual template
        @Autowired
        private JdbcTemplate jdbcTemplate;
    }


将dataSourceProperties和dataSource两个bean的scope都设置为version-refresh,并且设置依赖属性为datasource.url，这样当有新版本的配置发布且datasource.url属性有更改，那么dataSourceProperties和dataSource两个bean会重新创建。

新的请求会使用新创建的dataSource，但是这时候旧的dataSource的bean并没有destory，当没有任何线程使用旧的dataSource时，configx-client会将其destory并从scope中移除，然后被gc掉，所有可能同时存在多个dataSource bean实例。
这个有点像nginx重启一样，nginx先启动新的进程用于服务新的请求，但是这时候旧的nginx进程并没有关闭，继续在服务旧的请求，直接没有任何旧的请求了，再关闭旧的nginx进程。
通过这种方式，可以实现线上热修改redis到新的地址，而并不会影响正在使用旧redis地址的请求。
