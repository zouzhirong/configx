CREATE TABLE `app` (
  `id` int(11) unsigned not null auto_increment primary key comment '应用ID',
  `name` varchar(100) not null comment '应用名称',
  `description` varchar(255) not null comment '描述',
  `app_key` varchar(255) not null default '' comment 'App Key',
  `app_secret` varchar(255) not null default '' comment 'App Secret',
  `admins` varchar(250) not null default '' comment '管理员邮件，多个以,分割',
  `developers` varchar(250) not null default '' comment '开发者邮件，多个以,分割',

  `creator` varchar(36) not null default '' comment '创建人',
  `create_time` datetime default '1970-01-01 00:00:00' comment '创建时间',
  `updater` varchar(36) not null default '' comment '修改人',
  `update_time` datetime default '1970-01-01 00:00:00' comment '修改时间',

  UNIQUE KEY(`name`),
  UNIQUE KEY(`app_key`)
) comment='App表' ENGINE=InnoDB DEFAULT CHARSET=utf8;