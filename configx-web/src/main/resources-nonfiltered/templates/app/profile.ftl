<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>Profiles · ${app.name}</title>

    <script src="/libs/jquery/jquery-2.1.4.min.js"></script>

    <script src="/libs/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/libs/bootstrap/css/bootstrap.min.css"/>

    <script src="/libs/colorpicker/js/bootstrap-colorpicker.min.js"></script>
    <link rel="stylesheet" href="/libs/colorpicker/css/bootstrap-colorpicker.min.css"/>

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
                    <li class="active">Profiles</li>
                </ol>
            </div>
        </div>


        <!-- Profile列表面板 -->
        <div class="panel panel-default">
            <div class="panel-heading">
                <div class="row">
                    <label class="col-lg-2"></label>
                    <div class="col-lg-2 col-lg-push-8 text-right">
                        <#if PrivilegeUtils.isAppDeveloper(app.id)>
                            <!-- Button trigger modal -->
                            <button type="button" class="btn btn-sm btn-default" data-toggle="modal"
                                    data-target="#addProfileModal" data-app="${app.id}">添加Profile
                            </button>
                        </#if>
                    </div>
                </div>
            </div>

            <#if profileList?? && (profileList?size > 1)>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <tr>
                            <th>ID</th>
                            <th>名称</th>
                            <th>描述</th>
                            <th>顺序</th>
                            <th>颜色</th>
                            <th class="action text-right">操作</th>
                        </tr>
                        <#list profileList as profile>
                            <tr style="<#if profile.color??>background-color: ${profile.color}</#if>">
                                <td class="id">
                                ${profile.id}
                                </td>
                                <td class="name">
                                ${profile.name}
                                </td>
                                <td>
                                ${profile.description}
                                </td>
                                <td>
                                ${profile.order}
                                </td>
                                <td>
                                ${profile.color}
                                </td>
                                <td class="text-right">
                                    <#if PrivilegeUtils.isAppDeveloper(app.id)>
                                        <#if profile.id gt 0>
                                            <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                                    data-target="#editProfileModal" data-app="${app.id}"
                                                    data-profile="${profile.id}"
                                                    aria-label="编辑Profile">
                                                <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                                            </button>
                                            <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                                    data-target="#delProfileModal" data-app="${app.id}"
                                                    data-profile="${profile.id}"
                                                    aria-label="删除Profile">
                                                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                            </button>
                                        </#if>
                                    </#if>
                                </td>
                            </tr>
                        </#list>
                    </table>
                </div><!-- /.table-responsive -->
            <#else>
                <div class="panel-body">
                <#if PrivilegeUtils.isAppDeveloper(app.id)>
                    <p class="text-center">
                        您目前没有Profile，<a href="#" data-toggle="modal" data-target="#addProfileModal"
                                        data-app="${app.id}">创建一个Profile</a>
                    </p>
                </#if>
                </div>
            </#if>

            <div class="panel-footer"></div>
        </div>

    </div>

        <#include "profile_add.ftl">
        <#include "profile_edit.ftl">
        <#include "profile_del.ftl">

    </#escape>
</#compress>

<script src="/static/js/common.js"></script>
<script src="/static/js/profile.js"></script>
</body>

</html>
