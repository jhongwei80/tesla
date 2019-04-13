DROP TABLE IF EXISTS `oauth_sys_dept`;
CREATE TABLE `oauth_sys_dept` (
  `dept_id` bigint(20) NOT NULL auto_increment ,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级部门ID，一级部门为0',
  `name` varchar(50) DEFAULT NULL COMMENT '部门名称',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  `del_flag` tinyint(4) DEFAULT '0' COMMENT '是否删除  -1：已删除  0：正常',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='部门管理';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `oauth_sys_dept` VALUES ('6', '0', '基础架构', '1', '1');
INSERT INTO `oauth_sys_dept` VALUES ('9', '0', '业务研发', '2', '1');

-- ----------------------------
-- Table structure for `sys_log`
-- ----------------------------
DROP TABLE IF EXISTS `oauth_sys_log`;
CREATE TABLE `oauth_sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) DEFAULT NULL COMMENT '用户操作',
  `time` int(11) DEFAULT NULL COMMENT '响应时间',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` varchar(5000) DEFAULT NULL COMMENT '请求参数',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='系统日志';

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_menu`
-- ----------------------------
DROP TABLE IF EXISTS `oauth_sys_menu`;
CREATE TABLE `oauth_sys_menu` (
  `menu_id` bigint(20) NOT NULL auto_increment,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(500) DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` int(11) DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='菜单管理';



-- ----------------------------
-- Table structure for `sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `oauth_sys_role`;
CREATE TABLE `oauth_sys_role` (
  `role_id` bigint(20) NOT NULL auto_increment,
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `role_sign` varchar(100) DEFAULT NULL COMMENT '角色标识',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `user_id_create` bigint(255) DEFAULT NULL COMMENT '创建用户id',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `oauth_sys_role` VALUES ('1', '超级用户角色', 'admin', '拥有最高权限', '2', now(), now());
INSERT INTO `oauth_sys_role` VALUES ('2', '普通用户', 'user', '普通用户',  '2', now(), now());
-- ----------------------------
-- Table structure for `sys_role_menu`
-- ----------------------------
DROP TABLE IF EXISTS `oauth_sys_role_menu`;
CREATE TABLE `oauth_sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='角色与菜单对应关系';

DROP TABLE IF EXISTS `oauth_sys_user`;
CREATE TABLE `oauth_sys_user` (
  `user_id` bigint(20) NOT NULL auto_increment,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `name` varchar(100) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `dept_id` int(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) DEFAULT NULL COMMENT '手机号',
  `status` tinyint(255) DEFAULT NULL COMMENT '状态 0:禁用，1:正常',
  `user_id_create` bigint(255) DEFAULT NULL COMMENT '创建用户id',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `sys_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `oauth_sys_user_role`;
CREATE TABLE `oauth_sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='用户与角色对应关系';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `oauth_sys_user_role` VALUES ('1', '1', '1');
INSERT INTO `oauth_sys_user_role` VALUES ('2', '2', '2');

-- roles_permissions
DROP TABLE IF EXISTS oauth_roles_permissions;
CREATE TABLE oauth_roles_permissions (
	`id` int(11) NOT NULL auto_increment,
  roles_id int(11) not null,
  permission varchar(255) not null,
  PRIMARY KEY (`id`),
	UNIQUE KEY roles_id(`roles_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;



--
--  Oauth sql  -- MYSQL
--

DROP TABLE IF EXISTS oauth_client_details;
CREATE TABLE oauth_client_details (
	`id` int(11) NOT NULL auto_increment,
  client_id VARCHAR(255),
  client_secret VARCHAR(255),
  client_name VARCHAR(255),
  client_uri VARCHAR(255),
  client_icon_uri VARCHAR(255),
  resource_ids VARCHAR(255),
  scope VARCHAR(255),
  grant_types VARCHAR(255),
  redirect_uri VARCHAR(255),
  roles VARCHAR(255),
  access_token_validity INTEGER default -1,
  refresh_token_validity INTEGER default -1,
  description VARCHAR(4096),
  create_time timestamp default now(),
  archived tinyint(1) default '0',
  trusted tinyint(1) default '0',
  PRIMARY KEY (`id`),
	UNIQUE KEY client_id(`client_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS oauth_access_token;
CREATE TABLE oauth_access_token (
	`id` int(11) NOT NULL auto_increment,
  create_time timestamp default now(),
  token_id VARCHAR(255),
  token_expired_seconds INTEGER default -1,
  authentication_id VARCHAR(255),
  username VARCHAR(255),
  client_id VARCHAR(255),
  token_type VARCHAR(255),
  refresh_token_expired_seconds INTEGER default -1,
  refresh_token VARCHAR(255) unique,
  PRIMARY KEY (`id`),
	UNIQUE KEY token_id(`token_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS oauth_code;
CREATE TABLE oauth_code (
	`id` int(11) NOT NULL auto_increment,
  create_time timestamp default now(),
  `code` VARCHAR(255) unique,
  username VARCHAR(255),
  client_id VARCHAR(255),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

INSERT INTO `oauth_sys_user`(`user_id`, `username`, `name`, `password`, `dept_id`, `email`, `mobile`, `status`, `user_id_create`, `gmt_create`, `gmt_modified`) VALUES (1, 'admin', '超级管理员', '00d7aa78b181be8023819efe7ad5abfd', 6, 'admin@example.com', '123456', 1, 1, '2017-08-15 21:40:39', '2018-11-27 11:44:39');
INSERT INTO `oauth_sys_user`(`user_id`, `username`, `name`, `password`, `dept_id`, `email`, `mobile`, `status`, `user_id_create`, `gmt_create`, `gmt_modified`) VALUES (2, 'test', '普通用户', 'b132f5f968c9373261f74025c23c2222', 6, 'test@test.com', NULL, 1, 1, '2017-08-14 13:43:05', '2017-08-14 21:15:36');


INSERT INTO oauth_client_details ( client_id, client_secret, client_name, client_uri, client_icon_uri, resource_ids, scope, grant_types, redirect_uri, roles )
VALUES
	( 'test', 'test', 'Test Client', 'http://andaily.com', 'http://andaily.com/favicon.ico', 'os-resource', 'read write', 'authorization_code,password,refresh_token,client_credentials', 'http://localhost:7777/spring-oauth-client/authorization_code_callback', '22' );
INSERT INTO oauth_client_details ( client_id, client_secret, client_name, client_uri, client_icon_uri, resource_ids, scope, grant_types, redirect_uri, roles )
VALUES
	( 'mobile', 'mobile', 'Mobile Client', 'http://andaily.com', 'http://andaily.com/favicon.ico', 'mobile-resource', 'read write', 'password,refresh_token', 'http://localhost:7777/spring-oauth-client/authorization_code_callback', '22' );



INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (1, 0, '系统管理', NULL, NULL, 0, 'fa fa-desktop', 0, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (2, 0, '系统监控', NULL, NULL, 0, 'fa fa-video-camera', 1, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (11, 1, '系统菜单', 'sys/menu', 'sys:menu:menu', 1, 'fa fa-th-list', 0, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (12, 1, '用户管理', 'sys/user', 'sys:user:user', 1, 'fa fa-user', 1, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (13, 1, '角色管理', 'sys/role', 'sys:role:role', 1, 'fa fa-paw', 2, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (14, 1, '部门管理', 'sys/dept', 'sys:dept:dept', 1, 'fa fa-users', 3, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (21, 11, '新增', '', 'sys:menu:add', 2, '', 0, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (22, 11, '批量删除', '', 'sys:menu:batchRemove', 2, '', 1, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (23, 11, '编辑', '', 'sys:menu:edit', 2, '', 2, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (24, 11, '删除', '', 'sys:menu:remove', 2, '', 3, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (25, 12, '新增', '', 'sys:user:add', 2, '', 0, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (26, 12, '编辑', '', 'sys:user:edit', 2, '', 1, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (27, 12, '删除', '', 'sys:user:remove', 2, '', 2, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (28, 12, '批量删除', '', 'sys:user:batchRemove', 2, '', 3, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (29, 12, '停用', '', 'sys:user:disable', 2, '', 4, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (30, 12, '重置密码', '', 'sys:user:resetPwd', 2, '', 5, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (31, 13, '新增', '', 'sys:role:add', 2, '', 0, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (32, 13, '批量删除', '', 'sys:role:batchRemove', 2, '', 1, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (33, 13, '编辑', '', 'sys:role:edit', 2, '', 2, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (34, 13, '删除', '', 'sys:role:remove', 2, '', 3, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (35, 14, '增加', '', 'sys:dept:add', 2, '', 0, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (36, 14, '刪除', '', 'sys:dept:remove', 2, '', 1, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (37, 14, '编辑', '', 'sys:dept:edit', 2, '', 2, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (38, 2, '在线用户', 'sys/online', 'sys:monitor:online', 1, 'fa fa-user', 0, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (39, 2, '系统日志', 'sys/log', 'sys:monitor:log', 1, 'fa fa-warning', 1, '2017-08-09 23:06:55', '2017-08-14 14:13:43');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (40, 2, '运行监控', 'sys/log/run', 'sys:monitor:run', 1, 'fa fa-caret-square-o-right', 2, '2017-08-09 23:06:55', '2017-08-14 14:13:43');


INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (3, 0, '认证管理', NULL, NULL, 0, 'fa fa-tachometer', 3, '2019-01-03 08:18:39', '2019-01-03 16:21:45');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (51, 3, '客户端管理', 'oauth2/client/list', 'oauth:client:list', 0, '', 0, '2019-01-03 08:19:40', '2019-01-03 16:20:36');

INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (52, 3, '新增客户端', '', 'oauth:client:add', 2, '', 0, '2019-01-03 08:19:40', '2019-01-03 16:20:36');

INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (53, 3, '编辑客户端', '', 'oauth:client:edit', 2, '', 0, '2019-01-03 08:19:40', '2019-01-03 16:20:36');
INSERT INTO `oauth_sys_menu`(`menu_id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`, `gmt_create`, `gmt_modified`) VALUES (54, 3, '删除客户端', '', 'oauth:client:remove', 2, '', 0, '2019-01-03 08:19:40', '2019-01-03 16:20:36');



INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,1);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,2);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,11);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,12);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,13);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,14);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,21);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,22);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,23);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,24);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,25);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,26);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,27);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,28);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,29);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,30);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,31);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,32);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,33);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,34);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,35);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,36);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,37);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,38);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,39);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,40);

INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,3);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,51);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,52);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,53);
INSERT INTO `oauth_sys_role_menu`(`role_id`, `menu_id`) VALUES (1,54);