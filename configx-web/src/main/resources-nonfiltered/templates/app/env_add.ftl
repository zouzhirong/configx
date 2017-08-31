<!-- Modal -->
<div class="modal fade" id="addEnvModal" tabindex="-1" role="dialog" aria-labelledby="add env">
    <div class="modal-dialog" role="document" aria-hidden="true">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">新环境</h4>
            </div>

            <div class="modal-body">
                <form class="form-horizontal" action="" method="post"
                      data-remote="true" data-method="post" data-location="">
                    <input type="hidden" name="_method" value="post"/>
                    <div class="form-group">
                        <label for="name" class="col-sm-2 control-label">名称</label>
                        <div class="col-sm-10">
                            <input type="text" name="name" class="form-control"
                                   aria-describedby="helpBlock">
                            <span class="help-block">环境的唯一标识符，请使用英文/数字和下划线。</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="alias" class="col-sm-2 control-label">别名</label>
                        <div class="col-sm-10">
                            <input type="text" name="alias" class="form-control"
                                   aria-describedby="helpBlock">
                            <span class="help-block">环境的别名，多个别名之间用,分割。</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="description" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10">
                                <textarea name="description" class="form-control" rows="1"
                                          aria-describedby="helpBlock"></textarea>
                            <span class="help-block">描述环境的基本信息。</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="order" class="col-sm-2 control-label">顺序</label>
                        <div class="col-sm-10">
                            <input type="text" name="order" class="form-control"
                                   aria-describedby="helpBlock">
                            <span class="help-block">顺序必须为数字，数字越小越靠前，可以为负数。</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-12 text-right">
                            <button type="submit" class="btn btn-primary">创建</button>
                        </div>
                    </div>
                </form>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->