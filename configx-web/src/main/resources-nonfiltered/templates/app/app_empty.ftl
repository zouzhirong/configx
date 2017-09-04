<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>没有应用</title>

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
        <h2>欢迎使用ConfigX配置管理系统！</h2>
        <p>您目前没有应用</p>
        <p><a class="btn btn-primary" href="/apps" role="button">去创建应用</a></p>
    </div>

</div>

</#compress>
</body>

</html>
