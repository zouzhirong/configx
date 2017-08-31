/**
 * 添加用户模态框
 */
$('#addUserModal').on('show.bs.modal', function (event) {
    var $modal = $(this);

    $modal.find('.modal-body form').attr("action", "/users");
    $modal.find('.modal-body form').attr("data-location", "/users");
})


/**
 * 修改用户模态框
 */
$('#editUserModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var userId = button.data('user'); // Extract info from data-* attributes

    var modal = $(this);

    $.getJSON("/users/" + userId, function (user) {
        modal.find('.modal-body form').attr("action", "/users/" + user.id);
        modal.find('.modal-body form').attr("data-location", "/users");

        modal.find('.modal-body p[name=user-id]').text(user.id);
        modal.find('.modal-body p[name=user-email]').text(user.email);
        modal.find('.modal-body input[name=name]').val(user.name);
        modal.find('.modal-body input[name=password]').val(user.password);
        modal.find('.modal-body input[name=isAdmin]').attr("checked", user.admin);
    });
})


/**
 * 删除用户模态框
 */
$('#delUserModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget); // Button that triggered the modal
    var userId = button.data('user'); // Extract info from data-* attributes

    var modal = $(this);

    $.getJSON("/users/" + userId, function (user) {
        modal.find('.modal-body form').attr("action", "/users/" + user.id);
        modal.find('.modal-body form').attr("data-location", "/users");

        modal.find('.modal-body p[name=user-name]').text(user.name);
    });
})