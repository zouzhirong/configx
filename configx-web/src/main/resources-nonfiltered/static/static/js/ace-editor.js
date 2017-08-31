/**
 * 创建文件编辑器
 *
 * @param editorId  editor ID
 * @param saveCallback  保存回调函数
 * @returns {*}
 */
function newFileEditor(editorId, filename, readonly, saveCallback) {
    var editor = ace.edit(editorId);

    // theme
    editor.setTheme("ace/theme/github");

    //modelist detect mode based on file path
    var modelist = ace.require("ace/ext/modelist");
    if (typeof (filename) !== 'undefined') {
        var mode = modelist.getModeForPath(filename).mode;
        editor.session.setMode(mode);
    }

    // trigger extension
    ace.require("ace/ext/chromevox");

    // Generates a popup menu with current keybindings
    ace.config.loadModule("ace/ext/keybinding_menu", function (module) {
        module.init(editor);
    })

    // auto height
    editor.setAutoScrollEditorIntoView(true);
    editor.setOption("maxLines", 30);
    editor.setOption("minLines", 20);

    editor.setOption("selectionStyle", "wide");

    // Syntax validation
    editor.session.setOption("useWorker", true);

    // Use soft tabs:
    editor.getSession().setUseSoftTabs(true);

    // Set the default tab size:
    editor.getSession().setTabSize(4);

    // Set the font size:
    editor.setFontSize(16);

    // normal 默认。设置合理的行间距。
    // number 设置数字，此数字会与当前的字体尺寸相乘来设置行间距。
    // length 设置固定的行间距。
    // % 基于当前字体尺寸的百分比行间距。
    // inherit 规定应该从父元素继承 line-height 属性的值。
    editor.container.style.lineHeight = 1.5;

    // Toggle word wrapping:
    editor.getSession().setUseWrapMode(true);

    // Set line highlighting:
    editor.setHighlightActiveLine(true);

    // Set the print margin visibility:
    editor.setShowPrintMargin(false);

    // If showInvisibles is set to true, invisible characters—like spaces or new
    // lines—are show in the editor.
    editor.setShowInvisibles(false);

    // Set the editor to read-only:
    if (readonly) {
        editor.setReadOnly(true);
    }


    // To listen for an onchange:
    editor.getSession().on('change', function (e) {
    });

    // To listen for an selection change:
    editor.getSession().selection.on('changeSelection', function (e) {
    });

    // To listen for a cursor change:
    editor.getSession().selection.on('changeCursor', function (e) {
    });


    //simple status widget showing selection and keyboard handler status
    var StatusBar = ace.require("ace/ext/statusbar").StatusBar;
    // var statusBar = new StatusBar(editor, document.getElementById("statusbar"));


    //get the current mode
    var mode = editor.session.$modeId;
    var modeBar = $("#modebar");
    if (typeof (modeBar) !== 'undefined') {
        modeBar.text(mode);
    }

    // Autocompletion bindKey Ctrl-Space|Ctrl-Shift-Space|Alt-Space
    editor.setOptions({
        enableBasicAutocompletion: true,
        enableSnippets: true,
        enableLiveAutocompletion: false
    });


    // Adding New Commands and Keybindings
    editor.commands.addCommand({
        name: 'Save',
        bindKey: {win: 'Ctrl-S', mac: 'Command-S'},
        exec: function (editor) {
            saveCallback(editor);
        },
        readOnly: false // false if this command should not apply in readOnly mode
    });


    // set editor value
    // editor.getValue(); // or session.getValue
    // editor.setValue($("#content").val());

    return editor;
}
