$(function () {
    var appId = $("input[name='appId']").val();
    var revision1 = $("input[name='revision1']").val();
    var revision2 = $("input[name='revision2']").val();

    $.when(
        $.ajax("/apps/" + appId + "/config/history/" + revision1),
        $.ajax("/apps/" + appId + "/config/history/" + revision2)
    )
        .done(function (response1, response2) { // response: [data, status, jqXHR]
            var left = response1[0];
            var right = response2[0];

            var leftMode = getMode(left.configName);
            var rightMode = getMode(right.configName);
            var leftContent = left.configValue;
            var rightContent = right.configValue;

            var aceDiffer = new AceDiff({
                theme: "ace/theme/github",
                maxDiffs: "1000000",
                left: {
                    id: "left-editor",
                    content: leftContent,
                    mode: leftMode
                },
                right: {
                    id: "right-editor",
                    content: rightContent,
                    mode: rightMode
                },
                classes: {
                    gutterID: "gutter"
                }
            });

            var editors = aceDiffer.getEditors();
            var leftEditor = editors.left;
            var rightEditor = editors.right;
            configureEditor(leftEditor);
            configureEditor(rightEditor);
        });
});


/**
 * 获取Editor Mode
 *
 * @param filename
 * @returns {*}
 */
function getMode(filename) {
    //modelist detect mode based on file path
    var modelist = ace.require("ace/ext/modelist");
    if (typeof (filename) !== 'undefined') {
        var mode = modelist.getModeForPath(filename).mode;
        return mode;
    }
}

/**
 * 配置编辑器
 *
 * @param editor
 */
function configureEditor(editor) {
    // Set the font size:
    // editor.setFontSize(16);

    // normal 默认。设置合理的行间距。
    // number 设置数字，此数字会与当前的字体尺寸相乘来设置行间距。
    // length 设置固定的行间距。
    // % 基于当前字体尺寸的百分比行间距。
    // inherit 规定应该从父元素继承 line-height 属性的值。
    // editor.container.style.lineHeight = 1.0;
}

