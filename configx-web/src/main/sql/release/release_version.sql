CREATE TABLE `release_version` (
  `number` bigint(20) unsigned not null auto_increment primary key comment '版本号',
  `app_id` int(11) unsigned not null comment '应用ID',
  `app_name` varchar(255) not null comment '应用名称',
  `env_id` int(11) unsigned not null comment '环境ID',
  `env_name` varchar(255) not null comment '环境名称',
  
  `build_id` bigint(20) unsigned not null comment '构建ID',
  
  `release_id` bigint(20) unsigned not null default 0 comment '发布ID',
  `rollback_id` bigint(20) unsigned not null default 0 comment '回滚的发布ID',
  
  `create_time` datetime default '1970-01-01 00:00:00' comment '创建时间',
  
  KEY(`app_id`,`env_id`),
  KEY release_idx(`release_id`)
) comment='发行版本表' ENGINE=InnoDB DEFAULT CHARSET=utf8; 