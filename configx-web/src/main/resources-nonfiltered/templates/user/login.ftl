<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>登录</title>

    <script src="/libs/jquery/jquery-2.1.4.min.js"></script>

    <script src="/libs/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/libs/bootstrap/css/bootstrap.min.css"/>

</head>

<body>
<#compress>
<div>

    <!-- 容器 -->
    <div class="container" style="width:500px; margin-top:100px;">

        <form class="form-horizontal js-login-form" action="/user/login/submit" method="post"
              data-remote="true" data-method="post" data-location="<#if back??>${back}<#else>/</#if>">

            <#if back??>
                <input type="hidden" name="back" value="${back}"/>
            </#if>
            <div class="form-group">
                <label for="email" class="col-sm-2 control-label">用户名</label>
                <div class="col-sm-10">
                    <input type="text" class="form-control" id="email" name="email" placeholder="">
                </div>
            </div>
            <div class="form-group">
                <label for="password" class="col-sm-2 control-label">密&emsp;码</label>
                <div class="col-sm-10">
                    <input type="password" class="form-control" id="password" name="password" placeholder="">
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" name="remember" value="1"> 一周内免登录
                        </label>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="submit" class="btn btn-default">登录</button>
                </div>
            </div>
        </form>

    </div><!-- /.container-fluid -->

</div>

<!-- 错误提示框 -->
    <#include "../error.ftl">

</#compress>

<script src="/static/js/common.js"></script>
</body>

</html>
