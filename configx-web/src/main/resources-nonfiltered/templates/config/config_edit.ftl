<!-- Modal -->
<div class="modal fade" id="editConfigModal" tabindex="-1" role="dialog" aria-labelledby="edit config">
    <div class="modal-dialog" role="document" aria-hidden="true">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">修改配置</h4>
            </div>

            <div class="modal-body">
                <form class="form-horizontal" action="" method="post"
                      data-remote="true" data-method="put" data-location="">
                    <input type="hidden" name="_method" value="put"/>
                    <div class="form-group">
                        <label for="name" class="col-sm-2 control-label">配置名</label>
                        <div class="col-sm-10">
                            <input type="text" name="name" class="form-control"
                                   aria-describedby="helpBlock" value="">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="value" class="col-sm-2 control-label">配置值</label>
                        <div class="col-sm-10">
                            <input type="text" name="value" class="form-control"
                                   aria-describedby="helpBlock" value="">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="tagId" class="col-sm-2 control-label">标签</label>
                        <div class="col-sm-10">
                            <select multiple name="tagId" class="form-control"
                                    aria-describedby="helpBlock">
                            <#list tagList as tag>
                                <option value="${tag.id}">${tag.name}</option>
                            </#list>
                            </select>
                            <span class="help-block">配置项所属标签。</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="description" class="col-sm-2 control-label">描述</label>
                        <div class="col-sm-10">
                                <textarea name="description" class="form-control" rows="1"
                                          aria-describedby="helpBlock"></textarea>
                            <span class="help-block">配置描述信息。</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="message" class="col-sm-2 control-label">注释</label>
                        <div class="col-sm-10">
                                <textarea name="message" class="form-control" rows="1"
                                          aria-describedby="helpBlock" required></textarea>
                            <span class="help-block">提交注释。</span>
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
