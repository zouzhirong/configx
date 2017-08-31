var editor

$(function () {
    var appId = $("#editor").data("app");
    var configId = $("#editor").data("config");
    var filename = $("#editor").data("filename");
    var readonly = $("#editor").data("readonly");

    editor = newFileEditor("editor", filename, readonly, onsave);

    if (configId > 0) {
        $.getJSON("/apps/" + appId + "/configitem/" + configId, {},
            function (config) {
                editor.setValue(config.value);
            }
        );
    }
});


$(function () {
    var theme = editor.getTheme();
    $('select.js-file-theme').val(theme.split("/")[2]);

    var mode = editor.session.$modeId;
    $('select.js-file-mode').val(mode.split("/")[2]);

    var fontSize = editor.getFontSize();
    $('select.js-file-font-size').val(fontSize);

    var wrapMode = editor.getSession().getUseWrapMode();
    if (wrapMode) {
        $('select.js-file-wrap-mode').val("on");
    } else {
        $('select.js-file-wrap-mode').val("off");
    }
});


/**
 * 修改editor Theme
 */
$('select.js-file-theme').on("change", function () {
    var theme = $(this).val();
    editor.setTheme("ace/theme/" + theme);
});


/**
 * 修改editor Mode
 */
$('select.js-file-mode').on("change", function () {
    var mode = $(this).val();
    editor.session.setMode("ace/mode/" + mode);
});


/**
 * 修改editor Font size
 */
$('select.js-file-font-size').on("change", function () {
    var fontSize = $(this).val();
    editor.setFontSize(parseInt(fontSize));
});


/**
 * 修改editor Wrap mode
 */
$('select.js-file-wrap-mode').on("change", function () {
    var wrapMode = $(this).val();
    editor.getSession().setUseWrapMode("on" == wrapMode);
});


/**
 * 文件创建成功后处理
 */
$("form.js-file-form").on("ajax:success", function (event, config) {
    window.location.href = "/apps/" + config.appId + "/config/file/" + config.envName + "/" + config.profileName + "/" + config.name;
});


/**
 * 文件保存
 *
 * @param editor
 */
function onsave(editor) {
    $("form.js-file-form :submit").click();
}


/**
 * 文件操作发送ajax之前，将编辑器中的内容添加到ajax data中
 *
 * @param $form
 * @param data
 */
function ajaxBeforeSend($form, data) {
    var value = editor.getValue();
    data.push({"name": "value", "value": value});
}


/**
 * 删除文件模态框
 */
$('#delFileOptionsModal').on('show.bs.modal', function (event) {
    var $button = $(event.relatedTarget) // Button that triggered the modal
    var appId = $button.data("app");
    var configId = $button.data('config') // Extract info from data-* attributes

    var modal = $(this)

    $.getJSON("/apps/" + appId + "/configitem/" + configId, function (config) {
        modal.find('.modal-body form').attr("action", "/apps/" + appId + "/configitem/" + configId);
        modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/config/" + config.envName);

        modal.find('.modal-body p[name=config-name]').text(config.name);
    });

})