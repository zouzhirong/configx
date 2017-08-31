CREATE TABLE `release_form` (
  `id` bigint(20) unsigned not null auto_increment primary key comment '发布单ID',
  `release_id` bigint(20) unsigned not null default 0 comment '发布ID',
  
  -- 应用环境信息
  `app_id` int(11) unsigned not null comment '应用ID',
  `app_name` varchar(255) not null comment '应用名称',
  `env_id` int(11) unsigned not null comment '环境ID',
  `env_name` varchar(255) not null comment '环境名称',
  
  -- 发布单信息
  `name` varchar(50) not null comment '发布单名称',
  `remark` varchar(255) not null default '' comment '备注',
  `plan_pub_time` datetime default '1970-01-01 00:00:00' comment '计划发布时间',
  
  `creator` varchar(36) not null default '' comment '创建人',
  `create_time` datetime default '1970-01-01 00:00:00' comment '创建时间',
  `updater` varchar(36) not null default '' comment '修改人',
  `update_time` datetime default '1970-01-01 00:00:00' comment '修改时间',
  
  -- 发布单审核信息
  `auditor` varchar(36) not null default '' comment '审核人',
  `audit_status` tinyint unsigned not null default 0 comment '审核状态:1 编辑中 2 待审核 3 通过 4 驳回',
  `audit_time` datetime default '1970-01-01 00:00:00' comment '审核时间',
  
  KEY(`app_id`,`env_id`)
) comment='发布单' ENGINE=InnoDB DEFAULT CHARSET=utf8; 	