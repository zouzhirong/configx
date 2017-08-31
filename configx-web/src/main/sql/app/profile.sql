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