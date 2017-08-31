<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>修改发布单</title>

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

        <div class="panel panel-default">
            <div class="panel-heading">
                建立发布单
            </div><!-- /.panel-heading -->


            <div class="panel-body">
                <div class="container-fluid">
                    <form class="form-horizontal" action="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}"
                          method="post"
                          data-remote="true"
                          data-method="put"
                          data-location="/apps/${releaseForm.appId}/releaseform/${releaseForm.id}/detail">
                        <input type="hidden" name="_method" value="put"/>
                        <div class="form-group">
                            <label for="name" class="col-lg-1">环境</label>
                            <p class="form-control-static">${releaseForm.envName}</p>
                        </div>
                        <div class="form-group">
                            <label for="name">发布单名称</label>
                            <input type="text" id="name" class="form-control" name="name" value="${releaseForm.name}"
                                   aria-describedby="helpBlock">
                        </div>
                        <div class="form-group">
                            <label for="planPubTime">计划发布时间</label>
                            <input type="text" id="planPubTime" class="form-control" name="planPubTime"
                                   data-date-format="yyyy-mm-dd hh:ii:ss"
                                   value="${releaseForm.planPubTime?string('yyyy-MM-dd HH:mm:ss')}"
                                   aria-describedby="helpBlock">
                        </div>
                        <div class="form-group">
                            <label for="remark" class="control-label">备注</label>
                    <textarea id="remark" name="remark" class="form-control" rows="3"
                              aria-describedby="helpBlock">${releaseForm.remark}</textarea>
                        </div>
                        <div class="form-group">
                            <button type="submit" class="btn btn-primary">保存</button>
                            <a href="/apps/${app.id}/releaseforms/${releaseForm.envName}" class="btn btn-default"
                               rel="nofollow">取消</a>
                        </div>
                    </form><!-- /.form -->
                </div>
            </div><!-- /.panel-body -->

        </div><!-- /.panel -->

    </div><!-- /.container-fluid -->

    </#escape>
</#compress>

<script src="/static/js/common.js"></script>
<script src="/static/js/release.js"></script>
</body>

</html>
