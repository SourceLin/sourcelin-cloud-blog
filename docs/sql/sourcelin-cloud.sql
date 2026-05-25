-- Sourcelin Cloud Blog database initialization
-- Generated from local database schema. Business/demo/runtime data is intentionally excluded.
-- Required seed data only: system config, dicts, menus, roles, admin user, jobs, site config and navbar.
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
CREATE DATABASE IF NOT EXISTS `sourcelin-cloud` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `sourcelin-cloud`;

-- ----------------------------
-- Table structure for `announcement`
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` mediumtext NOT NULL COMMENT '内容',
  `scope_type` varchar(16) NOT NULL COMMENT '作用域 all/role/segment/assign',
  `scope_payload` json DEFAULT NULL COMMENT '作用域参数',
  `publish_status` char(1) NOT NULL DEFAULT '0' COMMENT '0草稿 1发布 2下线',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `created_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` int(11) DEFAULT '0' COMMENT '软删标记',
  PRIMARY KEY (`id`),
  KEY `idx_announcement_publish` (`publish_status`,`publish_time`,`expire_time`,`deleted`),
  KEY `idx_announcement_scope` (`scope_type`,`deleted`),
  KEY `idx_announcement_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客前台通知公告';

-- ----------------------------
-- Table structure for `announcement_read`
-- ----------------------------
DROP TABLE IF EXISTS `announcement_read`;
CREATE TABLE `announcement_read` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `announcement_id` bigint(20) NOT NULL COMMENT '公告ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `read_time` datetime NOT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_announcement` (`user_id`,`announcement_id`),
  KEY `idx_announcement_id` (`announcement_id`),
  KEY `idx_user_read_time` (`user_id`,`read_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客前台公告已读';

-- ----------------------------
-- Table structure for `b_article`
-- ----------------------------
DROP TABLE IF EXISTS `b_article`;
CREATE TABLE `b_article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类id',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `avatar` varchar(255) DEFAULT NULL COMMENT '封面URL，外链时使用，优先级低于avatar_file_id',
  `avatar_file_id` bigint(20) DEFAULT NULL COMMENT '封面文件ID',
  `summary` text COMMENT '简介',
  `content` text COMMENT '内容',
  `read_auth` int(11) DEFAULT NULL COMMENT '阅读权限',
  `status` bigint(20) DEFAULT NULL COMMENT '文章状态',
  `is_comment` int(11) DEFAULT NULL COMMENT '是否启用评论',
  `is_recommend` int(11) DEFAULT NULL COMMENT '是否推荐',
  `is_top` int(11) DEFAULT NULL COMMENT '是否置顶',
  `is_original` int(11) DEFAULT NULL COMMENT '是否原创',
  `original_url` varchar(255) DEFAULT NULL COMMENT '转载地址',
  `view_count` bigint(20) DEFAULT '0' COMMENT '浏览量',
  `like_count` bigint(20) DEFAULT '0' COMMENT '点赞数',
  `collect_count` bigint(20) DEFAULT '0' COMMENT '收藏数',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_b_article_avatar_file_id` (`avatar_file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客文章对象';

-- ----------------------------
-- Table structure for `b_article_tag`
-- ----------------------------
DROP TABLE IF EXISTS `b_article_tag`;
CREATE TABLE `b_article_tag` (
  `article_id` bigint(20) NOT NULL COMMENT '文章ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`article_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`) USING BTREE,
  KEY `idx_article_id` (`article_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章与标签关联表';

-- ----------------------------
-- Table structure for `b_category`
-- ----------------------------
DROP TABLE IF EXISTS `b_category`;
CREATE TABLE `b_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '博客分类ID',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `summary` text COMMENT '分类简介',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  `click_count` bigint(20) DEFAULT '0' COMMENT '点击量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客分类对象';

-- ----------------------------
-- Table structure for `b_collect`
-- ----------------------------
DROP TABLE IF EXISTS `b_collect`;
CREATE TABLE `b_collect` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `target_type` varchar(20) NOT NULL COMMENT '目标类型(article/say)',
  `target_id` bigint(20) NOT NULL COMMENT '目标id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_type`,`target_id`) USING BTREE,
  KEY `idx_user_type_time` (`user_id`,`target_type`,`create_time`) USING BTREE,
  KEY `idx_target` (`target_type`,`target_id`,`deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏对象';

-- ----------------------------
-- Table structure for `b_comment`
-- ----------------------------
DROP TABLE IF EXISTS `b_comment`;
CREATE TABLE `b_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `parent_user_id` bigint(20) DEFAULT NULL COMMENT '父发表用户名ID',
  `article_id` bigint(20) NOT NULL COMMENT '文章id',
  `parent_comment_id` bigint(20) DEFAULT NULL COMMENT '父评论id',
  `content` text COMMENT '评论内容',
  `comment_info` varchar(500) DEFAULT NULL COMMENT '评论额外信息',
  `floor_comment_id` bigint(20) DEFAULT NULL COMMENT '楼层评论ID',
  `like_count` bigint(20) DEFAULT '0' COMMENT '点赞数',
  `status` int(11) DEFAULT '0' COMMENT '审核状态（0待审核 1通过 2拒绝）',
  `source` varchar(90) DEFAULT NULL COMMENT '评论来源（博客文章、关于本站、留言板、友链）',
  `browser` varchar(50) DEFAULT NULL COMMENT '浏览器名',
  `browser_version` varchar(50) DEFAULT NULL COMMENT '浏览器版本',
  `system` varchar(50) DEFAULT NULL COMMENT '系统名',
  `system_version` varchar(50) DEFAULT NULL COMMENT '系统名版本',
  `ip_address` varchar(70) DEFAULT NULL COMMENT 'ip地址',
  `ip_source` varchar(200) DEFAULT NULL COMMENT 'ip来源',
  `deleted` int(1) DEFAULT '0' COMMENT '是否删除（0未删除 1已删除）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论管理对象';

-- ----------------------------
-- Table structure for `b_comment_moderation`
-- ----------------------------
DROP TABLE IF EXISTS `b_comment_moderation`;
CREATE TABLE `b_comment_moderation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `comment_id` bigint(20) NOT NULL COMMENT '评论ID b_comment.id',
  `decision` varchar(32) NOT NULL COMMENT 'PASS/REJECT/REVIEW/AI_REVIEW',
  `lexicon_version` int(11) NOT NULL DEFAULT '0' COMMENT '词库版本快照',
  `trace_id` varchar(64) DEFAULT NULL COMMENT '链路追踪/异步AI预留',
  `hit_keywords` json DEFAULT NULL COMMENT '命中关键词JSON数组',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_id` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论机审记录';

-- ----------------------------
-- Table structure for `b_follow`
-- ----------------------------
DROP TABLE IF EXISTS `b_follow`;
CREATE TABLE `b_follow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `follow_user_id` bigint(20) NOT NULL COMMENT '关注的用户id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_follow_user_id` (`follow_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注对象（用户关注）';

-- ----------------------------
-- Table structure for `b_friend_link`
-- ----------------------------
DROP TABLE IF EXISTS `b_friend_link`;
CREATE TABLE `b_friend_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL，外链时使用，优先级低于avatar_file_id',
  `avatar_file_id` bigint(20) DEFAULT NULL COMMENT '头像文件ID，本地上传时使用，优先使用此字段',
  `url` varchar(90) NOT NULL COMMENT '链接',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `cover` varchar(64) DEFAULT NULL COMMENT '封面',
  `email` varchar(75) DEFAULT NULL COMMENT '邮箱',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  `label` varchar(50) DEFAULT NULL COMMENT '标签',
  `category` tinyint(4) NOT NULL COMMENT '分类(1推荐 2精选 3更多)',
  `status` tinyint(4) NOT NULL COMMENT '状态(0申请 1上架 2下架)',
  `reason` varchar(255) DEFAULT NULL COMMENT '下架原因',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  PRIMARY KEY (`id`),
  KEY `idx_b_friend_link_avatar_file_id` (`avatar_file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='友链对象';

-- ----------------------------
-- Table structure for `b_like_record`
-- ----------------------------
DROP TABLE IF EXISTS `b_like_record`;
CREATE TABLE `b_like_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_type` varchar(20) NOT NULL COMMENT '目标类型(article/say/treehole)',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `deleted` int(11) NOT NULL DEFAULT '0' COMMENT '0有效 1取消',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_type`,`target_id`) USING BTREE,
  KEY `idx_target` (`target_type`,`target_id`,`deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录';

-- ----------------------------
-- Table structure for `b_navigation`
-- ----------------------------
DROP TABLE IF EXISTS `b_navigation`;
CREATE TABLE `b_navigation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) NOT NULL COMMENT '名称',
  `url` varchar(512) NOT NULL COMMENT '链接地址',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `icon` varchar(128) DEFAULT NULL COMMENT '图标',
  `cover` varchar(512) DEFAULT NULL COMMENT '封面地址',
  `category` varchar(128) NOT NULL COMMENT '分类',
  `source` varchar(128) DEFAULT NULL COMMENT '来源',
  `is_recommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推荐：0否 1是',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0下架 1上架',
  `order_num` int(11) NOT NULL DEFAULT '0' COMMENT '排序值',
  `click_count` bigint(20) NOT NULL DEFAULT '0' COMMENT '点击数',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_b_navigation_category_status` (`category`,`status`),
  KEY `idx_b_navigation_order_num` (`order_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站导航目录';

-- ----------------------------
-- Table structure for `b_say`
-- ----------------------------
DROP TABLE IF EXISTS `b_say`;
CREATE TABLE `b_say` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `content` text COMMENT '说说内容',
  `images` varchar(500) DEFAULT NULL COMMENT '图片URL，外链时使用，多个用逗号分隔，优先级低于image_file_ids',
  `image_file_ids` varchar(500) DEFAULT NULL COMMENT '图片文件ID，多个用逗号分隔',
  `like_count` bigint(20) DEFAULT '0' COMMENT '点赞数',
  `comment_count` bigint(20) DEFAULT '0' COMMENT '评论数',
  `collect_count` bigint(20) DEFAULT '0' COMMENT '收藏数',
  `status` bigint(20) DEFAULT '0' COMMENT '状态（0:正常 1:置顶 2:删除）',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除（0:正常 1:删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='说说/瞬间表';

-- ----------------------------
-- Table structure for `b_tag`
-- ----------------------------
DROP TABLE IF EXISTS `b_tag`;
CREATE TABLE `b_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `summary` text COMMENT '简介',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  `click_count` bigint(20) DEFAULT '0' COMMENT '点击量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客标签对象';

-- ----------------------------
-- Table structure for `b_treehole`
-- ----------------------------
DROP TABLE IF EXISTS `b_treehole`;
CREATE TABLE `b_treehole` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id（可为空，匿名）',
  `content` text COMMENT '树洞内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '图片地址（多个用逗号分隔）',
  `like_count` bigint(20) DEFAULT '0' COMMENT '点赞数',
  `comment_count` bigint(20) DEFAULT '0' COMMENT '评论数',
  `collect_count` bigint(20) DEFAULT '0' COMMENT '收藏数',
  `status` bigint(20) DEFAULT '0' COMMENT '状态（0:正常 1:置顶 2:删除）',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除（0:正常 1:删除）',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `browser` varchar(50) DEFAULT NULL COMMENT '浏览器',
  `system` varchar(50) DEFAULT NULL COMMENT '操作系统',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='树洞表';

-- ----------------------------
-- Table structure for `b_user`
-- ----------------------------
DROP TABLE IF EXISTS `b_user`;
CREATE TABLE `b_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(75) NOT NULL COMMENT '用户账号',
  `nickname` varchar(100) DEFAULT NULL COMMENT '用户昵称',
  `password` varchar(100) DEFAULT NULL COMMENT '用户密码',
  `user_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户类型（1普通用户，2博主）',
  `email` varchar(255) DEFAULT NULL COMMENT '用户邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '用户绑定手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `avatar_file_id` bigint(20) DEFAULT NULL COMMENT '头像文件ID',
  `sex` tinyint(4) DEFAULT NULL COMMENT '用户性别（0男，1女，2未知）',
  `birthday` date DEFAULT NULL COMMENT '出生年月',
  `introduction` varchar(1200) DEFAULT NULL COMMENT '简介(最多1200字)',
  `user_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0：启用，1：停用',
  `praise` bigint(20) DEFAULT '0' COMMENT '赞赏数（点赞数）',
  `follow` bigint(20) DEFAULT '0' COMMENT '订阅数（关注数）',
  `login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在，2代表删除）',
  `uuid` varchar(36) DEFAULT NULL COMMENT '设备唯一标识(平台uuid)',
  `bind_qq_id` varchar(50) DEFAULT NULL COMMENT 'QQ绑定的id',
  `bind_wechat_id` varchar(50) DEFAULT NULL COMMENT '绑定微信id',
  `bind_sina_id` varchar(50) DEFAULT NULL COMMENT '绑定微博id',
  `bind_alipay_id` varchar(50) DEFAULT NULL COMMENT '绑定支付宝id',
  `realname_auth` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否实名认证（1未验证，2已验证）',
  `open_id` varchar(100) DEFAULT NULL COMMENT '微信登录openid',
  `session_key` varchar(100) DEFAULT NULL COMMENT '微信登录会话KEY',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`) USING BTREE,
  KEY `idx_email` (`email`) USING BTREE,
  KEY `idx_b_user_avatar_file_id` (`avatar_file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ----------------------------
-- Table structure for `b_web_config`
-- ----------------------------
DROP TABLE IF EXISTS `b_web_config`;
CREATE TABLE `b_web_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `logo` varchar(255) DEFAULT NULL COMMENT '网站logo',
  `tourist_avatar` varchar(255) DEFAULT NULL COMMENT '游客头像',
  `name` varchar(100) NOT NULL COMMENT '网站名称',
  `summary` text COMMENT '简介',
  `keyword` varchar(255) DEFAULT NULL COMMENT '关键字',
  `record_num` varchar(100) DEFAULT NULL COMMENT '备案号',
  `web_url` varchar(255) DEFAULT NULL COMMENT '网站地址',
  `notice` text COMMENT '公告',
  `ali_pay` varchar(255) DEFAULT NULL COMMENT '支付宝收款码FileId',
  `weixin_pay` varchar(255) DEFAULT NULL COMMENT '微信收款码FileId',
  `github` varchar(255) DEFAULT NULL COMMENT 'github地址',
  `gitee` varchar(255) DEFAULT NULL COMMENT 'gitee地址',
  `qq_number` varchar(20) DEFAULT NULL COMMENT 'QQ号',
  `qq_group` varchar(20) DEFAULT NULL COMMENT 'QQ群',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `wechat` varchar(50) DEFAULT NULL COMMENT '微信',
  `show_list` varchar(255) DEFAULT NULL COMMENT '显示的列表',
  `login_type_list` varchar(255) DEFAULT NULL COMMENT '登录方式列表',
  `open_comment` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否开启评论',
  `open_admiration` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否开启赞赏',
  `author` varchar(100) DEFAULT NULL COMMENT '作者',
  `author_info` text COMMENT '作者简介',
  `author_avatar` varchar(255) DEFAULT NULL COMMENT '作者头像',
  `about_me` text COMMENT '关于我',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站配置表';

-- ----------------------------
-- Table structure for `b_web_navbar`
-- ----------------------------
DROP TABLE IF EXISTS `b_web_navbar`;
CREATE TABLE `b_web_navbar` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '导航栏ID',
  `name` varchar(50) NOT NULL COMMENT '导航栏名称',
  `summary` varchar(255) DEFAULT NULL COMMENT '导航栏简介',
  `icon` varchar(45) DEFAULT NULL COMMENT '导航栏图标',
  `path` varchar(80) DEFAULT NULL COMMENT '路由地址',
  `component` varchar(90) DEFAULT NULL COMMENT '组件路径',
  `is_frame` varchar(2) NOT NULL DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `type` char(1) NOT NULL COMMENT '导航栏类型（M目录 C菜单）',
  `visible` varchar(2) NOT NULL DEFAULT '0' COMMENT '导航栏状态（0显示 1隐藏）',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID',
  `order_num` int(11) NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `ancestors` varchar(200) DEFAULT NULL COMMENT '祖级列表',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门户导航栏对象';

-- ----------------------------
-- Table structure for `file_info`
-- ----------------------------
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info` (
  `file_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `file_name` varchar(255) NOT NULL COMMENT '存储文件名',
  `original_name` varchar(255) DEFAULT NULL COMMENT '原始文件名',
  `file_ext` varchar(32) DEFAULT NULL COMMENT '文件扩展名',
  `content_type` varchar(128) DEFAULT NULL COMMENT 'MIME类型',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `file_hash` varchar(128) DEFAULT NULL COMMENT '文件哈希值',
  `storage_type` varchar(32) NOT NULL COMMENT '存储类型',
  `bucket_name` varchar(128) DEFAULT NULL COMMENT '存储桶名称',
  `storage_path` varchar(500) NOT NULL COMMENT '存储路径',
  `access_url` varchar(500) NOT NULL COMMENT '访问地址',
  `access_scope` varchar(32) DEFAULT 'PUBLIC' COMMENT '访问范围',
  `biz_type` varchar(64) DEFAULT NULL COMMENT '业务类型',
  `biz_id` varchar(64) DEFAULT NULL COMMENT '业务ID',
  `owner_type` varchar(32) DEFAULT NULL COMMENT '所有者类型',
  `owner_id` bigint(20) DEFAULT NULL COMMENT '所有者ID',
  `status` varchar(32) NOT NULL DEFAULT 'TEMP' COMMENT '状态：TEMP/ACTIVE/PENDING_DELETE',
  `ref_count` int(11) NOT NULL DEFAULT '0' COMMENT '引用计数',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `last_access_time` datetime DEFAULT NULL COMMENT '最后访问时间',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`file_id`),
  KEY `idx_file_info_biz` (`biz_type`,`biz_id`),
  KEY `idx_file_info_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件中心主表';

-- ----------------------------
-- Table structure for `gen_table`
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table` (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='代码生成业务表';

-- ----------------------------
-- Table structure for `gen_table_column`
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column` (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint(20) DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='代码生成业务表字段';

-- ----------------------------
-- Table structure for `qrtz_blob_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='Blob类型的触发器表';

-- ----------------------------
-- Table structure for `qrtz_calendars`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='日历信息表';

-- ----------------------------
-- Table structure for `qrtz_cron_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='Cron类型的触发器表';

-- ----------------------------
-- Table structure for `qrtz_fired_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint(13) NOT NULL COMMENT '触发的时间',
  `sched_time` bigint(13) NOT NULL COMMENT '定时器制定的时间',
  `priority` int(11) NOT NULL COMMENT '优先级',
  `state` varchar(16) NOT NULL COMMENT '状态',
  `job_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='已触发的触发器表';

-- ----------------------------
-- Table structure for `qrtz_job_details`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) NOT NULL COMMENT '任务组名',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='任务详细信息表';

-- ----------------------------
-- Table structure for `qrtz_locks`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='存储的悲观锁信息表';

-- ----------------------------
-- Table structure for `qrtz_paused_trigger_grps`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='暂停的触发器表';

-- ----------------------------
-- Table structure for `qrtz_scheduler_state`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint(13) NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint(13) NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='调度器状态表';

-- ----------------------------
-- Table structure for `qrtz_simple_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint(7) NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint(12) NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint(10) NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='简单触发器的信息表';

-- ----------------------------
-- Table structure for `qrtz_simprop_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int(11) DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int(11) DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint(20) DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint(20) DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='同步机制的行锁表';

-- ----------------------------
-- Table structure for `qrtz_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint(13) DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint(13) DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int(11) DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) NOT NULL COMMENT '触发器的类型',
  `start_time` bigint(13) NOT NULL COMMENT '开始时间',
  `end_time` bigint(13) DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint(2) DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`) USING BTREE,
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='触发器详细信息表';

-- ----------------------------
-- Table structure for `sys_config`
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='参数配置表';

-- ----------------------------
-- Table structure for `sys_dept`
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='部门表';

-- ----------------------------
-- Table structure for `sys_dict_data`
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='字典数据表';

-- ----------------------------
-- Table structure for `sys_dict_type`
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE KEY `dict_type` (`dict_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='字典类型表';

-- ----------------------------
-- Table structure for `sys_job`
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='定时任务调度表';

-- ----------------------------
-- Table structure for `sys_job_log`
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='定时任务调度日志表';

-- ----------------------------
-- Table structure for `sys_logininfor`
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示信息',
  `access_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE,
  KEY `idx_sys_logininfor_s` (`status`) USING BTREE,
  KEY `idx_sys_logininfor_lt` (`access_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='系统访问记录';

-- ----------------------------
-- Table structure for `sys_menu`
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) NOT NULL DEFAULT '' COMMENT '路由名称',
  `is_frame` int(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='菜单权限表';

-- ----------------------------
-- Table structure for `sys_notice`
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='通知公告表';

-- ----------------------------
-- Table structure for `sys_notice_read`
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice_read`;
CREATE TABLE `sys_notice_read` (
  `read_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '已读主键',
  `notice_id` int(4) NOT NULL COMMENT '公告id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `read_time` datetime NOT NULL COMMENT '阅读时间',
  PRIMARY KEY (`read_id`),
  UNIQUE KEY `uk_user_notice` (`user_id`,`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告已读记录表';

-- ----------------------------
-- Table structure for `sys_oper_log`
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int(1) DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint(20) DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  KEY `idx_sys_oper_log_bt` (`business_type`) USING BTREE,
  KEY `idx_sys_oper_log_s` (`status`) USING BTREE,
  KEY `idx_sys_oper_log_ot` (`oper_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='操作日志记录';

-- ----------------------------
-- Table structure for `sys_post`
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='岗位信息表';

-- ----------------------------
-- Table structure for `sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色信息表';

-- ----------------------------
-- Table structure for `sys_role_dept`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色和部门关联表';

-- ----------------------------
-- Table structure for `sys_role_menu`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色和菜单关联表';

-- ----------------------------
-- Table structure for `sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL，外链时使用，优先级低于avatar_file_id',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar_file_id` bigint(20) DEFAULT NULL COMMENT '头像文件ID',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE,
  KEY `idx_sys_user_avatar_file_id` (`avatar_file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户信息表';

-- ----------------------------
-- Table structure for `sys_user_blog_relation`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_blog_relation`;
CREATE TABLE `sys_user_blog_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sys_user_id` bigint(20) NOT NULL COMMENT '后台用户ID',
  `blog_user_id` bigint(20) NOT NULL COMMENT '博客用户ID',
  `relation_type` tinyint(4) DEFAULT '1' COMMENT '关联类型: 1绑定',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user` (`sys_user_id`),
  UNIQUE KEY `uk_blog_user` (`blog_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台用户与博客用户关联表';

-- ----------------------------
-- Table structure for `sys_user_post`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户与岗位关联表';

-- ----------------------------
-- Table structure for `sys_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户和角色关联表';

-- ----------------------------
-- Seed data for `b_web_config`
-- ----------------------------
INSERT INTO `b_web_config` (`id`, `logo`, `tourist_avatar`, `name`, `summary`, `keyword`, `record_num`, `web_url`, `notice`, `ali_pay`, `weixin_pay`, `github`, `gitee`, `qq_number`, `qq_group`, `email`, `wechat`, `show_list`, `login_type_list`, `open_comment`, `open_admiration`, `author`, `author_info`, `author_avatar`, `about_me`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `deleted`) VALUES
(1, '', '', 'Sourcelin Blog', '分享美好，记录生活', '博客,技术,生活', '', '/', '欢迎访问 Sourcelin Blog。', '', '', '', '', '', '', '', '', '4,3,6,5,1,2', '1,2', 1, 0, 'SourceLin', 'Sourcelin Blog 作者', '', '这里记录技术与生活。', 'admin', '2026-04-02 10:55:57', 'admin', '2026-04-24 01:53:56', 'open_source_seed', 0);

-- ----------------------------
-- Seed data for `b_web_navbar`
-- ----------------------------
INSERT INTO `b_web_navbar` (`id`, `name`, `summary`, `icon`, `path`, `component`, `is_frame`, `type`, `visible`, `parent_id`, `order_num`, `ancestors`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`, `deleted`) VALUES
(62001, '首页', '博客首页', 'home', '/', NULL, '1', 'M', '0', 0, 1, NULL, 'admin', '2026-04-02 09:09:39', 'admin', '2026-04-24 01:05:09', 'open_source_seed', 0),
(62002, '热门', '热门内容', 'sparkles', '/hot', NULL, '1', 'M', '0', 0, 2, NULL, 'admin', '2026-04-02 09:09:39', 'admin', '2026-04-02 09:09:39', 'open_source_seed', 0),
(62003, '文章', '文章索引', 'archive', '/archive', NULL, '1', 'M', '0', 0, 3, NULL, 'admin', '2026-04-02 09:09:39', 'admin', '2026-04-02 09:09:39', 'open_source_seed', 0),
(62004, '说说', '碎片动态', 'sparkles', '/say', NULL, '1', 'M', '0', 0, 4, NULL, 'admin', '2026-04-02 09:09:39', 'admin', '2026-04-02 09:09:39', 'open_source_seed', 0),
(62006, '树洞', '匿名空间', 'chatbubbles', '/treehole', NULL, '1', 'M', '0', 0, 6, NULL, 'admin', '2026-04-02 09:09:39', 'admin', '2026-04-02 09:09:39', 'open_source_seed', 0),
(62007, '关于', '关于本站', 'information', '/about', NULL, '1', 'M', '0', 0, 7, NULL, 'admin', '2026-04-02 09:09:39', 'admin', '2026-04-02 09:09:39', 'open_source_seed', 0),
(62031, '文章归档', '时间归档', 'archive', '/archive', NULL, '1', 'C', '0', 62003, 1, NULL, 'admin', '2026-04-02 09:09:39', 'admin', '2026-04-02 09:09:39', 'open_source_seed', 0),
(62032, '文章分类', '按分类浏览', 'category', '/categories', NULL, '1', 'C', '0', 62003, 2, NULL, 'admin', '2026-04-02 09:09:39', 'admin', '2026-04-02 09:09:39', 'open_source_seed', 0),
(62033, '文章标签', '按标签浏览', 'tag', '/tags', NULL, '1', 'C', '0', 62003, 3, NULL, 'admin', '2026-04-02 09:09:39', 'admin', '2026-04-02 09:09:39', 'open_source_seed', 0),
(62061, '关于本站', '站点介绍', 'information', '/about', NULL, '1', 'C', '0', 62007, 1, NULL, 'admin', '2026-04-02 09:09:39', 'admin', '2026-04-02 09:09:39', 'open_source_seed', 0),
(62062, '网站导航', '站点与工具导航', 'navigation', '/navigation', NULL, '1', 'C', '0', 62007, 2, NULL, 'admin', '2026-04-02 09:09:39', 'admin', '2026-04-02 09:09:39', 'open_source_seed', 0),
(62063, '友情链接', '友情链接页面', 'link', '/links', NULL, '1', 'C', '0', 62007, 3, NULL, 'admin', '2026-04-02 09:09:39', 'admin', '2026-04-02 09:09:39', 'open_source_seed', 0),
(62064, '后台管理', NULL, '', 'http://localhost:81', NULL, '0', 'C', '0', 62007, 8, NULL, NULL, '2026-04-16 23:10:52', NULL, '2026-04-21 02:37:13', NULL, 0);

-- ----------------------------
-- Seed data for `sys_config`
-- ----------------------------
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2023-10-17 14:29:37', '1', '2026-04-10 09:49:50', '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),
(2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2023-10-17 14:29:37', '', NULL, '初始化密码 123456'),
(3, '主框架页-侧边栏主题', 'sys.index.sideTheme', '\"theme-dark\"', 'N', 'admin', '2023-10-17 14:29:37', '', '2026-04-16 22:14:12', '深色主题theme-dark，浅色主题theme-light'),
(4, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2023-10-17 14:29:37', '', NULL, '是否开启注册用户功能（true开启，false关闭）'),
(5, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2023-10-17 14:29:37', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),
(6, '用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 'Y', 'admin', '2026-03-30 13:35:46', '', NULL, '0：关闭；1：若从未更新过密码(pwd_update_date 为空)则登录后提示修改'),
(7, '用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 'Y', 'admin', '2026-03-30 13:35:47', '', NULL, '0：不限制；填写大于0小于365的正整数为有效天数，超期登录后提示修改'),
(11, '管理后台UI默认配置', 'sys.admin.ui.defaults', '{\"themePreference\":\"system\",\"layout\":\"left\",\"sidebarAppearance\":\"classic\",\"themeColor\":\"#4B55FA\",\"showTagsView\":true,\"showAppLogo\":true,\"pageSwitchingAnimation\":\"fade-slide\",\"componentSize\":\"default\"}', 'Y', 'system', '2026-04-17 18:59:30', '', '2026-04-23 20:35:05', '管理后台默认 UI 配置'),
(12, '管理后台UI策略', 'sys.admin.ui.policy', '{\"allowCustomThemeColor\":true,\"allowUserLayoutSwitch\":true,\"allowUserSidebarAppearanceSwitch\":true}', 'Y', 'system', '2026-04-17 18:59:30', '', '2026-04-23 20:35:06', '管理后台 UI 策略'),
(13, '管理后台主题色预设', 'sys.admin.ui.presets', '[\"#4B55FA\",\"#0F766E\",\"#C2410C\",\"#1D4ED8\",\"#BE185D\"]', 'Y', 'system', '2026-04-17 18:59:30', '', '2026-04-23 20:35:06', '管理后台主题色预设');

-- ----------------------------
-- Seed data for `sys_dept`
-- ----------------------------
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
(100, 0, '0', '圆圈科技', 0, 'SourceLin', '15888888888', 'sourcelin_0201@qq.com', '0', '0', 'admin', '2026-02-01 00:00:00', 'admin', '2026-02-01 00:00:00'),
(101, 100, '0,100', '厦门总公司', 1, 'SourceLin', '15888888888', 'sourcelin_0201@qq.com', '0', '0', 'admin', '2026-02-01 00:00:00', 'admin', '2026-02-01 00:00:00'),
(102, 100, '0,100', '漳州分公司', 2, 'SourceLin', '15888888888', 'sourcelin_0201@qq.com', '0', '0', 'admin', '2026-02-01 00:00:00', 'admin', '2026-02-01 00:00:00'),
(103, 101, '0,100,101', '研发部门', 1, 'SourceLin', '15888888888', 'sourcelin_0201@qq.com', '0', '0', 'admin', '2026-02-01 00:00:00', '', '2026-02-01 00:00:00'),
(104, 101, '0,100,101', '市场部门', 2, 'SourceLin', '15888888888', 'sourcelin_0201@qq.com', '0', '0', 'admin', '2026-02-01 00:00:00', '', '2026-02-01 00:00:00'),
(105, 101, '0,100,101', '测试部门', 3, 'SourceLin', '15888888888', 'sourcelin_0201@qq.com', '0', '0', 'admin', '2026-02-01 00:00:00', '', '2026-02-01 00:00:00'),
(106, 101, '0,100,101', '财务部门', 4, 'SourceLin', '15888888888', 'sourcelin_0201@qq.com', '0', '0', 'admin', '2026-02-01 00:00:00', '', '2026-02-01 00:00:00'),
(107, 101, '0,100,101', '运维部门', 5, 'SourceLin', '15888888888', 'sourcelin_0201@qq.com', '0', '0', 'admin', '2026-02-01 00:00:00', '', '2026-02-01 00:00:00'),
(108, 102, '0,100,102', '市场部门', 1, 'SourceLin', '15888888888', 'sourcelin_0201@qq.com', '0', '0', 'admin', '2026-02-01 00:00:00', '', '2026-02-01 00:00:00'),
(109, 102, '0,100,102', '财务部门', 2, 'SourceLin', '15888888888', 'sourcelin_0201@qq.com', '0', '0', 'admin', '2026-02-01 00:00:00', '', '2026-02-01 00:00:00');

-- ----------------------------
-- Seed data for `sys_dict_data`
-- ----------------------------
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '性别男'),
(2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '性别女'),
(3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '性别未知'),
(4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '显示菜单'),
(5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '隐藏菜单'),
(6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '正常状态'),
(7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '停用状态'),
(8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '正常状态'),
(9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '停用状态'),
(10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '默认分组'),
(11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '系统分组'),
(12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '系统默认是'),
(13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '系统默认否'),
(14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '通知'),
(15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '公告'),
(16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '正常状态'),
(17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '关闭状态'),
(18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '其他操作'),
(19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '新增操作'),
(20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '修改操作'),
(21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '删除操作'),
(22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '授权操作'),
(23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '导出操作'),
(24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '导入操作'),
(25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '强退操作'),
(26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '生成操作'),
(27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '清空操作'),
(28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '正常状态'),
(29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '停用状态'),
(37, 1, '博客文章', 'article', 'blog_comment_source', '', '', 'N', '0', 'admin', '2026-04-05 12:35:29', '', NULL, NULL),
(38, 2, '说说', 'say', 'blog_comment_source', '', '', 'N', '0', 'admin', '2026-04-05 12:35:29', '', NULL, NULL),
(39, 3, '树洞', 'treehole', 'blog_comment_source', '', '', 'N', '0', 'admin', '2026-04-05 12:35:29', '', NULL, NULL),
(40, 1, '推荐', '1', 'blog_friend_category', '', 'primary', 'N', '0', 'admin', '2026-04-05 12:35:36', '', NULL, NULL),
(41, 2, '精选', '2', 'blog_friend_category', '', 'success', 'N', '0', 'admin', '2026-04-05 12:35:36', '', NULL, NULL),
(42, 3, '更多', '3', 'blog_friend_category', '', 'info', 'N', '0', 'admin', '2026-04-05 12:35:36', '', NULL, NULL),
(43, 1, '申请', '0', 'blog_friend_status', '', 'warning', 'N', '0', 'admin', '2026-04-05 12:35:43', '', NULL, NULL),
(44, 2, '上架', '1', 'blog_friend_status', '', 'success', 'N', '0', 'admin', '2026-04-05 12:35:43', '', NULL, NULL),
(45, 3, '下架', '2', 'blog_friend_status', '', 'danger', 'N', '0', 'admin', '2026-04-05 12:35:43', '', NULL, NULL),
(46, 1, '公开', '1', 'blog_read_auth', '', 'success', 'Y', '0', 'admin', '2026-04-05 12:35:52', '', NULL, NULL),
(47, 2, '登录可读', '2', 'blog_read_auth', '', 'primary', 'N', '0', 'admin', '2026-04-05 12:35:52', '', NULL, NULL),
(48, 3, '评论后可读', '3', 'blog_read_auth', '', 'info', 'N', '0', 'admin', '2026-04-05 12:35:52', '', NULL, NULL),
(49, 4, '验证后可读', '4', 'blog_read_auth', '', 'warning', 'N', '0', 'admin', '2026-04-05 12:35:52', '', NULL, NULL),
(50, 5, 'VIP可读', '5', 'blog_read_auth', '', 'warning', 'N', '0', 'admin', '2026-04-05 12:35:52', '', NULL, NULL),
(51, 6, '付费后可读', '6', 'blog_read_auth', '', 'danger', 'N', '0', 'admin', '2026-04-05 12:35:52', '', NULL, NULL),
(52, 7, '点赞后可读', '7', 'blog_read_auth', '', 'info', 'N', '0', 'admin', '2026-04-05 12:35:52', '', NULL, NULL),
(53, 8, '收藏后可读', '8', 'blog_read_auth', '', 'info', 'N', '0', 'admin', '2026-04-05 12:35:52', '', NULL, NULL),
(54, 9, '关注后可读', '9', 'blog_read_auth', '', 'info', 'N', '0', 'admin', '2026-04-05 12:35:52', '', NULL, NULL),
(55, 1, '审核中', '1', 'blog_status', '', 'warning', 'N', '0', 'admin', '2026-04-05 12:35:59', '', NULL, NULL),
(56, 2, '已发布', '2', 'blog_status', '', 'success', 'N', '0', 'admin', '2026-04-05 12:35:59', '', NULL, NULL),
(57, 3, '草稿', '3', 'blog_status', '', 'info', 'N', '0', 'admin', '2026-04-05 12:35:59', '', NULL, NULL),
(58, 1, '否', '0', 'blog_comment', '', 'info', 'N', '0', 'admin', '2026-04-05 12:36:08', '', NULL, NULL),
(59, 2, '是', '1', 'blog_comment', '', 'success', 'N', '0', 'admin', '2026-04-05 12:36:08', '', NULL, NULL),
(60, 1, '否', '0', 'blog_original', '', 'info', 'N', '0', 'admin', '2026-04-05 12:36:11', '', NULL, NULL),
(61, 2, '是', '1', 'blog_original', '', 'success', 'N', '0', 'admin', '2026-04-05 12:36:11', '', NULL, NULL),
(62, 1, '否', '0', 'blog_yes_no', '', 'info', 'N', '0', 'admin', '2026-04-05 12:36:19', '', NULL, NULL),
(63, 2, '是', '1', 'blog_yes_no', '', 'success', 'N', '0', 'admin', '2026-04-05 12:36:19', '', NULL, NULL),
(64, 1, '普通用户', '1', 'blog_user_type', '', 'primary', 'Y', '0', 'admin', '2026-04-05 12:36:22', '', NULL, NULL),
(65, 2, '博主', '2', 'blog_user_type', '', 'success', 'N', '0', 'admin', '2026-04-05 12:36:22', '', NULL, NULL),
(66, 1, '正常', '0', 'blog_user_status', '', 'success', 'Y', '0', 'admin', '2026-04-05 12:36:31', '', NULL, NULL),
(67, 2, '停用', '1', 'blog_user_status', '', 'danger', 'N', '0', 'admin', '2026-04-05 12:36:31', '', NULL, NULL),
(68, 1, '账号密码', '1', 'blog_login_type', '', 'primary', 'Y', '0', 'admin', '2026-04-05 12:36:36', '', NULL, NULL),
(69, 2, '码云', '2', 'blog_login_type', '', '', 'N', '0', 'admin', '2026-04-05 12:36:36', '', NULL, NULL),
(70, 3, 'Github', '3', 'blog_login_type', '', '', 'N', '0', 'admin', '2026-04-05 12:36:36', '', NULL, NULL),
(71, 4, 'QQ', '4', 'blog_login_type', '', '', 'N', '0', 'admin', '2026-04-05 12:36:36', '', NULL, NULL),
(72, 5, '微信', '5', 'blog_login_type', '', '', 'N', '0', 'admin', '2026-04-05 12:36:36', '', NULL, NULL),
(20101, 1, '全体', 'all', 'blog_announcement_scope_type', '', 'primary', 'Y', '0', 'admin', '2026-04-16 14:36:48', '', NULL, '所有用户可见'),
(20102, 2, '角色', 'role', 'blog_announcement_scope_type', '', 'success', 'N', '0', 'admin', '2026-04-16 14:36:48', '', NULL, '按角色可见'),
(20103, 3, '分群', 'segment', 'blog_announcement_scope_type', '', 'warning', 'N', '0', 'admin', '2026-04-16 14:36:48', '', NULL, '按分群可见'),
(20104, 4, '指定用户', 'assign', 'blog_announcement_scope_type', '', 'danger', 'N', '0', 'admin', '2026-04-16 14:36:48', '', NULL, '按用户可见'),
(20201, 1, '草稿', '0', 'blog_announcement_publish_status', '', 'info', 'Y', '0', 'admin', '2026-04-16 14:36:48', '', NULL, '未发布'),
(20202, 2, '已发布', '1', 'blog_announcement_publish_status', '', 'success', 'N', '0', 'admin', '2026-04-16 14:36:48', '', NULL, '对前台可见'),
(20203, 3, '已下线', '2', 'blog_announcement_publish_status', '', 'danger', 'N', '0', 'admin', '2026-04-16 14:36:48', '', NULL, '前台不可见');

-- ----------------------------
-- Seed data for `sys_dict_type`
-- ----------------------------
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, '用户性别', 'sys_user_sex', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '用户性别列表'),
(2, '菜单状态', 'sys_show_hide', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '菜单状态列表'),
(3, '系统开关', 'sys_normal_disable', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '系统开关列表'),
(4, '任务状态', 'sys_job_status', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '任务状态列表'),
(5, '任务分组', 'sys_job_group', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '任务分组列表'),
(6, '系统是否', 'sys_yes_no', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '系统是否列表'),
(7, '通知类型', 'sys_notice_type', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '通知类型列表'),
(8, '通知状态', 'sys_notice_status', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '通知状态列表'),
(9, '操作类型', 'sys_oper_type', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '操作类型列表'),
(10, '系统状态', 'sys_common_status', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '登录状态列表'),
(14, '博客评论来源', 'blog_comment_source', '0', 'admin', '2026-04-05 12:35:27', '', NULL, 'b_comment.source，与 CommentSourceEnum 对齐'),
(15, '友链分类', 'blog_friend_category', '0', 'admin', '2026-04-05 12:35:34', '', NULL, 'FriendLink.category：1推荐 2精选 3更多'),
(16, '友链状态', 'blog_friend_status', '0', 'admin', '2026-04-05 12:35:41', '', NULL, 'FriendLink.status：0申请 1上架 2下架'),
(17, '文章阅读权限', 'blog_read_auth', '0', 'admin', '2026-04-05 12:35:48', '', NULL, 'ArticleConstants 1～9'),
(18, '文章状态', 'blog_status', '0', 'admin', '2026-04-05 12:35:57', '', NULL, '1审核中 2已发布 3草稿'),
(19, '文章评论开关', 'blog_comment', '0', 'admin', '2026-04-05 12:36:06', '', NULL, '0否 1是'),
(20, '是否原创', 'blog_original', '0', 'admin', '2026-04-05 12:36:10', '', NULL, '0否 1是'),
(21, '博客是否', 'blog_yes_no', '0', 'admin', '2026-04-05 12:36:17', '', NULL, '0否 1是'),
(22, '博客用户类型', 'blog_user_type', '0', 'admin', '2026-04-05 12:36:21', '', NULL, 'UserTypeEnum：1普通 2博主'),
(23, '博客用户状态', 'blog_user_status', '0', 'admin', '2026-04-05 12:36:29', '', NULL, '0正常 1停用'),
(24, '博客登录方式', 'blog_login_type', '0', 'admin', '2026-04-05 12:36:33', '', NULL, 'login_type_list：1账号密码 2码云 3Github 4QQ 5微信'),
(201, '博客通知公告作用域', 'blog_announcement_scope_type', '0', 'admin', '2026-04-16 14:38:29', '', NULL, 'announcement.scope_type'),
(202, '博客通知公告发布状态', 'blog_announcement_publish_status', '0', 'admin', '2026-04-16 14:38:29', '', NULL, 'announcement.publish_status');

-- ----------------------------
-- Seed data for `sys_job`
-- ----------------------------
INSERT INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2023-10-17 14:29:37', '', NULL, ''),
(2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2023-10-17 14:29:37', '', NULL, ''),
(3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2023-10-17 14:29:37', '', NULL, '');

-- ----------------------------
-- Seed data for `sys_menu`
-- ----------------------------
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, '系统管理', 0, 1, 'system', NULL, '', 'System', 1, 0, 'M', '0', '0', '', 'Setting', 'admin', '2023-10-17 14:29:36', '', NULL, '系统管理目录'),
(2, '系统监控', 0, 2, 'monitor', NULL, '', 'Monitor', 1, 0, 'M', '0', '0', '', 'Monitor', 'admin', '2023-10-17 14:29:36', '', NULL, '系统监控目录'),
(3, '系统工具', 0, 3, 'tool', NULL, '', 'Tool', 1, 0, 'M', '0', '0', '', 'Tools', 'admin', '2023-10-17 14:29:36', '', NULL, '系统工具目录'),
(100, '用户管理', 1, 1, 'user', 'system/user/index', '', 'SystemUser', 1, 0, 'C', '0', '0', 'system:user:list', 'User', 'admin', '2023-10-17 14:29:36', '1', '2026-04-10 12:49:34', '用户管理菜单'),
(101, '角色管理', 1, 2, 'role', 'system/role/index', '', 'SystemRole', 1, 0, 'C', '0', '0', 'system:role:list', 'UserFilled', 'admin', '2023-10-17 14:29:36', '', NULL, '角色管理菜单'),
(102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 'SystemMenu', 1, 0, 'C', '0', '0', 'system:menu:list', 'Grid', 'admin', '2023-10-17 14:29:36', '', NULL, '菜单管理菜单'),
(103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 'SystemDept', 1, 0, 'C', '0', '0', 'system:dept:list', 'Share', 'admin', '2023-10-17 14:29:36', '', NULL, '部门管理菜单'),
(104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 'SystemPost', 1, 0, 'C', '0', '0', 'system:post:list', 'Postcard', 'admin', '2023-10-17 14:29:36', '', NULL, '岗位管理菜单'),
(105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 'SystemDict', 1, 0, 'C', '0', '0', 'system:dict:list', 'CollectionTag', 'admin', '2023-10-17 14:29:36', '', NULL, '字典管理菜单'),
(106, '参数设置', 1, 7, 'config', 'system/config/index', '', 'SystemConfig', 1, 0, 'C', '0', '0', 'system:config:list', 'Edit', 'admin', '2023-10-17 14:29:36', '1', '2026-04-10 12:49:34', '参数设置菜单'),
(107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 'SystemNotice', 1, 0, 'C', '0', '0', 'system:notice:list', 'Message', 'admin', '2023-10-17 14:29:36', '', NULL, '通知公告菜单'),
(108, '日志管理', 1, 9, 'log', '', '', 'SystemLog', 1, 0, 'M', '0', '0', '', 'Document', 'admin', '2023-10-17 14:29:36', '', NULL, '日志管理菜单'),
(109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 'MonitorOnline', 1, 0, 'C', '0', '0', 'monitor:online:list', 'Connection', 'admin', '2023-10-17 14:29:36', '', NULL, '在线用户菜单'),
(110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', 'MonitorJob', 1, 0, 'C', '0', '0', 'monitor:job:list', 'Timer', 'admin', '2023-10-17 14:29:36', '', NULL, '定时任务菜单'),
(111, 'Sentinel控制台', 2, 3, 'http://localhost:8718', '', '', 'MonitorLocalhost8718', 0, 1, 'C', '0', '0', 'monitor:sentinel:list', 'Warning', 'admin', '2023-10-17 14:29:36', '', NULL, '流量控制菜单'),
(112, 'Nacos控制台', 2, 4, 'http://localhost:8848/nacos', '', '', 'MonitorLocalhost8848Nacos', 0, 1, 'C', '0', '0', 'monitor:nacos:list', 'SetUp', 'admin', '2023-10-17 14:29:36', 'admin', '2026-04-15 15:42:54', '服务治理菜单'),
(113, 'Admin控制台', 2, 5, 'http://localhost:9100/login', '', '', 'MonitorLocalhost9100Login', 0, 0, 'C', '0', '0', 'monitor:server:list', 'Cpu', 'admin', '2023-10-17 14:29:36', 'admin', '2026-04-15 15:26:14', '服务监控菜单'),
(116, '系统接口', 3, 3, 'http://localhost:8080/swagger-ui/index.html', '', '', 'ToolLocalhost8080SwaggerUiIndexHtml', 1, 1, 'C', '0', '0', 'tool:swagger:list', 'Promotion', 'admin', '2023-10-17 14:29:36', '', NULL, '系统接口菜单'),
(500, '操作日志', 108, 1, 'operlog', 'system/operlog/index', '', 'SystemLogOperlog', 1, 0, 'C', '0', '0', 'system:operlog:list', 'Document', 'admin', '2023-10-17 14:29:36', '', NULL, '操作日志菜单'),
(501, '登录日志', 108, 2, 'logininfor', 'system/logininfor/index', '', 'SystemLogLogininfor', 1, 0, 'C', '0', '0', 'system:logininfor:list', 'Clock', 'admin', '2023-10-17 14:29:36', '', NULL, '登录日志菜单'),
(1000, '用户查询', 100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1001, '用户新增', 100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1002, '用户修改', 100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1003, '用户删除', 100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1004, '用户导出', 100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1005, '用户导入', 100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1006, '重置密码', 100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1007, '角色查询', 101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1008, '角色新增', 101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1009, '角色修改', 101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1010, '角色删除', 101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1011, '角色导出', 101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1012, '菜单查询', 102, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1013, '菜单新增', 102, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1014, '菜单修改', 102, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1015, '菜单删除', 102, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1016, '部门查询', 103, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1017, '部门新增', 103, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1018, '部门修改', 103, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1019, '部门删除', 103, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1020, '岗位查询', 104, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1021, '岗位新增', 104, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1022, '岗位修改', 104, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1023, '岗位删除', 104, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1024, '岗位导出', 104, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1025, '字典查询', 105, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1026, '字典新增', 105, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1027, '字典修改', 105, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1028, '字典删除', 105, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1029, '字典导出', 105, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1030, '参数查询', 106, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1031, '参数新增', 106, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1032, '参数修改', 106, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1033, '参数删除', 106, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1034, '参数导出', 106, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1035, '公告查询', 107, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1036, '公告新增', 107, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1037, '公告修改', 107, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1038, '公告删除', 107, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1039, '操作查询', 500, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:operlog:query', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1040, '操作删除', 500, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:operlog:remove', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1041, '日志导出', 500, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:operlog:export', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1042, '登录查询', 501, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:logininfor:query', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1043, '登录删除', 501, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:logininfor:remove', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1044, '日志导出', 501, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:logininfor:export', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1045, '账户解锁', 501, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:logininfor:unlock', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1046, '在线查询', 109, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1047, '批量强退', 109, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1048, '单条强退', 109, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1049, '任务查询', 110, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1050, '任务新增', 110, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1051, '任务修改', 110, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1052, '任务删除', 110, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1053, '状态修改', 110, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1054, '任务导出', 110, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', 'Operation', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(1061, '网站管理', 0, 4, 'config', NULL, NULL, 'Config', 1, 0, 'M', '0', '0', '', 'Monitor', 'admin', '2024-07-01 20:24:21', 'admin', '2024-07-19 17:09:59', ''),
(1063, '博客管理', 0, 6, 'blog', NULL, NULL, 'Blog', 1, 0, 'M', '0', '0', NULL, 'Document', 'admin', '2024-07-01 20:26:15', '', NULL, ''),
(1064, '网站配置', 1061, 1, 'config', 'blog/config/index', '', 'BlogConfig', 1, 0, 'C', '0', '0', 'blog:config:list', 'Setting', 'admin', '2024-07-01 20:28:19', '1', '2026-04-10 12:49:34', '网站配置菜单'),
(1065, '网站配置查询', 1064, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:config:query', 'Operation', 'admin', '2024-07-01 20:29:18', '', NULL, ''),
(1066, '网站配置新增', 1064, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:config:add', 'Operation', 'admin', '2024-07-01 20:29:18', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1067, '网站配置修改', 1064, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:config:edit', 'Operation', 'admin', '2024-07-01 20:29:18', '', NULL, ''),
(1068, '网站配置删除', 1064, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:config:remove', 'Operation', 'admin', '2024-07-01 20:29:18', '', NULL, ''),
(1069, '网站配置导出', 1064, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:config:export', 'Operation', 'admin', '2024-07-01 20:29:18', '', NULL, ''),
(1070, '门户导航栏', 1061, 1, 'navbar', 'blog/navbar/index', NULL, 'ConfigNavbar', 1, 0, 'C', '0', '0', 'blog:navbar:list', 'Grid', 'admin', '2024-07-01 20:34:34', 'admin', '2024-07-19 17:21:45', '门户导航栏对象菜单'),
(1071, '门户导航栏对象查询', 1070, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:navbar:query', 'Operation', 'admin', '2024-07-01 20:34:34', '', NULL, ''),
(1072, '门户导航栏对象新增', 1070, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:navbar:add', 'Operation', 'admin', '2024-07-01 20:34:34', '', NULL, ''),
(1073, '门户导航栏对象修改', 1070, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:navbar:edit', 'Operation', 'admin', '2024-07-01 20:34:34', '', NULL, ''),
(1074, '门户导航栏对象删除', 1070, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:navbar:remove', 'Operation', 'admin', '2024-07-01 20:34:34', '', NULL, ''),
(1075, '门户导航栏对象导出', 1070, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:navbar:export', 'Operation', 'admin', '2024-07-01 20:34:34', '', NULL, ''),
(1088, '友链管理', 1061, 1, 'link', 'blog/link/index', NULL, 'ConfigLink', 1, 0, 'C', '0', '0', 'blog:link:list', 'Link', 'admin', '2024-07-01 20:42:39', 'admin', '2024-07-19 17:22:16', '友链管理菜单'),
(1089, '友链管理查询', 1088, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:link:query', 'Operation', 'admin', '2024-07-01 20:42:39', '', NULL, ''),
(1090, '友链管理新增', 1088, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:link:add', 'Operation', 'admin', '2024-07-01 20:42:39', '', NULL, ''),
(1091, '友链管理修改', 1088, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:link:edit', 'Operation', 'admin', '2024-07-01 20:42:39', '', NULL, ''),
(1092, '友链管理删除', 1088, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:link:remove', 'Operation', 'admin', '2024-07-01 20:42:39', '', NULL, ''),
(1093, '友链管理导出', 1088, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:link:export', 'Operation', 'admin', '2024-07-01 20:42:39', '', NULL, ''),
(1094, '博客文章', 1063, 2, 'article', 'blog/article/index', NULL, 'BlogArticle', 1, 1, 'C', '0', '0', 'blog:article:list', 'Reading', 'admin', '2024-07-02 09:04:21', 'admin', '2026-04-23 21:11:05', '博客文章菜单'),
(1095, '博客文章查询', 1094, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:article:query', 'Operation', 'admin', '2024-07-02 09:04:23', '', NULL, ''),
(1096, '博客文章新增', 1094, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:article:add', 'Operation', 'admin', '2024-07-02 09:04:23', '', NULL, ''),
(1097, '博客文章修改', 1094, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:article:edit', 'Operation', 'admin', '2024-07-02 09:04:23', '', NULL, ''),
(1098, '博客文章删除', 1094, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:article:remove', 'Operation', 'admin', '2024-07-02 09:04:23', '', NULL, ''),
(1099, '博客文章导出', 1094, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:article:export', 'Operation', 'admin', '2024-07-02 09:04:23', '', NULL, ''),
(1100, '博客分类', 1063, 1, 'category', 'blog/category/index', NULL, 'BlogCategory', 1, 0, 'C', '0', '0', 'blog:category:list', 'Grid', 'admin', '2024-07-02 09:05:15', 'admin', '2026-04-23 21:10:59', '博客分类菜单'),
(1101, '博客分类查询', 1100, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:category:query', 'Operation', 'admin', '2024-07-02 09:05:15', '', NULL, ''),
(1102, '博客分类新增', 1100, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:category:add', 'Operation', 'admin', '2024-07-02 09:05:15', '', NULL, ''),
(1103, '博客分类修改', 1100, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:category:edit', 'Operation', 'admin', '2024-07-02 09:05:15', '', NULL, ''),
(1104, '博客分类删除', 1100, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:category:remove', 'Operation', 'admin', '2024-07-02 09:05:15', '', NULL, ''),
(1105, '博客分类导出', 1100, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:category:export', 'Operation', 'admin', '2024-07-02 09:05:15', '', NULL, ''),
(1106, '评论管理', 1063, 3, 'comment', 'blog/comment/index', NULL, 'BlogComment', 1, 1, 'C', '0', '0', 'blog:comment:list', 'Edit', 'admin', '2024-07-02 09:05:46', 'admin', '2026-04-23 21:11:28', '评论管理菜单'),
(1107, '评论管理查询', 1106, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:comment:query', 'Operation', 'admin', '2024-07-02 09:05:46', '', NULL, ''),
(1108, '评论管理新增', 1106, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:comment:add', 'Operation', 'admin', '2024-07-02 09:05:46', '', NULL, ''),
(1109, '评论管理修改', 1106, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:comment:edit', 'Operation', 'admin', '2024-07-02 09:05:46', '', NULL, ''),
(1110, '评论管理删除', 1106, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:comment:remove', 'Operation', 'admin', '2024-07-02 09:05:46', '', NULL, ''),
(1111, '评论管理导出', 1106, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:comment:export', 'Operation', 'admin', '2024-07-02 09:05:46', '', NULL, ''),
(1112, '博客标签', 1063, 1, 'tag', 'blog/tag/index', NULL, 'BlogTag', 1, 0, 'C', '0', '0', 'blog:tag:list', 'CollectionTag', 'admin', '2024-07-02 09:06:21', 'admin', '2024-07-19 17:20:06', '博客标签菜单'),
(1113, '博客标签查询', 1112, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:tag:query', 'Operation', 'admin', '2024-07-02 09:06:21', '', NULL, ''),
(1114, '博客标签新增', 1112, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:tag:add', 'Operation', 'admin', '2024-07-02 09:06:21', '', NULL, ''),
(1115, '博客标签修改', 1112, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:tag:edit', 'Operation', 'admin', '2024-07-02 09:06:21', '', NULL, ''),
(1116, '博客标签删除', 1112, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:tag:remove', 'Operation', 'admin', '2024-07-02 09:06:21', '', NULL, ''),
(1117, '博客标签导出', 1112, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:tag:export', 'Operation', 'admin', '2024-07-02 09:06:21', '', NULL, ''),
(1118, '用户信息', 1061, 1, 'user', 'blog/user/index', '', 'BlogUser', 1, 0, 'C', '0', '0', 'blog:user:list', 'User', 'admin', '2024-07-02 09:06:37', 'admin', '2026-04-23 20:39:02', '用户信息菜单'),
(1119, '用户信息查询', 1118, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:user:query', 'Operation', 'admin', '2024-07-02 09:06:37', '', NULL, ''),
(1120, '用户信息新增', 1118, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:user:add', 'Operation', 'admin', '2024-07-02 09:06:37', '', NULL, ''),
(1121, '用户信息修改', 1118, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:user:edit', 'Operation', 'admin', '2024-07-02 09:06:37', '', NULL, ''),
(1122, '用户信息删除', 1118, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:user:remove', 'Operation', 'admin', '2024-07-02 09:06:37', '', NULL, ''),
(1123, '用户信息导出', 1118, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:user:export', 'Operation', 'admin', '2024-07-02 09:06:37', '', NULL, ''),
(1124, '说说管理', 1063, 2, 'say', 'blog/say/index', '', 'BlogSay', 1, 0, 'C', '0', '0', 'blog:say:list', 'Message', 'admin', '2026-03-23 01:24:11', '', NULL, ''),
(1125, '说说查询', 1124, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:say:query', 'Operation', 'admin', '2026-03-23 01:24:32', '', NULL, ''),
(1126, '说说新增', 1124, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:say:add', 'Operation', 'admin', '2026-03-23 01:24:32', '', NULL, ''),
(1127, '说说修改', 1124, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:say:edit', 'Operation', 'admin', '2026-03-23 01:24:32', '', NULL, ''),
(1128, '说说删除', 1124, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:say:remove', 'Operation', 'admin', '2026-03-23 01:24:32', '', NULL, ''),
(1129, '树洞管理', 1063, 3, 'treehole', 'blog/treehole/index', '', 'BlogTreehole', 1, 0, 'C', '0', '0', 'blog:treehole:list', 'Share', 'admin', '2026-03-23 01:24:33', '', NULL, ''),
(1130, '树洞查询', 1129, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:treehole:query', 'Operation', 'admin', '2026-03-23 01:24:47', '', NULL, ''),
(1131, '树洞新增', 1129, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:treehole:add', 'Operation', 'admin', '2026-03-23 01:24:47', '', NULL, ''),
(1132, '树洞修改', 1129, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:treehole:edit', 'Operation', 'admin', '2026-03-23 01:24:47', '', NULL, ''),
(1133, '树洞删除', 1129, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:treehole:remove', 'Operation', 'admin', '2026-03-23 01:24:47', '', NULL, ''),
(1134, '友链审核', 1088, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:link:examine', 'Operation', 'admin', '2026-03-23 01:40:41', '', NULL, '上下架审核，与 blog:link:edit 二选一即可调用接口'),
(1135, '导航目录', 1063, 8, 'navigation', 'blog/navigation/index', '', 'BlogNavigation', 1, 0, 'C', '0', '0', 'blog:navigation:list', 'Guide', 'admin', '2026-04-02 09:09:39', '', NULL, '博客导航目录管理'),
(1136, '导航查询', 1135, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:navigation:query', 'Operation', 'admin', '2026-04-02 09:09:39', '', NULL, ''),
(1137, '导航新增', 1135, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:navigation:add', 'Operation', 'admin', '2026-04-02 09:09:39', '', NULL, ''),
(1138, '导航修改', 1135, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:navigation:edit', 'Operation', 'admin', '2026-04-02 09:09:39', '', NULL, ''),
(1139, '导航删除', 1135, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:navigation:remove', 'Operation', 'admin', '2026-04-02 09:09:39', '', NULL, ''),
(1140, '导航导出', 1135, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:navigation:export', 'Operation', 'admin', '2026-04-02 09:09:39', '', NULL, ''),
(1141, '收藏管理', 1063, 9, 'collect', 'blog/collect/index', '', 'BlogCollect', 1, 0, 'C', '0', '0', 'blog:collect:list', 'Star', 'admin', '2026-04-02 09:09:39', '', NULL, '博客收藏管理'),
(1142, '收藏查询', 1141, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:collect:query', 'Operation', 'admin', '2026-04-02 09:09:39', '', NULL, ''),
(1143, '收藏删除', 1141, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:collect:remove', 'Operation', 'admin', '2026-04-02 09:09:39', '', NULL, ''),
(1145, '关注管理', 1063, 10, 'follow', 'blog/follow/index', '', 'BlogFollow', 1, 0, 'C', '0', '0', 'blog:follow:list', 'UserFilled', 'admin', '2026-04-02 09:09:39', '', NULL, '博客关注关系管理'),
(1146, '关注查询', 1145, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:follow:query', 'Operation', 'admin', '2026-04-02 09:09:39', '', NULL, ''),
(1147, '关注删除', 1145, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:follow:remove', 'Operation', 'admin', '2026-04-02 09:09:39', '', NULL, ''),
(1149, '评论词库', 1152, 11, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:moderation:query', 'Edit', 'admin', '2026-04-05 01:50:41', '', NULL, '查询当前词库与版本（接口）'),
(1150, '词库刷新', 1152, 12, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:moderation:edit', 'Operation', 'admin', '2026-04-05 01:50:41', '', NULL, '从配置重建内存词树'),
(1151, '词库保存', 1152, 13, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:moderation:edit', 'Operation', 'admin', '2026-04-05 01:50:42', '', NULL, '内存词库覆盖（不落 Nacos）'),
(1152, '机审词库', 1061, 14, 'moderation', 'blog/moderation/index', '', 'BlogModeration', 1, 1, 'C', '0', '0', 'blog:moderation:query', 'Reading', 'admin', '2026-04-05 01:50:43', 'admin', '2026-04-23 20:39:17', '评论机审词库与内存刷新'),
(1154, '待处理评论', 1063, 15, 'commentPending', 'blog/comment/index', '{\"workbench\":\"1\"}', 'BlogCommentPending', 1, 0, 'C', '0', '0', 'blog:comment:list', 'Message', 'admin', '2026-04-05 12:08:38', '', NULL, '按待审核+机审决策筛选，复用评论列表接口'),
(1155, '数据统计', 1063, 0, 'stats', 'blog/stats/index', '', 'BlogStats', 1, 1, 'C', '0', '0', 'blog:stats:view', 'Odometer', 'admin', '2026-04-05 13:04:05', 'admin', '2026-04-11 22:32:29', '博客文章/评论/用户趋势与汇总'),
(1165, '圆圈博客', 0, 7, 'http://localhost:80', NULL, NULL, '', 0, 0, 'M', '0', '0', '', 'client', 'admin', '2026-04-15 15:50:13', 'admin', '2026-04-15 16:07:51', ''),
(1201, '博客公告', 1061, 16, 'announcement', 'blog/announcement/index', '', 'Announcement', 1, 0, 'C', '0', '0', 'blog:announcement:list', 'Bell', 'admin', '2026-04-16 14:59:53', 'admin', '2026-04-16 15:28:38', '博客前台公告管理'),
(1202, '公告查询', 1201, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:announcement:query', '#', 'admin', '2026-04-16 14:59:53', '', NULL, ''),
(1203, '公告新增', 1201, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:announcement:add', '#', 'admin', '2026-04-16 14:59:53', '', NULL, ''),
(1204, '公告修改', 1201, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:announcement:edit', '#', 'admin', '2026-04-16 14:59:53', '', NULL, ''),
(1205, '公告发布', 1201, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:announcement:publish', '#', 'admin', '2026-04-16 14:59:53', '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1206, '公告下线', 1201, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:announcement:offline', '#', 'admin', '2026-04-16 14:59:53', '', NULL, ''),
(1207, '公告删除', 1201, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'blog:announcement:remove', '#', 'admin', '2026-04-16 14:59:53', '', NULL, ''),
(1211, '互动记录', 1063, 17, 'interactions/records', 'blog/interactions/records', '', 'Interaction', 1, 0, 'C', '0', '0', 'blog:interaction:list', 'Comment', 'admin', '2026-04-23 21:01:04', 'admin', '2026-04-23 21:35:01', '点赞/收藏行为记录'),
(1212, '互动记录查询', 1211, 1, '', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:interaction:query', '#', 'admin', '2026-04-23 21:41:30', '', NULL, '互动记录详情查询权限'),
(1213, '互动记录删除', 1211, 2, '', '', NULL, '', 1, 0, 'F', '0', '0', 'blog:interaction:remove', '#', 'admin', '2026-04-23 21:52:36', '', NULL, '互动记录删除权限');

-- ----------------------------
-- Seed data for `sys_post`
-- ----------------------------
INSERT INTO `sys_post` (`post_id`, `post_code`, `post_name`, `post_sort`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, 'ceo', '董事长', 1, '0', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(2, 'se', '项目经理', 2, '0', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(3, 'hr', '人力资源', 3, '0', 'admin', '2023-10-17 14:29:36', '', NULL, ''),
(4, 'user', '普通员工', 4, '0', 'admin', '2023-10-17 14:29:36', '', NULL, '');

-- ----------------------------
-- Seed data for `sys_role`
-- ----------------------------
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2023-10-17 14:29:36', '', NULL, '超级管理员'),
(2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2023-10-17 14:29:36', 'admin', '2026-04-11 00:32:08', '普通角色');

-- ----------------------------
-- Seed data for `sys_role_dept`
-- ----------------------------
INSERT INTO `sys_role_dept` (`role_id`, `dept_id`) VALUES
(2, 100),
(2, 101),
(2, 105);

-- ----------------------------
-- Seed data for `sys_role_menu`
-- ----------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1211),
(1, 1212),
(1, 1213),
(2, 1),
(2, 2),
(2, 100),
(2, 101),
(2, 102),
(2, 103),
(2, 104),
(2, 105),
(2, 106),
(2, 107),
(2, 108),
(2, 109),
(2, 110),
(2, 111),
(2, 112),
(2, 113),
(2, 500),
(2, 501),
(2, 1000),
(2, 1001),
(2, 1002),
(2, 1003),
(2, 1004),
(2, 1005),
(2, 1006),
(2, 1007),
(2, 1008),
(2, 1009),
(2, 1010),
(2, 1011),
(2, 1012),
(2, 1013),
(2, 1014),
(2, 1015),
(2, 1016),
(2, 1017),
(2, 1018),
(2, 1019),
(2, 1020),
(2, 1021),
(2, 1022),
(2, 1023),
(2, 1024),
(2, 1025),
(2, 1026),
(2, 1027),
(2, 1028),
(2, 1029),
(2, 1030),
(2, 1031),
(2, 1032),
(2, 1033),
(2, 1034),
(2, 1035),
(2, 1036),
(2, 1037),
(2, 1038),
(2, 1039),
(2, 1040),
(2, 1041),
(2, 1042),
(2, 1043),
(2, 1044),
(2, 1045),
(2, 1046),
(2, 1047),
(2, 1048),
(2, 1049),
(2, 1050),
(2, 1051),
(2, 1052),
(2, 1053),
(2, 1054);

-- ----------------------------
-- Seed data for `sys_user`
-- ----------------------------
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `avatar`, `user_type`, `email`, `phonenumber`, `sex`, `avatar_file_id`, `password`, `status`, `del_flag`, `login_ip`, `login_date`, `pwd_update_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(1, 103, 'admin', '管理员', '', '00', '', '', '2', NULL, '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '', NULL, NULL, 'admin', '2026-02-01 00:00:00', '', NULL, '系统管理员');

-- ----------------------------
-- Seed data for `sys_user_post`
-- ----------------------------
INSERT INTO `sys_user_post` (`user_id`, `post_id`) VALUES
(1, 1);

-- ----------------------------
-- Seed data for `sys_user_role`
-- ----------------------------
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1);

SET FOREIGN_KEY_CHECKS = 1;
