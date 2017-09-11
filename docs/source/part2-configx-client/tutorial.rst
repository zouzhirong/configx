configx-client教程
==================

configx-client基于Spring，可以无缝接入spring应用，全部基于注解，不用修改任何代码即可将spring的配置迁移到configx配置管理平台。
本教程中涉及到的spring代码全部采用注解式，不使用Spring Xml配置文件。

一个Spring例子
~~~~~~~~~~~~~~

我们来看一个在Spring中使用Redis的例子。

**新建redis.properties文件**
::

    redis.host=localhost
    redis.port=6379

首先我们需要在/src/main/resources下新建redis.properties文件。

**添加redis.properties文件到Spring环境**
::

    @PropertySource("classpath:redis.properties")

**新建redis.properties对应的配置属性类RedisProperties.java**
::

    @Component
    public class RedisProperties {
        @Value("${redis.host}")
        private String host;

        @Value("${redis.port}")
        private int port;

        // 省略getter/setter
    }

**新建操作redis的模板类RedisTemplate.java**
::

    @Component
    public class RedisTemplate{

         // Redis配置属性
         @Autowired
         private RedisProperties properties;

         // Jedis实例
         private Jedis jedis;

         @PostConstruct
         public void init() {
            this.jedis = new Jedis(properties.getHost(), properties.getPort());
         }

         @PreDestroy
         public void destroy() {
            // 关闭连接
         }
    }

然后就可以使用RedisTemplate的Jedis实例来访问Redis。
::

    @Service
    public class TestService {

        @Autowired
        private RedisTemplate template;
    }


使用Configx来管理Spring properties文件
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

将前面例子中的redis.properties迁移到configx配置管理平台只需要两步：

1. 在configx-web中新建配置。
2. 在客户端代码中，添加@EnableConfigService注解来启动configx配置管理。

只需要这两步，就将redis.properties无缝的迁移到了configx配置管理平台中。

为什么将redis.properties迁移到configx后，不用修改代码，依然可以使用@Value来注入？

这是因为configx与Spring无缝集成，支持Spring里面Environment和PropertySource的接口，对于已有的Spring应用程序的迁移成本非常低，在配置获取的接口上完全一致。

现在我们来看下具体怎么将例子中的Spring程序集成configx。


**在configx-web中新建配置**

在configx配置管理平台中，创建一个文件类型的配置redis.properties,内容为：
::

    redis.host=localhost
    redis.port=6379

在创建一个文本类型的配置项：spring.property.sources=redis，用于解析redis.properties中的属性到spring环境中。

spring.property.sources配置项的作用等价于
::

    @PropertySource("classpath:redis.properties")



.. image:: /images/t11.png
.. image:: /images/t12.png

configx管理平台不仅支持properties属性文件，还支持打散的属性配置。
也就是说，我可以在configx配置管理平台直接创建两个配置:
redis.host::

    redis.host=localhost

redis.port::

    redis.port=6379

由于这两个属性分别作为configx管理平台中的配置了，就不需要创建spring.property.sources配置了。

.. image:: /images/t13.png


使用Configx来实时修改配置
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Spring的@Value属性注入是在创建bean时将属性源中的属性设置到bean的字段中。
对于单例Bean，在spring中只有一个实例，修改configx配置管理平台中的属性后，会更新Spring的Environment和PropertySource中的属性值，但是并不会更新已经创建的单例bean中的字段值。

为了修改单例bean中的字段中，我们的解决方案是使用自定义Scope来解决，它叫VersionRefreshScope，类似于Spring Cloud的RefreshScope。
也就是说，如果bean注入了可能需要动态修改的属性，那么将bean的scope设置为VersionRefreshScope，那么当配置修改时，这个bean就会重新刷新，并且使用最新的配置注入到bean中。

我们来将教程1中的redis配置支持动态修改，只需要在RedisProperties类上加上@VersionRefreshScope注解，并通过dependsOn刷新依赖的属性名称列表。
::

    @Component
    @VersionRefreshScope(dependsOn={"redis.host", "redis.port"})
    public class RedisProperties {
        @Value("${redis.host}")
        private String host;

        @Value("${redis.port}")
        private int port;

        // 省略getter/setter
    }

这样当redis.host和redis.port任何一个属性修改后，RedisProperties bean都会重新刷新，之后访问RedisProperties的属性时，得到就是修改后的值。


如果是一般的业务配置，可能我们只是需要获取它的值，当配置修改时，并不需要做一些其他的事情，只需要静静等待下次被访问即可；
但是对于redis配置，我们需要建立redis连接，所以虽然现在redis属性是已经修改了，RedisProperties也刷新了，但是RedisTemplate中使用的jedis实例还是之前旧的配置值。

