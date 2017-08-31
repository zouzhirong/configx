CREATE TABLE `build_config_change` (
  `build_id` bigint(20) unsigned not null comment '构建ID',
  `app_id` int(11) unsigned not null comment '应用ID',
  `app_name` varchar(255) not null comment '应用名称',
  `env_id` int(11) unsigned not null comment '环境ID',
  `env_name` varchar(255) not null comment '环境名称',
  `profile_id` int(11) unsigned not null comment 'Profile ID',
  `profile_name` varchar(255) not null comment 'Profile名称',
  
  `config_id` bigint(20) unsigned not null comment '配置ID',
  `config_name` varchar(255) not null comment '配置名',
  
   KEY(`build_id`),
   KEY app_env_build_idx(`app_id`,`env_id`,`build_id`)
) comment='构建的配置变更记录(Change List)' ENGINE=InnoDB DEFAULT CHARSET=utf8;