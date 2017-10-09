使用configx热修改线程池
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**在configx配置管理平台中增加配置**

在configx配置管理平台中，创建一个文件类型的配置threadpool.properties,内容为：
::

    threadpool.corePoolSize=10

.. image:: /images/threadpool1.png
.. image:: /images/threadpool2.png

将threadpool.properties添加到spring.property.sources配置项中，如果没有spring.property.sources配置项，则创建一个。

.. image:: /images/threadpool3.png

程序代码如下：

::

    /**
     * 线程池属性
     * Created by zouzhirong on 2017/9/26.
     */
    public class ThreadPoolProperties {

        @Value("${threadpool.corePoolSize}")
        private int corePoolSize;

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getCorePoolSize() {
            return corePoolSize;
        }

    }

    /**
     * 线程池Bean Configuration
     * Created by zouzhirong on 2017/9/25.
     */
    @Configuration
    public class ThreadPoolConfiguration {

        @Bean
        @VersionRefreshScope(dependsOn = {"threadpool.corePoolSize"})
        public ThreadPoolProperties threadPoolProperties() {
            return new ThreadPoolProperties();
        }

        @Bean
        @VersionRefreshScope(dependsOn = {"threadpool.corePoolSize"})
        public ThreadPoolExecutor threadPoolExecutor(ThreadPoolProperties threadPoolProperties) {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)
                    Executors.newFixedThreadPool(threadPoolProperties.getCorePoolSize());
            return threadPoolExecutor;
        }

    }

    /**
     * 线程池样例
     * 支持热修改线程池参数
     */
    @Service
    public class ThreadPoolExample implements ConfigItemListener, InitializingBean {

        @Autowired
        private ThreadPoolExecutor threadPoolExecutor;

        @Override
        public void onApplicationEvent(ConfigItemChangeEvent event) {
            if (event.getItemList() != null) {
                for (ConfigItem configItem : event.getItemList()) {
                    if ("threadpool.corePoolSize".equals(configItem.getName())) {

                        // threadpool.corePoolSize属性修改，需要更新ThreadPoolExecutor的corePoolSize
                        int corePoolSize = Integer.valueOf(configItem.getValue());
                        threadPoolExecutor.setCorePoolSize(corePoolSize);

                    }
                }
            }
        }
    }
