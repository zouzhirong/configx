<#import "/spring.ftl" as spring />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">

    <title>用户列表</title>

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

        <div class="panel panel-default">
            <div class="panel-heading">
                <div class="row">
                    <label class="col-lg-2">用户列表</label>
                    <div class="col-lg-2 col-lg-push-8 text-right">
                        <!-- Button trigger modal -->
                        <button type="button" class="btn btn-sm btn-default" data-toggle="modal"
                                data-target="#addUserModal">
                            创建用户
                        </button>
                    </div>
                </div>
            </div>

            <table class="table table-hover">
                <tr>
                    <th>用户ID</th>
                    <th>姓名</th>
                    <th>邮箱</th>
                    <th>是否是管理员</th>
                    <th class="action text-right">操作</th>
                </tr>
                <#if page.content??>
                    <#list page.content as user>
                        <tr>
                            <td class="id">
                            ${user.id}
                            </td>
                            <td class="name">
                            ${user.name}
                            </td>
                            <td>
                            ${user.email}
                            </td>
                            <td>
                                <#if user.admin>
                                    <span class="text-success">是</span>
                                    <span class="glyphicon glyphicon glyphicon-ok" aria-hidden="true"></span>
                                <#else>
                                    <span class="text-success">否</span>
                                    <span class="glyphicon glyphicon glyphicon-remove" aria-hidden="true"></span>
                                </#if>
                            </td>
                            <td class="text-right">
                                <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                        data-target="#editUserModal" data-user="${user.id}"
                                        aria-label="编辑用户">
                                    <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                                </button>
                                <button type="button" class="glyphicon-btn tooltipped" data-toggle="modal"
                                        data-target="#delUserModal" data-user="${user.id}" aria-label="删除用户">
                                    <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                                </button>
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

        <#include "user_add.ftl">
        <#include "user_edit.ftl">
        <#include "user_del.ftl">

    </#escape>
</#compress>
<script>
    pageCount =${page.pageCount};
    totalElements =${page.totalElements};
</script>
<script src="/static/js/common.js"></script>
<script src="/static/js/user.js"></script>
<script src="/libs/twbs-pagination/jquery.twbsPagination.min.js"></script>
<script src="/static/js/pagination.js"></script>
</body>

</html>
