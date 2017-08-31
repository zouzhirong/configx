<!-- Modal -->
<div class="modal fade" id="addConfigModal" tabindex="-1" role="dialog" aria-labelledby="add config">
    <div class="modal-dialog" role="document" aria-hidden="true">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">创建配置</h4>
            </div>

            <div class="modal-body">
                <form class="form-horizontal" action="" method="post"
                      data-remote="true" data-method="post" data-location="">
                    <input type="hidden" name="_method" value="post"/>
                    <input type="hidden" name="valueType" value=""/>
                    <div class="form-group">
                        <label for="envId" class="col-sm-2 control-label">环境</label>
                        <div class="col-sm-10">
                            <select name="envId" class="form-control" aria-describedby="helpBlock">
                            <#list envList as env>
                                <option value="${env.id}"
                                        <#if ((form.envId)!0)==env.id>selected</#if>>${env.name}</option>
                            </#list>
                            </select>
                            <span class="help-block">指定配置的环境</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="profileId" class="col-sm-2 control-label">Profile</label>
                        <div class="col-sm-10">
                            <select name="profileId" class="form-control"
                                    aria-describedby="helpBlock">
                            <#list profileList as profile>
                                <option value="${profile.id}"
                                        <#if ((form.profileId)!0)==profile.id>selected</#if>>
                                ${MessageUtils.getMessage("app.profile.name."+profile.name, profile.name)}
                                </option>
                            </#list>
                            </select>
                            <span class="help-block">指定配置的Profile</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="name" class="col-sm-2 control-label">配置名</label>
                        <div class="col-sm-10">
                            <input type="text" name="name" value="" class="form-control"
                                   aria-describedby="helpBlock">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="value" class="col-sm-2 control-label">配置值</label>
                        <div class="col-sm-10">
                            <input type="text" name="value" value="" class="form-control"
                                   aria-describedby="helpBlock">
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
                            <button type="submit" class="btn btn-primary">创建</button>
                        </div>
                    </div>
                </form>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

