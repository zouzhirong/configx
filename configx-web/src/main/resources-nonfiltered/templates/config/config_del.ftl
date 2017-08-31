<!-- Modal -->
<div class="modal fade" id="delConfigModal" tabindex="-1" role="dialog" aria-labelledby="delete config">
    <div class="modal-dialog" role="document">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">删除配置</h4>
            </div>

            <div class="modal-body">
                <form class="form-horizontal" action="" method="post"
                      data-remote="true" data-method="delete" data-location="">
                    <input type="hidden" name="_method" value="delete"/>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">配置名称</label>
                        <div class="col-sm-10">
                            <p class="form-control-static" name="config-name"></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="message" class="col-sm-2 control-label">注释</label>
                        <div class="col-sm-10">
                                <textarea id="message" name="message" class="form-control" rows="1"
                                          aria-describedby="helpBlock" required></textarea>
                            <span id="helpBlock" class="help-block">提交注释。</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">警告！</label>
                        <div class="col-sm-10">
                            <p class="form-control-static">您真的要删除这个配置吗？</p>
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

