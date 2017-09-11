configx-client使用
^^^^^^^^^^^^^^^^^^^^^^

**添加configx-client Maven依赖**
::

    <dependency>
      <groupId>com.configx</groupId>
      <artifactId>configx-client</artifactId>
      <version>1.0.1</version>
    </dependency>


**在src/main/resources下增加configx.properties配置文件**
::

    configx.client.config.app=应用ID，必填
    configx.client.config.env=应用环境名称，必填
    configx.client.config.profiles=Profile列表，多个之间用逗号分隔，如果多个Profile存在相同的配置，则越靠前的Profile优先级越高，选填，如果为空，则只获取默认Profile下的配置
    configx.client.config.uri=配置管理系统提供的API URL：http://配置管理系统host/v1/config/


**使用@EnableConfigService注解开启配置管理服务**