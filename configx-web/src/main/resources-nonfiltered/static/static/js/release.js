$(function () {
    $('span[name=spinner]').each(function () {
        var appId = $(this).data("app");
        var releaseFormId = $(this).data("release-form");
        queryReleaseStatus(appId, releaseFormId);
    });
});


/**
 * 创建发布单成功后处理
 */
$("form.js-release-form").on("ajax:success", function (event, releaseForm) {
    window.location.href = "/apps/" + releaseForm.appId + "/releaseform/" + releaseForm.id + "/detail";
});


/**
 * 删除发布单模态框
 */
$('#delReleaseFormModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes
    var formId = button.data('form'); // Extract info from data-* attributes

    var modal = $(this);

    $.getJSON("/apps/" + appId + "/releaseform/" + formId, function (releaseForm) {
        modal.find('.modal-body form').attr("action", "/apps/" + appId + "/releaseform/" + releaseForm.id);
        modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/releaseforms/" + releaseForm.envName);

        modal.find('.modal-body p[name=release-form-name]').text(releaseForm.name);
    });
})

$("#delReleaseFormModal").find("form").on("ajax:success", function (event, releaseForm) {
    $("#delReleaseFormModal").modal('hide');
    $("#tr-form-" + releaseForm.id).remove();
});


/**
 * 查询发布单状态
 */
function queryReleaseStatus(appId, releaseFormId) {
    $.get("/apps/" + appId + "/releaseform/" + releaseFormId + "/release-status", function (status) {
        if (status == 1 || status == 4) {
            setTimeout("queryReleaseStatus(" + appId + "," + releaseFormId + ")", 1000);
        } else {
            window.location.href = "/apps/" + appId + "/releaseform/" + releaseFormId + "/detail";
        }
    });
}