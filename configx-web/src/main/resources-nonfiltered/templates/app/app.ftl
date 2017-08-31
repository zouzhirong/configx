<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>应用列表</title>

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
                        <#if Request["isAdmin"]>
                            <button type="button" class="btn btn-sm btn-default" data-toggle="modal"
                                    data-target="#addAppModal">
                                创建应用
                            </button>
                        </#if>
                    </div>
                </div>
            </div>

            <table class="table table-hover">
                <tr>
                    <th colspan="2">应用ID</th>
                    <th>名称</th>
                    <th>描述</th>
                    <th>App Key Secret</th>
                    <th>管理员</th>
                    <th>开发者</th>
                    <th>创建</th>
                    <th>修改</th>
                    <th class="action text-right">操作</th>
                </tr>
                <#if page.content??>
                    <#list page.content as app>
                        <tr>
                            <td class="icon">
                                <span class="glyphicon glyphicon-folder-open" aria-hidden="true"></span>
                            </td>
                            <td class="id">
                            ${app.id}
                            </td>
                            <td class="name">
                            ${app.name}
                            </td>
                            <td class="description">
                            ${app.description}
                            </td>
                            <td>
                                App_Key: ${app.appKey}
                                <p/>
                                App_Secret: ${app.appSecret}
                            </td>
                            <td>
                                <#list app.admins?split(",") as admin>
                                ${admin} <br>
                                </#list>
                            </td>
                            <td>
                                <#list app.developers?split(",") as developer>
                                ${developer} <br>
                                </#list>
                            </td>
                            <td class="time">
                            ${app.creator} (${app.createTime?datetime})
                            </td>
                            <td class="time">
                                <#if app.updateTime?datetime gt app.createTime?datetime>
                                ${app.updater} (${app.updateTime?datetime})
                                <#else>
                                    无
                                </#if>
                            </td>
                            <td class="text-right">
                                <a href="/apps/${app.id}/envs" class="btn btn-sm btn-default" rel="nofollow">环境</a>
                                <a href="/apps/${app.id}/profiles" class="btn btn-sm btn-default"
                                   rel="nofollow">Profiles</a>
                                <a href="/apps/${app.id}/tags" class="btn btn-sm btn-default" rel="nofollow">标签</a>
                                <a class="btn btn-default" href="/apps/${app.id}/hooks">Webhooks</a>
                                <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                        data-target="#editAppModal" data-app="${app.id}" aria-label="编辑应用">
                                    <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                                </button>
                                <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                        data-target="#delAppModal" data-app="${app.id}" aria-label="删除应用">
                                    <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                </button>
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

        <#include "app_add.ftl">
        <#include "app_edit.ftl">
        <#include "app_del.ftl">

    </#escape>
</#compress>

<script>
    pageCount =${page.pageCount};
    totalElements =${page.totalElements};
</script>
<script src="/static/js/common.js"></script>
<script src="/static/js/app.js"></script>
<script src="/libs/twbs-pagination/jquery.twbsPagination.min.js"></script>
<script src="/static/js/pagination.js"></script>
</body>

</html>
