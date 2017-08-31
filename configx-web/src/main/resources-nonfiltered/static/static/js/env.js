/**
 * 添加环境模态框
 */
$('#addEnvModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes

    var modal = $(this);

    modal.find('.modal-body form').attr("action", "/apps/" + appId + "/envs");
    modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/envs");
})


/**
 * 修改环境模态框
 */
$('#editEnvModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes
    var envId = button.data('env'); // Extract info from data-* attributes

    var modal = $(this);

    $.getJSON("/apps/" + appId + "/envs/" + envId, function (env) {
        modal.find('.modal-body form').attr("action", "/apps/" + appId + "/envs/" + env.id);
        modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/envs");

        modal.find('.modal-body p[name=env-id]').text(env.id);
        modal.find('.modal-body input[name=name]').val(env.name);
        modal.find('.modal-body input[name=alias]').val(env.alias);
        modal.find('.modal-body input[name=autoRelease]').attr("checked", env.autoRelease);
        modal.find('.modal-body textarea[name=description]').val(env.description);
        modal.find('.modal-body input[name=order]').val(env.order);
    });
})


/**
 * 删除环境模态框
 */
$('#delEnvModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes
    var envId = button.data('env'); // Extract info from data-* attributes

    var modal = $(this);

    $.getJSON("/apps/" + appId + "/envs/" + envId, function (env) {
        modal.find('.modal-body form').attr("action", "/apps/" + appId + "/envs/" + env.id);
        modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/envs");

        modal.find('.modal-body p[name=env-name]').text(env.name);
    });
})