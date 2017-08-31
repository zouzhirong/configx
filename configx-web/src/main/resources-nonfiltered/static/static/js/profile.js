/**
 * 添加Profile模态框
 */
$('#addProfileModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes

    var modal = $(this);

    modal.find('.modal-body form').attr("action", "/apps/" + appId + "/profiles");
    modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/profiles");

    modal.find('.modal-body input[name=color]').colorpicker({"format": "hex"});
})
$('#addProfileModal').on('hidden.bs.modal', function (event) {
    modal.find('.modal-body input[name=color]').colorpicker('destroy');
})


/**
 * 修改Profile模态框
 */
$('#editProfileModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes
    var profileId = button.data('profile'); // Extract info from data-* attributes

    var modal = $(this);

    $.getJSON("/apps/" + appId + "/profiles/" + profileId, function (profile) {
        modal.find('.modal-body form').attr("action", "/apps/" + appId + "/profiles/" + profile.id);
        modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/profiles");

        modal.find('.modal-body p[name=profile-id]').text(profile.id);
        modal.find('.modal-body input[name=name]').val(profile.name);
        modal.find('.modal-body textarea[name=description]').val(profile.description);
        modal.find('.modal-body input[name=order]').val(profile.order);
        modal.find('.modal-body input[name=color]').val(profile.color);

        modal.find('.modal-body input[name=color]').colorpicker({"format": "hex"}).setColor(profile.color);
    });
})
$('#editProfileModal').on('hidden.bs.modal', function (event) {
    modal.find('.modal-body input[name=color]').colorpicker('destroy');
})


/**
 * 删除Profile模态框
 */
$('#delProfileModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes
    var profileId = button.data('profile'); // Extract info from data-* attributes

    var modal = $(this);

    $.getJSON("/apps/" + appId + "/profiles/" + profileId, function (profile) {
        modal.find('.modal-body form').attr("action", "/apps/" + appId + "/profiles/" + profile.id);
        modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/profiles");

        modal.find('.modal-body p[name=profile-name]').text(profile.name);
    });
})