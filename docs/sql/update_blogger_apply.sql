-- ----------------------------
-- Table structure for `b_blogger_apply`
-- ----------------------------
DROP TABLE IF EXISTS `b_blogger_apply`;
CREATE TABLE `b_blogger_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `reason` varchar(500) NOT NULL COMMENT '申请理由',
  `contact` varchar(100) DEFAULT NULL COMMENT '联系方式',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '申请状态：0待审核，1已通过，2已拒绝',
  `audit_opinion` varchar(255) DEFAULT NULL COMMENT '审批意见',
  `audit_time` datetime DEFAULT NULL COMMENT '审批时间',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审批人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  PRIMARY KEY (`id`),
  KEY `idx_blogger_apply_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博主申请表';

-- ----------------------------
-- Menu configuration for blogger apply
-- ----------------------------
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1223, '博主申请', 1063, 19, 'blogger-apply', 'blog/blogger-apply/index', '', 'BloggerApply', 1, 0, 'C', '0', '0', 'blog:blogger:list', 'Stamp', 'admin', '2026-06-16 10:22:00', '', NULL, '博主申请管理菜单'),
(1224, '申请查询', 1223, 1, '', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:blogger:query', '#', 'admin', '2026-06-16 10:22:00', '', NULL, '博主申请详情查询权限'),
(1225, '申请审核', 1223, 2, '', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:blogger:audit', '#', 'admin', '2026-06-16 10:22:00', '', NULL, '博主申请审核权限');



