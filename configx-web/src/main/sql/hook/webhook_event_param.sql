CREATE TABLE `webhook_event_param` (
  `webhook_id` int(11) unsigned not null comment 'Webhook ID',
  `event_type` int(11) unsigned not null comment '事件类型',
  `name` varchar(255) not null comment '参数名',
  `value` varchar(255) not null comment '参数值'

) comment='事件参数表' ENGINE=InnoDB DEFAULT CHARSET=utf8;