<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>没有环境</title>

    <script src="/libs/jquery/jquery-2.1.4.min.js"></script>

    <script src="/libs/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/libs/bootstrap/css/bootstrap.min.css"/>

    <link rel="stylesheet" href="/static/css/config.css">

</head>

<body>
<#compress>
<!-- 容器 -->
<div class="container-fluid">
    <!-- 导航条 -->
    <#include "../navbar.ftl">

    <div class="jumbotron text-center">
        <p>您目前没有环境</p>
        <#if PrivilegeUtils.isAppDeveloper(app.id)>
            <p><a class="btn btn-primary" href="/apps/${app.id}/envs" role="button">去创建环境</a></p>
        </#if>
    </div>

</div>

</#compress>
</body>

</html>