由于RedisProperties已经注入到单例的RedisTemplate中，尽管RedisProperties刷新了，但是RedisTemplate中的RedisProperties属性还是旧的实例，为了让RedisTemplate也能在配置修改时刷新，我们只需要也将RedisTemplate的scope设置为VersionRefreshScope。
::

    @Component
    @VersionRefreshScope(dependsOn={"redis.host", "redis.port"})
    public class RedisTemplate{

         // Redis配置属性
         @Autowired
         private RedisProperties properties;

         // Jedis实例
         private Jedis jedis;

         @PostConstruct
         public void init() {
            this.jedis = new Jedis(properties.getHost(), properties.getPort());
         }

         @PreDestroy
         public void destroy() {
            // 关闭连接
         }
    }

这样当redis属性修改时，RedisTemplate也会刷新，新的请求会使用新创建的RedisTemplate，但是这时候旧的RedisTemplate的bean并没有destory，当没有任何线程使用旧的RedisTemplate时，configx-client会将其destory并从scope中移除，然后被gc掉，所有可能同时存在多个RedisTemplate bean实例。
这个有点像nginx重启一样，nginx先启动新的进程用于服务新的请求，但是这时候旧的nginx进程并没有关闭，继续在服务旧的请求，直接没有任何旧的请求了，再关闭旧的nginx进程。
通过这种方式，可以实现线上热修改redis到新的地址，而并不会影响正在使用旧redis地址的请求。



使用Configx来配置自定义文件
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

在项目开发中，不仅会用简单的key-value属性，还会用到自定义的复杂的配置文件，比如有一个学生的配置文件：
students.xml::

    <students>
        <student id="1" name="张三"></student>
        <student id="2" name="李四"></student>
        <student id="3" name="王五"></student>
        <student id="4" name="赵333六"></student>
    </students>

不管是简单的配置属性，还是自定义的配置文件，我们都把它当成key-value的属性对，也就是说对于configx来说，redis.host是一个配置项，内容是localhost，students.xml也是一个配置项，内容是：
::

    <students>
        <student id="1" name="张三"></student>
        <student id="2" name="李四"></student>
        <student id="3" name="王五"></student>
        <student id="4" name="赵333六"></student>
    </students>

redis.host和students.xml都是key，都会被添加到Spring的Environment和PropertySource中，students.xml可以和redis.host一样使用@Value注入到bean中。
例如::

    @Component
    public class Example {
        @Value("${students.xml}")
        private String students;
    }

这样只是将students.xml的内容作为字符串注入到了Example中。


如果我们需要将这个配置文件映射到Spring的Students bean呢？

假如我们之前已经定义了Students相关的类。
::

    public class Student {
        @Attribute
        private String id;

        @Attribute
        private String name;
    }

    @Component
    public class Students {
        @ElementList(inline = true, entry = "student")
        private List<Student> students;
    }


为了使得Student bean的内容是由students.xml转换而来的，我们只需要将Students类上的@Component注解换成@ConfigBean和VersionRefreshScope。
::

    public class Student {
        @Attribute
        private String id;

        @Attribute
        private String name;
    }

    @ConfigBean(value="students.xml", converter=XmlConfigConverter.class)
    @VersionRefreshScope
    public class Students {
        @ElementList(inline = true, entry = "student")
        private List<Student> students;
    }

    /**
     * 通过继承ConfigConverterSupport类自定义配置转换器，将XML文件转换成Bean
     * 我这里使用的是[simple-xml](http://simple.sourceforge.net/)框架将xml映射成Bean。
     */
    public class XmlConfigConverter extends ConfigConverterSupport {

        private static final Logger LOGGER = LoggerFactory.getLogger(XmlConfigConverter.class);

        @Override
        protected boolean matches(ConfigBean annotation, TypeDescriptor sourceType, TypeDescriptor targetType) {
            return annotation.value().endsWith(".xml");
        }

        @Override
        protected Object convert(String source, Class<?> targetClass) {
            Serializer serializer = new Persister(new AnnotationStrategy());
            try {
                return serializer.read(targetClass, source, false);
            } catch (Exception e) {
                LOGGER.error("Convert xml to " + targetClass + " error", e);
            }
            return null;
        }

    }

    @ConfigBean首先是一个@Component注解，另外通过value指定了bean由哪个配置名转换而来，通过converter指定具体的转换器。
    @VersionRefreshScope用于当student.xml修改时，刷新Students bean，这里并不需要设置dependsOn={"students.xml"}，因为对于@ConfigBean的bean，会自动把@ConfigBean的value的值当成dependsOn。


 当项目中有大量的@ConfigBean时，如果每个都在@ConfigBean中指定converter，实在有点难看，这里推荐的做法是在@EnableConfigService注解中通过converters来注册多个转换器。
 ::

    @EnableConfigService(converters = {XmlConfigConverter.class}) // 启动配置管理，并注册XmlConfigConverter



使用Configx来配置Spring国际化消息
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


只需要一个注解@EnableMessageSource，就可以无缝将Spring国际化消息迁移到配置管理系统中管理。

首先，在配置管理系统中，创建一个配置spring.messages.basename，内容为messages。
然后，创建一个messages.xml文件，将项目本地中的messages.xml内容复制到配置管理系统的messages.xml文件中。

.. image:: /images/config_messagesource.png
.. image:: /images/messages.png

最后，再任意会被扫描到的类上加上@EnableMessageSource注解。