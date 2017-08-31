<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>新建文件</title>

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

        <form class="form-horizontal js-file-form"
              action="/apps/${app.id}/config" method="post"
              data-remote="true" data-method="post" data-before="ajaxBeforeSend"
              data-location="">

            <input type="hidden" name="envId" value="${env.id}"/>
            <div class="form-group">
                <div class="col-md-4 nopadding form-inline">
                    <ol class="breadcrumb">
                        <li><a href="/apps/${app.id}/config">${app.name}</a></li>
                        <li><a href="/apps/${app.id}/config/${env.name}">${env.name}</a></li>
                        <li>
                        <span>
                            <select name="profileId" class="form-control form-select">
                            <optgroup label="选择Profile">
                                <#if profileList??>
                                    <#list profileList as p>
                                        <option value="${p.id}"<#if ((profile.id)!0) == p.id>selected</#if>>
                                        ${MessageUtils.getMessage("app.profile.name."+p.name, p.name)}
                                        </option>
                                    </#list>
                                </#if>
                            </optgroup>
                        </select>
                        </span>
                        </li>
                        <li>
                            <input type="text" name="name" value="<#if (copy.name)??>Copy of ${(copy.name)!}</#if>"
                                   class="form-control" size="16" placeholder="文件名" aria-describedby="helpBlock"
                                   required>
                        </li>
                    </ol>
                </div>
                <label for="tagId" class="col-md-1 control-label">标签</label>
                <div class="col-md-1">
                    <select multiple size="3" name="tagId" class="form-control"
                            aria-describedby="helpBlock">
                        <#list tagList as tag>
                            <option value="${tag.id}"
                                    <#if ((copy.tagIdList)![])?seq_contains(tag.id)>selected</#if>>${tag.name}</option>
                        </#list>
                    </select>
                </div>
                <label for="description" class="col-md-1 control-label">描述</label>
                <div class="col-md-2">
                                <textarea name="description" class="form-control" rows="1"
                                          placeholder="文件描述" aria-describedby="helpBlock">
                                    <#if (copy.description)??>${(copy.description)!}</#if>
                                </textarea>
                </div>
                <div class="col-md-2">
                                <textarea name="message" class="form-control" rows="1"
                                          placeholder="提交注释" aria-describedby="helpBlock" required></textarea>
                </div>
                <div class="col-md-1 text-right">
                    <button type="submit" class="btn btn-primary" title="保存 (Ctrl+S)">创建文件</button>
                </div>
            </div>
        </form>

        <div class="panel panel-default">
            <div class="panel-heading">
                <div class="col-lg-8">
                </div>

                <div class="col-lg-4 text-right">
                    <select class="form-select js-file-theme">
                        <optgroup label="Theme">
                            <option value="chrome">chrome</option>
                            <option value="eclipse">eclipse</option>
                            <option value="github">github</option>
                            <option value="merbivore">merbivore</option>
                            <option value="terminal">terminal</option>
                            <option value="twilight">twilight</option>
                        </optgroup>
                    </select>
                    <select class="form-select js-file-mode">
                        <optgroup label="Mode">
                            <option value="text">text</option>
                            <option value="json">json</option>
                            <option value="xml">xml</option>
                            <option value="yaml">yaml</option>
                            <option value="toml">toml</option>
                            <option value="properties">properties</option>
                        </optgroup>
                    </select>
                    <select class="form-select js-file-font-size">
                        <optgroup label="Font size">
                            <option value="10">10</option>
                            <option value="12">12</option>
                            <option value="14">14</option>
                            <option value="16">16</option>
                            <option value="18">18</option>
                        </optgroup>
                    </select>
                    <select class="form-select js-file-wrap-mode">
                        <optgroup label="Line wrap mode">
                            <option value="on">Soft wrap</option>
                            <option value="off">No wrap</option>
                        </optgroup>
                    </select>
                </div>
            </div><!-- /.panel-heading -->

            <div class="panel-body" id="editor"
                 data-app="${appId}" data-config="${(copy.id?c)!0}" data-filename="${(copy.name)!}"></div>

            <div class="panel-footer">
                <div class="row">
                    <div class="col-lg-12 text-right">
                        <ul class="list-inline text-muted">
                            <li>快捷键:</li>
                            <li>保存：Ctrl-S</li>
                            <li>查找：Ctrl-F</li>
                            <li>替换：Ctrl-H</li>
                            <li>撤销：Ctrl-Z</li>
                            <li>恢复：Ctrl-Y</li>
                            <li>跳转到行：Ctrl-L</li>
                            <li>自动完成：Ctrl-Shift-Space</li>
                            <li>更多快捷键： Ctrl-Alt-H</li>
                        </ul>
                    </div>
                </div>
            </div><!-- /.panel-footer -->

        </div><!-- /.panel -->

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
