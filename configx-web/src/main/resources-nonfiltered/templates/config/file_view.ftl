<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>${(config.profile.name)!}/${config.name} · ${app.name}/${env.name}</title>

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

        <form class="form-horizontal">
            <div class="form-group">
                <div class="col-md-4 nopadding form-inline">
                    <ol class="breadcrumb">
                        <li><a href="/apps/${app.id}/config">${app.name}</a></li>
                        <li><a href="/apps/${app.id}/config/${env.name}">${env.name}</a></li>
                        <li><a href="/apps/${app.id}/config/${env.name}?profileId=${profile.id}">${profile.name}</a>
                        </li>
                        <li class="active">
                            <strong>${config.name}</strong>
                            <small>${config.description}</small>
                        </li>
                    </ol>
                </div>
            </div>
        </form>

        <!-- 容器 -->
        <div class="panel panel-default">
            <div class="panel-heading">
                <div class="col-lg-8">
                </div>

                <div class="col-lg-4 text-right">
                    <a href="/apps/${app.id}/config/file/new//${env.name}/${(config.profile.name)}"
                       class="glyphicon-btn tooltipped"
                       aria-label="新建文件">
                        <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
                    </a>
                    <a href="/apps/${app.id}/config/file/new/${env.name}/${(config.profile.name)}?copyId=${config.id}"
                       class="glyphicon-btn tooltipped" aria-label="复制文件">
                        <span class="glyphicon glyphicon-copy" aria-hidden="true"></span>
                    </a>
                    <a href="/apps/${app.id}/config/file/edit/${env.name}/${(config.profile.name)}/${config.name}"
                       class="glyphicon-btn tooltipped" aria-label="编辑文件">
                        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                    </a>
                    <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                            data-target="#delFileOptionsModal" data-app="$config.appId}" data-config="${config.id}"
                            aria-label="删除文件">
                        <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                    </button>
                </div>
            </div><!-- /.panel-heading -->

            <div class="panel-body" id="editor" data-readonly="true"
                 data-app="${appId}" data-config="${config.id}" data-filename="${config.name}"></div>

        </div><!-- /.panel -->

        <#include "file_del.ftl">

    </div>

    </#escape>
</#compress>

<script src="/libs/ace/src-noconflict/ace.js" type="text/javascript" charset="utf-8"></script>
<script src="/libs/ace/src-noconflict/ext-language_tools.js"></script>
<script src="/libs/ace/src-noconflict/ext-modelist.js"></script>
<script src="/libs/ace/src-noconflict/ext-statusbar.js"></script>
<script src="/libs/ace/src-noconflict/ext-keybinding_menu.js"></script>
<script src="/libs/ace/src-noconflict/ext-chromevox.js"></script>
<script src="/static/js/common.js"></script>
<script src="/static/js/ace-editor.js"></script>
<script src="/static/js/file.js"></script>
</body>

</html>
