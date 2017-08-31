CREATE TABLE `build_config_item` (
  `build_id` bigint(20) unsigned not null comment '构建ID',
  `app_id` int(11) unsigned not null comment '应用ID',
  `env_id` int(11) unsigned not null comment '环境ID',
  `profile_id` int(11) unsigned not null comment 'Profile ID',
  
  `revision` bigint(20) unsigned not null default 0 comment '修订版本',
  `config_id` bigint(20) unsigned not null comment '配置ID',
  `config_name` varchar(255) not null comment '配置名',
  `config_value` mediumtext not null comment '配置值',
  `config_value_id` bigint(20) unsigned not null default 0 comment '配置值ID',
  `config_tags` varchar(255) not null default '' comment '标签，多个标签ID之间用逗号分隔',

  KEY build_config_id(`build_id`,`config_id`),
  KEY build_config_name(`build_id`,`config_name`),
  KEY app_env_build_idx(`app_id`,`env_id`,`build_id`)
) comment='构建的配置项信息' ENGINE=InnoDB DEFAULT CHARSET=utf8;