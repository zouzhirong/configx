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