<!-- Modal -->
<div class="modal fade" id="editAppModal" tabindex="-1" role="dialog" aria-labelledby="edit app">
    <div class="modal-dialog" role="document" aria-hidden="true">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">更改应用</h4>
            </div>

            <div class="modal-body">
                <form class="form-horizontal" action="" method="post"
                      data-remote="true" data-method="put" data-location="">
                    <input type="hidden" name="_method" value="put"/>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">应用ID</label>
                        <div class="col-sm-10">
                            <p class="form-control-static" name="app-id"></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="name" class="col-sm-2 control-label">名称</label>
                        <div class="col-sm-10">
                            <input type="text" name="name" class="form-control" value=""
                                   aria-describedby="helpBlock">
                            <span class="help-block">项目的唯一标识符，请使用英文/数字和下划线。</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="description" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10">
                                <textarea name="description" class="form-control" rows="1"
                                          aria-describedby="helpBlock"></textarea>
                            <span class="help-block">描述项目的基本信息。</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="admins" class="col-sm-2 control-label">管理员邮箱</label>
                        <div class="col-sm-10">
                            <textarea name="admins" class="form-control" rows="5"
                                      aria-describedby="helpBlock"></textarea>
                            <span class="help-block">用户邮箱，可以使用(,)逗号、(;)分号、( )空格、(\t)Tab、(\n)换行来分隔。</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="developers" class="col-sm-2 control-label">开发者邮箱</label>
                        <div class="col-sm-10">
                            <textarea name="developers" class="form-control" rows="5"
                                      aria-describedby="helpBlock"></textarea>
                            <span class="help-block">用户邮箱，可以使用(,)逗号、(;)分号、( )空格、(\t)Tab、(\n)换行来分隔。</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-12 text-right">
                            <button type="submit" class="btn btn-primary">修改</button>
                        </div>
                    </div>
                </form>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
