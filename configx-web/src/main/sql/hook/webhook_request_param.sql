CREATE TABLE `webhook_request_param` (
  `webhook_id` int(11) unsigned not null comment 'Webhook ID',
  `name` varchar(255) not null comment '参数名',
  `value` varchar(255) not null comment '参数值'

) comment='Webhook请求参数表' ENGINE=InnoDB DEFAULT CHARSET=utf8;