CREATE TABLE `session` (
  `email` varchar(50) not null comment '邮箱',
  `token` varchar(50) not null comment '令牌',
  UNIQUE KEY(`email`)
) comment='Session表' ENGINE=InnoDB DEFAULT CHARSET=utf8;