<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>${app.name}/${env.name}: ${env.description}</title>

    <script src="/libs/jquery/jquery-2.1.4.min.js"></script>

    <script src="/libs/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/libs/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/libs/font-awesome/css/font-awesome.min.css"/>

    <script src="/libs/icheck/icheck.min.js"></script>
    <link href="/libs/icheck/skins/square/blue.css" rel="stylesheet">

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
                <form class="form-horizontal" action="/apps/${app.id}/config/{envName}">
                    <div class="form-group">
                        <label for="envId" class="col-md-1 control-label">环境</label>
                        <div class="col-md-2">
                            <select name="envName" class="form-control form-select" onchange="submitForm(this.form)">
                                <#list envList as env>
                                    <option value="${env.name}"
                                            <#if (form.envId == env.id)>selected</#if>>${env.name}</option>
                                </#list>
                            </select>
                        </div>
                        <label for="profileId" class="col-md-1 control-label"
                               title="Profile顺序差小于100则属于同一组">Profile</label>
                        <div class="col-md-2">
                            <select name="profileId" class="form-control form-select" onchange="submitForm(this.form)">
                                <option value="-1">全部</option>
                                <#if profileList??>
                                <optgroup label="分组">
                                    <#list profileList as profile>
                                        <#if (profile_index > 0)>
                                            <#if (profile_index < (profileList?size-1))>
                                                <#if profile.order - profileList[profile_index-1].order gte 100>
                                                </optgroup>
                                                <optgroup label="分组">
                                                </#if>
                                            </#if>
                                        </#if>
                                        <option value="${profile.id}"
                                                <#if ((form.profileId)!0) == profile.id>selected</#if>>
                                        ${MessageUtils.getMessage("app.profile.name."+profile.name, profile.name)}
                                        </option>
                                    </#list>
                                </optgroup>
                                </#if>
                            </select>
                        </div>
                        <div class="col-md-1">
                            <label class="checkbox-inline">
                                <input type="checkbox" class="ichecked form-control" name="onlyUnposted" value="1"
                                       <#if (form.onlyUnposted)!false>checked</#if>> 仅未发布
                            </label>
                        </div>
                        <label for="configName" class="col-md-1 control-label">配置名</label>
                        <div class="col-md-2">
                            <input type="text" class="form-control" id="configName" name="configName"
                                   value="${(form.configName)!}" autofocus="autofocus" placeholder="">
                        </div>
                        <div class="col-md-1 text-right">
                            <button type="submit" class="btn btn-primary btn-block">查询</button>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="valueType" class="col-md-1 control-label text-muted">类型</label>
                        <div class="col-md-1">
                            <select name="valueType" class="form-control form-select">
                                <option value="">全部</option>
                                <#list valueTypes as valueType>
                                    <option value="${valueType.type}"
                                            <#if ((form.valueType)![])?seq_contains(valueType.type)>selected</#if>>
                                    ${MessageUtils.getMessage("config.value_type.name."+valueType.type)}
                                    </option>
                                </#list>
                            </select>
                        </div>
                        <div class="col-md-1"></div>
                        <label for="tagId" class="col-md-1 control-label text-muted">标签</label>
                        <div class="col-md-1">
                            <select name="tagId" class="form-control form-select">
                                <option value="">全部</option>
                                <#if tagList??>
                                    <#list tagList as tag>
                                        <option value="${tag.id}"
                                                <#if ((form.tagId)![])?seq_contains(tag.id)>selected</#if>>${tag.name}</option>
                                    </#list>
                                </#if>
                            </select>
                        </div>
                        <div class="col-md-1"></div>
                        <div class="col-md-1">
                        <#--<label class="checkbox-inline text-muted">-->
                                <#--<input type="checkbox" class="ichecked form-control" name="onlyDisabled" value="1"-->
                                       <#--<#if (form.onlyDisabled)!false>checked</#if>> 仅禁用-->
                            <#--</label>-->
                        </div>
                        <label for="configValue" class="col-md-1 control-label text-muted">配置值</label>
                        <div class="col-md-2">
                            <input type="text" class="form-control" id="configValue" name="configValue"
                                   value="${(form.configValue)!}" placeholder="">
                        </div>
                        <div class="col-md-1 text-right">
                            <a href="/apps/${app.id}/config/${env.name}" class="btn btn-default btn-block">重置</a>
                        </div>
                    </div>
                </form>
            </div><!-- /.panel-body -->
        </div><!-- /.panel -->

        <div class="panel panel-default">
            <div class="panel-heading">
                <div class="row">
                    <label class="col-lg-2"></label>
                    <form class="form-inline">
                        <div class="form-group col-lg-4 col-lg-offset-0 col-lg-push-6 text-right">

                            <div class="btn-group">
                                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                                        aria-haspopup="true" aria-expanded="false">
                                    新建配置 <span class="caret"></span>
                                </button>
                                <ul class="dropdown-menu">
                                    <#list valueTypes as valueType>
                                        <#if valueType.type ==0>
                                            <li>
                                                <a href="/apps/${app.id}/config/file/new/${env.name}/${(profile.name)}">文件类型</a>
                                            </li>
                                        <#else>
                                            <li>
                                                <a data-toggle="modal" href="#addConfigModal"
                                                   data-app="${app.id}" data-env-name="${env.name}"
                                                   data-profile="${profile.id}"
                                                   data-value-type="${valueType.type}">
                                                ${MessageUtils.getMessage("config.value_type.name."+valueType.type)}类型
                                                </a>
                                            </li>
                                        </#if>
                                    </#list>
                                </ul>
                            </div>
                            <a href="/apps/${app.id}/commits/${env.name}" class="btn btn-default">Commits</a>

                        </div>
                    </form>
                </div>
            </div><!-- /.panel-heading -->

            <table class="table table-hover">
                <tr>
                    <th>配置ID</th>
                    <th>配置名</th>
                    <th>环境</th>
                    <th>Profile</th>
                    <th>标签</th>
                    <th>配置值</th>
                    <th>描述</th>
                    <th>创建人</th>
                    <th>创建时间</th>
                    <th>修改人</th>
                    <th>修改时间</th>
                    <th>最新修订版</th>
                    <th>发布状态</th>
                    <th class="action text-right">操作</th>
                </tr>
                <#if page.content??>
                    <#list page.content as config>
                        <tr id="tr-config-${config.id}"
                            style="<#if (config.profile.color)??>background-color: ${config.profile.color}</#if>">
                            <td class="id">
                            ${config.id}
                            </td>
                            <td>
                            ${config.name}
                            </td>
                            <td>${config.env.name}</td>
                            <td>${(config.profile.name)!}</td>
                            <td>
                                <#if config.tagList??>
                                    <#list config.tagList as tag>
                                        <code>${tag.name}</code>
                                    </#list>
                                </#if>
                            </td>
                            <td>
                                <#if config.valueType==0>
                                    <a href="<@spring.url '/apps/${app.id}/config/file/${env.name}/${(config.profile.name)}/${config.name}' />">打开</a>
                                <#else>
                                ${config.value}
                                </#if>
                            </td>
                            <td>
                            ${config.description}
                            </td>
                            <td class="author">${config.creator}</td>
                            <td class="time">
                            ${config.createTime?datetime}
                            </td>
                            <td class="author">${config.updater}</td>
                            <td class="time">
                                <#if config.updateTime?datetime gt config.createTime?datetime>
                                ${config.updateTime?datetime}
                                <#else>
                                    无
                                </#if>
                            </td>
                            <td>
                                <a href="/apps/${app.id}/commit/${config.revision?c}">${config.revision?c}</a>
                            </td>
                            <td>
                                <#if config.revision gt releaseRevision>
                                    <a href="/apps/${config.appId}/config/${config.id}/diff/release"
                                       class="btn btn-sm btn-default"
                                       rel="nofollow"><span class="text-primary">未发布</span></a>
                                <#else>
                                    <span class="text-success">已发布</span>
                                </#if>
                            </td>
                            <td class="text-right action">
                                <!--启用|禁用开关 -->
                            <#--<input type="checkbox" class="switched" name="value-enable" data-app="${config.appId}"-->
                            <#--data-config="${config.id}" <#if config.enable>checked</#if>/>-->

                                <!-- 文本类型使用文件编辑器编辑，其他类型使用弹出层编辑 -->
                                <#if config.valueType==0>
                                    <a href="/apps/${app.id}/config/file/new/${env.name}/${(config.profile.name)}?copyId=${config.id}"
                                       class="glyphicon-btn tooltipped" aria-label="复制配置">
                                        <span class="glyphicon glyphicon-copy" aria-hidden="true"></span>
                                    </a>
                                    <a href="/apps/${app.id}/config/file/edit/${env.name}/${(config.profile.name)}/${config.name}"
                                       class="glyphicon-btn tooltipped" aria-label="编辑配置">
                                        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                                    </a>
                                <#else>
                                    <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                            data-target="#addConfigModal"
                                            data-app="${config.appId}"
                                            data-env-name="${env.name}"
                                            data-profile="${profile.id}"
                                            data-config="${config.id}"
                                            data-value-type="${config.valueType}"
                                            aria-label="复制配置">
                                        <span class="glyphicon glyphicon-copy" aria-hidden="true"></span>
                                    </button>
                                    <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                            data-target="#editConfigModal"
                                            data-app="${config.appId}" data-config="${config.id}"
                                            data-value-type="${config.valueType}"
                                            aria-label="编辑配置">
                                        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                                    </button>
                                </#if>

                                <!--Delete-->
                                <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                        data-target="#delConfigModal"
                                        data-app="${config.appId}" data-config="${config.id}"
                                        aria-label="删除配置">
                                    <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                </button>

                                <!--History-->
                                <a href="/apps/${app.id}/commits/${env.name}/?profileId=${config.profileId}&configName=${config.name}"
                                   class="btn btn-sm btn-default tooltipped" aria-label="历史" rel="nofollow"><i
                                        class="fa fa-history"></i></a>
                            </td>
                        </tr>
                    </#list>
                </#if>
            </table>


            <div class="panel-footer">
                <#if latestCommit??>
                    <div class="text-right">
                    ${latestCommit.author} 于 ${latestCommit.date?datetime} 提交版本 <a
                            href="/apps/${latestCommit.appId}/commits/${latestCommit.envName}?revision=${latestCommit.revision?c}">#${latestCommit.revision?c}</a>
                        &emsp;
                    ${latestCommit.message}
                    </div>
                </#if>
            </div><!-- /.panel-footer -->
        </div><!-- /.panel -->

        <!-- 分页 -->
        <#include "../pagination.ftl">

    </div>

        <#include "config_add.ftl">
        <#include "config_edit.ftl">
        <#include "config_del.ftl">

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
