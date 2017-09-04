<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>Tags · ${app.name}</title>

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

        <!-- 路径导航 -->
        <div class="row">
            <div class="col-lg-4 nopadding">
                <ol class="breadcrumb">
                    <li><a href="/apps">应用</a></li>
                    <li><a href="/apps">${app.name}</a></li>
                    <li class="active">标签</li>
                </ol>
            </div>
        </div>


        <!-- 标签列表面板-->
        <div class="panel panel-default">
            <div class="panel-heading">
                <div class="row">
                    <label class="col-lg-2"></label>
                    <div class="col-lg-2 col-lg-push-8 text-right">
                        <#if PrivilegeUtils.isAppDeveloper(app.id)>
                            <!-- Button trigger modal -->
                            <button type="button" class="btn btn-sm btn-default" data-toggle="modal"
                                    data-target="#addTagModal" data-app="${app.id}">新建Tag
                            </button>
                        </#if>
                    </div>
                </div>
            </div>

            <#if tagList?? && (tagList?size > 0)>
                <table class="table table-hover">
                    <tr>
                        <th>环境ID</th>
                        <th>名称</th>
                        <th>描述</th>
                        <th class="action text-right">操作</th>
                    </tr>
                    <#list tagList as tag>
                        <tr>
                            <td class="id">
                            ${tag.id}
                            </td>
                            <td class="name">
                            ${tag.name}
                            </td>
                            <td>
                            ${tag.description}
                            </td>
                            <td class="text-right">
                                <#if PrivilegeUtils.isAppDeveloper(app.id)>
                                    <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                            data-target="#editTagModal" data-app="${app.id}" data-tag="${tag.id}"
                                            aria-label="编辑标签">
                                        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                                    </button>
                                    <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                            data-target="#delTagModal" data-app="${app.id}" data-tag="${tag.id}"
                                            aria-label="删除标签">
                                        <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                    </button>
                                </#if>
                            </td>
                        </tr>
                    </#list>
                </table>
            <#else>
                <div class="panel-body">
                    <#if PrivilegeUtils.isAppDeveloper(app.id)>
                        <p class="text-center">
                            您目前没有标签，<a href="#" data-toggle="modal" data-target="#addTagModal"
                                       data-app="${app.id}">创建一个标签</a>
                        </p>
                    </#if>
                </div>
            </#if>

            <div class="panel-footer"></div>
        </div>

    </div>

        <#include "tag_add.ftl">
        <#include "tag_edit.ftl">
        <#include "tag_del.ftl">

    </#escape>
</#compress>

<script src="/static/js/common.js"></script>
<script src="/static/js/tag.js"></script>
</body>

</html>
