CREATE TABLE `config_item` (
  `id` bigint(20) unsigned not null auto_increment primary key comment '配置ID',
  `app_id` int(11) unsigned not null comment '应用ID',
  `env_id` int(11) unsigned not null comment '环境ID',
  `profile_id` int(11) unsigned not null comment 'Profile ID',
  `name` varchar(50) not null comment '配置名',
  `value` mediumtext not null comment '配置值',
  `value_id` bigint(20) unsigned not null default 0 comment '配置值ID',
  `value_type` tinyint not null comment '配置值类型',
  `tags` varchar(255) not null default '' comment '标签，多个标签ID之间用逗号分隔',
  `description` varchar(255) not null default '' comment '描述',
  
  `enable` boolean not null default false comment '是否启用',
  
  `creator` varchar(36) not null default '' comment '创建人',
  `create_time` datetime default '1970-01-01 00:00:00' comment '创建时间',
  `updater` varchar(36) not null default '' comment '修改人',
  `update_time` datetime default '1970-01-01 00:00:00' comment '修改时间',
  
  `revision` bigint(20) unsigned not null default 0 comment '最新修订版本号',
  `last_revision` bigint(20) unsigned not null default 0 comment '上次修订版本号',
  `data_change_last_time` datetime default '1970-01-01 00:00:00' comment '数据最新修改时间',
  
   KEY(`app_id`,`env_id`,`profile_id`)
) comment='配置表' ENGINE=InnoDB DEFAULT CHARSET=utf8; 