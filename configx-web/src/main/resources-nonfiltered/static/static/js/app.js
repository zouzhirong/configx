/**
 * 添加项目模态框
 */
$('#addAppModal').on('show.bs.modal', function (event) {
    var modal = $(this);

    modal.find('.modal-body form').attr("action", "/apps");
    modal.find('.modal-body form').attr("data-location", "/apps");
})


/**
 * 修改项目模态框
 */
$('#editAppModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes

    var modal = $(this);

    $.getJSON("/apps/" + appId, function (app) {
        modal.find('.modal-body form').attr("action", "/apps/" + app.id);
        modal.find('.modal-body form').attr("data-location", "/apps");

        modal.find('.modal-body p[name=app-id]').text(app.id);
        modal.find('.modal-body input[name=name]').val(app.name);
        modal.find('.modal-body textarea[name=description]').val(app.description);
        modal.find('.modal-body textarea[name=admins]').val(app.admins);
        modal.find('.modal-body textarea[name=developers]').val(app.developers);
    });
})


/**
 * 删除项目模态框
 */
$('#delAppModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes

    var modal = $(this);

    $.getJSON("/apps/" + appId, function (app) {
        modal.find('.modal-body form').attr("action", "/apps/" + app.id);
        modal.find('.modal-body form').attr("data-location", "/apps");

        modal.find('.modal-body p[name=app-name]').text(app.name);
    });
})