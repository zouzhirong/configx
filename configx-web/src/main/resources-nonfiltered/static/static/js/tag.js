/**
 * 添加Tag模态框
 */
$('#addTagModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes

    var modal = $(this);

    modal.find('.modal-body form').attr("action", "/apps/" + appId + "/tags");
    modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/tags");
})


/**
 * 修改Tag模态框
 */
$('#editTagModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes
    var tagId = button.data('tag'); // Extract info from data-* attributes

    var modal = $(this);

    $.getJSON("/apps/" + appId + "/tags/" + tagId, function (tag) {
        modal.find('.modal-body form').attr("action", "/apps/" + appId + "/tags/" + tag.id);
        modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/tags");

        modal.find('.modal-body p[name=tag-id]').text(tag.id);
        modal.find('.modal-body input[name=name]').val(tag.name);
        modal.find('.modal-body textarea[name=description]').val(tag.description);
    });
})


/**
 * 删除Tag模态框
 */
$('#delTagModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var appId = button.data('app'); // Extract info from data-* attributes
    var tagId = button.data('tag'); // Extract info from data-* attributes

    var modal = $(this);

    $.getJSON("/apps/" + appId + "/tags/" + tagId, function (tag) {
        modal.find('.modal-body form').attr("action", "/apps/" + appId + "/tags/" + tag.id);
        modal.find('.modal-body form').attr("data-location", "/apps/" + appId + "/tags");

        modal.find('.modal-body p[name=tag-name]').text(tag.name);
    });
})