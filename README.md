# 项目介绍

ConfigX项目提供了一个解决分布式系统的配置管理方案，为分布式系统中的外部配置提供服务器和客户端支持。

使用ConfigX Server，您可以在所有环境中管理应用程序的外部属性。

客户端和服务器上的概念映射与Spring Environment和PropertySource抽象相同，因此它们与Spring应用程序非常契合，但可以与任何以任何语言运行的应用程序一起使用。


ConfigX包含了Client和Server两个部分：
configx-web 配置服务端，管理配置信息
configx-client 配置客户端，客户端调用服务端暴露接口获取配置信息，并集成到Spring Environment和PropertySource中。

ConfigX Client根据Spring框架的Environment和PropertySource从ConfigX Sever获取配置，因此需要先了解Spring Environment、PropertySource 、Profile等技术。


# 特性

* 配置管理
    - 属性配置管理，配置管理平台提供友好的配置编辑器，对于日期时间可以非常方便的进行设置。
    - 文件配置管理，配置管理平台提供强大的文件编辑器，可以非常方便的对文件进行操作。
    - 配置修改并发布后，应用可以实时获取到修改的配置并实时生效。

* 维度设计
    - App

        具体的应用

    - Env

        应用的环境，比如开发环境，测试环境，线上环境，不同环境之间的配置完全隔离。

    - Profile

        Maven和Spring中都提供了Profile，然后可以激活不同的profiles，Spring Boot和Spring Cloud Config中的配置管理也充分使用了Spring的Profile技术来进行配置管理。

        Profile可以让我们定义一系列的配置，然后客户端指定激活哪些profile。这样我们就可以定义多个profile，然后不同的客户端激活不同的profiles，从而达到不同环境使用不同配置信息的效果，如果多个profile下有相同的配置，则按profile的指定顺序来获取较前面的profile下的配置。

        由于Env之间的配置是完全隔离的，所以增加Profile维度来弥补Env维度的不足。

        配置管理平台会创建一个default profile，如果未定义profile，配置将会放在default profile下，default profile自动激活，不用客户端显示激活。

        通过Profile维度，可以轻松实现以下功能：

        + 配置继承，一个应用部署的多套系统使用的配置几乎相同，只有少部分不同，这样这些系统就可以使用同一个环境，然后定义多个Profile，这些系统通过激活不同的profiles来达到配置继承（覆盖）的目的。

        + 配置分支，当项目同时多个分支开发时，为了保证配置不相互影响，可以给每个分支定义新的profile，每个分支的配置放在分支自己的profile下，然后激活分支自己的profile即可。

        + 灰度发布，为新版本的代码定义一个新的profile，然后灰度发布的系统激活这个新的profile下的配置。

* 配置版本控制

    跟代码版本控制类似，我们对配置也就行了版本控制，可以查看配置的历史修改记录。

* 发布模式

    我们提供两种发布模式

    - 自动发布

        配置修改完立刻自动发布，配置实时生效

    - 审核发布

        配置修改完后，并不立刻发布，而是需要建立一个发布单，发布单审核通过后，才能发布，审核发布模式可以实现更安全可控的发布，减少配置错误导致的严重后果的风险。

* 配置发布的原子性

    在客户端中实现了多版本的配置管理控制，当发布多个配置时，每个线程看到的这几个配置要么都是修改前的值，要么都是修改后的值，不会出现某些配置是修改后值而另外一些配置是修改前的值，保证配置的原子性。

* 与Spring集成简单，对应用代码无侵入。

    - 支持Spring属性文件

    原来Spring应用中的属性文件迁移到配置管理平台后，仍然可以使用@Value注解来注入属性文件中的属性，应用程序并不需要修改任何代码，原因是ConfigX使用了Spring框架的Environment和PropertySource扩展。

    - 支持XML/JSON等文件映射到Spring Bean

    当需要将一个文件映射成Spring Bean时，只需要将这个文件配置在配置管理平台中，然后在Spring Bean上增加注解@ConfigBean(value="配置文件名", converter=文件转换成Bean的转换器类)。

    - 支持Spring国际化消息文件
    原来Spring应用中的i18n消息文件迁移到配置管理平台后，仍然可以使用MessageSource.getMessage方法来获取国际化消息，应用程序并不需要修改任何代码，原因是CongigX使用了Spring的MessageSource扩展。


config-web demo： [http://47.94.241.50](http://47.94.241.50)

参考文档： [http://configx.readthedocs.io/](http://configx.readthedocs.io/)

