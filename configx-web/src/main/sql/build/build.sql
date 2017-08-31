CREATE TABLE `build` (
  `id` bigint(20) unsigned not null auto_increment primary key comment '构建ID',
  `last_id` bigint(20) unsigned not null default 0 comment '上次构建ID',
  `app_id` int(11) unsigned not null comment '应用ID',
  `app_name` varchar(255) not null comment '应用名称',
  `env_id` int(11) unsigned not null comment '环境ID',
  `env_name` varchar(255) not null comment '环境名称',
  `revision` bigint(20) unsigned not null default 0 comment '修正版本号',
  `build_time` datetime default '1970-01-01 00:00:00' comment '构建时间',
  
  KEY(`app_id`,`env_id`)
) comment='构建表' ENGINE=InnoDB DEFAULT CHARSET=utf8; 