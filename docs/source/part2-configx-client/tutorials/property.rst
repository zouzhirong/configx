使用Configx管理Spring属性配置
=================================================

configx与Spring无缝集成，支持Spring里面Environment和PropertySource的接口，对于已有的Spring应用程序的迁移成本非常低，在配置获取的接口上完全一致。

假设我有一个TestBean，它有一个timeout属性需要注入：
::

    public class TestBean {

        @Value("${timeout}")
        private long timeout;

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public long getTimeout() {
            return timeout;
        }
    }


**在configx配置管理平台中添加timeout配置项**

configx既支持直接将属性作为一个配置项，也支持将属性放在属性文件中并将属性文件作为配置项。

**直接将timeout属性作为配置项**

如果直接将属性作为一个配置项，只需要在configx管理平台中添加一个配置项，配置名称为属性名timeout，配置值为属性值比如2000.

**属性配置文件作为配置项**

将timeout属性放在属性配置管理中，并将属性配置文件作为一个配置项。

跟Spring属性配置文件类似，当使用属性配置文件来配置属性时，需要指定属性配置文件名称。

在Spring中通过context:property-placeholder来指定属性配置文件。
::

    <context:property-placeholder location="classpath:foo.properties" />


在configx管理平台中通过spring.property.sources配置项来指定属性配置文件的配置项名称
::
    spring.property.sources=application.properties,database.properties,redis.properties

如果没有spring.property.sources配置项，则默认使用application.properties属性文件来配置属性。

**在程序中使用timeout属性**

在configx管理平台添加完timeout配置项后，就可以在Spring应用中跟使用普通Spring属性一样来使用timeout属性了。

**基于XML的配置**
在使用属性之前，首先需要通过configx扩展标签：<configx:config/>来开启configx。
::

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:aop="http://www.springframework.org/schema/aop"
           xmlns:configx="http://www.configx.com/schema/configx"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd
           http://www.springframework.org/schema/aop
           http://www.springframework.org/schema/aop/spring-aop.xsd
           http://www.configx.com/schema/configx
           http://www.configx.com/schema/configx/configx.xsd">

        <!-- 开启配置管理服务 -->
        <configx:config/>

        <bean name="testBean" class="com.configx.demo.TestBean">
            <property name="timeout" value="${timeout}"/>
        </bean>

    </beans>

**基于Java的配置（推荐）**
在使用属性之前，首先需要通过@EnableConfigService注解来开启configx。
::

    @Configuration
    @EnableConfigService // 启动配置管理，并注册XmlConfigConverter
    public class Application {

    }

    @Component
    public class TestBean {

        @Value("${timeout}")
        private long timeout;

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public long getTimeout() {
            return timeout;
        }
    }


到这里，属性注入的工作已经完成。

Spring并没有提供属性刷新的功能，为了解决这个问题，configx通过自定义的scope来解决bean的属性热修改问题。

这个自定义的scope叫version-refresh，即基于配置版本刷新，类似于spring cloud config的refresh scope，但是区别在于configx会基于配置版本自动刷新，并且做了多版本并发控制。

如果将一个bean的scope设置为version-refresh，那么当configx有新版本的配置发布时，并且bean依赖的属性在新版本有修改时，那么这个bean会重新创建并在创建时重新注入新的属性值，一个bean可能同时存在多个版本，当有新版本的bean创建时，旧版本的bean在没有任何引用的情况下configx会将其销毁。

为了将TestBean中的timeout能够热修改，只需要将bean的scope设置为version-refresh，同时设置bean依赖的属性为timeout即可。

**基于XML的配置**
::

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:aop="http://www.springframework.org/schema/aop"
           xmlns:configx="http://www.configx.com/schema/configx"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd
           http://www.springframework.org/schema/aop
           http://www.springframework.org/schema/aop/spring-aop.xsd
           http://www.configx.com/schema/configx
           http://www.configx.com/schema/configx/configx.xsd">

        <!-- 开启配置管理服务 -->
        <configx:config/>

        <bean name="testBean" class="com.configx.demo.TestBean">
            <property name="timeout" value="${timeout}"/>
            <configx:version-refresh dependsOn="timeout"/>
            <aop:scoped-proxy proxy-target-class="true"/>
        </bean>

    </beans>

注意：基于XML的配置中，属性的热修改无法正常工作，这是因为在Bean定义解析阶段，spring就将${timeout}属性占位符解析成最终的值并添加到bean定义的propertyValues中，当bean创建时，直接使用的是timeout实际的值，而非$timeout}占位符。
所以尽管将testBean的scope设置为version-refresh，且设置依赖的属性为timeout，但是在timeout属性修改时，testBean会重新创建，但是使用的仍然是bean定义中的最初的timeout的值。

具体可查阅Spring源码：org.springframework.beans.factory.config.PlaceholderConfigurerSupport.doProcessProperties

所以要想bean属性支持热修改，请使用基于Java的配置。

**基于Java的配置（推荐）**
::

    @Configuration
    @EnableConfigService // 启动配置管理，并注册XmlConfigConverter
    public class Application {

    }

    @Component
    @VersionRefreshScope(dependsOn = {"timeout"})
    public class TestBean {

        @Value("${timeout}")
        private long timeout;

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public long getTimeout() {
            return timeout;
        }
    }

在TestBean类上添加@VersionRefreshScope(dependsOn = {"timeout"})，就将bean的scope设置成了version-refresh，并且设置了依赖的属性为timeout，这样当有新版本的配置发布时，如果timeout属性修改了，那么TestBean将会重新创建。

注意：TestBean的scope虽然为version-refresh，但是依然可以正常注入到其他单例的bean中，其原理是Spring为自定义scope的bean创建了一个代理，并将代理注入到其他单例bean中。

具体可参考Spring文档。