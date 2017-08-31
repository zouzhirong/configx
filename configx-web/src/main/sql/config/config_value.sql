CREATE TABLE `config_value` (
  `id` bigint(20) unsigned not null auto_increment primary key comment '配置值ID',
  `value` mediumtext not null default '' comment '配置值'
) comment='配置值表' ENGINE=InnoDB DEFAULT CHARSET=utf8; 