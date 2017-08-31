CREATE TABLE `webhook` (
  `id` int(11) unsigned not null auto_increment primary key comment 'webhook ID',
  `app_id` int(11) unsigned not null comment '应用ID',
  `name` varchar(255) not null comment '名称',
  `url` varchar(1000) not null comment 'url',
  `content_type` varchar(255) not null comment 'Content type',
  `secret` varchar(255) not null default '' comment 'secret',
  `event_type` int(11) unsigned not null comment '事件类型'

) comment='Webhook表' ENGINE=InnoDB DEFAULT CHARSET=utf8;