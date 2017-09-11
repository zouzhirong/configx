安装
------

configx-web安装
^^^^^^^^^^^^^^^^^^^^^^

**下载configx-web**
::

    https://github.com/zouzhirong/configx/releases/latest

**解压**
::

    tar -zxvf configx-web-1.0.1.tar.gz

**安装Mysql**
::

    https://dev.mysql.com/downloads/mysql/

**执行sql文件**
::

    tables_mysql.sql

**修改配置文件configx.properties**
::

    # 端口
    http.port=3964

    # 数据库信息
    datasource.host=localhost
    datasource.port=3306
    datasource.database=configx
    datasource.username=root
    datasource.password=

**启动configx-web**
::

    java -jar configx-web-1.0.1.jar



configx-client安装
^^^^^^^^^^^^^^^^^^^^^^

**添加configx-client的Maven依赖**
::

    <dependency>
      <groupId>com.configx</groupId>
      <artifactId>configx-client</artifactId>
      <version>1.0.1</version>
    </dependency>
