维度设计
----------

ConfigX配置包括App、Env和Profile三个维度。



App 应用
^^^^^^^^^^^^^^^^^^^^^^

App用来指定一个具体的应用。


Env 环境
^^^^^^^^^^^^^^^^^^^^^^

Env环境用来指定应用的环境，比如开发环境，测试环境，线上环境，不同环境之间的配置完全隔离。


Profile 剖面
^^^^^^^^^^^^^^^^^^^^^^

Profile是ConfigX中所定义的一系列配置的逻辑组名称，只有当这些Profile被激活的时候，才会将Profile中所对应的配置注册到应用中。

Profile可以让我们定义一系列的配置，然后客户端指定激活哪些profile。这样我们就可以定义多个profile，然后不同的客户端激活不同的profiles，从而达到不同环境使用不同配置信息的效果，如果多个profile下有相同的配置，则按profile的指定顺序来获取较前面的profile下的配置。

由于Env之间的配置是完全隔离的，所以增加Profile维度来弥补Env维度的不足。

配置管理平台会创建一个default profile，如果未定义profile，配置将会放在default profile下，default profile自动激活，不用客户端显示激活。

通过Profile维度，可以轻松实现以下功能：

+ 配置继承，一个应用部署的多套系统使用的配置几乎相同，只有少部分不同，这样这些系统就可以使用同一个环境，然后定义多个Profile，这些系统通过激活不同的profiles来达到配置继承（覆盖）的目的。

+ 配置分支，当项目同时多个分支开发时，为了保证配置不相互影响，可以给每个分支定义新的profile，每个分支的配置放在分支自己的profile下，然后激活分支自己的profile即可。

+ 灰度发布，为新版本的代码定义一个新的profile，然后灰度发布的系统激活这个新的profile下的配置。


更多Profile设计参考
^^^^^^^^^^^^^^^^^^^^^^

**Maven Profile**

mvn命令可以通过-p选项来激活profiles。

-P,--activate-profiles <arg>           Comma-delimited list of profiles

了解更多 `Maven Profiles
<http://maven.apache.org/guides/introduction/introduction-to-profiles.html>`_


**Spring  Profile**

Spring Profile是Spring 3引入的概念，包括默认Profile和明确激活的Profiles。

默认Profile是指在没有任何profile被激活的情况下也能自动激活的profile，通过spring.profiles.default指定默认的profile。

明确激活的Profile，通过spring.profiles.active指定，也可以在程序中使用ConfigurableEnvironment#setActiveProfiles来激活profiles。

了解更多 `Spring Environment abstraction
<https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-environment>`_


**Spring Boot**

Spring Boot中也支持profiles来获取配置。

相关的属性：

    - spring.profiles.active
    - spring.profiles.include
    - spring.profiles

了解更多 `Spring Boot Profiles
<https://docs.spring.io/spring-boot/docs/1.5.6.RELEASE/reference/htmlsingle/#boot-features-profiles>`_


**Spring Cloud Config**

Spring Cloud Config也支持profile来获取配置。

了解更多 `Spring Cloud Config
<http://cloud.spring.io/spring-cloud-static/Dalston.SR3/#_spring_cloud_config>`_
