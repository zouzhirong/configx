动机
------

随着微服务的流行，配置管理是实现微服务必不可少的一个组件。

现在市面上有很多配置管理系统：

* 淘宝的diamond
* 百度的disconf
* 奇虎360的QConf
* 微博的vintage

其中disconf对spring集成比较好，但是我觉得disconf有以下几点不足：

1. 与Spring集成并不是特别简单。
2. 没有提供配置文件到Spring Bean简单映射实现。
3. 没有提供Spring国际化消息文件的集成。
4. 无法实现配置继承（覆盖）。
5. 无法实现配置发布的原子性。
6. 没有提供配置修改历史记录。

Spring Cloud是一个实现微服务的框架，它提供了Spring Cloud Config来管理配置，Spring Cloud Config客户端和服务器上的概念映射与Spring Environment和PropertySource抽象相同，因此它们与Spring应用程序非常契合，但可以与任何以任何语言运行的应用程序一起使用。

但是个人觉得Spring Cloud Config有以下几点不足：

1. 没有提供配置文件到Spring Bean简单映射实现。
2. 没有提供Spring国际化消息文件的集成。
3. 无法实现配置发布的原子性。

为了解决这些问题，我决定自己实现一套配置管理系统，它必须做到以下几点：

1. 与Spring集成简单

    ConfigX-Client借鉴了Spring Boot和Spring Cloud Config的设计，全部使用标准的Spring扩展点与Spring进行集成，所以与Spirng应用的集成非常简单。

    在Spring应用中使用ConfigX只需要4个注解：

    + @EnableConfigService
        启用ConfigX支持
    + @ConfigBean
        定义配置文件转换成Spring Bean
    + @VersionRefreshScope
        指定Spring Bean是可刷新的bean，当配置修改时，bean会重新刷新
    + @EnableMessageSource
        启用ConfigX对Spring消息国际化的支持

2. 支持配置文件映射成Spring Bean

    ConfigX只需要在Spring Bean上加上注解@ConfigBean，ConfigBean的value直接配置文件名称，就可以轻松将配置文件映射成Spring的bean。

3. 支持管理Spring国际化消息文件

    ConfigX只需要通过注解@EnableMessageSource来启用ConfigX对Spring国际化的支持，并不用修改获取国际化消息的代码，使用Spring标准的API来获取国际化消息。

4. 支持配置继承（覆盖）

    ConfigX的配置文件定义了不同的profile，客户端通过激活多个profile来激活不同的配置，如果不同的profile下有相同的配置，则按客户端激活profile的顺序优先获取较前面的profile的配置。

5. 支持配置发布的原子性

    ConfigX通过对配置进行多版本并发控制，使得修改的多个配置可以原子性的发布，应用程序中不用担心访问到部分生效的配置。

