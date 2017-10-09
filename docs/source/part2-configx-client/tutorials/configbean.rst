使用Configx来配置自定义文件
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

在项目开发中，不仅有简单的key-value属性，还会有自定义的复杂的配置文件，比如有一个学生的配置文件：
students.xml::

    <students>
        <student id="1" name="张三"></student>
        <student id="2" name="李四"></student>
        <student id="3" name="王五"></student>
        <student id="4" name="赵六"></student>
    </students>


然后我们需要将这个配置文件映射到Spring的bean。

首先，我们需要在configx配置管理平台中创建一个students.xml的配置文件。

.. image:: /images/students.png

然后，定义一个解析students.xml的解析器类。
::

    /**
     * 通过实现ConfigBeanConverter接口自定义配置转换器，将XML文件转换成Bean
     * 我这里使用的是[simple-xml](http://simple.sourceforge.net/)框架将xml映射成Bean。
     */
    public class XmlConfigConverter implements ConfigBeanConverter {

        private static final Logger LOGGER = LoggerFactory.getLogger(XmlConfigConverter.class);

        @Override
        public boolean matches(String propertyName, String propertyValue, TypeDescriptor targetType) {
            return propertyName != null && propertyName.endsWith(".xml");
        }

        @Override
        public Object convert(String propertyName, String propertyValue, TypeDescriptor targetType) {
            Class<?> targetClass = targetType.getType();
            Serializer serializer = new Persister(new AnnotationStrategy());
            try {
                return serializer.read(targetClass, propertyValue, false);
            } catch (Exception e) {
                LOGGER.error("Convert xml to " + targetClass + " error", e);
            }
            return null;
        }

    }

最后，在Students类上使用注解@ConfigBean和VersionRefreshScope。
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

@ConfigBean首先是一个@Component注解，另外通过value指定了bean由哪个配置名转换而来，通过converter指定具体的转换器。

@VersionRefreshScope用于当student.xml修改时，刷新Students bean，这里并不需要设置dependsOn={"students.xml"}，因为对于@ConfigBean的bean，会自动把@ConfigBean的value的值当成dependsOn。


当项目中有大量的@ConfigBean时，并且使用相同的转换器来解析配置，可以在@EnableConfigService注解中通过converters来统一注册多个转换器，并不需要在每个ConfigBean上注册转换器。
::

    @EnableConfigService(converters = {XmlConfigConverter.class}) // 启动配置管理，并注册XmlConfigConverter

