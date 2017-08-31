<!-- Modal -->
<div class="modal fade" id="addProfileModal" tabindex="-1" role="dialog" aria-labelledby="add profile">
    <div class="modal-dialog modal-lg" role="document" aria-hidden="true">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">新增Profile</h4>
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
                            <span class="help-block">请使用字母、数字、下划线。</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="description" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10">
                                <textarea name="description" class="form-control" rows="1"
                                          aria-describedby="helpBlock"></textarea>
                            <span class="help-block">描述一下您的Profile。</span>
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
                        <label for="color" class="col-sm-2 control-label">颜色</label>
                        <div class="col-sm-10">
                            <input type="text" name="color" class="form-control"
                                   aria-describedby="helpBlock">
                            <span class="help-block">红 red, 黄 yellow, 绿 green, 激活 #f5f5f5, 成功 #dff0d8, 信息 #d9edf7, 警告 #fcf8e3, 危险 #f2dede</span>
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
