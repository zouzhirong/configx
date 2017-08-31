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