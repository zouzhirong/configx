<nav class="navbar navbar-default navbar-inverse">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                    data-target="#configx-navbar-collapse" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">ConfigX</a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="configx-navbar-collapse">
            <ul class="nav navbar-nav">
                <li <#if rc.requestUri=="/apps">class="active"</#if>>
                    <a href="/apps">应用管理<span class="sr-only">(current)</span></a>
                </li>
                <li <#if rc.requestUri?contains("config")>class="active"</#if>>
                    <a href="/apps/${currentApp.id}/config">配置管理</a>
                </li>
                <li <#if rc.requestUri?contains("releaseforms")>class="active"</#if>>
                    <a href="/apps/${currentApp.id}/releaseforms">发布管理</a>
                </li>
            </ul>

            <ul class="nav navbar-nav navbar-right">
                <li class="dropdown">
                <#if currentApp??>
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
                       aria-expanded="false">${currentApp.name}<span class="caret"></span></a>
                    <ul class="dropdown-menu dropdown-menu-left">
                        <#list userAppList as app>
                            <#if app.id != currentApp.id>
                                <li><a href="/apps/${app.id}/config">${app.name}</a></li>
                                <li role="separator" class="divider"></li>
                            </#if>
                        </#list>
                    </ul>
                </#if>
                </li>
            <#if Request["isAdmin"]>
                <li <#if rc.requestUri=="/users">class="active"</#if>><a href="/users">用户管理</a></li>
            </#if>
                <li><p class="navbar-text"><a href="#" class="navbar-link">欢迎：${Request["name"]}</a></p></li>
                <li><a href="/user/logout">退出</a></li>
            </ul>

            <!--
            <form class="navbar-form navbar-right" role="search" action="">
                <div class="form-group">
                    <input type="hidden" name="profileId" class="form-control" value="-1">
                    <input type="text" name="configName" class="form-control" placeholder="">
                </div>
                <button type="submit" class="btn btn-default">搜索</button>
            </form>
            -->

        </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
</nav>

<!-- 错误提示框 -->
<#include "error.ftl">

