configx-web使用
^^^^^^^^^^^^^^^^^^^^^^

**打开configx配置管理平台**

安装完configx-web并启动后，访问http://host:port/，host是运行configx-web的主机ip，port是configx-web的端口，默认是3964，进入configx配置管理平台。


**登录**

configx-web默认的管理员账号：
::

    账号 admin
    密码 admin123

.. image:: /images/login.png


**创建应用**

进入配置管理平台后，出现应用列表界面。

.. image:: /images/app_empty.png

现在还没有应用，点击创建应用。

.. image:: /images/app_add.png


- 名称：应用的名称，唯一标识一个应用，可以包括中文。
- 描述：应用的描述信息。
- 管理员邮箱：设置对这个应用有管理员权限的用户的邮箱列表，可以使用(,)逗号、(;)分号、( )空格、(\t)Tab、(\n)换行来分隔，默认会把当前创建应用的用户邮箱添加到管理员列<表中。
- 开发者邮箱：设置对这个应用有开发者权限的用户的邮箱列表，可以使用(,)逗号、(;)分号、( )空格、(\t)Tab、(\n)换行来分隔，默认会把当前创建应用的用户邮箱添- 加到开发者列表中。

应用创建后，在应用列表中可以看到刚才新创建的应用。

.. image:: /images/app.png


**创建环境**

在应用列表页面，点击应用列表右侧的“环境”按钮，进入到应用环境管理页面。

.. image:: /images/env_empty.png

现在应用下没有任何环境，点击创建环境。

.. image:: /images/env_add.png


- 名称：环境的名称，唯一标识应用的一个环境，只能包含英文。
- 别名：环境的别名，为这个环境设置别名，客户端在指定环境时，既可以使用环境名称，也可以使用环境别名。
- 描述：环境的描述信息。
- 顺序：设置环境的显示顺序，与功能无关，在管理系统中，在涉及到环境选择列表时，都会根据这个顺序来排序显示，值越小越靠前，值越大越靠后。

创建完环境后，就可以添加配置了。点击顶部导航条的“配置管理”，进入到配置管理页面。

.. image:: /images/config_empty.png


**创建文件配置**

.. image:: /images/config_file_add.png


**创建文本配置**

.. image:: /images/config_text_add.png


**创建数值配置**

.. image:: /images/config_number_add.png


**创建日期配置**

.. image:: /images/config_date_add.png


**创建时间配置**

.. image:: /images/config_time_add.png

**创建日期时间配置**

.. image:: /images/config_datetime_add.png


**查看配置列表**

.. image:: /images/config.png


