<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>发布单列表</title>

    <script src="/libs/jquery/jquery-2.1.4.min.js"></script>

    <script src="/libs/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/libs/bootstrap/css/bootstrap.min.css"/>

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
                <form class="form-horizontal" action="/apps/${app.id}/releaseforms/{envName}">
                    <div class="form-group">
                        <label for="envId" class="col-md-1 control-label">环境</label>
                        <div class="col-md-2">
                            <select name="envName" class="form-control" onchange="submitForm(this.form)">
                                <#list envList as env>
                                    <option value="${env.name}"
                                            <#if ((form.envId)!0)==env.id>selected</#if>>${env.name}</option>
                                </#list>
                            </select>
                        </div>
                        <label for="createDate" class="col-md-1 control-label">创建日期</label>
                        <div class="col-md-1">
                            <input type="text" class="form-control" id="createDate" name="createDate"
                                   data-date-format="yyyy-mm-dd" value="${(form.createDate)!}">
                        </div>
                        <label for="releaseDate" class="col-md-1 control-label">发布日期</label>
                        <div class="col-md-1">
                            <input type="text" class="form-control" id="releaseDate" name="releaseDate"
                                   data-date-format="yyyy-mm-dd" value="${(form.releaseDate)!}">
                        </div>
                        <div class="col-md-1 text-right">
                            <button type="submit" class="btn btn-primary btn-block">查询</button>
                        </div>
                        <div class="col-md-1 text-right">
                            <a href="/apps/${app.id}/releaseforms" class="btn btn-default btn-block">重置</a>
                        </div>
                    </div>
                </form>
            </div><!-- /.panel-body -->
        </div><!-- /.panel -->

        <div class="panel panel-default">
            <div class="panel-heading">
                <div class="row">
                    <label class="col-lg-2"></label>
                    <div class="col-lg-2 col-lg-push-8 text-right">
                        <a href="/apps/${app.id}/releaseform/new" class="btn btn-sm btn-default"
                           rel="nofollow">建立发布单</a>
                    </div>
                </div>
            </div>

            <table class="table table-hover">
                <tr>
                    <th>发布单ID</th>
                    <th>应用</th>
                    <th>环境</th>
                    <th>发布单名称</th>
                    <th>备注</th>
                    <th>创建人</th>
                    <th>创建时间</th>
                    <th>计划发布时间</th>
                    <th>审核人</th>
                    <th>审核状态</th>
                    <th>审核时间</th>
                    <th>发布状态</th>
                    <th>发布版本号</th>
                    <th>发布人</th>
                    <th>发布时间</th>
                    <th>回滚人</th>
                    <th>回滚时间</th>
                    <th class="action text-right">操作</th>
                </tr>
                <#if page.content??>
                    <#list page.content as releaseForm>
                        <tr id="tr-form-${releaseForm.id}">
                            <td class="id">
                            ${releaseForm.id}
                            </td>
                            <td>
                            ${releaseForm.appName}
                            </td>
                            <td>
                            ${releaseForm.envName}
                            </td>
                            <td>
                                <a href="/apps/${app.id}/releaseform/${releaseForm.id}/detail">${releaseForm.name}</a>
                            </td>
                            <td>
                            ${releaseForm.remark}
                            </td>
                            <td class="author">
                            ${releaseForm.creator}
                            </td>
                            <td class="time">
                            ${releaseForm.createTime?datetime}
                            </td>
                            <td class="time">
                                <#if releaseForm.planPubTime?datetime gt releaseForm.createTime?datetime>
                                ${releaseForm.planPubTime?datetime}
                                <#else>
                                    无
                                </#if>
                            </td>
                            <td class="author">
                            ${releaseForm.auditor}
                            </td>
                            <td class="status">
                                <#if releaseForm.auditStatus==1>
                                    <span class="glyphicon glyphicon glyphicon-pencil" aria-hidden="true"></span>
                                    <span class="text-info">编辑中</span>
                                </#if>
                                <#if releaseForm.auditStatus==2>
                                    <span class="glyphicon glyphicon glyphicon-lock" aria-hidden="true"></span>
                                    <span class="text-warning">待审核</span>
                                </#if>
                                <#if releaseForm.auditStatus==3>
                                    <span class="glyphicon glyphicon glyphicon-ok" aria-hidden="true"></span>
                                    <span class="text-success">审核通过</span>
                                </#if>
                                <#if releaseForm.auditStatus==4>
                                    <span class="glyphicon glyphicon glyphicon-remove" aria-hidden="true"></span>
                                    <span class="text-danger">驳回</span>
                                </#if>
                            </td>
                            <td class="time">
                                <#if releaseForm.auditTime?datetime gt releaseForm.createTime?datetime>
                                ${(releaseForm.auditTime?datetime)!}
                                <#else>
                                    无
                                </#if>

                            </td>
                            <td class="status">
                                <#if releaseForm.releaseStatus??>
                                    <#if releaseForm.releaseStatus== 1> <!--发布中-->
                                        <span name="spinner" data-release-form="${releaseForm.id}"><img alt=""
                                                                                                        src="/static/img/spinner.gif"></span>
                                        <span class="text-warning">发布中</span>
                                    </#if>
                                    <#if releaseForm.releaseStatus== 2> <!--发布成功-->
                                        <span class="glyphicon glyphicon glyphicon-ok" aria-hidden="true"></span>
                                        <span class="text-primary">发布成功</span>
                                    </#if>
                                    <#if releaseForm.releaseStatus== 3> <!--发布失败-->
                                        <span class="glyphicon glyphicon glyphicon-remove" aria-hidden="true"></span>
                                        <span class="text-danger">发布失败</span>
                                    </#if>
                                    <#if releaseForm.releaseStatus==4> <!--回滚中-->
                                        <span name="spinner" data-release-form="${releaseForm.id}"><img alt=""
                                                                                                        src="/static/img/spinner.gif"></span>
                                        <span class="text-warning">回滚中</span>
                                    </#if>
                                    <#if releaseForm.releaseStatus==5> <!--回滚成功-->
                                        <span class="glyphicon glyphicon glyphicon-ok" aria-hidden="true"></span>
                                        <span class="text-success">回滚成功</span>
                                    </#if>
                                    <#if releaseForm.releaseStatus==6> <!--回滚失败-->
                                        <span class="glyphicon glyphicon glyphicon-remove" aria-hidden="true"></span>
                                        <span class="text-danger">回滚失败</span>
                                    </#if>
                                </#if>
                            </td>
                            <td>
                            ${releaseForm.releaseVersionNumber}
                            </td>
                            <td>
                                <#if releaseForm.releaseStatus??>
					                ${releaseForm.releaseUserCode}
				                </#if>
                            </td>
                            <td class="time">
                                <#if releaseForm.releaseStatus??>
                                    <#if releaseForm.releaseTime?datetime gt releaseForm.auditTime?datetime>
                                    ${(releaseForm.releaseTime?datetime)!}
                                    <#else>
                                        无
                                    </#if>
                                </#if>
                            </td>
                            <td>
                                <#if releaseForm.releaseStatus??>
					                ${releaseForm.rollbackUserCode}
				                </#if>
                            </td>
                            <td class="time">
                                <#if releaseForm.releaseStatus??>
                                    <#if releaseForm.rollbackTime?datetime gt releaseForm.releaseTime?datetime>
                                    ${(releaseForm.rollbackTime?datetime)!}
                                    <#else>
                                        无
                                    </#if>
                                </#if>
                            </td>
                            <td class="action text-right action">
                                <#if releaseForm.auditStatus!=3>
                                    <a href="/apps/${app.id}/releaseform/${releaseForm.id}/edit"
                                       class="glyphicon-btn tooltipped"
                                       aria-label="编辑发布单">
                                        <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                                    </a>
                                </#if>
                                <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                        data-target="#delReleaseFormModal" data-app="${releaseForm.appId}"
                                        data-form="${releaseForm.id}"
                                        aria-label="删除发布单">
                                    <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                </button>
                                <a href="/apps/${app.id}/releaseform/${releaseForm.id}/changelist"
                                   class="btn btn-sm btn-default tooltipped" aria-label="发布单的配置更改列表">Change List</a>
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

        <#include "release_form_del.ftl">

    </#escape>
</#compress>
<script>
    pageCount =${page.pageCount};
    totalElements =${page.totalElements};
</script>
<script src="/static/js/common.js"></script>
<script src="/static/js/release.js"></script>
<script src="/libs/twbs-pagination/jquery.twbsPagination.min.js"></script>
<script src="/static/js/pagination.js"></script>
</body>

</html>
