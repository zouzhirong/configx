CREATE TABLE `env` (
  `id` int(11) unsigned not null auto_increment primary key comment '环境ID',
  `app_id` int(11) unsigned not null comment '应用ID',
  `name` varchar(255) not null comment '环境名称',
  `alias` varchar(255) not null default '' comment '环境别名，多个以,分割',
  `description` varchar(255) not null comment '描述',
  `order` int(11) not null default 0 comment '顺序',
  
  `creator` varchar(36) not null default '' comment '创建人',
  `create_time` datetime default '1970-01-01 00:00:00' comment '创建时间',
  `updater` varchar(36) not null default '' comment '修改人',
  `update_time` datetime default '1970-01-01 00:00:00' comment '修改时间',
  
  `revision` bigint(20) unsigned not null default 0 comment '最新修订版本号',
  `data_change_last_time` datetime default '1970-01-01 00:00:00' comment '数据最新修改时间',
  
  `build_id` bigint(20) unsigned not null default 0 comment '最新构建ID',
  `build_time` datetime default '1970-01-01 00:00:00' comment '最新构建时间',
  
  `release_version` bigint(20) unsigned not null default 0 comment '最新发布版本',
  `release_time` datetime default '1970-01-01 00:00:00' comment '最新发布时间',
  `auto_release` boolean default false comment '是否自动发布，用于开发环境快速发布',
  
   KEY(`app_id`)
) comment='Env表' ENGINE=InnoDB DEFAULT CHARSET=utf8; 