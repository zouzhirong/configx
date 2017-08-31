<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>Webhook</title>

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

        <div class="container">

            <div class="row">
                <ol class="breadcrumb">
                    <li><a href="/apps">应用</a></li>
                    <li class="active">${app.name}</li>
                </ol>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-md-12">
                            <h5><a href="/apps/${app.id}/hooks">Webhooks</a> / Manage webhook</h5>
                        </div>
                    </div>
                </div>

                <div class="panel-body">
                    <form class="form-horizontal" action="/apps/${app.id}/hooks/edit/${webhook.id}" method="post">
                        <div class="form-group">
                            <label for="name" class="col-md-2 control-label">钩子名称</label>
                            <div class="col-md-10">
                                <input type="text" class="form-control" size="40" id="name" name="name"
                                       value="${webhook.name}" placeholder="">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="eventType" class="col-md-2 control-label">事件类型</label>
                            <div class="col-md-10">
                                <select class="form-control" id="hook_event_type" name="eventType"
                                        disabled="disabled" onchange="onEventTypeChange()">
                                    <#list eventTypes as item>
                                        <option value="${item.type}"
                                                <#if item.type==webhook.eventType.type>selected="selected"</#if>>${item.typeName}</option>
                                    </#list>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="" class="col-md-2 control-label">事件参数</label>
                            <div class="col-md-10">
                                <div class="panel panel-default">
                                    <div class="panel-body">
                                        <#list webhook.eventParams?keys as eventParamName>
                                            <div class="row">
                                                <div class="col-md-2" style="height:34px; line-height: 34px">
                                                    <p class="text-primary">${eventParamName}</p>
                                                </div>
                                                <div class="col-md-4">
                                                    <input type="text" class="form-control"
                                                           name="eventParams[${eventParamName}]"
                                                           value="${webhook.eventParams[eventParamName]}"
                                                           placeholder="">
                                                </div>
                                                <div class="col-md-6" style="height:34px; line-height: 34px">
                                                    <p class="text-muted"> ${MessageUtils.getMessage("event_type."+webhook.eventType.typeName+".event_param."+eventParamName)}</p>
                                                </div>
                                            </div>
                                            <p/>
                                        </#list>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <hr/>
                        <div class="form-group">
                            <label for="url" class="col-md-2 control-label">请求地址</label>
                            <div class="col-md-10">
                                <input type="text" class="form-control" id="url" name="url" value="${webhook.url}"
                                       placeholder="https://example.com/postreceive">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="contentType" class="col-md-2 control-label">内容类型</label>
                            <div class="col-md-10">
                                <select class="form-control" id="hook_content_type" name="contentType">
                                    <#list contentTypes as item>
                                        <option value="${item.simpleType}"
                                                <#if webhook.contentType==item.simpleType>selected="selected"</#if>>${item.fullType}</option>
                                    </#list>
                                </select>
                            </div>
                        </div>
                        </p>
                        <div class="form-group">
                            <label for="" class="col-md-2 control-label">请求参数</label>
                            <div class="col-md-10">
                                <div class="panel panel-default">
                                    <div class="panel-body">
                                        <#list webhook.requestParams?keys as requestParamName>
                                            <div class="row">
                                                <div class="col-md-2" style="height:34px; line-height: 34px">
                                                    <p class="text-primary">${requestParamName}</p>
                                                </div>
                                                <div class="col-md-4">
                                                    <input type="text" class="form-control"
                                                           name="requestParams[${requestParamName}]"
                                                           value="${webhook.requestParams[requestParamName]}"
                                                           placeholder="${requestParamName}">
                                                </div>
                                                <div class="col-md-6" style="height:34px; line-height: 34px">
                                                    <p class="text-muted"> ${MessageUtils.getMessage("event_type."+webhook.eventType.typeName+".webhook_param."+requestParamName)}</p>
                                                </div>
                                            </div>
                                            <p/>
                                        </#list>
                                        <div class="row">
                                            <div class="col-md-12">
                                                <p/>
                                                <p class="help-block">重命名请求参数名，自定义参数可直接拼接在请求地址URL后面。</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="secret" class="col-md-2 control-label">Secret Token</label>
                            <div class="col-md-10">
                                <input type="text" class="form-control" id="secret" name="secret"
                                       value="${webhook.secret}"
                                       placeholder="">
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-md-offset-2 col-md-10">
                                <button type="submit" class="btn btn-default">更新Webhook</button>
                            </div>
                        </div>
                    </form>
                </div>

            </div><!-- /.panel -->

        </div>
    </div>


    <script src="/static/js/common.js"></script>
    <script src="/static/js/app.js"></script>

    </#escape>
</#compress>

<script>
    function onEventTypeChange() {
        var eventType = $("select[name=eventType]").val();
        window.location.href = "/apps/${app.id}/hooks/new?eventType=" + eventType;
    }
</script>

</body>

</html>
