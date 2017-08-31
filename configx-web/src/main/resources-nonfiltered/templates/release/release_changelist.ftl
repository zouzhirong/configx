<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>发布单Change List</title>

    <script src="/libs/jquery/jquery-2.1.4.min.js"></script>

    <script src="/libs/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/libs/bootstrap/css/bootstrap.min.css"/>

    <script src="/libs/datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
    <link href="/libs/datetimepicker/css/bootstrap-datetimepicker.min.css" rel="stylesheet">

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
                    <label class="col-lg-2">发布单Change List</label>
                    <div class="col-lg-2 col-lg-push-10 text-right">
                        <a href="/releaseforms/add" class="btn btn-sm btn-default" rel="nofollow">查看详细修改</a>
                    </div>
                </div>
            </div>

            <table class="table table-hover">
                <tr>
                    <th>发布单ID</th>
                    <th>应用</th>
                    <th>环境</th>
                    <th>Profile</th>
                    <th>配置名</th>
                    <th class="action text-right">操作</th>
                </tr>
                <#if changeList??>
                    <#list changeList as change>
                        <tr>
                            <td class="id">
                            ${releaseForm.id}
                            </td>
                            <td class="name">
                            ${change.appName}
                            </td>
                            <td class="name">
                            ${change.envName}
                            </td>
                            <td class="name">
                            ${change.profileName}
                            </td>
                            <td class="name">
                            ${change.configName}
                            </td>
                            <td class="action text-right">
                                <a href="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/compare/${change.configId}"
                                   class="btn btn-sm btn-default tooltipped" aria-label="配置项这次发布与上次发布的差异">与上次发布对比</a>
                                <a href="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/commits/${change.configId}"
                                   class="btn btn-sm btn-default tooltipped" aria-label="配置项这次发布与上次发布的修改历史">查看提交日志</a>
                            </td>
                        </tr>
                    </#list>
                </#if>
            </table>

            <div class="panel-footer"></div>
        </div>

    </div>

    </#escape>
</#compress>

<script src="/static/js/common.js"></script>
<script src="/static/js/release.js"></script>
</body>

</html>
