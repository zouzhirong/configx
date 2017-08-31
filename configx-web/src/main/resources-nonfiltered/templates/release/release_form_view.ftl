<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>发布单 - ${releaseForm.name}</title>

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

        <div class="panel panel-info">
            <div class="panel-heading">
                <div class="row">
                    <label class="col-lg-2">发布单信息</label>
                    <div class="col-lg-2 col-lg-push-8 text-right">
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
                    </div>
                </div>

            </div><!-- /.panel-heading -->

            <table class="table table-bordered">
                <tr>
                    <th>发布单ID</th>
                    <td>${releaseForm.id}</td>
                    <th>发布单名称</th>
                    <td>${releaseForm.name}</td>
                </tr>
                <tr>
                    <th>应用</th>
                    <td>${releaseForm.appName}</td>
                    <th>环境</th>
                    <td>${releaseForm.envName}</td>
                </tr>
                <tr>
                    <th>计划发布时间</th>
                    <td colspan="100">${(releaseForm.planPubTime?datetime)!}</td>
                </tr>
                <tr>
                    <th>审核状态</th>
                    <td>
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
                    <th>审核人</th>
                    <td colspan="100">${releaseForm.auditor} (${(releaseForm.auditTime?datetime)!})</td>
                </tr>
                <tr>
                    <th>创建人</th>
                    <td>${releaseForm.creator}</td>
                    <th>创建时间</th>
                    <td>${releaseForm.createTime?datetime}</td>
                </tr>
                <tr>
                    <th>修改人</th>
                    <td>${releaseForm.updater}</td>
                    <th>修改时间</th>
                    <td colspan="100">
                        <#if releaseForm.updateTime?datetime gt releaseForm.createTime?datetime>
                        ${releaseForm.updateTime?datetime}
                        <#else>
                            无
                        </#if>
                    </td>
                </tr>
                <tr>
                    <th>备注</th>
                    <td colspan="100">${(releaseForm.remark)!}</td>
                </tr>
            </table>

            <div class="panel-footer">
                    <#if releaseForm.auditStatus==1><!-- 编辑中-->
                    <div class="row">
                        <div class="col-lg-12 text-right">
                            <form class="form-inline"
                                  action="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/submit"
                                  method="post"
                                  data-remote="true" data-method="post"
                                  data-location="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/detail">
                                <button type="submit" class="btn btn-primary">提交审核</button>
                            </form>
                        </div>
                    </div>
                </#if>
                    <#if releaseForm.auditStatus==2><!-- 待审核-->
                    <#if isAppAdmin>
                        <div class="row">
                            <div class="col-lg-1 col-md-offset-10 text-right">
                                <form
                                        action="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/audit?passed=true"
                                        method="post"
                                        data-remote="true" data-method="post"
                                        data-location="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/detail">
                                    <button type="submit" class="btn btn-success">审核通过</button>
                                </form>
                            </div>
                            <div class="col-lg-1 text-right">
                                <form
                                        action="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/audit?passed=false"
                                        method="post"
                                        data-remote="true" data-method="post"
                                        data-location="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/detail">
                                    <button type="submit" class="btn btn-warning">驳回</button>
                                </form>
                            </div>
                        </div>
                    </#if>
                </#if>

            </div><!-- /.panel-footer -->

        </div><!-- /.panel -->


        <div class="panel panel-default">
            <div class="panel-heading">
                发布信息
            </div><!-- /.panel-heading -->

            <#if releaseForm.releaseId==0>
                <h4>没有发布信息
                    <small>发布单还未发布！</small>
                </h4>
            <#else>
                <table class="table table-bordered">
                    <tr class="info">
                        <th>发布ID</th>
                        <td>${release.id}</td>
                        <th>发布状态</th>
                        <td>
                            <#if release.releaseStatus== 1> <!--发布中-->
                                <span name="spinner" data-app="${releaseForm.appId}"
                                      data-release-form="${releaseForm.id}"><img alt=""
                                                                                 src="/static/img/spinner.gif"></span>
                                <span class="text-warning">发布中</span>
                            </#if>
                            <#if release.releaseStatus== 2> <!--发布成功-->
                                <span class="glyphicon glyphicon glyphicon-ok" aria-hidden="true"></span>
                                <span class="text-success">发布成功</span>
                            </#if>
                            <#if release.releaseStatus== 3> <!--发布失败-->
                                <span class="glyphicon glyphicon glyphicon-remove" aria-hidden="true"></span>
                                <span class="text-danger">发布失败</span>
                            </#if>
                            <#if release.releaseStatus==4> <!--回滚中-->
                                <span name="spinner" data-app="${releaseForm.appId}"
                                      data-release-form="${releaseForm.id}"><img alt=""
                                                                                 src="/static/img/spinner.gif"></span>
                                <span class="text-warning">${statusText}</span>
                            </#if>
                            <#if release.releaseStatus==5> <!--回滚成功-->
                                <span class="glyphicon glyphicon glyphicon-ok" aria-hidden="true"></span>
                                <span class="text-success">回滚成功</span>
                            </#if>
                            <#if release.releaseStatus==6> <!--回滚失败-->
                                <span class="glyphicon glyphicon glyphicon-remove" aria-hidden="true"></span>
                                <span class="text-danger">回滚失败</span>
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="100"></td>
                    </tr>
                    <tr class="success">
                        <th>发布人</th>
                        <td>${release.releaseUserCode}</td>
                        <th>发布人IP</th>
                        <td colspan="100">${release.releaseIp}</td>
                    </tr>
                    <tr class="success">
                        <th>发布的构建ID</th>
                        <td>${release.releaseBuildId}</td>
                        <th>发布时间</th>
                        <td colspan="100">${release.releaseTime?datetime}</td>
                    </tr>
                    <tr class="success">
                        <th>发布备注</th>
                        <td colspan="100">${(release.releaseMessage)!}</td>
                    </tr>
                    <tr>
                        <td colspan="100"></td>
                    </tr>
                    <tr class="danger">
                        <th>回滚人</th>
                        <td>${release.rollbackUserCode}</td>
                        <th>回滚人IP</th>
                        <td colspan="100">${release.rollbackIp}</td>
                    </tr>
                    <tr class="danger">
                        <th>回滚到的构建ID</th>
                        <td>${release.rollbackBuildId}</td>
                        <th>回滚时间</th>
                        <td colspan="100">
                            <#if release.rollbackTime?datetime gt release.releaseTime?datetime>
                            ${release.rollbackTime?datetime}
                            <#else>
                                无
                            </#if>
                        </td>
                    </tr>
                    <tr class="danger">
                        <th>回滚备注</th>
                        <td colspan="100">${(release.rollbackMessage)!}</td>
                    </tr>
                </table>
            </#if>

            <div class="panel-footer">
                <div class="row">
                    <div class="col-lg-2 col-lg-push-10 text-right">
                            <#if releaseForm.auditStatus==3 && releaseForm.releaseId==0><!--发布单审核通过，并且未发布过-->
                            <form class="form-inline"
                                  action="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/release"
                                  method="post"
                                  data-remote="true" data-method="post"
                                  data-location="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/detail">
                                <button type="submit" class="btn btn-primary">发布</button>
                            </form>
                        </#if>
                            <#if ((release.releaseStatus)!0)==2><!--发布完成-->
                            <form class="form-inline"
                                  action="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/rollback"
                                  method="post"
                                  data-remote="true" data-method="post"
                                  data-location="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/detail">
                                <button type="submit" class="btn btn-danger">回滚</button>
                            </form>
                        </#if>
                    </div>
                </div>
            </div><!-- /.panel-footer -->

        </div><!-- /.panel -->

    </div><!-- /.container-fluid -->

        <#include "release_form_del.ftl">

    </#escape>
</#compress>

<script src="/static/js/common.js"></script>
<script src="/static/js/release.js"></script>
</body>

</html>
