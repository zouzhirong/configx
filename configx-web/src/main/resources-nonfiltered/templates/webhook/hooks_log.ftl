<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>Webhook日志</title>

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
                    <div class="col-md-12">
                        <h5><a href="/apps/${app.id}/hooks">Webhooks</a> / Webhook日志</h5>
                    </div>
                </div>
            </div>

            <table class="table table-hover">
                <tr>
                    <th>应用</th>
                    <th>Webhook 名称</th>
                    <th>URL</th>
                    <th>Content-Type</th>
                    <th>Secret</th>
                    <th>事件</th>
                    <th>时间</th>
                    <th class="action text-right">操作</th>
                </tr>
                <#if webhookLogs??>
                    <#list webhookLogs as log>
                        <tr>
                            <td class="name">
                            ${log.appName}
                            </td>
                            <td class="name">
                            ${log.name}
                            </td>
                            <td class="sourceLang">
                            ${log.url}
                            </td>
                            <td class="description">
                            ${log.contentType}
                            </td>
                            <td class="description">
                            ${log.secret}
                            </td>
                            <td class="description">
                            ${log.eventType}
                            </td>
                            <td>
                            ${log.createTime?datetime}
                            </td>
                            <td class="text-right">
                                <a href="/apps/${app.id}/hooks/log/${log.id}" class="btn btn-default">详细</a>
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
