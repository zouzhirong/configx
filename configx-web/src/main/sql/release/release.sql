CREATE TABLE `release` (
  `id` bigint(20) unsigned not null auto_increment primary key comment '发布ID',
  
  -- 应用环境信息
  `app_id` int(11) unsigned not null comment '应用ID',
  `app_name` varchar(255) not null comment '应用名称',
  `env_id` int(11) unsigned not null comment '环境ID',
  `env_name` varchar(255) not null comment '环境名称',
  
  -- 发布状态
  `release_status` tinyint unsigned not null default 0 comment '发布状态: 1 发布中 2 发布成功 3 发布失败 4 回滚中 5 回滚成功 6 回滚失败',
  
  -- 发布信息
  `release_user_code` varchar(36) not null default '' comment '发布操作人',
  `release_ip` varchar(20) not null default '' comment '发布操作人的机器IP',
  `release_build_id` bigint(20) unsigned not null default 0 comment '发布的构建ID',
  `release_time` datetime default '1970-01-01 00:00:00' comment '发布时间',
  `release_message` text default '' comment '发布异常信息',
  
  -- 回滚信息
  `rollback_user_code` varchar(36) not null default '' comment '回滚操作人',
  `rollback_ip` varchar(20) not null default '' comment '回滚操作人的机器IP',
  `rollback_build_id` bigint(20) unsigned not null default 0 comment '回滚的构建ID',
  `rollback_time` datetime default '1970-01-01 00:00:00' comment '回滚时间',
  `rollback_message` text not null default '' comment '处理异常信息',
  
  KEY(`app_id`,`env_id`)
) comment='发布表' ENGINE=InnoDB DEFAULT CHARSET=utf8; 