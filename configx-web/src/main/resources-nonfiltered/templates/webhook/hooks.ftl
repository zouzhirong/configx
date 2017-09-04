<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>Webhooks</title>

    <script src="/libs/jquery/jquery-2.1.4.min.js"></script>

    <script src="/libs/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/libs/bootstrap/css/bootstrap.min.css"/>

    <link rel="stylesheet" href="/static/css/config.css">

</head>

<body>
<#compress>
    <#escape x as x?html>

    <!-- 容器 -->
    <div class="container-fluid">
        <!-- 导航条 -->
        <#include "../navbar.ftl">

        <div class="panel panel-default">
            <div class="panel-heading">
                <div class="row">
                    <label class="col-lg-2">应用列表</label>
                    <div class="col-lg-2 col-lg-push-8 text-right">
                        <!-- Button trigger modal -->
                        <#if PrivilegeUtils.isAppDeveloper(app.id)>
                            <a class="btn btn-default" href="/apps/${app.id}/hooks/log">Webhook 日志</a>
                            <a class="btn btn-default" href="/apps/${app.id}/hooks/new">添加 Webhook</a>
                        </#if>
                    </div>
                </div>
            </div>

            <table class="table table-hover">
                <tr>
                    <th>名称</th>
                    <th>URL</th>
                    <th>Content-Type</th>
                    <th>Secret</th>
                    <th>事件</th>
                    <th class="action text-right">操作</th>
                </tr>
                <#if hooks??>
                    <#list hooks as hook>
                        <tr>
                            <td class="name">
                            ${hook.name}
                            </td>
                            <td class="sourceLang">
                            ${hook.url}
                            </td>
                            <td class="description">
                            ${hook.contentType}
                            </td>
                            <td class="description">
                            ${hook.secret}
                            </td>
                            <td class="description">
                            ${hook.eventType.typeName}
                            </td>
                            <td class="text-right">
                                <a href="/apps/${app.id}/hooks/log?webhookId=${hook.id}"
                                   class="btn btn-default">查看日志</a>
                                <a href="/apps/${app.id}/hooks/${hook.id}" class="btn btn-default">编辑</a>
                                <a href="/apps/${app.id}/hooks/delete/${hook.id}"
                                   class="btn btn-default">删除</a>
                            </td>
                        </tr>
                    </#list>
                </#if>
            </table>

            <div class="panel-footer"></div>
        </div><!-- /.panel -->

        <!-- 分页 -->
        <#include "../pagination.ftl">

    </div>

    </#escape>
</#compress>
</body>

</html>
