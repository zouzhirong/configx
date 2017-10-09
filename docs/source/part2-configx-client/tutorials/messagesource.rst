Configx支持Spring国际化消息
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

**开启Configx对Spring国际化消息的支持**

* 基于XML的配置

需要把configx相关的xml namespace加到配置文件头上。
::

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:configx="http://www.configx.com/schema/configx"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.configx.com/schema/configx
       http://www.configx.com/schema/configx/configx.xsd">

        <!-- 开启配置管理对message source的支持 -->
        <configx:message-source fallbackToSystemLocale="false" basenames="messages"/>

    </beans>

* 基于Java的配置（推荐）
相对于基于XML的配置，基于Java的配置是目前比较流行的方式，也是Spring Boot的默认配置方式。
::

    @Configuration
    @EnableMessageSource(fallbackToSystemLocale = false, basenames = {})    // 开启配置管理对消息国际化的支持
    public class Application {

    }

**指定basenames**

有3种方式可以指定basenames：

* 通过@EnableMessageSource注解的basenames方法指定，或configx:message标签的basenames属性指定。
* 在configx配置管理平台中通过spring.messages.basename配置项来指定。
* 在Spring的environment中指定spring.messages.basename属性。


首先，在配置管理系统中，创建一个配置spring.messages.basename，内容为messages。
然后，创建一个messages.xml文件，将项目本地中的messages.xml内容复制到配置管理系统的messages.xml文件中。

.. image:: /images/config_messagesource.png
.. image:: /images/messages.png
