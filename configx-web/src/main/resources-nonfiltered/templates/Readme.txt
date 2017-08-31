<button type="button" class="btn btn-sm btn-default" data-toggle="modal" data-target="#addProfileModal">
	按钮模态对话框
</button>
<button type="button" class="glyphicon-btn" data-toggle="modal" data-target="#editProfileModal" data-profile="${profile.id}" aria-label="edit the profile">
	<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
</button>
<button type="button" class="glyphicon-btn" data-toggle="modal" data-target="#delProfileModal" data-profile="${profile.id}" aria-label="delete the profile">
	<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
</button>


<a href="" class="btn btn-sm btn-default" rel="nofollow">链接</a>
<a href="" class="glyphicon-btn" aria-label="edit the file">
	<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
</a>
			

			
appIdInput.closest('.form-group').addClass('has-error');
appIdInput.closest('.form-group').removeClass('has-error');


1、配置值长度限制，跟Web服务器有关（Jetty和Tomcat限制POST请求数据长度为2M）和MySQL有关。
   