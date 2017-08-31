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