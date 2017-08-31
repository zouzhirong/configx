<!-- Modal -->
<div class="modal fade" id="delProfileModal" tabindex="-1" role="dialog" aria-labelledby="delete profile">
    <div class="modal-dialog" role="document">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">删除Profile</h4>
            </div>

            <div class="modal-body">
                <form class="form-horizontal" action="" method="post"
                      data-remote="true" data-method="delete" data-location="">
                    <input type="hidden" name="_method" value="delete"/>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">Profile名称</label>
                        <div class="col-sm-10">
                            <p class="form-control-static" name="profile-name"></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">警告！</label>
                        <div class="col-sm-10">
                            <p class="form-control-static">您真的要删除这个Profile吗？</p>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-12 text-right">
                            <button type="submit" class="sure btn btn-primary">删除</button>
                        </div>
                    </div>
                </form>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
