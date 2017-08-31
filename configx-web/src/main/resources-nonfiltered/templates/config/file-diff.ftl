<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>${revision1?c}  <-->  ${revision2?c}</title>

    <script src="/libs/jquery/jquery-2.1.4.min.js"></script>

    <script src="/libs/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/libs/bootstrap/css/bootstrap.min.css"/>

    <link rel="stylesheet" href="/static/css/config.css">
    <link rel="stylesheet" href="/libs/ace-diff/css/styles.css"/>
    <link rel="stylesheet" href="/static/css/file-diff.css">

</head>

<body>
<#compress>
    <#escape x as x?html>

    <input type="hidden" name="appId" value="${app.id}"/>
    <input type="hidden" name="revision1" value="${revision1?c}"/>
    <input type="hidden" name="revision2" value="${revision2?c}"/>

    <div class="container-fluid">
        <div id="diff-header" class="row">
            <div class="col-lg-6">
                应用：${(left.appName)!}
                <span class="divider-vertical"></span>
                环境：${(left.envName)!}
                <span class="divider-vertical"></span>
                Profile：${(left.profileName)!}
                <span class="divider-vertical"></span>
                配置名：${(left.configName)!} ${(left.revision?c)!0}
            </div>
            <div class="col-lg-6 text-right">
                应用：${(right.appName)!}
                <span class="divider-vertical"></span>
                环境：${(right.envName)!}
                <span class="divider-vertical"></span>
                Profile：${(right.profileName)!}
                <span class="divider-vertical"></span>
                配置名：${(right.configName)!} ${(right.revision?c)!0}
            </div>
        </div>
    </div>

    <div id="flex-container">
        <div>
            <div id="left-editor"></div>
        </div>
        <div id="gutter"></div>
        <div>
            <div id="right-editor"></div>
        </div>
    </div>

    </#escape>
</#compress>

<!--ace libs-->
<script src="/libs/ace/src-noconflict/ace.js" type="text/javascript" charset="utf-8"></script>
<script src="/libs/ace/src-noconflict/ext-language_tools.js"></script>
<script src="/libs/ace/src-noconflict/ext-modelist.js"></script>
<script src="/libs/ace/src-noconflict/ext-statusbar.js"></script>
<script src="/libs/ace/src-noconflict/ext-keybinding_menu.js"></script>
<script src="/libs/ace/src-noconflict/ext-chromevox.js"></script>

<!--ace-diff libs-->
<script>
    // Defines define if define is not defined -_-
    window.define = window.define || ace.define;
    window.require = window.require || ace.require;
</script>
<script src="/libs/ace-diff/libs/diff_match_patch.js"></script>
<script src="/libs/ace-diff/ace-diff.min.js"></script>
<script src="/static/js/file-diff.js"></script>

</body>

</html>
