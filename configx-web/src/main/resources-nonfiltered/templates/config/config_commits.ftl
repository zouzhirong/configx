<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>Commits · ${app.name}/${env.name}</title>

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

        <div class="panel panel-default panel-primary">
            <div class="panel-body">
                <form class="form-horizontal" action="/apps/${app.id}/commits/{envName}">
                    <div class="form-group">
                        <label for="envId" class="col-md-1 control-label">环境</label>
                        <div class="col-md-2">
                            <select name="envName" class="form-control" onchange="submitForm(this.form)">
                                <#list envList as env>
                                    <option value="${env.name}"
                                            <#if (form.envId == env.id)>selected</#if>>${env.name}</option>
                                </#list>
                            </select>
                        </div>
                        <label for="profileId" class="col-md-1 control-label">Profile</label>
                        <div class="col-md-2">
                            <select name="profileId" class="form-control">
                                <#if profileList??>
                                    <#list profileList as profile>
                                        <option value="${profile.id}"
                                                <#if ((form.profileId)!0) == profile.id>selected</#if>>
                                        ${MessageUtils.getMessage("app.profile.name."+profile.name, profile.name)}
                                        </option>
                                    </#list>
                                </#if>
                            </select>
                        </div>
                        <label for="revision" class="col-md-1 control-label">修订版本号</label>
                        <div class="col-md-1">
                            <input type="text" class="form-control" id="revision" name="revision"
                                   value="${(form.revision)!}"
                                   placeholder="">
                        </div>
                        <label for="configName" class="col-md-1 control-label">配置名</label>
                        <div class="col-md-1">
                            <input type="text" class="form-control" id="configName" name="configName"
                                   value="${(form.configName)!}" placeholder="">
                        </div>
                        <div class="col-md-1 text-right">
                            <button type="submit" class="btn btn-primary btn-block">查询</button>
                        </div>

                        <div class="col-md-1 text-right">
                            <a href="/apps/${app.id}/commits/commits/${env.name}"
                               class="btn btn-default btn-block">重置</a>
                        </div>
                    </div>
                </form>
            </div><!-- /.panel-body -->
        </div><!-- /.panel -->



        <#assign contentMap=page.contentMap>
        <#if contentMap??>
            <div class="commits-listing commits-listing-padded">
                <#list contentMap?keys?sort?reverse as date>
                    <div class="commit-group-title">
                        <svg aria-hidden="true" class="octicon octicon-git-commit" height="16" version="1.1"
                             viewBox="0 0 14 16"
                             width="14">
                            <path fill-rule="evenodd"
                                  d="M10.86 7c-.45-1.72-2-3-3.86-3-1.86 0-3.41 1.28-3.86 3H0v2h3.14c.45 1.72 2 3 3.86 3 1.86 0 3.41-1.28 3.86-3H14V7h-3.14zM7 10.2c-1.22 0-2.2-.98-2.2-2.2 0-1.22.98-2.2 2.2-2.2 1.22 0 2.2.98 2.2 2.2 0 1.22-.98 2.2-2.2 2.2z"></path>
                        </svg>
                        提交于 ${date}
                    </div>
                    <div class="commit-group">
                        <div class="panel  panel-default">
                            <table class="table table-hover">
                                <tr>
                                    <td class="text-muted">修订</td>
                                    <td class="text-muted">应用</td>
                                    <td class="text-muted">环境</td>
                                    <td class="text-muted">Profile</td>
                                    <td class="text-muted">配置名</td>
                                    <td class="text-muted">配置值</td>
                                    <td class="text-muted">提交注释</td>
                                    <td class="text-muted">提交者</td>
                                    <td class="text-muted">提交日期</td>
                                    <td class="action text-muted text-center">操作</td>
                                </tr>
                                <#list contentMap[date]?sort_by("revision")?reverse as commit>
                                    <#assign config=commit.configItemHistory />
                                    <tr>
                                        <td width="5%">r${commit.revision?c}</td>
                                        <td width="10%">${commit.appName}</td>
                                        <td width="5%">${commit.envName}</td>
                                        <td width="5%">${(commit.profileName)!}</td>
                                        <td width="10%">${config.configName}</td>
                                        <td width="20%">
                                            <#if config.valueType==0>
                                            <#else>
                                            ${config.configValue}
                                            </#if>
                                        </td>
                                        <td width="20%">${commit.message}</td>
                                        <td width="10%">${commit.author}</td>
                                        <td width="10%">${commit.date?datetime}</td>
                                        <td width="5%" class="text-right">
                                            <a href="/apps/${app.id}/config/compare/${config.lastRevision?c}:${config.revision?c}"
                                               class="btn btn-sm btn-default">与前一版本对比</a>
                                        </td>
                                    </tr>
                                </#list>
                            </table>
                        </div><!-- /.panel -->
                    </div>
                </#list>
            </div>
        </#if>

        <!-- 分页 -->
        <#include "../pagination.ftl">

    </div>

    </#escape>
</#compress>

<script>
    pageCount =${page.pageCount};
    totalElements =${page.totalElements};
</script>
<script src="/static/js/common.js"></script>
<script src="/static/js/config.js"></script>
<script src="/libs/twbs-pagination/jquery.twbsPagination.min.js"></script>
<script src="/static/js/pagination.js"></script>
</body>

</html>
