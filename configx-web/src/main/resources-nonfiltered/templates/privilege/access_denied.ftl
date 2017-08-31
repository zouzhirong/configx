<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>拒绝访问</title>

    <link rel="stylesheet" href="/libs/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/static/css/config.css">

    <script src="/libs/jquery/jquery-2.1.4.min.js"></script>
    <script src="/libs/bootstrap/js/bootstrap.min.js"></script>

</head>

<body>
<#compress>
<!-- 容器 -->
<div class="container-fluid">
    <!-- 导航条 -->
    <#include "../navbar.ftl">

    <div class="jumbotron text-center">
        <h2>拒绝访问</h2>
        <p>您没有权限执行此操作！</p>
    </div>

</div>

</#compress>
</body>

</html>
