<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>Webhook日志详细信息</title>

    <script src="/libs/jquery/jquery-2.1.4.min.js"></script>

    <script src="/libs/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/libs/bootstrap/css/bootstrap.min.css"/>

    <!-- jsonview -->
    <link rel="stylesheet" href="/libs/jsonview/jquery.jsonview.css"/>
    <script type="text/javascript" src="/libs/jsonview/jquery.jsonview.js"></script>

    <link rel="stylesheet" href="/static/css/config.css">

    <style>
        .highlight {
            padding: 9px 14px;
            margin-bottom: 14px;
            background-color: #f7f7f9;
            border: 1px solid #e1e1e8;
            border-radius: 4px;
        }

        .highlight pre {
            padding: 0;
            margin-top: 0;
            margin-bottom: 0;
            word-break: normal;
            white-space: nowrap;
            background-color: transparent;
            border: 0;
        }
    </style>
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
                <div class="row">
                    <label class="col-lg-2">Webhook信息</label>
                </div>
            </div>

            <table class="table table-bordered">
                <tr>
                    <td width="110px"><span class="text-muted">钩子名称</span></td>
                    <td><span>${webhookLog.name}</span></td>
                    <td width="300"><span class="text-muted">应用名称：</span><span>${webhookLog.appName}</span></td>
                    <td width="250"><span class="text-muted">创建时间：</span><span>${webhookLog.createTime?datetime}</span>
                    </td>
                </tr>
                <tr>
                    <td><span class="text-muted">事件类型</span></td>
                    <td colspan="100"><span>${webhookLog.eventName}</span></td>
                </tr>
                <tr>
                    <td><span class="text-muted">事件参数</span></td>
                    <td colspan="100">
                    <textarea class="hidden" id="textarea-event-params">
                    ${webhookLog.eventParams}
                    </textarea>
                        <span id="event-params"></span>
                        <script>
                            $("#event-params").JSONView($("#textarea-event-params").val());
                        </script>
                    </td>
                </tr>
                <tr>
                    <td><span class="text-muted">请求地址</span></td>
                    <td colspan="100"><span>${webhookLog.url}</span></td>
                </tr>
                <tr>
                    <td><span class="text-muted">内容类型</span></td>
                    <td colspan="100"><span>${webhookLog.contentType}</span>
                    </td>
                </tr>
                <tr>
                    <td><span class="text-muted">请求参数</span></td>
                    <td colspan="100">
                    <textarea class="hidden" id="textarea-request-params">
                    ${webhookLog.requestParams}
                    </textarea>
                        <span id="request-params"></span>
                        <script>
                            $("#request-params").JSONView($("#textarea-request-params").val());
                        </script>
                    </td>
                </tr>
                <tr>
                    <td><span class="text-muted">Secret：</span></td>
                    <td colspan="100"><span class="text-muted">Secret Token：</span><span>${webhookLog.secret}</span>
                    </td>
                </tr>
            </table>

        </div><!-- /.panel -->

        <ul class="nav nav-tabs">
            <li role="presentation" class="active"><a href="#request" data-toggle="tab">请求信息</a></li>
            <li role="presentation"><a href="#response" data-toggle="tab">响应信息 ${webhookLog.statusCode}</a></li>
            <li role="presentation"><a href="#exception" data-toggle="tab">异常信息</a></li>
        </ul>

        <div class="tab-content">
            <div class="tab-pane fade in active" id="request">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <h4>请求头</h4>
                        <textarea class="hidden" id="textarea-request-headers">${webhookLog.requestHeaders}</textarea>
                        <figure class="highlight">
                                <pre>
                                    <code class="language-html" data-lang="html">
                                        <span id="request-headers"></span>
                                    </code>
                                </pre>
                        </figure>
                        <script>
                            $("#request-headers").JSONView($("#textarea-request-headers").val());
                        </script>

                        <h4>请求体</h4>
                        <figure class="highlight">
                                <pre>
                                    <code class="language-html" data-lang="html">
                                        <span>${webhookLog.requestBody}</span>
                                    </code>
                                </pre>
                        </figure>
                    </div>
                </div><!-- /.panel -->
            </div>
            <div class="tab-pane fade" id="response">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <h4>响应头</h4>
                        <textarea class="hidden" id="textarea-response-headers">${webhookLog.responseHeaders}</textarea>
                        <figure class="highlight">
                                <pre>
                                    <code class="language-html" data-lang="html">
                                        <span id="response-headers"></span>
                                    </code>
                                </pre>
                        </figure>
                        <script>
                            $("#response-headers").JSONView($("#textarea-response-headers").val());
                        </script>

                        <h4>响应体</h4>
                        <figure class="highlight">
                                <pre>
                                    <code class="language-html" data-lang="html">
                                        <span>${webhookLog.responseBody}</span>
                                    </code>
                                </pre>
                        </figure>
                    </div>
                </div><!-- /.panel -->
            </div>
            <div class="tab-pane fade" id="exception">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <h5 class="lead">Post请求异常</h5>
                        <figure class="highlight">
                                <pre>
                                    <code class="language-html" data-lang="html">
                                        <span>${webhookLog.errorMsg}</span>
                                    </code>
                                </pre>
                        </figure>
                    </div>
                </div><!-- /.panel -->
            </div>
        </div>


    </div>

    </#escape>
</#compress>
</body>

</html>
