-- ----------------------------
-- Table structure for b_mobile_capability
-- ----------------------------
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `b_mobile_capability` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `client` varchar(20) NOT NULL COMMENT '客户端类型(mini/h5/app)',
  `review_safe_mode` tinyint(1) NOT NULL DEFAULT '1' COMMENT '小程序安全模式',
  `article_read_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '文章阅读',
  `search_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '搜索',
  `profile_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '个人资料',
  `favorite_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '收藏',
  `reading_history_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '阅读历史',
  `settings_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '体验设置',
  `policy_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '协议与政策',
  `about_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '关于本站',
  `friend_link_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '友情链接',
  `navigation_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '网站导航',
  `article_publish_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '写文章',
  `article_publish_role` varchar(50) DEFAULT 'blogger' COMMENT '允许写文章的最小角色',
  `comment_enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '评论与回复',
  `community_enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '社区Tab',
  `say_publish_enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发布说说',
  `treehole_enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '树洞投递',
  `interaction_center_enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '我的互动',
  `follow_enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '关注/粉丝',
  `message_center_enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '消息中心',
  `user_home_enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '用户主页',
  `user_upload_enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '普通用户上传图片/音频',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client` (`client`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='移动端能力配置表';

-- ----------------------------
-- Init data for b_mobile_capability
-- ----------------------------
INSERT INTO `b_mobile_capability` (`client`, `review_safe_mode`, `article_read_enabled`, `search_enabled`, `profile_enabled`, `favorite_enabled`, `reading_history_enabled`, `settings_enabled`, `policy_enabled`, `about_enabled`, `friend_link_enabled`, `navigation_enabled`, `article_publish_enabled`, `article_publish_role`, `comment_enabled`, `community_enabled`, `say_publish_enabled`, `treehole_enabled`, `interaction_center_enabled`, `follow_enabled`, `message_center_enabled`, `user_home_enabled`, `user_upload_enabled`)
VALUES 
('mini', 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 'blogger', 0, 0, 0, 0, 0, 0, 0, 0, 0),
('h5', 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 'blogger', 1, 1, 1, 1, 1, 1, 1, 1, 1),
('app', 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 'blogger', 1, 1, 1, 1, 1, 1, 1, 1, 1)
ON DUPLICATE KEY UPDATE `update_time` = NOW();

-- ----------------------------
-- Sys Menu Injection
-- ----------------------------
-- 插入“移动端权限管理”菜单，挂到“网站管理”(menu_id = 1061)
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES 
(2001, '移动端权限管理', 1061, 17, 'mobile-capability', 'blog/mobile/index', NULL, 'MobileCapability', 1, 0, 'C', '0', '0', 'blog:mobile:list', 'Iphone', 'admin', NOW(), '', NULL, '管理小程序端能力配置')
ON DUPLICATE KEY UPDATE
`menu_name` = VALUES(`menu_name`),
`parent_id` = VALUES(`parent_id`),
`order_num` = VALUES(`order_num`),
`path` = VALUES(`path`),
`component` = VALUES(`component`),
`query` = VALUES(`query`),
`route_name` = VALUES(`route_name`),
`is_frame` = VALUES(`is_frame`),
`is_cache` = VALUES(`is_cache`),
`menu_type` = VALUES(`menu_type`),
`visible` = VALUES(`visible`),
`status` = VALUES(`status`),
`perms` = VALUES(`perms`),
`icon` = VALUES(`icon`),
`remark` = VALUES(`remark`);

-- 插入“查询能力配置”按钮级权限
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES 
(2002, '移动端权限查询', 2001, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'blog:mobile:query', '#', 'admin', NOW(), '', NULL, '查询小程序能力配置按钮')
ON DUPLICATE KEY UPDATE
`menu_name` = VALUES(`menu_name`),
`parent_id` = VALUES(`parent_id`),
`order_num` = VALUES(`order_num`),
`path` = VALUES(`path`),
`component` = VALUES(`component`),
`query` = VALUES(`query`),
`route_name` = VALUES(`route_name`),
`is_frame` = VALUES(`is_frame`),
`is_cache` = VALUES(`is_cache`),
`menu_type` = VALUES(`menu_type`),
`visible` = VALUES(`visible`),
`status` = VALUES(`status`),
`perms` = VALUES(`perms`),
`icon` = VALUES(`icon`),
`remark` = VALUES(`remark`);

-- 插入“修改能力配置”按钮级权限
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES 
(2003, '移动端权限修改', 2001, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'blog:mobile:edit', '#', 'admin', NOW(), '', NULL, '修改小程序能力配置按钮')
ON DUPLICATE KEY UPDATE
`menu_name` = VALUES(`menu_name`),
`parent_id` = VALUES(`parent_id`),
`order_num` = VALUES(`order_num`),
`path` = VALUES(`path`),
`component` = VALUES(`component`),
`query` = VALUES(`query`),
`route_name` = VALUES(`route_name`),
`is_frame` = VALUES(`is_frame`),
`is_cache` = VALUES(`is_cache`),
`menu_type` = VALUES(`menu_type`),
`visible` = VALUES(`visible`),
`status` = VALUES(`status`),
`perms` = VALUES(`perms`),
`icon` = VALUES(`icon`),
`remark` = VALUES(`remark`);

INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES
(1, 2001),
(1, 2002),
(1, 2003);

-- 插入"新增能力配置"按钮级权限
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
(2004, '移动端权限新增', 2001, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'blog:mobile:add', '#', 'admin', NOW(), '', NULL, '新增小程序能力配置按钮')
ON DUPLICATE KEY UPDATE
`menu_name` = VALUES(`menu_name`),
`parent_id` = VALUES(`parent_id`),
`order_num` = VALUES(`order_num`),
`path` = VALUES(`path`),
`component` = VALUES(`component`),
`query` = VALUES(`query`),
`route_name` = VALUES(`route_name`),
`is_frame` = VALUES(`is_frame`),
`is_cache` = VALUES(`is_cache`),
`menu_type` = VALUES(`menu_type`),
`visible` = VALUES(`visible`),
`status` = VALUES(`status`),
`perms` = VALUES(`perms`),
`icon` = VALUES(`icon`),
`remark` = VALUES(`remark`);

-- 插入"删除能力配置"按钮级权限
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
(2005, '移动端权限删除', 2001, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'blog:mobile:remove', '#', 'admin', NOW(), '', NULL, '删除小程序能力配置按钮')
ON DUPLICATE KEY UPDATE
`menu_name` = VALUES(`menu_name`),
`parent_id` = VALUES(`parent_id`),
`order_num` = VALUES(`order_num`),
`path` = VALUES(`path`),
`component` = VALUES(`component`),
`query` = VALUES(`query`),
`route_name` = VALUES(`route_name`),
`is_frame` = VALUES(`is_frame`),
`is_cache` = VALUES(`is_cache`),
`menu_type` = VALUES(`menu_type`),
`visible` = VALUES(`visible`),
`status` = VALUES(`status`),
`perms` = VALUES(`perms`),
`icon` = VALUES(`icon`),
`remark` = VALUES(`remark`);

INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES
(1, 2004),
(1, 2005);
