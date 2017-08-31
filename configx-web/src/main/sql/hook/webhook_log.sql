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