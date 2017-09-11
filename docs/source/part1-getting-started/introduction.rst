项目介绍
----------

ConfigX项目提供了一个解决分布式系统的配置管理方案，为分布式系统中的外部配置提供服务器和客户端支持。
使用ConfigX Server，您可以在所有环境中管理应用程序的外部属性。
客户端和服务器上的概念映射与Spring Environment和PropertySource抽象相同，因此它们与Spring应用程序非常契合，但可以与任何以任何语言运行的应用程序一起使用。


ConfigX包含了Client和Server两个部分：
configx-web 配置服务端，管理配置信息
configx-client 配置客户端，客户端调用服务端暴露接口获取配置信息，并集成到Spring Environment和PropertySource中。

ConfigX Client根据Spring框架的Environment和PropertySource从ConfigX Sever获取配置，因此需要先了解Spring Environment、PropertySource 、Profile等技术。

