/**
 * bootstrap datetimepicker初始化
 */
$(function () {
    if (typeof ($.fn.datetimepicker) === 'function') {
        $("input[type=text][data-date-format]").datetimepicker();
    }
});


/**
 * bootstrap icheck初始化
 */
$(function () {
    if (typeof ($.fn.iCheck) === 'function') {
        $("input[type='checkbox'].ichecked").iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '5%'
        });
    }
});


/**
 * bootstrap popover初始化
 *
 * 由于性能的原因，工具提示和弹出框的 data 编程接口（data api）是必须要手动初始化的。
 * 在一个页面上一次性初始化所有弹出框的方式是通过 data-toggle 属性选中他们：
 */
$(function () {
    $('[data-toggle="popover"]').popover(
        {
            html: true,
            content: function () {
                var contentId = $(this).data("content-id");
                return $(contentId).html();
            },
        });
})


/**
 * bootstrap tooltip初始化
 */
$(function () {
    $('.tooltipped').tooltip(
        {
            html: true,
            title: function () {
                return $(this).attr("aria-label");
            },
        });
})


/**
 * 面板折叠，处理三角图标
 *
 */
$(function () {
    $("[data-toggle='collapse']").each(function () {
        var target = $(this).data("target");
        var button = $(this);

        $(target).on('show.bs.collapse', function (event) {
            button.find('.glyphicon-triangle-bottom').removeClass('glyphicon-triangle-bottom').addClass('glyphicon-triangle-top');
        })

        $(target).on('hide.bs.collapse', function (event) {
            button.find('.glyphicon-triangle-top').removeClass('glyphicon-triangle-top').addClass('glyphicon-triangle-bottom');
        })
    });
})


/**
 * 提交表单
 *
 * @param form
 */
function submitForm(form) {
    $(form).submit();
}

/**
 * 表单提交拦截
 */
$(function () {
    $('form').on('submit', function (event) {
        event.preventDefault(); // 阻止默认事件，表单将不会提交到服务器
        var $form = $(this);
        interceptFormSubmit($form);
    })
})

/**
 * 拦截表单提交
 *
 */
function interceptFormSubmit($form) {
    var remote = $form.data("remote");
    console.log("data-remote=" + remote);
    if (remote) {
        ajaxSubmitForm($form);
    } else {
        normalSubmitForm($form);
    }
}

/**
 * Ajax提交表单
 *
 * @param $form
 */
function ajaxSubmitForm($form) {
    var action = $form.attr("action");
    var method = $form.data("method");
    var location = $form.data("location");

    var data = $form.serializeArray();
    if (typeof(method) != "undefined" && method.length > 0) {
        data.push({"name": "_method", "value": method});
    }

    var beforeSend = $form.data("before");
    if (typeof(beforeSend) != "undefined" && beforeSend.length > 0) {
        window[beforeSend]($form, data);
    }

    console.log("Ajax submit, action=" + action + ", method=" + method + ", location=" + location);
    console.log(data);

    $.ajax(action, {
        type: "POST",
        data: data,
        dataType: "json",
        timeout: 10000,
        beforeSend: function (jqXHR, settings) {
            $form.find(":submit").attr("disabled", "disabled"); // 禁用提交按钮
        },
        success: function (data, textStatus, jqXHR) {
            if (typeof(data.code) != "undefined") { // 返回错误信息
                handleErrorResponse(data);
            } else {
                $form.trigger("ajax:success", [data]);
                if (typeof(location) != "undefined" && location.length > 0) {
                    window.location.href = location;
                }
            }
        },
        error: function (jqXHR, textStatus, errorThrown) { // textStatus: null, "timeout", "error", "abort" ，和 "parsererror"
            $form.trigger("ajax:error");
            handleErrorAjax(jqXHR, textStatus, errorThrown);
        },
        complete: function (jqXHR, textStatus) {
            $form.find(":submit").removeAttr("disabled"); // 启用提交按钮
        }
    });
}


/**
 * 处理Ajax请求错误
 *
 * @param jqXHR
 * @param textStatus
 * @param errorThrown
 */
function handleErrorAjax(jqXHR, textStatus, errorThrown) {
    console.log("Ajax error, textStatus=" + textStatus + ", errorThrown=" + errorThrown);
    var msg = "请求错误，请稍后重试";
    if (textStatus === 'timeout') { // 超时
        msg = "请求超时，请稍后重试";
    }

    $("#error-modal").find(".js-error-msg").text(msg);
    $("#error-modal").modal({"backdrop": "static", "keyboard": false});
}

/**
 * 处理服务器返回的错误信息
 *
 * @param data
 */
function handleErrorResponse(data) {
    console.log("Ajax success, data=" + data);
    $("#error-modal").find(".js-error-msg").text(data.msg);
    $("#error-modal").modal({"backdrop": "static", "keyboard": false});
}

/**
 * 正常提交表单
 *
 * @param $form
 */
function normalSubmitForm($form) {

    // 过滤掉空的表单字段
    $form.find('input').each(function () {
        if ($(this).val().length == 0) this.disabled = 'disabled'; // 含有disabled属性的表单字段将不会被提交
    });
    $form.find('textarea').each(function () {
        if ($(this).val().length == 0) this.disabled = 'disabled'; // 含有disabled属性的表单字段将不会被提交
    });
    $form.find('select').each(function () {
        if ($(this).val().length == 0) this.disabled = 'disabled'; // 含有disabled属性的表单字段将不会被提交
    });

    // 替换form action中的占位符，action可引用表单中的字段来动态构造action
    var action = $form.attr("action");
    var fields = $form.serializeArray();
    $.each(fields, function (i, field) {
        if (action.indexOf("{" + field.name + "}") > -1) {
            action = action.replace("{" + field.name + "}", field.value);
            $form.find("input[name=" + field.name + "]").each(function () {
                this.disabled = 'disabled'; // 含有disabled属性的表单字段将不会被提交
            });
            $form.find("select[name=" + field.name + "]").each(function () {
                this.disabled = 'disabled'; // 含有disabled属性的表单字段将不会被提交
            });
        }
    });
    $form.attr("action", action);
    console.log("Form submit, action=" + action);
    $form[0].submit();
}




