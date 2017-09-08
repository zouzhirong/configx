CREATE DATABASE IF NOT EXISTS configx;
USE configx;

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

CREATE TABLE `profile` (
  `id` int(11) unsigned not null auto_increment primary key comment 'Profile ID',
  `app_id` int(11) unsigned not null comment '应用ID',
  `name` varchar(255) not null comment 'Profile 名称',
  `description` varchar(255) not null comment '描述',
  `order` int(11) not null default 0 comment '顺序',
  `color` varchar(25) not null default '' comment '颜色',

  `creator` varchar(36) not null default '' comment '创建人',
  `create_time` datetime default '1970-01-01 00:00:00' comment '创建时间',
  `updater` varchar(36) not null default '' comment '修改人',
  `update_time` datetime default '1970-01-01 00:00:00' comment '修改时间',

   KEY(`app_id`)
) comment='Profile表' ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tag` (
  `id` int(11) unsigned not null auto_increment primary key comment '标签ID',
  `app_id` int(11) unsigned not null comment '应用ID',
  `name` varchar(255) not null comment '标签名称',
  `description` varchar(255) not null comment '描述',

  `creator` varchar(36) not null default '' comment '创建人',
  `create_time` datetime default '1970-01-01 00:00:00' comment '创建时间',
  `updater` varchar(36) not null default '' comment '修改人',
  `update_time` datetime default '1970-01-01 00:00:00' comment '修改时间',

   KEY(`app_id`)
) comment='标签表' ENGINE=InnoDB DEFAULT CHARSET=utf8;

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

CREATE TABLE `config_commit` (
  `revision` bigint(20) unsigned not null auto_increment primary key comment '修订版本号',
  `app_id` int(11) unsigned not null comment '应用ID',
  `app_name` varchar(255) not null comment '应用名称',
  `env_id` int(11) unsigned not null comment '环境ID',
  `env_name` varchar(255) not null comment '环境名称',
  `profile_id` int(11) unsigned not null comment 'Profile ID',
  `profile_name` varchar(255) not null comment 'Profile名称',

  `author` varchar(255) not null comment '提交者',
  `date` datetime default '1970-01-01 00:00:00' comment '提交日期',
  `message` varchar(255) not null comment '提交注释',

  KEY(`app_id`,`env_id`,`profile_id`),
  KEY(`date`)
) comment='配置提交日志表' ENGINE=InnoDB DEFAULT CHARSET=utf8;

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

CREATE TABLE `config_value` (
  `id` bigint(20) unsigned not null auto_increment primary key comment '配置值ID',
  `value` mediumtext not null comment '配置值'
) comment='配置值表' ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `webhook` (
  `id` int(11) unsigned not null auto_increment primary key comment 'webhook ID',
  `app_id` int(11) unsigned not null comment '应用ID',
  `name` varchar(255) not null comment '名称',
  `url` varchar(1000) not null comment 'url',
  `content_type` varchar(255) not null comment 'Content type',
  `secret` varchar(255) not null default '' comment 'secret',
  `event_type` int(11) unsigned not null comment '事件类型'

) comment='Webhook表' ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `webhook_event_param` (
  `webhook_id` int(11) unsigned not null comment 'Webhook ID',
  `event_type` int(11) unsigned not null comment '事件类型',
  `name` varchar(255) not null comment '参数名',
  `value` varchar(255) not null comment '参数值'

) comment='事件参数表' ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `webhook_log` (
  `id` int(11) unsigned not null auto_increment primary key comment '主键ID',
  `app_id` int(11) unsigned not null comment '应用ID',
  `app_name` varchar(255) not null comment '应用名称',
  `webhook_id` int(11) unsigned not null comment 'hookId',
  `name` varchar(255) not null comment '名称',
  `url` varchar(1000) not null comment 'url',
  `content_type` varchar(255) not null comment 'Content type',
  `secret` varchar(255) not null default '' comment 'secret',
  `event_type` int(11) unsigned not null comment '事件类型',
  `event_name` varchar(255) not null comment '事件名称',
  `create_time` datetime default '1970-01-01 00:00:00' comment '创建时间',
  `event_params` varchar(255) not null default '' comment '事件参数',
  `request_params` varchar(255) not null default '' comment '请求参数',
  `request_headers` varchar(500) not null default '' comment '请求头',
  `request_body` mediumtext not null comment '请求体',
  `status_code` int(11) unsigned not null default 0 comment '响应码',
  `response_headers` varchar(1000) not null default '' comment '响应头',
  `response_body` mediumtext not null comment '响应体',
  `error_msg` mediumtext not null comment '错误消息'

) comment='Webhook日志记录表' ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `webhook_request_param` (
  `webhook_id` int(11) unsigned not null comment 'Webhook ID',
  `name` varchar(255) not null comment '参数名',
  `value` varchar(255) not null comment '参数值'

) comment='Webhook请求参数表' ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `release_message` text comment '发布异常信息',

  -- 回滚信息
  `rollback_user_code` varchar(36) not null default '' comment '回滚操作人',
  `rollback_ip` varchar(20) not null default '' comment '回滚操作人的机器IP',
  `rollback_build_id` bigint(20) unsigned not null default 0 comment '回滚的构建ID',
  `rollback_time` datetime default '1970-01-01 00:00:00' comment '回滚时间',
  `rollback_message` text comment '处理异常信息',

  KEY(`app_id`,`env_id`)
) comment='发布表' ENGINE=InnoDB DEFAULT CHARSET=utf8;

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

CREATE TABLE `session` (
  `email` varchar(50) not null comment '邮箱',
  `token` varchar(50) not null comment '令牌',
  UNIQUE KEY(`email`)
) comment='Session表' ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
  `id` int(11) unsigned not null auto_increment primary key comment '用户ID',
  `name` varchar(50) not null comment '姓名',
  `email` varchar(50) not null comment '邮箱',
  `password` varchar(50) not null comment '密码',
  `admin` boolean default false comment '是否是管理员',
  `error_count` int(11) unsigned not null default 0 comment '错误次数',
  `last_login_time` datetime default '1970-01-01 00:00:00' comment '上次登录时间',
  UNIQUE KEY(`email`)
) comment='用户表' ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Admin User
insert into user(name, email, password, admin) value('Admin', 'admin', 'admin123', 1);
