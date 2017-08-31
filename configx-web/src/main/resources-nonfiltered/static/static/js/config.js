/**
 * bootstrap switch初始化
 */
$(function () {
    $("input[name='value-enable'].switched").bootstrapSwitch({
        onColor: 'success',
        offColor: 'danger',
        onText: '启用',
        offText: '禁用',
        onSwitchChange: function (event, state) {
            console.log(this); // DOM element
            console.log(event); // jQuery event
            console.log(state); // true | false

            var appId = $(this).data("app");
            var configId = $(this).data('config');
            console.log(configId);

            $.post("/apps/" + appId + "/configitem/" + configId + "/enable", {"_method": "PUT", "enable": state},
                function (response) {
                });
        }
    });
});


/**
 * 根据value type设置input
 *
 * @param input
 * @param valueType
 */
function setInput(input, valueType) {
    // 数字类型
    if (valueType == "2") {
        input.attr('type', 'number');
    }

    // 布尔类型
    if (valueType == "3") {
        // input.attr('type', 'checkbox');
    }

    // 日期类型
    if (valueType == "4") {
        input.datetimepicker({
            format: 'yyyy-mm-dd'
        });
    }
    // 时间类型
    if (valueType == "5") {
        input.datetimepicker({
            format: 'hh:ii:ss'
        });
    }
    // 日期时间类型
    if (valueType == "6") {
        input.datetimepicker({
            format: 'yyyy-mm-dd hh:ii:ss'
        });
    }
}


/**
 * 重置input
 *
 * @param input
 */
function resetInput(input) {
    // 销毁iSwitch
    input.bootstrapSwitch('destroy');

    // 移除日期时间选择器
    input.datetimepicker('remove');

    // 重置配置值input
    input.attr('type', 'text');
    input.addClass('form-control');
    input.val("");
}


/**
 * 添加环境模态框
 */
$('#addConfigModal').on('show.bs.modal', function (event) {
    // Bootstrap show.bs.modal / Datepicker show.bs.modal conflict
    // https://github.com/uxsolutions/bootstrap-datepicker/issues/978
    if (event.namespace !== 'bs.modal') {
        return;
    }

    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes
    var envName = button.data('env-name');
    var profileId = button.data("profile");
    var copyId = button.data("config");
    var valueType = button.data("value-type");

    // 根据配置值类型设置输入框
    setInput($("input[name='value']"), valueType);

    var modal = $(this);

    modal.find('.modal-body form').attr("action", "/apps/" + appId + "/config");
    modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/config/" + envName + "?profileId=" + profileId);

    modal.find('.modal-body input[name=valueType]').val(valueType);
    modal.find('.modal-body input[name=name]').val("");
    modal.find('.modal-body input[name=value]').val("");
    modal.find('.modal-body select[name=tagId]').val([]);
    modal.find('.modal-body textarea[name=description]').val("");
    modal.find('.modal-body textarea[name=message]').val("");


    if (typeof(copyId) != "undefined") {
        $.getJSON("/apps/" + appId + "/configitem/" + copyId, function (config) {
            modal.find('.modal-body select[name=profileId]').val(config.profileId);
            modal.find('.modal-body input[name=name]').val(config.name);
            modal.find('.modal-body input[name=value]').val(config.value);
            if (typeof(config.tagIdList) != "undefined") {
                modal.find('.modal-body select[name=tagId]').val(config.tagIdList);
            }
            modal.find('.modal-body textarea[name=description]').val(config.description);
        });
    }

})
$('#addConfigModal').on('hidden.bs.modal', function (event) {
    resetInput($("input[name='value']"));
})


/**
 * 修改配置
 */
$('#editConfigModal').on('show.bs.modal', function (event) {
    // Bootstrap show.bs.modal / Datepicker show.bs.modal conflict
    // https://github.com/uxsolutions/bootstrap-datepicker/issues/978
    if (event.namespace !== 'bs.modal') {
        return;
    }

    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data("app");
    var configId = button.data("config");
    var valueType = button.data("value-type");

    // 根据配置值类型设置输入框
    setInput($("input[name='value']"), valueType);

    var modal = $(this);

    $.getJSON("/apps/" + appId + "/configitem/" + configId, function (config) {
        modal.find('.modal-body form').attr("action", "/apps/" + appId + "/configitem/" + configId);
        modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/config/" + config.envName + "?profileId=" + config.profileId);

        modal.find('.modal-body select[name=profileId]').val(config.profileId);
        modal.find('.modal-body input[name=name]').val(config.name);
        modal.find('.modal-body input[name=value]').val(config.value);
        if (typeof(config.tagIdList) != "undefined") {
            modal.find('.modal-body select[name=tagId]').val(config.tagIdList);
        }
        modal.find('.modal-body textarea[name=description]').val(config.description);
    });
})
$('#editConfigModal').on('hidden.bs.modal', function (event) {
    resetInput($("input[name='value']"));
})


/**
 * 删除配置模态框
 */
$('#delConfigModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget) // Button that triggered the modal
    var appId = button.data("app");
    var configId = button.data('config') // Extract info from data-* attributes

    var modal = $(this);

    $.getJSON("/apps/" + appId + "/configitem/" + configId, function (config) {
        modal.find('.modal-body form').attr("action", "/apps/" + appId + "/configitem/" + configId);

        modal.find('.modal-body p[name=config-name]').text(config.name);
        modal.find('.modal-body textarea[name=message]').val("");

        // $("#delConfigModal").modal('hide');
        // $("#tr-config-" + configId).remove();
    });
})
$('#delConfigModal').on('hidden.bs.modal', function (event) {
    resetInput($("input[name='value']"));
})

$("#delConfigModal").find("form").on("ajax:success", function (event, config) {
    $("#delConfigModal").modal('hide');
    $("#tr-config-" + config.id).remove();
});





