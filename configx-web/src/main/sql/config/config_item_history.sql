CREATE TABLE `config_item_history` (
  `revision` bigint(20) unsigned not null default 0 comment '修订版本号',
  `last_revision` bigint(20) unsigned not null default 0 comment '上次修订版本号',
  
  `app_id` int(11) unsigned not null comment '应用ID',
  `app_name` varchar(255) not null comment '应用名称',
  `env_id` int(11) unsigned not null comment '环境ID',
  `env_name` varchar(255) not null comment '环境名称',
  `profile_id` int(11) unsigned not null comment 'Profile ID',
  `profile_name` varchar(255) not null comment 'Profile名称',
  
  `config_id` bigint(20) unsigned not null comment '配置ID',
  `config_name` varchar(255) not null comment '配置名',
  `config_value` mediumtext not null comment '配置值',
  `config_value_id` bigint(20) unsigned not null default 0 comment '配置值ID',
  `config_value_type` tinyint not null comment '配置值类型',
  `config_tags` varchar(255) not null default '' comment '标签，多个标签ID之间用逗号分隔',
  
  `create_time` datetime default '1970-01-01 00:00:00' comment '创建时间',
  
  KEY(`app_id`,`env_id`,`profile_id`),
  KEY(`config_id`)
) comment='配置历史表' ENGINE=InnoDB DEFAULT CHARSET=utf8; 