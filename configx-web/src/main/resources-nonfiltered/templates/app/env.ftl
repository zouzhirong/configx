<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>Envs · ${app.name}</title>

    <script src="/libs/jquery/jquery-2.1.4.min.js"></script>

    <script src="/libs/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/libs/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/libs/font-awesome/css/font-awesome.min.css"/>

    <script src="/libs/switch/js/bootstrap-switch.min.js"></script>
    <link href="/libs/switch/css/bootstrap-switch.min.css" rel="stylesheet">

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

        <!-- 路径导航 -->
        <div class="row">
            <div class="col-lg-4 nopadding">
                <ol class="breadcrumb">
                    <li><a href="/apps">应用</a></li>
                    <li><a href="/apps">${app.name}</a></li>
                    <li class="active">环境</li>
                </ol>
            </div>
        </div>

        <!-- 环境列表面板 -->
        <div class="panel panel-default">
            <div class="panel-heading">
                <div class="row">
                    <label class="col-lg-2"></label>
                    <div class="col-lg-2 col-lg-push-8 text-right">
                        <#if PrivilegeUtils.isAppDeveloper(app.id)>
                            <!-- Button trigger modal -->
                            <button type="button" class="btn btn-sm btn-default" data-toggle="modal"
                                    data-target="#addEnvModal" data-app="${app.id}">
                                新建环境
                            </button>
                        </#if>
                    </div>
                </div>
            </div>

            <#if envList?? && (envList?size > 0)>
                <table class="table table-hover">
                    <tr>
                        <th>环境ID</th>
                        <th>名称</th>
                        <th>别名</th>
                        <th>描述</th>
                        <th>顺序</th>
                        <th>创建</th>
                        <th>修改</th>
                        <th>最新修订</th>
                        <th>上次构建</th>
                        <th>最新发布</th>
                        <th>发布模式</th>
                        <th class="action text-right">操作</th>
                    </tr>
                    <#list envList as env>
                        <tr>
                            <td class="id">
                            ${env.id}
                            </td>
                            <td class="name">
                            ${env.name}
                            </td>
                            <td>
                            ${env.alias}
                            </td>
                            <td>
                            ${env.description}
                            </td>
                            <td>
                            ${env.order}
                            </td>
                            <td class="time">
                            ${env.creator} (${env.createTime?datetime})
                            </td>
                            <td class="time">
                            ${env.updater} (${env.updateTime?datetime})
                            </td>
                            <td class="time">
                            ${env.revision?c} (${(env.dataChangeLastTime?datetime)!})
                            </td>
                            <td class="time">
                                #${env.buildId} (${(env.buildTime?datetime)!})
                            </td>
                            <td class="time">
                                #${env.releaseVersion} (${(env.releaseTime?datetime)!})
                            </td>
                            <td>
                                <#if env.autoRelease>
                                    <span class="text-danger">自动发布</span>
                                <#else>
                                    <span class="text-info">手动发布</span>
                                </#if>
                            <td class="text-right">
                                <#if PrivilegeUtils.isAppAdmin(app.id)>
                                    <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                            data-target="#editEnvModal" data-app="${app.id}" data-env="${env.id}"
                                            aria-label="编辑环境">
                                        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                                    </button>
                                    <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                            data-target="#delEnvModal" data-app="${app.id}" data-env="${env.id}"
                                            aria-label="删除环境">
                                        <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                    </button>
                                </#if>
                            </td>
                        </tr>
                    </#list>
                </table>
            <#else>
                <div class="panel-body">
                    <p class="text-center">
                        您目前没有环境，<a href="#" data-toggle="modal" data-target="#addEnvModal"
                                   data-app="${app.id}">创建一个环境</a>
                    </p>
                </div>
            </#if>

            <div class="panel-footer"></div>
        </div>

    </div>

        <#include "env_add.ftl">
        <#include "env_edit.ftl">
        <#include "env_del.ftl">

    </#escape>
</#compress>

<script src="/static/js/common.js"></script>
<script src="/static/js/env.js"></script>
</body>

</html>
