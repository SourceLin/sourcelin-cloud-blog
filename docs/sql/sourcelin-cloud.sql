-- MySQL dump 10.13  Distrib 5.7.20, for Win32 (AMD64)
--
-- Host: <DB_HOST>    Database: sourcelin-cloud
-- ------------------------------------------------------
-- Server version	5.7.44-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `b_article`
--

DROP TABLE IF EXISTS `b_article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=86314 DEFAULT CHARSET=utf8mb4 COMMENT='博客文章对象';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_article`
--

LOCK TABLES `b_article` WRITE;
/*!40000 ALTER TABLE `b_article` DISABLE KEYS */;
INSERT INTO `b_article` VALUES (86301,86001,86101,'在晨雾里慢慢醒来的页面','https://picsum.photos/1200/600?random=1',NULL,'把光线、留白与文字的呼吸感留在同一张首页里。','清晨的界面不需要喧闹。把间距、色彩和文字节奏放缓，读者会更愿意停留。首页首先要传达的是情绪和秩序，而不是堆叠信息。',1,2,1,0,0,1,NULL,2095,416,238,0,'admin','2026-03-20 08:30:00','admin','2026-04-07 16:33:45','home_static_seed_20260402'),(86302,86002,86103,'把风景折叠进代码的缝隙','https://picsum.photos/1200/600?random=2',NULL,'页面、组件与状态边界之间，也可以安放一点温柔。','工程化不是冷冰冰的约束。好的结构让视觉表达更稳定，也让后续维护更可预期。把风景留在细节里，系统会更耐看。',1,2,1,0,0,1,NULL,1957,389,219,0,'admin','2026-03-21 10:15:00','admin','2026-04-06 13:33:49','home_static_seed_20260402'),(86303,86003,86104,'把晚风写进一页长图','https://picsum.photos/1200/600?random=3',NULL,'那些不急着被解释的片段，适合被安静地陈列。','长图不是信息堆砌，而是叙事。每一段内容都应该有呼吸区间，像晚风一样推进，不急于给出结论。',1,2,1,0,0,1,NULL,1673,334,201,0,'admin','2026-03-22 20:40:00','admin','2026-04-06 11:39:51','home_static_seed_20260402'),(86304,86004,86105,'春有百花，心里有一盏灯','https://picsum.photos/200/150?random=10',NULL,'把喜欢的片段收进同一束光里，页面就有了温度。','页面温度来自真实表达。把喜欢的片段持续记录，形成稳定内容节奏，网站自然会拥有独特气质。',1,2,1,1,0,1,NULL,1935,382,214,0,'admin','2026-03-23 09:20:00','admin','2026-04-05 14:31:47','home_static_seed_20260402'),(86305,86005,86105,'夏有凉风，字里有水声','https://picsum.photos/200/150?random=11',NULL,'写作不必喧哗，轻轻落下就能留下涟漪。','内容运营不一定追求爆发。保持稳定输出和真实体验，反而更容易形成长期信任。',1,2,1,1,0,1,NULL,1690,331,189,0,'admin','2026-03-24 14:10:00','admin','2026-04-06 13:11:08','home_static_seed_20260402'),(86306,86006,86104,'月光落在键盘上','https://picsum.photos/200/150?random=12',NULL,'当夜色安静下来，许多细小的想法才开始发光。','夜晚是整理思路的好时段。把分散想法写成完整文章，再沉淀为可复用模块，效率会稳定提升。',1,2,1,1,0,1,NULL,1522,297,172,0,'admin','2026-03-25 22:18:00','admin','2026-04-06 07:18:20','home_static_seed_20260402'),(86307,86003,86102,'落霞与孤鹜，齐飞于一页之间','https://picsum.photos/400/280?random=100',NULL,'把目录、样式和状态边界慢慢理顺，页面也会更安静。','前端复杂度常常来自边界不清。梳理目录、拆分职责、统一样式源之后，页面会显著稳定，联调成本也会下降。',1,2,1,0,0,1,NULL,1718,322,184,0,'admin','2026-03-26 11:45:00','admin','2026-04-06 13:36:02','home_static_seed_20260402'),(86308,86006,86103,'山有木兮木有枝','https://picsum.photos/400/280?random=101',NULL,'写给未来自己的代码，最后也会成为项目风景的一部分。','可读性是最实用的工程资产。命名清晰、结构清晰、注释克制，都是给未来自己和团队节省成本。',1,2,1,0,0,1,NULL,1459,284,163,0,'admin','2026-03-27 16:08:00','admin','2026-04-05 13:43:48','home_static_seed_20260402'),(86309,86001,86101,'风从纸页间经过','https://picsum.photos/400/280?random=102',NULL,'一篇文章的余温，往往藏在那些没有说尽的地方。','文章并不需要把所有问题一次讲完。保留适度留白，让读者带着问题继续探索，反而更有持续价值。',1,2,1,0,0,1,NULL,1324,261,148,0,'admin','2026-03-28 09:36:00','admin','2026-04-02 10:55:57','home_static_seed_20260402'),(86310,86004,86104,'在黄昏里整理星光','https://picsum.photos/400/280?random=103',NULL,'把零散的灵感收束起来，像把暮色缓缓折好。','写作前先做一轮信息归档，把零散想法聚合为主题，再决定结构。这样内容更连贯，也更容易扩展。',1,2,1,0,0,1,NULL,1273,246,139,0,'admin','2026-03-29 18:22:00','admin','2026-04-07 17:11:26','home_static_seed_20260402'),(86311,86005,86102,'雨落时，字也会变得柔软','https://picsum.photos/400/280?random=104',NULL,'柔软不是退让，是让内容拥有被阅读的耐心。','表达可以有力量，也可以有温度。把节奏放慢、把句子写短，往往更容易被真正读完。',1,2,1,0,0,1,NULL,1188,229,126,0,'admin','2026-03-30 07:55:00','admin','2026-04-03 00:31:47','home_static_seed_20260402'),(86312,1,86102,'花眠','http://127.0.0.1:9300/statics/2026/04/02/002843aVj7M_20260402144941A001.jpg',NULL,'这篇短文以春日花海为背景，描绘了一位少女在花中安睡的静谧画面：她枕着石栏，长发如瀑，眼睫似羽扇，粉衣与花海相融，在暖光与花香中沉眠。全文以细腻的笔触、灵动的比喻，将少女的柔美与春日的温柔融为一体，定格了时间静止、万物安然的诗意瞬间，营造出治愈、浪漫的氛围，书写了一场关于春日与梦境的美好诗篇','<p>风把春日揉成了一捧软光，落在漫山遍野的花海里。少女枕着石栏沉沉睡去，长发如墨色的瀑，顺着肩头垂落，发梢沾着细碎的花瓣，像落了一场温柔的雪。</p><p>她的眼睫轻垂，掩去了眼底的星河，只留一抹淡粉的晕染在颊边，似是被花气熏得醉了。指尖轻轻蜷着，搭在微凉的石面上，仿佛还握着半梦半醒间的余温。粉色的衣裙被风掀起细碎的褶皱，衣摆绣着的花影与周遭的花海相融，分不清是衣上生花，还是花入衣间。</p><p>周遭是盛放的春，粉白的花簇层层叠叠，在风里漾起温柔的浪。阳光穿过花枝，在她的发间、肩头筛下斑驳的金，把每一片花瓣都镀上了暖融融的光。空气里浮动着清甜的香，混着泥土的湿润，成了最安神的眠歌。</p><p>时间仿佛在这一刻静止了。没有喧嚣，没有纷扰，只有花与风，光与影，与一个沉在春梦里的人。她像是把整个春天的温柔都拥入了怀中，在花海的簇拥里，做着一场永不醒来的、关于春日的梦。</p><p>风停时，花也静了，只余下她均匀的呼吸，与花的绽放同频，成了春日里最动人的诗篇。</p><p><img src=\"http://127.0.0.1:9300/statics/2026/04/02/002843aVj7M_20260402145554A002.jpg\"></p><p><br></p>',1,2,1,0,1,1,NULL,29,2,1,0,'','2026-04-02 14:51:19','','2026-04-09 18:59:16',NULL),(86313,1,86103,'苇间书声','http://127.0.0.1:9300/statics/2026/04/03/QQ截图20210526163407_20260403001414A001.png',NULL,'本文描绘了少女在青苇丛中静心阅读的场景，以微风、苇浪与书香交织，营造出静谧安然的氛围，展现了自然与文字相融的美好，凸显阅读带来的内心安宁与精神力量。','<p>风掠过青苇，掀起满野温柔的浪。少女立于其间，白裙如月光漫过膝头，长发随微风轻扬，垂落的眼睫掩住眼底的光，只余下一份沉静的专注，落在手中摊开的书页上。</p><p>周遭的芦苇青黄相间，在日光里晕出朦胧的暖调，像是给天地笼上了一层薄纱。风拂过苇梢，沙沙的声响成了最温柔的背景音，与书页翻动的轻响相和，成了独属于此刻的白噪音。她指尖轻抵着纸页，目光在字里行间缓缓游走，仿佛整个世界都只剩下她、书，与这一片青苇。</p><p>时间在这一刻慢了下来。没有喧嚣，没有纷扰，只有自然的呼吸与文字的温度相拥。青苇为她撑起一方静谧的天地，书本为她打开一个辽阔的世界，她在自然与文字的交汇处，寻得了最安稳的栖居。</p><p>这是独属于阅读的浪漫：在旷野里与自己对话，在字行间与世界相拥。风会记得苇间的书香，而那些读过的文字，终将化作她骨子里的温柔与力量，在往后的岁月里，熠熠生辉。</p><p><br></p>',1,2,1,0,1,1,NULL,14,1,1,0,'','2026-04-03 00:15:27','','2026-04-09 00:40:50',NULL);
/*!40000 ALTER TABLE `b_article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_article_tag`
--

DROP TABLE IF EXISTS `b_article_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_article_tag` (
  `article_id` bigint(20) NOT NULL COMMENT '文章ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`article_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`) USING BTREE,
  KEY `idx_article_id` (`article_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章与标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_article_tag`
--

LOCK TABLES `b_article_tag` WRITE;
/*!40000 ALTER TABLE `b_article_tag` DISABLE KEYS */;
INSERT INTO `b_article_tag` VALUES (86301,86202),(86307,86202),(86312,86202),(86313,86203),(86306,86204),(86313,86205),(86304,86206),(86301,86207),(86303,86210),(86309,86216),(86309,86220),(86301,86251),(86302,86252),(86302,86253),(86302,86254),(86308,86254),(86303,86255),(86303,86256),(86304,86257),(86305,86258),(86307,86258),(86305,86259),(86306,86260),(86307,86261),(86308,86262),(86308,86263),(86310,86264),(86310,86265),(86310,86266),(86311,86267),(86311,86268);
/*!40000 ALTER TABLE `b_article_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_category`
--

DROP TABLE IF EXISTS `b_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=86106 DEFAULT CHARSET=utf8mb4 COMMENT='博客分类对象';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_category`
--

LOCK TABLES `b_category` WRITE;
/*!40000 ALTER TABLE `b_category` DISABLE KEYS */;
INSERT INTO `b_category` VALUES (86101,'晨读','首页静态分类：晨读','code',1,18,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86102,'随笔','首页静态分类：随笔','server',2,24,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86103,'栖居','首页静态分类：栖居','mobile',3,32,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86104,'夜记','首页静态分类：夜记','document',4,15,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86105,'诗行','首页静态分类：诗行','star',5,22,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin');
/*!40000 ALTER TABLE `b_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_collect`
--

DROP TABLE IF EXISTS `b_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_collect` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `target_type` varchar(20) NOT NULL COMMENT '目标类型(article/say/treehole)',
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='收藏记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_collect`
--

LOCK TABLES `b_collect` WRITE;
/*!40000 ALTER TABLE `b_collect` DISABLE KEYS */;
INSERT INTO `b_collect` (`id`, `user_id`, `target_type`, `target_id`, `create_time`, `update_time`, `create_by`, `update_by`, `remark`, `deleted`) VALUES
(1,1,'article',86313,'2026-04-03 00:18:38',NULL,NULL,NULL,NULL,1),
(2,1,'article',86311,'2026-04-03 13:49:26',NULL,NULL,NULL,NULL,1),
(3,1,'article',86312,'2026-04-04 23:44:08',NULL,NULL,NULL,NULL,0),
(4,1,'say',25,'2026-04-05 22:38:09',NULL,NULL,NULL,NULL,1),
(5,1,'say',21,'2026-04-05 22:38:11',NULL,NULL,NULL,NULL,0),
(6,86010,'article',86312,'2026-04-07 23:52:48',NULL,NULL,NULL,NULL,1);
/*!40000 ALTER TABLE `b_collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_like_record`
--

DROP TABLE IF EXISTS `b_like_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `b_like_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `target_type` varchar(20) NOT NULL COMMENT '目标类型(article/say/treehole)',
  `target_id` bigint(20) NOT NULL COMMENT '目标id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除（0有效 1取消）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_type`,`target_id`) USING BTREE,
  KEY `idx_target` (`target_type`,`target_id`,`deleted`) USING BTREE,
  KEY `idx_user_type_time` (`user_id`,`target_type`,`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_like_record`
--

LOCK TABLES `b_like_record` WRITE;
/*!40000 ALTER TABLE `b_like_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `b_like_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_comment`
--

DROP TABLE IF EXISTS `b_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COMMENT='评论管理对象';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_comment`
--

LOCK TABLES `b_comment` WRITE;
/*!40000 ALTER TABLE `b_comment` DISABLE KEYS */;
INSERT INTO `b_comment` VALUES (1,1,NULL,3,NULL,'111',NULL,NULL,0,0,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-02 12:14:42',NULL,'','',NULL),(2,1,NULL,86301,NULL,'1',NULL,NULL,0,0,'article',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-02 14:48:57',NULL,'','',NULL),(3,1,NULL,5,NULL,'1',NULL,NULL,0,0,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-03 12:12:36',NULL,'','',NULL),(4,1,NULL,5,NULL,'3',NULL,NULL,0,0,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 00:23:43',NULL,'','',NULL),(5,1,NULL,5,NULL,'6',NULL,NULL,0,0,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 00:23:50',NULL,'','',NULL),(6,1,NULL,9,NULL,'热热热热',NULL,NULL,0,0,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 00:25:14',NULL,'','',NULL),(7,1,NULL,9,NULL,'2',NULL,NULL,0,0,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 10:07:27',NULL,'','',NULL),(8,1,NULL,11,NULL,'1',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 17:26:12',NULL,'','',NULL),(9,1,NULL,11,NULL,'2',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 17:26:17',NULL,'','',NULL),(10,1,1,11,8,'@SourceLin 1',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 17:26:26',NULL,'','',NULL),(11,1,NULL,6,NULL,'1',NULL,NULL,0,1,'treehole',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 18:29:30',NULL,'','',NULL),(12,1,1,6,11,'@SourceLin 2',NULL,NULL,0,1,'treehole',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 18:29:40',NULL,'','',NULL),(13,1,NULL,6,NULL,'5',NULL,NULL,0,1,'treehole',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 18:29:47',NULL,'','',NULL),(14,1,NULL,6,NULL,'66666',NULL,NULL,0,1,'treehole',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 18:30:04',NULL,'','',NULL),(15,1,NULL,21,NULL,'成功',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 23:06:23',NULL,'','',NULL),(16,1,NULL,21,NULL,'啦啦啦',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 23:06:28',NULL,'','',NULL),(17,1,NULL,21,NULL,'可以了',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 23:06:33',NULL,'','',NULL),(18,1,1,21,16,'@SourceLin hhh~',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-04 23:06:41',NULL,'','',NULL),(19,1,NULL,86313,NULL,'啦啦啦',NULL,NULL,0,0,'article',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 01:11:45',NULL,'','',NULL),(20,1,NULL,25,NULL,'煞笔',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 10:47:03',NULL,'','',NULL),(21,1,1,25,20,'@SourceLin 跑分',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 10:47:47',NULL,'','',NULL),(22,1,NULL,15,NULL,'跑分',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 10:47:58',NULL,'','',NULL),(23,1,NULL,19,NULL,'网赌',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 10:54:05',NULL,'','',NULL),(24,1,1,25,20,'@SourceLin 网赌',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 10:54:10',NULL,'','',NULL),(25,1,NULL,25,NULL,'网赌',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 10:54:17',NULL,'','',NULL),(26,1,NULL,25,NULL,'网赌',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 10:54:21',NULL,'','',NULL),(27,1,NULL,24,NULL,'网赌',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 10:54:41',NULL,'','',NULL),(28,1,NULL,21,NULL,'11',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 10:55:35',NULL,'','',NULL),(29,1,NULL,21,NULL,'2',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 10:55:41',NULL,'','',NULL),(30,1,1,25,26,'@SourceLin 沙比',NULL,NULL,0,2,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 11:13:03',NULL,'','',NULL),(31,1,NULL,14,NULL,'跑分',NULL,NULL,0,1,'treehole',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 11:14:00',NULL,'','',NULL),(32,1,NULL,25,NULL,'跑分',NULL,NULL,0,2,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 11:14:16',NULL,'','',NULL),(33,1,NULL,25,NULL,'加微信',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 11:15:45','2026-04-05 11:16:22','','',NULL),(34,1,NULL,25,NULL,'2',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 11:16:46',NULL,'','',NULL),(35,1,NULL,25,NULL,'4',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 11:16:49',NULL,'','',NULL),(36,1,NULL,25,NULL,'5',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 11:16:58',NULL,'','',NULL),(37,1,1,25,36,'@SourceLin 44',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 11:17:13',NULL,'','',NULL),(38,1,NULL,25,NULL,'5',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 11:17:21',NULL,'','',NULL),(39,1,NULL,25,NULL,'代写论文',NULL,NULL,0,2,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 11:19:00','2026-04-05 20:13:25','','',NULL),(40,1,NULL,20,NULL,'1',NULL,NULL,0,1,'treehole',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 15:36:42',NULL,'','',NULL),(41,1,NULL,86304,NULL,'1',NULL,NULL,0,1,'article',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 22:31:55',NULL,'','',NULL),(42,1,NULL,26,NULL,'煞笔',NULL,NULL,0,2,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-05 22:36:57',NULL,'','',NULL),(43,86010,NULL,25,NULL,'啦啦啦',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-07 21:24:12',NULL,'','',NULL),(44,86010,1,25,38,'@SourceLin 尼玛',NULL,NULL,0,2,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-07 21:24:30',NULL,'','',NULL),(45,86010,NULL,24,NULL,'尼玛',NULL,NULL,0,2,'treehole',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-07 21:31:17',NULL,'','',NULL),(46,86010,NULL,86312,NULL,'[UAT] article comment 20260407235248',NULL,NULL,0,1,'article',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-07 23:52:48',NULL,'','',NULL),(47,86010,NULL,26,NULL,'[UAT] say comment 20260407235249',NULL,NULL,0,1,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-07 23:52:49',NULL,'','',NULL),(48,86010,NULL,32,NULL,'[UAT] treehole comment 20260407235249',NULL,NULL,0,1,'treehole',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-07 23:52:50',NULL,'','',NULL),(49,86010,NULL,86310,NULL,'1',NULL,NULL,0,1,'article',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-08 01:11:29',NULL,'','',NULL),(50,1,86010,26,47,'@lyq 尼玛',NULL,NULL,0,2,'say',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-04-09 19:05:57',NULL,'','',NULL);
/*!40000 ALTER TABLE `b_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_comment_moderation`
--

DROP TABLE IF EXISTS `b_comment_moderation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COMMENT='评论机审记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_comment_moderation`
--

LOCK TABLES `b_comment_moderation` WRITE;
/*!40000 ALTER TABLE `b_comment_moderation` DISABLE KEYS */;
INSERT INTO `b_comment_moderation` VALUES (1,20,'PASS',1,NULL,'[]','2026-04-05 10:47:04'),(2,21,'PASS',1,NULL,'[]','2026-04-05 10:47:47'),(3,22,'PASS',1,NULL,'[]','2026-04-05 10:47:58'),(4,23,'PASS',1,NULL,'[]','2026-04-05 10:54:05'),(5,24,'PASS',1,NULL,'[]','2026-04-05 10:54:10'),(6,25,'PASS',1,NULL,'[]','2026-04-05 10:54:17'),(7,26,'PASS',1,NULL,'[]','2026-04-05 10:54:21'),(8,27,'PASS',1,NULL,'[]','2026-04-05 10:54:41'),(9,28,'PASS',1,NULL,'[]','2026-04-05 10:55:35'),(10,29,'PASS',1,NULL,'[]','2026-04-05 10:55:41'),(11,30,'REJECT',2,NULL,'[\"沙比\"]','2026-04-05 11:13:03'),(12,32,'REJECT',2,NULL,'[\"跑分\"]','2026-04-05 11:14:17'),(13,33,'AI_REVIEW',2,NULL,'[\"加微信\"]','2026-04-05 11:15:45'),(14,34,'PASS',2,NULL,'[]','2026-04-05 11:16:46'),(15,35,'PASS',2,NULL,'[]','2026-04-05 11:16:49'),(16,36,'PASS',2,NULL,'[]','2026-04-05 11:16:58'),(17,37,'PASS',2,NULL,'[]','2026-04-05 11:17:13'),(18,38,'PASS',2,NULL,'[]','2026-04-05 11:17:21'),(19,39,'AI_REVIEW',2,NULL,'[\"代写论文\"]','2026-04-05 11:19:00'),(20,40,'PASS',2,NULL,'[]','2026-04-05 15:36:42'),(21,41,'PASS',2,NULL,'[]','2026-04-05 22:31:55'),(22,42,'REJECT',2,NULL,'[\"煞笔\"]','2026-04-05 22:36:57'),(23,43,'PASS',2,NULL,'[]','2026-04-07 21:24:13'),(24,44,'REJECT',2,NULL,'[\"尼玛\"]','2026-04-07 21:24:30'),(25,45,'REJECT',2,NULL,'[\"尼玛\"]','2026-04-07 21:31:17'),(26,46,'PASS',2,NULL,'[]','2026-04-07 23:52:49'),(27,47,'PASS',2,NULL,'[]','2026-04-07 23:52:49'),(28,48,'PASS',2,NULL,'[]','2026-04-07 23:52:50'),(29,49,'PASS',2,NULL,'[]','2026-04-08 01:11:30'),(30,50,'REJECT',2,NULL,'[\"尼玛\"]','2026-04-09 19:05:58');
/*!40000 ALTER TABLE `b_comment_moderation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_follow`
--

DROP TABLE IF EXISTS `b_follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_follow`
--

LOCK TABLES `b_follow` WRITE;
/*!40000 ALTER TABLE `b_follow` DISABLE KEYS */;
/*!40000 ALTER TABLE `b_follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_friend_link`
--

DROP TABLE IF EXISTS `b_friend_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_friend_link`
--

LOCK TABLES `b_friend_link` WRITE;
/*!40000 ALTER TABLE `b_friend_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `b_friend_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_navigation`
--

DROP TABLE IF EXISTS `b_navigation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='网站导航目录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_navigation`
--

LOCK TABLES `b_navigation` WRITE;
/*!40000 ALTER TABLE `b_navigation` DISABLE KEYS */;
INSERT INTO `b_navigation` VALUES (1,'Naive UI','https://www.naiveui.com/','平台前台使用的 Vue3 组件库。','N',NULL,'前端','官方',1,1,10,1,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','初始化数据'),(2,'Spring Cloud Alibaba','https://spring.io/projects/spring-cloud-alibaba','后端微服务基础设施说明。','S',NULL,'后端','官方',1,1,20,0,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','初始化数据');
/*!40000 ALTER TABLE `b_navigation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_say`
--

DROP TABLE IF EXISTS `b_say`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COMMENT='说说/瞬间表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_say`
--

LOCK TABLES `b_say` WRITE;
/*!40000 ALTER TABLE `b_say` DISABLE KEYS */;
INSERT INTO `b_say` (`id`, `user_id`, `content`, `images`, `image_file_ids`, `like_count`, `comment_count`, `status`, `deleted`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1,1,'www',NULL,NULL,9,0,0,0,NULL,'2026-04-01 23:19:07',NULL,'2026-04-03 16:24:40',NULL),(2,1,'1111',NULL,NULL,11,0,0,0,NULL,'2026-04-01 23:44:00',NULL,'2026-04-03 16:24:34',NULL),(3,1,'222',NULL,NULL,7,1,0,0,NULL,'2026-04-01 23:44:11',NULL,'2026-04-03 00:12:23',NULL),(4,1,'随便说说',NULL,NULL,3,0,0,0,NULL,'2026-04-03 00:12:15',NULL,'2026-04-03 16:24:29',NULL),(5,1,'今天是周五',NULL,NULL,5,3,0,0,NULL,'2026-04-03 12:12:24',NULL,'2026-04-04 02:07:36',NULL),(6,1,'2',NULL,NULL,4,0,0,0,NULL,'2026-04-03 12:29:40',NULL,'2026-04-04 04:10:28',NULL),(7,1,'720',NULL,NULL,0,0,0,0,NULL,'2026-04-03 14:27:34',NULL,'2026-04-03 14:27:34',NULL),(8,1,'图片',NULL,NULL,0,0,0,0,NULL,'2026-04-03 15:09:33',NULL,'2026-04-03 15:09:33',NULL),(9,1,'郊野',NULL,NULL,0,2,0,0,NULL,'2026-04-03 15:35:20',NULL,'2026-04-04 02:07:27',NULL),(10,1,'111',NULL,NULL,0,0,0,0,NULL,'2026-04-04 02:09:04',NULL,'2026-04-04 02:09:04',NULL),(11,1,'图片测试',NULL,NULL,0,3,0,0,NULL,'2026-04-04 03:52:17',NULL,'2026-04-04 09:26:26',NULL),(12,1,'滑板车',NULL,NULL,0,0,0,0,NULL,'2026-04-04 05:01:45',NULL,'2026-04-04 05:01:45',NULL),(13,1,'图片',NULL,NULL,0,0,0,0,NULL,'2026-04-04 09:27:24',NULL,'2026-04-04 09:27:24',NULL),(14,1,'两张图片',NULL,NULL,0,0,0,0,NULL,'2026-04-04 09:28:04',NULL,'2026-04-04 09:28:04',NULL),(15,1,'图片测试',NULL,NULL,0,1,0,0,NULL,'2026-04-04 11:55:46',NULL,'2026-04-05 02:47:58',NULL),(16,1,'追风的少年，手里攥着风，身后飘着梦。?',NULL,'c9465ceb',0,0,0,0,NULL,'2026-04-04 12:07:21',NULL,'2026-04-04 12:07:21',NULL),(17,1,'1',NULL,'55e2b092',0,0,0,0,NULL,'2026-04-04 12:24:43',NULL,'2026-04-04 12:24:43',NULL),(18,1,'1',NULL,'55e2b092',0,0,0,0,NULL,'2026-04-04 12:24:59',NULL,'2026-04-04 12:24:59',NULL),(19,1,'今日份酷盖出街，滑板车是我的坐骑，风是我的搭档。?',NULL,'40,41',0,1,0,0,NULL,'2026-04-04 12:37:37',NULL,'2026-04-05 02:54:04',NULL),(20,1,'1',NULL,'43',0,0,0,0,NULL,'2026-04-04 12:42:06',NULL,'2026-04-04 12:42:06',NULL),(21,1,'这是 **Kimi 智能助手（Kimi Chat）** 的品牌图标，由北京月之暗面科技有限公司（Moonshot AI）开发，是国内头部的通用大模型 AI 产品。',NULL,'45,46,47,48,49,50,51,52,53',1,6,0,0,NULL,'2026-04-04 15:02:36',NULL,'2026-04-05 02:55:40',NULL),(22,1,'123',NULL,'56',0,0,0,0,NULL,'2026-04-04 18:14:32',NULL,'2026-04-04 18:14:32',NULL),(23,1,'222',NULL,'57,58,59',1,0,0,0,NULL,'2026-04-04 18:14:52',NULL,'2026-04-04 18:15:00',NULL),(24,1,'222',NULL,'69,70',0,1,0,0,NULL,'2026-04-04 18:26:13',NULL,'2026-04-05 02:54:40',NULL),(25,1,'111',NULL,'73,74',1,11,0,0,NULL,'2026-04-04 18:31:02',NULL,'2026-04-07 13:24:12',NULL),(26,1,'111',NULL,'80',0,1,0,0,NULL,'2026-04-05 14:32:26',NULL,'2026-04-07 15:52:49',NULL);
/*!40000 ALTER TABLE `b_say` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_tag`
--

DROP TABLE IF EXISTS `b_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=86269 DEFAULT CHARSET=utf8mb4 COMMENT='博客标签对象';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_tag`
--

LOCK TABLES `b_tag` WRITE;
/*!40000 ALTER TABLE `b_tag` DISABLE KEYS */;
INSERT INTO `b_tag` VALUES (86201,'晨雾','首页静态标签',NULL,1,100,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86202,'随笔','首页静态标签',NULL,2,94,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86203,'栖居','首页静态标签',NULL,3,88,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86204,'夜色','首页静态标签',NULL,4,82,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86205,'风景','首页静态标签',NULL,5,76,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86206,'诗句','首页静态标签',NULL,6,72,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86207,'留白','首页静态标签',NULL,7,68,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86208,'低语','首页静态标签',NULL,8,64,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86209,'月影','首页静态标签',NULL,9,62,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86210,'晚风','首页静态标签',NULL,10,60,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86211,'灯火','首页静态标签',NULL,11,58,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86212,'旧梦','首页静态标签',NULL,12,57,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86213,'青苔','首页静态标签',NULL,13,56,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86214,'山岚','首页静态标签',NULL,14,55,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86215,'浮光','首页静态标签',NULL,15,54,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86216,'纸页','首页静态标签',NULL,16,53,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86217,'回响','首页静态标签',NULL,17,52,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86218,'微雨','首页静态标签',NULL,18,51,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86219,'星河','首页静态标签',NULL,19,50,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86220,'余温','首页静态标签',NULL,20,49,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86221,'慢读','首页静态标签',NULL,21,48,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86222,'暮色','首页静态标签',NULL,22,47,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86223,'归途','首页静态标签',NULL,23,46,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86224,'清欢','首页静态标签',NULL,24,45,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86225,'薄暮','首页静态标签',NULL,25,44,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86226,'风铃','首页静态标签',NULL,26,43,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86227,'草木','首页静态标签',NULL,27,42,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86228,'落霞','首页静态标签',NULL,28,41,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86229,'旧巷','首页静态标签',NULL,29,40,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86230,'听雨','首页静态标签',NULL,30,39,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86231,'霜华','首页静态标签',NULL,31,38,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86232,'光尘','首页静态标签',NULL,32,37,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86233,'词章','首页静态标签',NULL,33,36,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86234,'月白','首页静态标签',NULL,34,35,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86235,'微醺','首页静态标签',NULL,35,34,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86236,'石阶','首页静态标签',NULL,36,33,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86237,'拾光','首页静态标签',NULL,37,32,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86238,'晴窗','首页静态标签',NULL,38,31,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86239,'潮声','首页静态标签',NULL,39,30,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86240,'春信','首页静态标签',NULL,40,29,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86241,'竹影','首页静态标签',NULL,41,28,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86242,'絮语','首页静态标签',NULL,42,27,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86243,'茶烟','首页静态标签',NULL,43,26,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86244,'川途','首页静态标签',NULL,44,25,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86245,'云隙','首页静态标签',NULL,45,24,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86246,'冬藏','首页静态标签',NULL,46,23,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86247,'夏鸣','首页静态标签',NULL,47,22,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86248,'花信','首页静态标签',NULL,48,21,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86249,'远山','首页静态标签',NULL,49,20,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86250,'心火','首页静态标签',NULL,50,19,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86251,'晨光','首页文章标签补充',NULL,51,18,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86252,'代码','首页文章标签补充',NULL,52,17,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86253,'秩序','首页文章标签补充',NULL,53,16,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86254,'重构','首页文章标签补充',NULL,54,15,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86255,'图像','首页文章标签补充',NULL,55,14,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86256,'记录','首页文章标签补充',NULL,56,13,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86257,'生活','首页文章标签补充',NULL,57,12,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86258,'感悟','首页文章标签补充',NULL,58,11,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86259,'夏日','首页文章标签补充',NULL,59,10,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86260,'书写','首页文章标签补充',NULL,60,9,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86261,'页面','首页文章标签补充',NULL,61,8,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86262,'技术','首页文章标签补充',NULL,62,7,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86263,'分享','首页文章标签补充',NULL,63,6,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86264,'黄昏','首页文章标签补充',NULL,64,5,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86265,'星光','首页文章标签补充',NULL,65,4,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86266,'摘记','首页文章标签补充',NULL,66,3,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86267,'雨声','首页文章标签补充',NULL,67,2,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin'),(86268,'阅读','首页文章标签补充',NULL,68,1,'2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0,'admin');
/*!40000 ALTER TABLE `b_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_treehole`
--

DROP TABLE IF EXISTS `b_treehole`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COMMENT='树洞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_treehole`
--

LOCK TABLES `b_treehole` WRITE;
/*!40000 ALTER TABLE `b_treehole` DISABLE KEYS */;
INSERT INTO `b_treehole` (`id`, `user_id`, `content`, `images`, `like_count`, `comment_count`, `status`, `deleted`, `ip_address`, `browser`, `system`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1,1,'2',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-04 10:28:48',NULL,'2026-04-04 10:28:48',NULL),(2,1,'2',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-04 10:28:49',NULL,'2026-04-04 10:28:49',NULL),(3,1,'3',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-04 10:28:56',NULL,'2026-04-04 10:28:56',NULL),(4,1,'1',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-04 10:29:00',NULL,'2026-04-04 10:29:00',NULL),(5,1,'1',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-04 10:29:01',NULL,'2026-04-04 10:29:01',NULL),(6,1,'1',NULL,0,4,0,0,NULL,NULL,NULL,NULL,'2026-04-04 10:29:02',NULL,'2026-04-04 10:30:04',NULL),(7,1,'1',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-04 10:29:03',NULL,'2026-04-04 10:29:03',NULL),(8,1,'1',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-04 10:29:03',NULL,'2026-04-04 10:29:03',NULL),(9,1,'啦啦啦',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-04 10:29:06',NULL,'2026-04-04 10:29:06',NULL),(10,NULL,'啦啦啦',NULL,1,0,0,0,NULL,NULL,NULL,NULL,'2026-04-04 18:11:57',NULL,'2026-04-04 18:12:06',NULL),(11,NULL,'尼玛',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-05 02:45:24',NULL,'2026-04-05 02:45:24',NULL),(12,NULL,'煞笔',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-05 02:45:54',NULL,'2026-04-05 02:45:54',NULL),(13,1,'上门服务',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-05 03:13:33',NULL,'2026-04-05 03:13:33',NULL),(14,1,'跑分',NULL,0,1,0,0,NULL,NULL,NULL,NULL,'2026-04-05 03:13:48',NULL,'2026-04-05 03:13:59',NULL),(15,1,'招嫖',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-05 03:18:15',NULL,'2026-04-05 03:18:15',NULL),(16,1,'代写论文',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-05 03:18:46',NULL,'2026-04-05 03:18:46',NULL),(17,1,'傻逼',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-05 03:32:23',NULL,'2026-04-05 03:32:23',NULL),(18,1,'草泥马',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-05 07:36:13',NULL,'2026-04-05 07:36:13',NULL),(19,1,'神经病',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-05 07:36:25',NULL,'2026-04-05 07:36:25',NULL),(20,1,'尼玛',NULL,0,1,0,0,NULL,NULL,NULL,NULL,'2026-04-05 07:36:32',NULL,'2026-04-05 07:36:42',NULL),(21,1,'kkk',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-05 07:36:54',NULL,'2026-04-05 07:36:54',NULL),(22,1,'尼玛',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-05 14:36:25',NULL,'2026-04-05 14:36:25',NULL),(23,1,'煞笔',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-05 14:36:38',NULL,'2026-04-05 14:36:38',NULL),(24,1,'煞笔',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-06 14:36:19',NULL,'2026-04-06 14:36:19',NULL),(25,NULL,'代发舒服点',NULL,1,0,0,0,NULL,NULL,NULL,NULL,'2026-04-06 14:41:56',NULL,'2026-04-07 13:31:09',NULL),(26,NULL,'地方撒啊啊啊啊啊啊啊啊啊啊',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-06 14:51:41',NULL,'2026-04-06 14:51:41',NULL),(27,NULL,'1',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-06 14:54:40',NULL,'2026-04-06 14:54:40',NULL),(28,NULL,'回家给个机会',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-06 15:22:24',NULL,'2026-04-06 15:22:24',NULL),(29,86010,'尼玛',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-07 13:31:25',NULL,'2026-04-07 13:31:25',NULL),(30,86010,'尼玛',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-07 13:35:57',NULL,'2026-04-07 13:35:57',NULL),(31,86010,'傻逼',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-07 13:36:13',NULL,'2026-04-07 13:36:13',NULL),(32,86010,'傻逼',NULL,0,1,0,0,NULL,NULL,NULL,NULL,'2026-04-07 13:38:38',NULL,'2026-04-07 15:52:50',NULL),(33,NULL,'1',NULL,0,0,0,0,NULL,NULL,NULL,NULL,'2026-04-09 19:03:04',NULL,'2026-04-09 19:03:04',NULL);
/*!40000 ALTER TABLE `b_treehole` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_user`
--

DROP TABLE IF EXISTS `b_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `deleted` int(11) DEFAULT '0' COMMENT '是否删除',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`) USING BTREE,
  KEY `idx_email` (`email`) USING BTREE,
  KEY `idx_b_user_avatar_file_id` (`avatar_file_id`)
) ENGINE=InnoDB AUTO_INCREMENT=86011 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_user`
--

LOCK TABLES `b_user` WRITE;
/*!40000 ALTER TABLE `b_user` DISABLE KEYS */;
INSERT INTO `b_user` VALUES (1,'SourceLin','SourceLin','$2a$10$IB4nMArb1eaHvF5XErcnZOWWHKhQ5xjBmiCyG5PswhNxhIuLTlOGG',2,'1012435723@qq.com',NULL,'http://127.0.0.1:9300/statics/2026/04/03/QQ图片20220304170905_20260403003010A004.jpg',NULL,NULL,NULL,NULL,0,0,0,'127.0.0.1','2026-04-09 12:32:32','0',NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,0,'','2026-03-31 19:36:28','','2026-04-07 13:07:05',NULL),(86001,'home_seed_lin_feng','林风',NULL,2,'linfeng@sourcelin.local',NULL,'https://picsum.photos/120/120?random=301',NULL,NULL,NULL,'把清晨与代码写进同一页。',0,0,0,NULL,NULL,'0',NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,0,'admin','2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402'),(86002,'home_seed_zhou_qi','周栖',NULL,2,'zhouqi@sourcelin.local',NULL,'https://picsum.photos/120/120?random=302',NULL,NULL,NULL,'关注结构、秩序和可维护性。',0,0,0,NULL,NULL,'0',NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,0,'admin','2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402'),(86003,'home_seed_shen_qing','沈青',NULL,2,'shenqing@sourcelin.local',NULL,'https://picsum.photos/120/120?random=303',NULL,NULL,NULL,'记录夜色与页面细节。',0,0,0,NULL,NULL,'0',NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,0,'admin','2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402'),(86004,'home_seed_bai_lu','白鹿',NULL,2,'bailu@sourcelin.local',NULL,'https://picsum.photos/120/120?random=304',NULL,NULL,NULL,'偏爱诗意表达与生活观察。',0,0,0,NULL,NULL,'0',NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,0,'admin','2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402'),(86005,'home_seed_cheng_ye','程野',NULL,2,'chengye@sourcelin.local',NULL,'https://picsum.photos/120/120?random=305',NULL,NULL,NULL,'擅长把日常体验转为文字。',0,0,0,NULL,NULL,'0',NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,0,'admin','2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402'),(86006,'home_seed_su_qi','苏栖',NULL,2,'suqi@sourcelin.local',NULL,'https://picsum.photos/120/120?random=306',NULL,NULL,NULL,'在夜晚写作，白天重构。',0,0,0,NULL,NULL,'0',NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,0,'admin','2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402'),(86010,'lyq','lyq','$2a$10$sXi.qON6lTUI1kEVDSujsOQI6Gzaf26juf/e2PEwXUXd1sAN6z482',1,'1012435726@qq.com',NULL,'http://127.0.0.1:8080/file/statics/2026/04/08/QQ图片20220304170905_20260408005913A001.jpg',84,NULL,NULL,NULL,0,0,0,'127.0.0.1','2026-04-08 14:21:41','0',NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,0,'','2026-04-07 21:09:34','','2026-04-07 16:47:56',NULL);
/*!40000 ALTER TABLE `b_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_web_config`
--

DROP TABLE IF EXISTS `b_web_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `ali_pay` varchar(90) DEFAULT NULL COMMENT '支付宝收款码FileId',
  `weixin_pay` varchar(90) DEFAULT NULL COMMENT '微信收款码FileId',
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='网站配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_web_config`
--

LOCK TABLES `b_web_config` WRITE;
/*!40000 ALTER TABLE `b_web_config` DISABLE KEYS */;
INSERT INTO `b_web_config` VALUES (1,'','http://www.wudada.online/dmImage/002843aVj7M.jpg','SourceLin','分享美好，记录生活','博客,技术,生活,随笔','ICP备案示例号','/','欢迎访问 圆圈 博客,分享美好，记录生活,如有侵权请联系删除',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,'林风','圆圈博客作者','https://picsum.photos/120/120?random=301','这里记录技术与生活，把每一次写作都当作一次认真表达。','admin','2026-04-02 10:55:57','admin','2026-04-02 10:55:57','home_static_seed_20260402',0);
/*!40000 ALTER TABLE `b_web_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `b_web_navbar`
--

DROP TABLE IF EXISTS `b_web_navbar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=62064 DEFAULT CHARSET=utf8mb4 COMMENT='门户导航栏对象';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `b_web_navbar`
--

LOCK TABLES `b_web_navbar` WRITE;
/*!40000 ALTER TABLE `b_web_navbar` DISABLE KEYS */;
INSERT INTO `b_web_navbar` VALUES (62001,'首页','博客首页','home','/',NULL,'1','M','0',0,1,NULL,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','alignment_seed_20260402',0),(62002,'热门','热门内容','sparkles','/hot',NULL,'1','M','0',0,2,NULL,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','alignment_seed_20260402',0),(62003,'文章','文章索引','archive','/archive',NULL,'1','M','0',0,3,NULL,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','alignment_seed_20260402',0),(62004,'说说','碎片动态','sparkles','/say',NULL,'1','M','0',0,4,NULL,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','alignment_seed_20260402',0),(62006,'树洞','匿名空间','chatbubbles','/treehole',NULL,'1','M','0',0,6,NULL,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','alignment_seed_20260402',0),(62007,'关于','关于本站','information','/about',NULL,'1','M','0',0,7,NULL,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','alignment_seed_20260402',0),(62031,'文章归档','时间归档','archive','/archive',NULL,'1','C','0',62003,1,NULL,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','alignment_seed_20260402',0),(62032,'文章分类','按分类浏览','category','/categories',NULL,'1','C','0',62003,2,NULL,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','alignment_seed_20260402',0),(62033,'文章标签','按标签浏览','tag','/tags',NULL,'1','C','0',62003,3,NULL,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','alignment_seed_20260402',0),(62061,'关于本站','站点介绍','information','/about',NULL,'1','C','0',62007,1,NULL,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','alignment_seed_20260402',0),(62062,'网站导航','站点与工具导航','navigation','/navigation',NULL,'1','C','0',62007,2,NULL,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','alignment_seed_20260402',0),(62063,'友情链接','友情链接页面','link','/links',NULL,'1','C','0',62007,3,NULL,'admin','2026-04-02 09:09:39','admin','2026-04-02 09:09:39','alignment_seed_20260402',0);
/*!40000 ALTER TABLE `b_web_navbar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_info`
--

DROP TABLE IF EXISTS `config_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `content` longtext COLLATE utf8_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COLLATE utf8_bin COMMENT 'source user',
  `src_ip` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `c_use` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `effect` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `type` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `c_schema` text COLLATE utf8_bin,
  `encrypted_data_key` text COLLATE utf8_bin COMMENT '秘钥',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='config_info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info`
--

LOCK TABLES `config_info` WRITE;
/*!40000 ALTER TABLE `config_info` DISABLE KEYS */;
INSERT INTO `config_info` VALUES (1,'application-dev.yml','DEFAULT_GROUP','spring:\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure\n  mvc:\n    pathmatch:\n      matching-strategy: ant_path_matcher\n\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n  okhttp:\n    enabled: true\n  httpclient:\n    enabled: false\n  client:\n    config:\n      default:\n        connectTimeout: 10000\n        readTimeout: 10000\n  compression:\n    request:\n      enabled: true\n    response:\n      enabled: true\n\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\n','aaa73b809cfd4d0058893aa13da57806','2020-05-20 12:00:00','2022-04-24 10:26:34','nacos','0:0:0:0:0:0:0:1','','','通用配置','null','null','yaml',NULL,''),(2,'sourcelin-gateway-dev.yml','DEFAULT_GROUP','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: sourcelin-auth\n          uri: lb://sourcelin-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestFilter\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: sourcelin-gen\n          uri: lb://sourcelin-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 定时任务\n        - id: sourcelin-job\n          uri: lb://sourcelin-job\n          predicates:\n            - Path=/schedule/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: sourcelin-system\n          uri: lb://sourcelin-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: sourcelin-file\n          uri: lb://sourcelin-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n','57cec5abd0e0a6b77d853750344a9dc0','2020-05-14 14:17:55','2022-09-29 02:48:32','nacos','0:0:0:0:0:0:0:1','','','网关模块','null','null','yaml','',''),(3,'sourcelin-auth-dev.yml','DEFAULT_GROUP','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n','8bd9dada9a94822feeab40de55efced6','2020-11-20 00:00:00','2022-09-29 02:48:42','nacos','0:0:0:0:0:0:0:1','','','认证中心','null','null','yaml','',''),(4,'sourcelin-monitor-dev.yml','DEFAULT_GROUP','# spring\nspring:\n  security:\n    user:\n      name: sourcelin\n      password: 123456\n  boot:\n    admin:\n      ui:\n        title: sourcelin服务状态监控\n','6f122fd2bfb8d45f858e7d6529a9cd44','2020-11-20 00:00:00','2022-09-29 02:48:54','nacos','0:0:0:0:0:0:0:1','','','监控中心','null','null','yaml','',''),(5,'sourcelin-system-dev.yml','DEFAULT_GROUP','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.system\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip','4e4eb1af342cbd58c229e2fd62986137','2020-11-20 00:00:00','2023-10-23 06:25:34','nacos','0:0:0:0:0:0:0:1','','','系统模块','null','null','yaml','',''),(6,'sourcelin-gen-dev.yml','DEFAULT_GROUP','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.gen.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n\n# 代码生成\ngen:\n  # 作者\n  author: sourcelin\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.sourcelin.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n','4872335e95ffb20796734a9bf55685fd','2020-11-20 00:00:00','2023-10-23 06:25:50','nacos','0:0:0:0:0:0:0:1','','','代码生成','null','null','yaml','',''),(7,'sourcelin-job-dev.yml','DEFAULT_GROUP','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.job.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n','569b2b00429b1154c3dd52efbd76f4d7','2020-11-20 00:00:00','2023-10-23 06:26:03','nacos','0:0:0:0:0:0:0:1','','','定时任务','null','null','yaml','',''),(8,'sourcelin-file-dev.yml','DEFAULT_GROUP','# 本地文件上传    \r\nfile:\r\n    domain: http://127.0.0.1:9300\r\n    path: D:/sourcelin/uploadPath\r\n    prefix: /statics\r\n\r\n# FastDFS配置\r\nfdfs:\r\n  domain: http://8.129.231.12\r\n  soTimeout: 3000\r\n  connectTimeout: 2000\r\n  trackerList: 8.129.231.12:22122\r\n\r\n# Minio配置\r\nminio:\r\n  url: http://8.129.231.12:9000\r\n  accessKey: minioadmin\r\n  secretKey: minioadmin\r\n  bucketName: test','5382b93f3d8059d6068c0501fdd41195','2020-11-20 00:00:00','2020-12-21 21:01:59',NULL,'0:0:0:0:0:0:0:1','','','文件服务','null','null','yaml',NULL,''),(9,'sentinel-sourcelin-gateway','DEFAULT_GROUP','[\r\n    {\r\n        \"resource\": \"sourcelin-auth\",\r\n        \"count\": 500,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"sourcelin-system\",\r\n        \"count\": 1000,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"sourcelin-gen\",\r\n        \"count\": 200,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"sourcelin-job\",\r\n        \"count\": 300,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    }\r\n]','9f3a3069261598f74220bc47958ec252','2020-11-20 00:00:00','2020-11-20 00:00:00',NULL,'0:0:0:0:0:0:0:1','','','限流策略','null','null','json',NULL,''),(21,'sourcelin-gateway-dev.yml','DEFAULT_GROUP','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: sourcelin-auth\n          uri: lb://sourcelin-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestFilter\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: sourcelin-gen\n          uri: lb://sourcelin-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 定时任务\n        - id: sourcelin-job\n          uri: lb://sourcelin-job\n          predicates:\n            - Path=/schedule/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: sourcelin-system\n          uri: lb://sourcelin-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: sourcelin-file\n          uri: lb://sourcelin-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n','eb34c667706a38bed8efc1e251ee4ff0','2023-10-23 09:07:51','2023-10-23 09:08:29','nacos','0:0:0:0:0:0:0:1','','','网关模块','','','yaml','',NULL),(23,'sourcelin-auth-dev.yml','DEFAULT_GROUP','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n','8bd9dada9a94822feeab40de55efced6','2023-10-23 09:09:22','2023-10-23 09:09:22',NULL,'0:0:0:0:0:0:0:1','','','认证中心',NULL,NULL,'yaml',NULL,NULL),(24,'sourcelin-monitor-dev.yml','DEFAULT_GROUP','# spring\nspring:\n  security:\n    user:\n      name: sourcelin\n      password: 123456\n  boot:\n    admin:\n      ui:\n        title: sourcelin服务状态监控\n','37cca58185e62cf785232b0bff321976','2023-10-23 09:09:22','2023-10-24 10:38:19','nacos','0:0:0:0:0:0:0:1','','','监控中心','','','yaml','',NULL),(25,'sourcelin-system-dev.yml','DEFAULT_GROUP','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/sourcelin-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.system\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip','c4dbcde02707a9a988efcda8dca44c30','2023-10-23 09:09:22','2023-10-24 10:38:55','nacos','0:0:0:0:0:0:0:1','','','系统模块','','','yaml','',NULL),(26,'sourcelin-gen-dev.yml','DEFAULT_GROUP','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/sourcelin-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.gen.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n\n# 代码生成\ngen:\n  # 作者\n  author: sourcelin\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.sourcelin.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n','1550eb781ad1c3774639651ecd169f8c','2023-10-23 09:09:22','2023-10-24 10:39:15','nacos','0:0:0:0:0:0:0:1','','','代码生成','','','yaml','',NULL),(27,'sourcelin-job-dev.yml','DEFAULT_GROUP','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/sourcelin-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.job.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n','fde8acd1c3f602255bc920225dac5bcd','2023-10-23 09:09:22','2023-10-24 10:39:34','nacos','0:0:0:0:0:0:0:1','','','定时任务','','','yaml','',NULL),(28,'sourcelin-file-dev.yml','DEFAULT_GROUP','# 本地文件上传    \nfile:\n    domain: http://127.0.0.1:9300\n    path: D:/sourcelin/uploadPath\n    prefix: /statics\n\n# FastDFS配置\nfdfs:\n  domain: http://8.129.231.12\n  soTimeout: 3000\n  connectTimeout: 2000\n  trackerList: 8.129.231.12:22122\n\n# Minio配置\nminio:\n  url: http://8.129.231.12:9000\n  accessKey: minioadmin\n  secretKey: minioadmin\n  bucketName: test','2f6168ec171dd306a29c06ddcff0a404','2023-10-23 09:09:22','2023-10-24 10:39:54','nacos','0:0:0:0:0:0:0:1','','','文件服务','','','yaml','',NULL),(29,'sentinel-sourcelin-gateway','DEFAULT_GROUP','[\n    {\n        \"resource\": \"sourcelin-auth\",\n        \"count\": 500,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"sourcelin-system\",\n        \"count\": 1000,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"sourcelin-gen\",\n        \"count\": 200,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"sourcelin-job\",\n        \"count\": 300,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    }\n]','e098656939fcd7d6b7da93f317b16b83','2023-10-23 09:09:22','2023-10-23 09:12:03','nacos','0:0:0:0:0:0:0:1','','','限流策略','','','json','',NULL);
/*!40000 ALTER TABLE `config_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_info_aggr`
--

DROP TABLE IF EXISTS `config_info_aggr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config_info_aggr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'datum_id',
  `content` longtext COLLATE utf8_bin NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='增加租户字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info_aggr`
--

LOCK TABLES `config_info_aggr` WRITE;
/*!40000 ALTER TABLE `config_info_aggr` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_info_aggr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_info_beta`
--

DROP TABLE IF EXISTS `config_info_beta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config_info_beta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT 'app_name',
  `content` longtext COLLATE utf8_bin NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COLLATE utf8_bin COMMENT 'source user',
  `src_ip` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` text COLLATE utf8_bin COMMENT '秘钥',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='config_info_beta';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info_beta`
--

LOCK TABLES `config_info_beta` WRITE;
/*!40000 ALTER TABLE `config_info_beta` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_info_beta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_info_tag`
--

DROP TABLE IF EXISTS `config_info_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config_info_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT 'app_name',
  `content` longtext COLLATE utf8_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COLLATE utf8_bin COMMENT 'source user',
  `src_ip` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='config_info_tag';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_info_tag`
--

LOCK TABLES `config_info_tag` WRITE;
/*!40000 ALTER TABLE `config_info_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_info_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config_tags_relation`
--

DROP TABLE IF EXISTS `config_tags_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config_tags_relation` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`) USING BTREE,
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='config_tag_relation';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config_tags_relation`
--

LOCK TABLES `config_tags_relation` WRITE;
/*!40000 ALTER TABLE `config_tags_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `config_tags_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_info`
--

DROP TABLE IF EXISTS `file_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COMMENT='文件中心主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_info`
--

LOCK TABLES `file_info` WRITE;
/*!40000 ALTER TABLE `file_info` DISABLE KEYS */;
INSERT INTO `file_info` VALUES (1,'微信图片_20241125172602_20260403233512A001.png','微信图片_20241125172602.png','png','image/png',1566204,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/03/微信图片_20241125172602_20260403233512A001.png','http://127.0.0.1:9300/statics/2026/04/03/微信图片_20241125172602_20260403233512A001.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-03 23:35:13',NULL,'2026-04-03 23:35:13'),(2,'a57445f57d707d287473b8b52f5c04bc_1_20260404100758A001.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404100758A001.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404100758A001.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 10:07:58',NULL,'2026-04-04 10:07:58'),(3,'a57445f57d707d287473b8b52f5c04bc_1_20260404102012A002.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404102012A002.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404102012A002.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 10:20:13',NULL,'2026-04-04 10:20:13'),(4,'002843aVj7M_20260404114302A002.jpg','002843aVj7M.jpg','jpg','image/jpeg',1147193,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/002843aVj7M_20260404114302A002.jpg','http://127.0.0.1:9300/statics/2026/04/04/002843aVj7M_20260404114302A002.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:43:02',NULL,'2026-04-04 11:43:02'),(5,'a57445f57d707d287473b8b52f5c04bc_1_20260404114302A003.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404114302A003.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404114302A003.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:43:02',NULL,'2026-04-04 11:43:02'),(6,'logo-icon-white-bg_20260404114327A004.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404114327A004.png','http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404114327A004.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:43:27',NULL,'2026-04-04 11:43:27'),(7,'002843aVj7M_20260404114258A001.jpg','002843aVj7M.jpg','jpg','image/jpeg',1147193,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/002843aVj7M_20260404114258A001.jpg','http://127.0.0.1:9300/statics/2026/04/04/002843aVj7M_20260404114258A001.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:42:59',NULL,'2026-04-04 11:42:59'),(8,'logo-icon-white-bg_20260404114523A005.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404114523A005.png','http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404114523A005.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:45:24',NULL,'2026-04-04 11:45:24'),(9,'a57445f57d707d287473b8b52f5c04bc_1_20260404114808A006.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404114808A006.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404114808A006.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:48:09',NULL,'2026-04-04 11:48:09'),(10,'002843aVj7M_20260404115008A007.jpg','002843aVj7M.jpg','jpg','image/jpeg',1147193,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/002843aVj7M_20260404115008A007.jpg','http://127.0.0.1:9300/statics/2026/04/04/002843aVj7M_20260404115008A007.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:50:08',NULL,'2026-04-04 11:50:08'),(11,'a57445f57d707d287473b8b52f5c04bc_1_20260404115011A008.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404115011A008.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404115011A008.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:50:11',NULL,'2026-04-04 11:50:11'),(12,'logo-icon-white-bg_20260404115018A009.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404115018A009.png','http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404115018A009.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:50:18',NULL,'2026-04-04 11:50:18'),(13,'QQ图片20211223091408_20260404115021A010.jpg','QQ图片20211223091408.jpg','jpg','image/jpeg',68577,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/QQ图片20211223091408_20260404115021A010.jpg','http://127.0.0.1:9300/statics/2026/04/04/QQ图片20211223091408_20260404115021A010.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:50:22',NULL,'2026-04-04 11:50:22'),(14,'kimi_20260404115025A011.jpg','kimi.jpg','jpg','image/jpeg',3083,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/kimi_20260404115025A011.jpg','http://127.0.0.1:9300/statics/2026/04/04/kimi_20260404115025A011.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:50:26',NULL,'2026-04-04 11:50:26'),(15,'logo_640_640_20260404115029A012.png','logo_640_640.png','png','image/png',24950,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/logo_640_640_20260404115029A012.png','http://127.0.0.1:9300/statics/2026/04/04/logo_640_640_20260404115029A012.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:50:30',NULL,'2026-04-04 11:50:30'),(16,'getQRCodeLogo_20260404115034A013.png','getQRCodeLogo.png','png','image/png',3359,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/getQRCodeLogo_20260404115034A013.png','http://127.0.0.1:9300/statics/2026/04/04/getQRCodeLogo_20260404115034A013.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:50:35',NULL,'2026-04-04 11:50:35'),(17,'QQ图片20220304170905_20260404115038A014.jpg','QQ图片20220304170905.jpg','jpg','image/jpeg',22398,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/QQ图片20220304170905_20260404115038A014.jpg','http://127.0.0.1:9300/statics/2026/04/04/QQ图片20220304170905_20260404115038A014.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:50:39',NULL,'2026-04-04 11:50:39'),(18,'tongyi_20260404115045A015.png','tongyi.png','png','image/png',4108,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/tongyi_20260404115045A015.png','http://127.0.0.1:9300/statics/2026/04/04/tongyi_20260404115045A015.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 11:50:45',NULL,'2026-04-04 11:50:45'),(19,'a57445f57d707d287473b8b52f5c04bc_1_20260404130040A018.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404130040A018.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404130040A018.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 13:00:40',NULL,'2026-04-04 13:00:40'),(20,'a57445f57d707d287473b8b52f5c04bc_1_20260404130022A017.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404130022A017.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404130022A017.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 13:00:22',NULL,'2026-04-04 13:00:22'),(21,'002843aVj7M_20260404130016A016.jpg','002843aVj7M.jpg','jpg','image/jpeg',1147193,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/002843aVj7M_20260404130016A016.jpg','http://127.0.0.1:9300/statics/2026/04/04/002843aVj7M_20260404130016A016.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 13:00:17',NULL,'2026-04-04 13:00:17'),(22,'logo-icon-white-bg_20260404172749A003.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404172749A003.png','http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404172749A003.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 17:27:49',NULL,'2026-04-04 17:27:49'),(23,'a57445f57d707d287473b8b52f5c04bc_1_20260404172731A002.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404172731A002.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404172731A002.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 17:27:32',NULL,'2026-04-04 17:27:32'),(24,'a57445f57d707d287473b8b52f5c04bc_1_20260404172751A004.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404172751A004.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404172751A004.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 17:27:52',NULL,'2026-04-04 17:27:52'),(25,'002843aVj7M_20260404172713A001.jpg','002843aVj7M.jpg','jpg','image/jpeg',1147193,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/002843aVj7M_20260404172713A001.jpg','http://127.0.0.1:9300/statics/2026/04/04/002843aVj7M_20260404172713A001.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 17:27:14',NULL,'2026-04-04 17:27:14'),(26,'微信图片_20241125172602_20260404183851A006.png','微信图片_20241125172602.png','png','image/png',1566204,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/微信图片_20241125172602_20260404183851A006.png','http://127.0.0.1:9300/statics/2026/04/04/微信图片_20241125172602_20260404183851A006.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 18:38:52',NULL,'2026-04-04 18:38:52'),(27,'002843aVj7M_20260404183904A007.jpg','002843aVj7M.jpg','jpg','image/jpeg',1147193,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/002843aVj7M_20260404183904A007.jpg','http://127.0.0.1:9300/statics/2026/04/04/002843aVj7M_20260404183904A007.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 18:39:05',NULL,'2026-04-04 18:39:05'),(28,'QQ截图20210713101850_20260404183830A005.png','QQ截图20210713101850.png','png','image/png',1707073,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/QQ截图20210713101850_20260404183830A005.png','http://127.0.0.1:9300/statics/2026/04/04/QQ截图20210713101850_20260404183830A005.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 18:38:31',NULL,'2026-04-04 18:38:31'),(29,'logo-icon-white-bg_20260404195536A010.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404195536A010.png','http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404195536A010.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 19:55:37',NULL,'2026-04-04 19:55:37'),(30,'a57445f57d707d287473b8b52f5c04bc_1_20260404195532A008.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404195532A008.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404195532A008.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 19:55:32',NULL,'2026-04-04 19:55:32'),(31,'a57445f57d707d287473b8b52f5c04bc_1_20260404195536A009.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404195536A009.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404195536A009.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 19:55:37',NULL,'2026-04-04 19:55:37'),(32,'a57445f57d707d287473b8b52f5c04bc_1_20260404200716A013.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404200716A013.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404200716A013.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:07:17',NULL,'2026-04-04 20:07:17'),(33,'a57445f57d707d287473b8b52f5c04bc_1_20260404200658A012.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404200658A012.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404200658A012.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:06:59',NULL,'2026-04-04 20:06:59'),(34,'a57445f57d707d287473b8b52f5c04bc_1_20260404200642A011.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404200642A011.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404200642A011.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:06:43',NULL,'2026-04-04 20:06:43'),(35,'a57445f57d707d287473b8b52f5c04bc_1_20260404202356A017.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404202356A017.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404202356A017.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:23:56',NULL,'2026-04-04 20:23:56'),(36,'a57445f57d707d287473b8b52f5c04bc_1_20260404202337A014.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404202337A014.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404202337A014.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:23:38',NULL,'2026-04-04 20:23:38'),(37,'a57445f57d707d287473b8b52f5c04bc_1_20260404202350A015.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404202350A015.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404202350A015.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:23:51',NULL,'2026-04-04 20:23:51'),(38,'a57445f57d707d287473b8b52f5c04bc_1_20260404202353A016.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404202353A016.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404202353A016.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:23:53',NULL,'2026-04-04 20:23:53'),(39,'logo-icon-white-bg_20260404202413A018.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404202413A018.png','http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404202413A018.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:24:14',NULL,'2026-04-04 20:24:14'),(40,'a57445f57d707d287473b8b52f5c04bc_1_20260404203558A020.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404203558A020.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404203558A020.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:35:59',NULL,'2026-04-04 20:35:59'),(41,'a57445f57d707d287473b8b52f5c04bc_1_20260404203607A021.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404203607A021.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404203607A021.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:36:08',NULL,'2026-04-04 20:36:08'),(42,'a57445f57d707d287473b8b52f5c04bc_1_20260404203531A019.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404203531A019.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404203531A019.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:35:31',NULL,'2026-04-04 20:35:31'),(43,'a57445f57d707d287473b8b52f5c04bc_1_20260404204203A022.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404204203A022.jpg','http://127.0.0.1:9300/statics/2026/04/04/a57445f57d707d287473b8b52f5c04bc_1_20260404204203A022.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:42:04',NULL,'2026-04-04 20:42:04'),(44,'logo-icon-white-bg_20260404204333A023.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404204333A023.png','http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404204333A023.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 20:43:34',NULL,'2026-04-04 20:43:34'),(45,'kimi_20260404230137A001.jpg','kimi.jpg','jpg','image/jpeg',3083,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\04\\kimi_20260404230137A001.jpg','http://127.0.0.1:9300/statics/2026/04/04/kimi_20260404230137A001.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 23:01:38',NULL,'2026-04-04 23:01:38'),(46,'kimi_20260404230201A002.jpg','kimi.jpg','jpg','image/jpeg',3083,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\04\\kimi_20260404230201A002.jpg','http://127.0.0.1:9300/statics/2026/04/04/kimi_20260404230201A002.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 23:02:02',NULL,'2026-04-04 23:02:02'),(47,'kimi_20260404230205A003.jpg','kimi.jpg','jpg','image/jpeg',3083,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\04\\kimi_20260404230205A003.jpg','http://127.0.0.1:9300/statics/2026/04/04/kimi_20260404230205A003.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 23:02:05',NULL,'2026-04-04 23:02:05'),(48,'kimi_20260404230209A004.jpg','kimi.jpg','jpg','image/jpeg',3083,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\04\\kimi_20260404230209A004.jpg','http://127.0.0.1:9300/statics/2026/04/04/kimi_20260404230209A004.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 23:02:09',NULL,'2026-04-04 23:02:09'),(49,'kimi_20260404230215A005.jpg','kimi.jpg','jpg','image/jpeg',3083,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\04\\kimi_20260404230215A005.jpg','http://127.0.0.1:9300/statics/2026/04/04/kimi_20260404230215A005.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 23:02:15',NULL,'2026-04-04 23:02:15'),(50,'kimi_20260404230220A006.jpg','kimi.jpg','jpg','image/jpeg',3083,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\04\\kimi_20260404230220A006.jpg','http://127.0.0.1:9300/statics/2026/04/04/kimi_20260404230220A006.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 23:02:20',NULL,'2026-04-04 23:02:20'),(51,'kimi_20260404230225A007.jpg','kimi.jpg','jpg','image/jpeg',3083,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\04\\kimi_20260404230225A007.jpg','http://127.0.0.1:9300/statics/2026/04/04/kimi_20260404230225A007.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 23:02:26',NULL,'2026-04-04 23:02:26'),(52,'kimi_20260404230229A008.jpg','kimi.jpg','jpg','image/jpeg',3083,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\04\\kimi_20260404230229A008.jpg','http://127.0.0.1:9300/statics/2026/04/04/kimi_20260404230229A008.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 23:02:30',NULL,'2026-04-04 23:02:30'),(53,'kimi_20260404230233A009.jpg','kimi.jpg','jpg','image/jpeg',3083,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\04\\kimi_20260404230233A009.jpg','http://127.0.0.1:9300/statics/2026/04/04/kimi_20260404230233A009.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 23:02:34',NULL,'2026-04-04 23:02:34'),(54,'kimi_20260404230303A010.jpg','kimi.jpg','jpg','image/jpeg',3083,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\04\\kimi_20260404230303A010.jpg','http://127.0.0.1:9300/statics/2026/04/04/kimi_20260404230303A010.jpg','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 23:03:03',NULL,'2026-04-04 23:03:03'),(55,'logo-icon-white-bg_20260404230307A011.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\04\\logo-icon-white-bg_20260404230307A011.png','http://127.0.0.1:9300/statics/2026/04/04/logo-icon-white-bg_20260404230307A011.png','PUBLIC',NULL,NULL,NULL,NULL,'TEMP',0,NULL,NULL,NULL,NULL,NULL,'2026-04-04 23:03:07',NULL,'2026-04-04 23:03:07'),(56,'logo-icon-white-bg_20260405021430A001.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\logo-icon-white-bg_20260405021430A001.png','http://127.0.0.1:8080/file/statics/2026/04/05/logo-icon-white-bg_20260405021430A001.png','PUBLIC','say','22','BLOG',1,'ACTIVE',1,NULL,NULL,NULL,NULL,'1','2026-04-05 02:14:31',NULL,'2026-04-05 02:14:32'),(57,'logo-icon-white-bg_20260405021444A002.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\logo-icon-white-bg_20260405021444A002.png','http://127.0.0.1:8080/file/statics/2026/04/05/logo-icon-white-bg_20260405021444A002.png','PUBLIC','say','23','BLOG',1,'ACTIVE',1,NULL,NULL,NULL,NULL,'1','2026-04-05 02:14:45',NULL,'2026-04-05 02:14:53'),(58,'logo-icon-white-bg_20260405021448A003.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\logo-icon-white-bg_20260405021448A003.png','http://127.0.0.1:8080/file/statics/2026/04/05/logo-icon-white-bg_20260405021448A003.png','PUBLIC','say','23','BLOG',1,'ACTIVE',1,NULL,NULL,NULL,NULL,'1','2026-04-05 02:14:48',NULL,'2026-04-05 02:14:53'),(59,'logo-icon-white-bg_20260405021450A004.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\logo-icon-white-bg_20260405021450A004.png','http://127.0.0.1:8080/file/statics/2026/04/05/logo-icon-white-bg_20260405021450A004.png','PUBLIC','say','23','BLOG',1,'ACTIVE',1,NULL,NULL,NULL,NULL,'1','2026-04-05 02:14:51',NULL,'2026-04-05 02:14:53'),(60,'logo-icon-white-bg_20260405021542A005.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\logo-icon-white-bg_20260405021542A005.png','http://127.0.0.1:8080/file/statics/2026/04/05/logo-icon-white-bg_20260405021542A005.png','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:15:42',NULL,'2026-04-05 02:15:42'),(61,'生成微信头像_20260405021551A006.png','生成微信头像.png','png','image/png',1562025,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\生成微信头像_20260405021551A006.png','http://127.0.0.1:8080/file/statics/2026/04/05/生成微信头像_20260405021551A006.png','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:15:52',NULL,'2026-04-05 02:15:52'),(62,'logo-icon-white-bg_20260405021606A007.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\logo-icon-white-bg_20260405021606A007.png','http://127.0.0.1:8080/file/statics/2026/04/05/logo-icon-white-bg_20260405021606A007.png','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:16:06',NULL,'2026-04-05 02:16:06'),(63,'生成微信头像_20260405021611A008.png','生成微信头像.png','png','image/png',1562025,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\生成微信头像_20260405021611A008.png','http://127.0.0.1:8080/file/statics/2026/04/05/生成微信头像_20260405021611A008.png','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:16:12',NULL,'2026-04-05 02:16:12'),(64,'微信图片_20241125172602_20260405021858A009.png','微信图片_20241125172602.png','png','image/png',1566204,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\微信图片_20241125172602_20260405021858A009.png','http://127.0.0.1:8080/file/statics/2026/04/05/微信图片_20241125172602_20260405021858A009.png','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:18:58',NULL,'2026-04-05 02:18:58'),(65,'logo-icon-white-bg_20260405022104A010.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\logo-icon-white-bg_20260405022104A010.png','http://127.0.0.1:8080/file/statics/2026/04/05/logo-icon-white-bg_20260405022104A010.png','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:21:05',NULL,'2026-04-05 02:21:05'),(66,'logo-icon-white-bg_20260405022108A011.png','logo-icon-white-bg.png','png','image/png',24409,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\logo-icon-white-bg_20260405022108A011.png','http://127.0.0.1:8080/file/statics/2026/04/05/logo-icon-white-bg_20260405022108A011.png','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:21:09',NULL,'2026-04-05 02:21:09'),(67,'a57445f57d707d287473b8b52f5c04bc_1_20260405022234A012.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\a57445f57d707d287473b8b52f5c04bc_1_20260405022234A012.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/a57445f57d707d287473b8b52f5c04bc_1_20260405022234A012.jpg','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:22:34',NULL,'2026-04-05 02:22:34'),(68,'a57445f57d707d287473b8b52f5c04bc_1_20260405022236A013.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\a57445f57d707d287473b8b52f5c04bc_1_20260405022236A013.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/a57445f57d707d287473b8b52f5c04bc_1_20260405022236A013.jpg','PUBLIC',NULL,NULL,'BLOG',1,'PENDING_DELETE',0,NULL,NULL,'2026-04-05 02:22:45',NULL,'1','2026-04-05 02:22:37',NULL,'2026-04-05 02:22:45'),(69,'a57445f57d707d287473b8b52f5c04bc_1_20260405022605A014.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\a57445f57d707d287473b8b52f5c04bc_1_20260405022605A014.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/a57445f57d707d287473b8b52f5c04bc_1_20260405022605A014.jpg','PUBLIC','say','24','BLOG',1,'ACTIVE',1,NULL,NULL,NULL,NULL,'1','2026-04-05 02:26:06',NULL,'2026-04-05 02:26:14'),(70,'a57445f57d707d287473b8b52f5c04bc_1_20260405022609A015.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\a57445f57d707d287473b8b52f5c04bc_1_20260405022609A015.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/a57445f57d707d287473b8b52f5c04bc_1_20260405022609A015.jpg','PUBLIC','say','24','BLOG',1,'ACTIVE',1,NULL,NULL,NULL,NULL,'1','2026-04-05 02:26:09',NULL,'2026-04-05 02:26:14'),(71,'a57445f57d707d287473b8b52f5c04bc_1_20260405022825A016.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\a57445f57d707d287473b8b52f5c04bc_1_20260405022825A016.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/a57445f57d707d287473b8b52f5c04bc_1_20260405022825A016.jpg','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:28:25',NULL,'2026-04-05 02:28:25'),(72,'a57445f57d707d287473b8b52f5c04bc_1_20260405023008A017.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\a57445f57d707d287473b8b52f5c04bc_1_20260405023008A017.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/a57445f57d707d287473b8b52f5c04bc_1_20260405023008A017.jpg','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:30:09',NULL,'2026-04-05 02:30:09'),(73,'a57445f57d707d287473b8b52f5c04bc_1_20260405023056A018.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\a57445f57d707d287473b8b52f5c04bc_1_20260405023056A018.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/a57445f57d707d287473b8b52f5c04bc_1_20260405023056A018.jpg','PUBLIC','say','25','BLOG',1,'ACTIVE',1,NULL,NULL,NULL,NULL,'1','2026-04-05 02:30:56',NULL,'2026-04-05 02:31:03'),(74,'a57445f57d707d287473b8b52f5c04bc_1_20260405023059A019.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\a57445f57d707d287473b8b52f5c04bc_1_20260405023059A019.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/a57445f57d707d287473b8b52f5c04bc_1_20260405023059A019.jpg','PUBLIC','say','25','BLOG',1,'ACTIVE',1,NULL,NULL,NULL,NULL,'1','2026-04-05 02:31:00',NULL,'2026-04-05 02:31:03'),(75,'a57445f57d707d287473b8b52f5c04bc_1_20260405023224A020.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\a57445f57d707d287473b8b52f5c04bc_1_20260405023224A020.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/a57445f57d707d287473b8b52f5c04bc_1_20260405023224A020.jpg','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:32:25',NULL,'2026-04-05 02:32:25'),(76,'a57445f57d707d287473b8b52f5c04bc_1_20260405023310A021.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\a57445f57d707d287473b8b52f5c04bc_1_20260405023310A021.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/a57445f57d707d287473b8b52f5c04bc_1_20260405023310A021.jpg','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:33:11',NULL,'2026-04-05 02:33:11'),(77,'002843aVj7M_20260405023546A022.jpg','002843aVj7M.jpg','jpg','image/jpeg',1147193,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\002843aVj7M_20260405023546A022.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/002843aVj7M_20260405023546A022.jpg','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 02:35:46',NULL,'2026-04-05 02:35:46'),(78,'a57445f57d707d287473b8b52f5c04bc_1_20260405140655A001.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\a57445f57d707d287473b8b52f5c04bc_1_20260405140655A001.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/a57445f57d707d287473b8b52f5c04bc_1_20260405140655A001.jpg','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 14:06:55',NULL,'2026-04-05 14:06:55'),(79,'a57445f57d707d287473b8b52f5c04bc_1_20260405140658A002.jpg','a57445f57d707d287473b8b52f5c04bc_1.jpg','jpg','image/jpeg',58010,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\a57445f57d707d287473b8b52f5c04bc_1_20260405140658A002.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/a57445f57d707d287473b8b52f5c04bc_1_20260405140658A002.jpg','PUBLIC',NULL,NULL,'BLOG',1,'TEMP',0,NULL,NULL,NULL,NULL,'1','2026-04-05 14:06:59',NULL,'2026-04-05 14:06:59'),(80,'012a336e6bdca22d4ea27526e348dbb_20260405223215A001.jpg','012a336e6bdca22d4ea27526e348dbb.jpg','jpg','image/jpeg',9209717,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\05\\012a336e6bdca22d4ea27526e348dbb_20260405223215A001.jpg','http://127.0.0.1:8080/file/statics/2026/04/05/012a336e6bdca22d4ea27526e348dbb_20260405223215A001.jpg','PUBLIC','say','26','BLOG',1,'ACTIVE',1,NULL,NULL,NULL,NULL,'1','2026-04-05 22:32:18',NULL,'2026-04-05 22:32:27'),(81,'blob_20260406230202A001.png','blob',NULL,'image/png',117934,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\06\\blob_20260406230202A001.png','http://127.0.0.1:8080/file/statics/2026/04/06/blob_20260406230202A001.png','PUBLIC','avatar','null','ADMIN',1,'ACTIVE',1,NULL,NULL,NULL,NULL,'1','2026-04-06 23:02:03',NULL,'2026-04-06 23:02:04'),(82,'blob_20260406230206A002.png','blob',NULL,'image/png',117934,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\06\\blob_20260406230206A002.png','http://127.0.0.1:8080/file/statics/2026/04/06/blob_20260406230206A002.png','PUBLIC','avatar','null','ADMIN',1,'ACTIVE',1,NULL,NULL,NULL,NULL,'1','2026-04-06 23:02:06',NULL,'2026-04-06 23:02:07'),(83,'uat-avatar_20260407235403A001.png','uat-avatar.png','png','image/png',2141,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\07\\uat-avatar_20260407235403A001.png','http://127.0.0.1:8080/file/statics/2026/04/07/uat-avatar_20260407235403A001.png','PUBLIC',NULL,NULL,'BLOG',86010,'TEMP',0,NULL,NULL,NULL,NULL,'86010','2026-04-07 23:54:03',NULL,'2026-04-07 23:54:03'),(84,'QQ图片20220304170905_20260408005913A001.jpg','QQ图片20220304170905.jpg','jpg','image/jpeg',22398,NULL,'local',NULL,'D:\\sourcelin\\uploadPath\\2026\\04\\08\\QQ图片20220304170905_20260408005913A001.jpg','http://127.0.0.1:8080/file/statics/2026/04/08/QQ图片20220304170905_20260408005913A001.jpg','PUBLIC',NULL,NULL,'BLOG',86010,'TEMP',0,NULL,NULL,NULL,NULL,'86010','2026-04-08 00:59:13',NULL,'2026-04-08 00:59:13');
/*!40000 ALTER TABLE `file_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--

LOCK TABLES `gen_table` WRITE;
/*!40000 ALTER TABLE `gen_table` DISABLE KEYS */;
INSERT INTO `gen_table` VALUES (1,'b_article','博客文章',NULL,NULL,'Article','crud','com.sourcelin.blog','blog','article','博客文章','sourcelin','0','/','{\"parentMenuId\":\"1063\"}','admin','2024-07-01 20:18:35','','2024-07-02 08:49:19',NULL),(3,'b_category','博客分类',NULL,NULL,'Category','crud','com.sourcelin.blog','blog','category','博客分类','sourcelin','0','/','{\"parentMenuId\":\"1063\"}','admin','2024-07-01 20:18:35','','2024-07-02 08:49:25',NULL),(4,'b_collect','收藏对象（文章收藏）',NULL,NULL,'Collect','crud','com.sourcelin.blog','blog','collect','文章收藏','sourcelin','0','/','{\"parentMenuId\":1062}','admin','2024-07-01 20:18:35','','2024-07-01 21:03:35',NULL),(5,'b_comment','评论管理',NULL,NULL,'Comment','crud','com.sourcelin.blog','blog','comment','评论管理','sourcelin','0','/','{}','admin','2024-07-01 20:18:35','','2024-07-02 08:49:34',NULL),(6,'b_follow','关注对象（用户关注）',NULL,NULL,'Follow','crud','com.sourcelin.blog','blog','follow','用户关注','sourcelin','0','/','{\"parentMenuId\":1062}','admin','2024-07-01 20:18:35','','2024-07-01 21:04:42',NULL),(7,'b_friend_link','友链',NULL,NULL,'FriendLink','crud','com.sourcelin.blog','blog','link','友链管理','sourcelin','0','/','{\"parentMenuId\":1063}','admin','2024-07-01 20:18:35','','2024-07-01 21:05:09',NULL),(8,'b_tag','博客标签对象',NULL,NULL,'Tag','crud','com.sourcelin.blog','blog','tag','博客标签','sourcelin','0','/','{\"parentMenuId\":\"1063\"}','admin','2024-07-01 20:18:35','','2024-07-02 08:48:36',NULL),(9,'b_user','用户信息表',NULL,NULL,'User','crud','com.sourcelin.blog','blog','user','用户信息','sourcelin','0','/','{\"parentMenuId\":1062}','admin','2024-07-01 20:18:35','','2024-07-02 08:48:59',NULL),(10,'b_web_navbar','门户导航栏',NULL,NULL,'WebNavbar','crud','com.sourcelin.blog','blog','navbar','门户导航栏','sourcelin','0','/','{\"parentMenuId\":\"1061\"}','admin','2024-07-01 20:18:35','','2024-07-02 08:49:11',NULL),(11,'b_web_config','网站配置表',NULL,NULL,'WebConfig','crud','com.sourcelin.blog','blog','config','网站配置','sourcelin','0','/','{\"parentMenuId\":1061}','admin','2024-07-01 20:21:17','','2024-07-01 20:27:15',NULL);
/*!40000 ALTER TABLE `gen_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=154 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--

LOCK TABLES `gen_table_column` WRITE;
/*!40000 ALTER TABLE `gen_table_column` DISABLE KEYS */;
INSERT INTO `gen_table_column` VALUES (1,1,'id','主键id','bigint(20)','Long','id','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(2,1,'user_id','用户id','bigint(20)','Long','userId','0','0',NULL,'1','1','1','1','EQ','input','',2,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(3,1,'category_id','分类id','bigint(20)','Long','categoryId','0','0',NULL,'1','1','1','1','EQ','input','',3,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(4,1,'title','标题','varchar(255)','String','title','0','0',NULL,'1','1','1','1','EQ','input','',4,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(5,1,'avatar','封面地址','varchar(255)','String','avatar','0','0',NULL,'1','1','1','1','EQ','input','',5,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(6,1,'summary','简介','text','String','summary','0','0',NULL,'1','1','1','1','EQ','textarea','',6,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(7,1,'content','内容','text','String','content','0','0',NULL,'1','1','1','1','EQ','editor','',7,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(8,1,'read_auth','阅读权限','int(11)','Long','readAuth','0','0',NULL,'1','1','1','1','EQ','input','',8,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(9,1,'status','文章状态','bigint(20)','Long','status','0','0',NULL,'1','1','1','1','EQ','radio','',9,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(10,1,'is_comment','是否启用评论','int(11)','Long','isComment','0','0',NULL,'1','1','1','1','EQ','input','',10,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(11,1,'is_recommend','是否推荐','int(11)','Long','isRecommend','0','0',NULL,'1','1','1','1','EQ','input','',11,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(12,1,'is_top','是否置顶','int(11)','Long','isTop','0','0',NULL,'1','1','1','1','EQ','input','',12,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(13,1,'is_original','是否原创','int(11)','Long','isOriginal','0','0',NULL,'1','1','1','1','EQ','input','',13,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(14,1,'original_url','转载地址','varchar(255)','String','originalUrl','0','0',NULL,'1','1','1','1','EQ','input','',14,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(15,1,'view_count','浏览量','bigint(20)','Long','viewCount','0','0',NULL,'1','1','1','1','EQ','input','',15,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(16,1,'like_count','点赞数','bigint(20)','Long','likeCount','0','0',NULL,'1','1','1','1','EQ','input','',16,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(17,1,'collect_count','收藏数','bigint(20)','Long','collectCount','0','0',NULL,'1','1','1','1','EQ','input','',17,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(18,1,'deleted','是否删除','int(11)','Long','deleted','0','0',NULL,'1','1','1','1','EQ','input','',18,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:19'),(21,3,'id','博客分类ID','bigint(20)','Long','id','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:25'),(22,3,'name','名称','varchar(255)','String','name','0','0','1','1','1','1','1','LIKE','input','',2,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:25'),(23,3,'summary','分类简介','text','String','summary','0','0',NULL,'1','1','1','1','EQ','textarea','',3,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:25'),(24,3,'icon','图标','varchar(255)','String','icon','0','0',NULL,'1','1','1','1','EQ','input','',4,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:25'),(25,3,'order_num','排序','int(11)','Long','orderNum','0','0',NULL,'1','1','1','1','EQ','input','',5,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:25'),(26,3,'click_count','点击量','bigint(20)','Long','clickCount','0','0',NULL,'1','1','1','1','EQ','input','',6,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:25'),(27,3,'create_time','创建时间','datetime','Date','createTime','0','0',NULL,'1',NULL,NULL,NULL,'EQ','datetime','',7,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:25'),(28,3,'update_by','更新者','varchar(64)','String','updateBy','0','0',NULL,'1','1',NULL,NULL,'EQ','input','',8,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:25'),(29,3,'update_time','更新时间','datetime','Date','updateTime','0','0',NULL,'1','1',NULL,NULL,'EQ','datetime','',9,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:25'),(30,3,'remark','备注','varchar(500)','String','remark','0','0',NULL,'1','1','1',NULL,'EQ','textarea','',10,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:25'),(31,4,'id','主键id','bigint(20)','Long','id','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2024-07-01 20:18:35','','2024-07-01 21:03:35'),(32,4,'user_id','用户id','bigint(20)','Long','userId','0','0','1','1','1','1','1','EQ','input','',2,'admin','2024-07-01 20:18:35','','2024-07-01 21:03:35'),(33,4,'article_id','文章id','bigint(20)','Long','articleId','0','0','1','1','1','1','1','EQ','input','',3,'admin','2024-07-01 20:18:35','','2024-07-01 21:03:35'),(34,4,'create_time','创建时间','datetime','Date','createTime','0','0','1','1',NULL,NULL,NULL,'EQ','datetime','',4,'admin','2024-07-01 20:18:35','','2024-07-01 21:03:35'),(35,4,'update_time','更新时间','datetime','Date','updateTime','0','0',NULL,'1','1',NULL,NULL,'EQ','datetime','',5,'admin','2024-07-01 20:18:35','','2024-07-01 21:03:35'),(36,4,'create_by','创建者','varchar(64)','String','createBy','0','0',NULL,'1',NULL,NULL,NULL,'EQ','input','',6,'admin','2024-07-01 20:18:35','','2024-07-01 21:03:35'),(37,4,'update_by','更新者','varchar(64)','String','updateBy','0','0',NULL,'1','1',NULL,NULL,'EQ','input','',7,'admin','2024-07-01 20:18:35','','2024-07-01 21:03:35'),(38,4,'remark','备注','varchar(500)','String','remark','0','0',NULL,'1','1','1',NULL,'EQ','textarea','',8,'admin','2024-07-01 20:18:35','','2024-07-01 21:03:35'),(39,5,'id','主键id','bigint(20)','Long','id','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(40,5,'user_id','用户id','bigint(20)','Long','userId','0','0','1','1','1','1','1','EQ','input','',2,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(41,5,'parent_user_id','父发表用户名ID','bigint(20)','Long','parentUserId','0','0',NULL,'1','1','1','1','EQ','input','',3,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(42,5,'article_id','文章id','bigint(20)','Long','articleId','0','0','1','1','1','1','1','EQ','input','',4,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(43,5,'parent_comment_id','父评论id','bigint(20)','Long','parentCommentId','0','0',NULL,'1','1','1','1','EQ','input','',5,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(44,5,'content','评论内容','text','String','content','0','0',NULL,'1','1','1','1','EQ','editor','',6,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(45,5,'comment_info','评论额外信息','varchar(500)','String','commentInfo','0','0',NULL,'1','1','1','1','EQ','textarea','',7,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(46,5,'floor_comment_id','楼层评论ID','bigint(20)','Long','floorCommentId','0','0',NULL,'1','1','1','1','EQ','input','',8,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(47,5,'like_count','点赞数','bigint(20)','Long','likeCount','0','0',NULL,'1','1','1','1','EQ','input','',9,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(48,5,'source','评论来源（博客文章、关于本站、留言板、友链）','varchar(90)','String','source','0','0',NULL,'1','1','1','1','EQ','input','',10,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(49,5,'browser','浏览器名','varchar(50)','String','browser','0','0',NULL,'1','1','1','1','EQ','input','',11,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(50,5,'browser_version','浏览器版本','varchar(50)','String','browserVersion','0','0',NULL,'1','1','1','1','EQ','input','',12,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(51,5,'system','系统名','varchar(50)','String','system','0','0',NULL,'1','1','1','1','EQ','input','',13,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(52,5,'system_version','系统名版本','varchar(50)','String','systemVersion','0','0',NULL,'1','1','1','1','EQ','input','',14,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(53,5,'ip_address','ip地址','varchar(70)','String','ipAddress','0','0',NULL,'1','1','1','1','EQ','input','',15,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(54,5,'ip_source','ip来源','varchar(200)','String','ipSource','0','0',NULL,'1','1','1','1','EQ','input','',16,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(55,5,'deleted','是否删除（0未删除 1已删除）','int(1)','Integer','deleted','0','0',NULL,'1','1','1','1','EQ','input','',17,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(56,5,'create_time','创建时间','datetime','Date','createTime','0','0','1','1',NULL,NULL,NULL,'EQ','datetime','',18,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(57,5,'update_time','更新时间','datetime','Date','updateTime','0','0',NULL,'1','1',NULL,NULL,'EQ','datetime','',19,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:34'),(58,6,'id','主键id','bigint(20)','Long','id','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2024-07-01 20:18:35','','2024-07-01 21:04:42'),(59,6,'user_id','用户id','bigint(20)','Long','userId','0','0','1','1','1','1','1','EQ','input','',2,'admin','2024-07-01 20:18:35','','2024-07-01 21:04:42'),(60,6,'follow_user_id','关注的用户id','bigint(20)','Long','followUserId','0','0','1','1','1','1','1','EQ','input','',3,'admin','2024-07-01 20:18:35','','2024-07-01 21:04:42'),(61,6,'create_time','创建时间','datetime','Date','createTime','0','0','1','1',NULL,NULL,NULL,'EQ','datetime','',4,'admin','2024-07-01 20:18:35','','2024-07-01 21:04:42'),(62,7,'id','主键id','bigint(20)','Long','id','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(63,7,'name','名称','varchar(50)','String','name','0','0','1','1','1','1','1','LIKE','input','',2,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(64,7,'avatar','头像','varchar(64)','String','avatar','0','0',NULL,'1','1','1','1','EQ','input','',3,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(65,7,'url','链接','varchar(90)','String','url','0','0','1','1','1','1','1','EQ','input','',4,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(66,7,'description','描述','varchar(255)','String','description','0','0',NULL,'1','1','1','1','EQ','input','',5,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(67,7,'cover','封面','varchar(64)','String','cover','0','0',NULL,'1','1','1','1','EQ','input','',6,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(68,7,'email','邮箱','varchar(75)','String','email','0','0',NULL,'1','1','1','1','EQ','input','',7,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(69,7,'order_num','排序','int(11)','Long','orderNum','0','0',NULL,'1','1','1','1','EQ','input','',8,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(70,7,'label','标签','varchar(50)','String','label','0','0',NULL,'1','1','1','1','EQ','input','',9,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(71,7,'category','分类(1推荐 2精选 3更多)','tinyint(4)','Integer','category','0','0','1','1','1','1','1','EQ','input','',10,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(72,7,'status','状态(0申请 1上架 2下架)','tinyint(4)','Integer','status','0','0','1','1','1','1','1','EQ','radio','',11,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(73,7,'reason','下架原因','varchar(255)','String','reason','0','0',NULL,'1','1','1','1','EQ','input','',12,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(74,7,'create_time','创建时间','datetime','Date','createTime','0','0','1','1',NULL,NULL,NULL,'EQ','datetime','',13,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(75,7,'update_time','更新时间','datetime','Date','updateTime','0','0','1','1','1',NULL,NULL,'EQ','datetime','',14,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(76,7,'remark','备注','varchar(255)','String','remark','0','0',NULL,'1','1','1',NULL,'EQ','input','',15,'admin','2024-07-01 20:18:35','','2024-07-01 21:05:09'),(77,8,'id','标签ID','bigint(20)','Long','id','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:36'),(78,8,'name','名称','varchar(255)','String','name','0','0','1','1','1','1','1','LIKE','input','',2,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:36'),(79,8,'summary','简介','text','String','summary','0','0',NULL,'1','1','1','1','EQ','textarea','',3,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:36'),(80,8,'icon','图标','varchar(255)','String','icon','0','0',NULL,'1','1','1','1','EQ','input','',4,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:36'),(81,8,'order_num','排序','int(11)','Long','orderNum','0','0',NULL,'1','1','1','1','EQ','input','',5,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:36'),(82,8,'click_count','点击量','bigint(20)','Long','clickCount','0','0',NULL,'1','1','1','1','EQ','input','',6,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:36'),(83,8,'create_time','创建时间','datetime','Date','createTime','0','0',NULL,'1',NULL,NULL,NULL,'EQ','datetime','',7,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:36'),(84,8,'update_by','更新者','varchar(64)','String','updateBy','0','0',NULL,'1','1',NULL,NULL,'EQ','input','',8,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:36'),(85,8,'update_time','更新时间','datetime','Date','updateTime','0','0',NULL,'1','1',NULL,NULL,'EQ','datetime','',9,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:36'),(86,8,'remark','备注','varchar(500)','String','remark','0','0',NULL,'1','1','1',NULL,'EQ','textarea','',10,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:36'),(87,9,'id','用户id','bigint(20)','Long','id','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(88,9,'username','用户账号','varchar(75)','String','username','0','0','1','1','1','1','1','LIKE','input','',2,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(89,9,'nickname','用户昵称','varchar(100)','String','nickname','0','0',NULL,'1','1','1','1','LIKE','input','',3,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(90,9,'password','用户密码','varchar(100)','String','password','0','0',NULL,'1','1','1','1','EQ','input','',4,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(91,9,'user_type','用户类型（1普通用户，2博主）','tinyint(4)','Integer','userType','0','0','1','1','1','1','1','EQ','select','',5,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(92,9,'email','用户邮箱','varchar(255)','String','email','0','0',NULL,'1','1','1','1','EQ','input','',6,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(93,9,'phone','用户绑定手机号','varchar(20)','String','phone','0','0',NULL,'1','1','1','1','EQ','input','',7,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(94,9,'avatar','头像','varchar(255)','String','avatar','0','0',NULL,'1','1','1','1','EQ','input','',8,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(95,9,'sex','用户性别（0男，1女，2未知）','tinyint(4)','Integer','sex','0','0',NULL,'1','1','1','1','EQ','select','',9,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(96,9,'birthday','出生年月','date','Date','birthday','0','0',NULL,'1','1','1','1','EQ','datetime','',10,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(97,9,'introduction','简介(最多1200字)','varchar(1200)','String','introduction','0','0',NULL,'1','1','1','1','EQ','textarea','',11,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(98,9,'user_status','状态：0：启用，1：停用','tinyint(4)','Integer','userStatus','0','0','1','1','1','1','1','EQ','radio','',12,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(99,9,'praise','赞赏数（点赞数）','bigint(20)','Long','praise','0','0',NULL,'1','1','1','1','EQ','input','',13,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(100,9,'follow','订阅数（关注数）','bigint(20)','Long','follow','0','0',NULL,'1','1','1','1','EQ','input','',14,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(101,9,'login_ip','最后登录IP','varchar(50)','String','loginIp','0','0',NULL,'1','1','1','1','EQ','input','',15,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(102,9,'login_date','最后登录时间','datetime','Date','loginDate','0','0',NULL,'1','1','1','1','EQ','datetime','',16,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(103,9,'del_flag','删除标志（0代表存在，2代表删除）','char(1)','String','delFlag','0','0','1','1',NULL,NULL,NULL,'EQ','input','',17,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(104,9,'uuid','设备唯一标识(平台uuid)','varchar(36)','String','uuid','0','0',NULL,'1','1','1','1','EQ','input','',18,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(105,9,'bind_qq_id','QQ绑定的id','varchar(50)','String','bindQqId','0','0',NULL,'1','1','1','1','EQ','input','',19,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(106,9,'bind_wechat_id','绑定微信id','varchar(50)','String','bindWechatId','0','0',NULL,'1','1','1','1','EQ','input','',20,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(107,9,'bind_sina_id','绑定微博id','varchar(50)','String','bindSinaId','0','0',NULL,'1','1','1','1','EQ','input','',21,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(108,9,'bind_alipay_id','绑定支付宝id','varchar(50)','String','bindAlipayId','0','0',NULL,'1','1','1','1','EQ','input','',22,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(109,9,'realname_auth','是否实名认证（1未验证，2已验证）','tinyint(4)','Integer','realnameAuth','0','0','1','1','1','1','1','EQ','input','',23,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(110,9,'open_id','微信登录openid','varchar(100)','String','openId','0','0',NULL,'1','1','1','1','EQ','input','',24,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(111,9,'session_key','微信登录会话KEY','varchar(100)','String','sessionKey','0','0',NULL,'1','1','1','1','EQ','input','',25,'admin','2024-07-01 20:18:35','','2024-07-02 08:48:59'),(112,10,'id','导航栏ID','bigint(20)','Long','id','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(113,10,'name','导航栏名称','varchar(50)','String','name','0','0','1','1','1','1','1','LIKE','input','',2,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(114,10,'summary','导航栏简介','varchar(255)','String','summary','0','0',NULL,'1','1','1','1','EQ','input','',3,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(115,10,'icon','导航栏图标','varchar(45)','String','icon','0','0',NULL,'1','1','1','1','EQ','input','',4,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(116,10,'path','路由地址','varchar(80)','String','path','0','0',NULL,'1','1','1','1','EQ','input','',5,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(117,10,'component','组件路径','varchar(90)','String','component','0','0',NULL,'1','1','1','1','EQ','input','',6,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(118,10,'is_frame','是否为外链（0是 1否）','varchar(2)','String','isFrame','0','0','1','1','1','1','1','EQ','input','',7,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(119,10,'type','导航栏类型（M目录 C菜单）','char(1)','String','type','0','0','1','1','1','1','1','EQ','select','',8,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(120,10,'visible','导航栏状态（0显示 1隐藏）','varchar(2)','String','visible','0','0','1','1','1','1','1','EQ','input','',9,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(121,10,'parent_id','父菜单ID','bigint(20)','Long','parentId','0','0',NULL,'1','1','1','1','EQ','input','',10,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(122,10,'order_num','显示顺序','int(11)','Long','orderNum','0','0','1','1','1','1','1','EQ','input','',11,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(123,10,'ancestors','祖级列表','varchar(200)','String','ancestors','0','0',NULL,'1','1','1','1','EQ','input','',12,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(124,10,'create_by','创建者','varchar(64)','String','createBy','0','0',NULL,'1',NULL,NULL,NULL,'EQ','input','',13,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(125,10,'create_time','创建时间','datetime','Date','createTime','0','0',NULL,'1',NULL,NULL,NULL,'EQ','datetime','',14,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(126,10,'update_by','更新者','varchar(64)','String','updateBy','0','0',NULL,'1','1',NULL,NULL,'EQ','input','',15,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(127,10,'update_time','更新时间','datetime','Date','updateTime','0','0',NULL,'1','1',NULL,NULL,'EQ','datetime','',16,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(128,10,'remark','备注','varchar(500)','String','remark','0','0',NULL,'1','1','1',NULL,'EQ','textarea','',17,'admin','2024-07-01 20:18:35','','2024-07-02 08:49:11'),(129,11,'id','主键','bigint(20)','Long','id','1','1',NULL,'1',NULL,NULL,NULL,'EQ','input','',1,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(130,11,'logo','网站logo','varchar(255)','String','logo','0','0',NULL,'1','1','1','1','EQ','input','',2,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(131,11,'tourist_avatar','游客头像','varchar(255)','String','touristAvatar','0','0',NULL,'1','1','1','1','EQ','input','',3,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(132,11,'name','网站名称','varchar(100)','String','name','0','0','1','1','1','1','1','LIKE','input','',4,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(133,11,'summary','简介','text','String','summary','0','0',NULL,'1','1','1','1','EQ','textarea','',5,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(134,11,'keyword','关键字','varchar(255)','String','keyword','0','0',NULL,'1','1','1','1','EQ','input','',6,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(135,11,'record_num','备案号','varchar(100)','String','recordNum','0','0',NULL,'1','1','1','1','EQ','input','',7,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(136,11,'web_url','网站地址','varchar(255)','String','webUrl','0','0',NULL,'1','1','1','1','EQ','input','',8,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(137,11,'notice','公告','text','String','notice','0','0',NULL,'1','1','1','1','EQ','textarea','',9,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(138,11,'ali_pay','支付宝收款码FileId','varchar(90)','String','aliPay','0','0',NULL,'1','1','1','1','EQ','input','',10,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(139,11,'weixin_pay','微信收款码FileId','varchar(90)','String','weixinPay','0','0',NULL,'1','1','1','1','EQ','input','',11,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(140,11,'github','github地址','varchar(255)','String','github','0','0',NULL,'1','1','1','1','EQ','input','',12,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(141,11,'gitee','gitee地址','varchar(255)','String','gitee','0','0',NULL,'1','1','1','1','EQ','input','',13,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(142,11,'qq_number','QQ号','varchar(20)','String','qqNumber','0','0',NULL,'1','1','1','1','EQ','input','',14,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(143,11,'qq_group','QQ群','varchar(20)','String','qqGroup','0','0',NULL,'1','1','1','1','EQ','input','',15,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(144,11,'email','邮箱','varchar(255)','String','email','0','0',NULL,'1','1','1','1','EQ','input','',16,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(145,11,'wechat','微信','varchar(50)','String','wechat','0','0',NULL,'1','1','1','1','EQ','input','',17,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(146,11,'show_list','显示的列表','varchar(255)','String','showList','0','0',NULL,'1','1','1','1','EQ','input','',18,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(147,11,'login_type_list','登录方式列表','varchar(255)','String','loginTypeList','0','0',NULL,'1','1','1','1','EQ','input','',19,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(148,11,'open_comment','是否开启评论','tinyint(1)','Integer','openComment','0','0','1','1','1','1','1','EQ','input','',20,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(149,11,'open_admiration','是否开启赞赏','tinyint(1)','Integer','openAdmiration','0','0','1','1','1','1','1','EQ','input','',21,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(150,11,'author','作者','varchar(100)','String','author','0','0',NULL,'1','1','1','1','EQ','input','',22,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(151,11,'author_info','作者简介','text','String','authorInfo','0','0',NULL,'1','1','1','1','EQ','textarea','',23,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(152,11,'author_avatar','作者头像','varchar(255)','String','authorAvatar','0','0',NULL,'1','1','1','1','EQ','input','',24,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15'),(153,11,'about_me','关于我','text','String','aboutMe','0','0',NULL,'1','1','1','1','EQ','textarea','',25,'admin','2024-07-01 20:21:17','','2024-07-01 20:27:15');
/*!40000 ALTER TABLE `gen_table_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_capacity`
--

DROP TABLE IF EXISTS `group_capacity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_group_id` (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='集群、各Group容量信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_capacity`
--

LOCK TABLES `group_capacity` WRITE;
/*!40000 ALTER TABLE `group_capacity` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_capacity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `his_config_info`
--

DROP TABLE IF EXISTS `his_config_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `his_config_info` (
  `id` bigint(64) unsigned NOT NULL,
  `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) COLLATE utf8_bin NOT NULL,
  `group_id` varchar(128) COLLATE utf8_bin NOT NULL,
  `app_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT 'app_name',
  `content` longtext COLLATE utf8_bin NOT NULL,
  `md5` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text COLLATE utf8_bin,
  `src_ip` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `op_type` char(10) COLLATE utf8_bin DEFAULT NULL,
  `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT '租户字段',
  `encrypted_data_key` text COLLATE utf8_bin COMMENT '秘钥',
  PRIMARY KEY (`nid`) USING BTREE,
  KEY `idx_gmt_create` (`gmt_create`) USING BTREE,
  KEY `idx_gmt_modified` (`gmt_modified`) USING BTREE,
  KEY `idx_did` (`data_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='多租户改造';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `his_config_info`
--

LOCK TABLES `his_config_info` WRITE;
/*!40000 ALTER TABLE `his_config_info` DISABLE KEYS */;
INSERT INTO `his_config_info` VALUES (5,1,'sourcelin-system-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://localhost:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: password\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.system\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip','48e0ed4a040c402bdc2444213a82c910','2023-10-17 15:23:28','2023-10-17 07:23:28','nacos','0:0:0:0:0:0:0:1','U','',NULL),(6,2,'sourcelin-gen-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://localhost:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: password\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.gen.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n\n# 代码生成\ngen:\n  # 作者\n  author: sourcelin\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.sourcelin.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n','eb592420b3fceae1402881887b8a6a0d','2023-10-17 15:23:57','2023-10-17 07:23:58','nacos','0:0:0:0:0:0:0:1','U','',NULL),(7,3,'sourcelin-job-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://localhost:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: password\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.job.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n','edcf0e3fe13fea07b4ec08b1088f30b3','2023-10-17 15:24:18','2023-10-17 07:24:18','nacos','0:0:0:0:0:0:0:1','U','',NULL),(5,4,'sourcelin-system-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://121.4.31.191:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 1994121\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.system\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip','fd634296d28475e91d07b1c6462c5da5','2023-10-17 16:09:45','2023-10-17 08:09:46','nacos','0:0:0:0:0:0:0:1','U','',NULL),(5,5,'sourcelin-system-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://121.4.31.191:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 1994121\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.system\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip','fd634296d28475e91d07b1c6462c5da5','2023-10-23 11:14:07','2023-10-23 03:14:09','nacos','0:0:0:0:0:0:0:1','U','',NULL),(6,6,'sourcelin-gen-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://121.4.31.191:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 1994121\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.gen.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n\n# 代码生成\ngen:\n  # 作者\n  author: sourcelin\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.sourcelin.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n','a4ac421efc1f1cfc129129a67b0049e7','2023-10-23 11:15:01','2023-10-23 03:15:04','nacos','0:0:0:0:0:0:0:1','U','',NULL),(7,7,'sourcelin-job-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://121.4.31.191:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 1994121\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.job.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n','fdc8303633421cbc0bca6d1f888c9e94','2023-10-23 11:15:17','2023-10-23 03:15:19','nacos','0:0:0:0:0:0:0:1','U','',NULL),(5,8,'sourcelin-system-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.system\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip','eacd9b3dd9d7949e6509d4795feb54bf','2023-10-23 11:53:34','2023-10-23 03:53:36','nacos','0:0:0:0:0:0:0:1','U','',NULL),(5,9,'sourcelin-system-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: root\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.system\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip','eacd9b3dd9d7949e6509d4795feb54bf','2023-10-23 14:25:33','2023-10-23 06:25:34','nacos','0:0:0:0:0:0:0:1','U','',NULL),(6,10,'sourcelin-gen-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.gen.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n\n# 代码生成\ngen:\n  # 作者\n  author: sourcelin\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.sourcelin.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n','20ba9c200b071b8666fed99847d2778a','2023-10-23 14:25:48','2023-10-23 06:25:50','nacos','0:0:0:0:0:0:0:1','U','',NULL),(7,11,'sourcelin-job-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: root\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.job.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n','8d149e27db88f8d35601debbd7458619','2023-10-23 14:26:02','2023-10-23 06:26:03','nacos','0:0:0:0:0:0:0:1','U','',NULL),(0,12,'sourcelin-gateway-dev.yml','DEFAULT_GROUP','','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: sourcelin-auth\n          uri: lb://sourcelin-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestFilter\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: sourcelin-gen\n          uri: lb://sourcelin-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 定时任务\n        - id: sourcelin-job\n          uri: lb://sourcelin-job\n          predicates:\n            - Path=/schedule/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: sourcelin-system\n          uri: lb://sourcelin-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: sourcelin-file\n          uri: lb://sourcelin-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n','57cec5abd0e0a6b77d853750344a9dc0','2023-10-23 17:07:49','2023-10-23 09:07:51',NULL,'0:0:0:0:0:0:0:1','I','',NULL),(21,13,'sourcelin-gateway-dev.yml','DEFAULT_GROUP','','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  cloud:\n    gateway:\n      discovery:\n        locator:\n          lowerCaseServiceId: true\n          enabled: true\n      routes:\n        # 认证中心\n        - id: sourcelin-auth\n          uri: lb://sourcelin-auth\n          predicates:\n            - Path=/auth/**\n          filters:\n            # 验证码处理\n            - CacheRequestFilter\n            - ValidateCodeFilter\n            - StripPrefix=1\n        # 代码生成\n        - id: sourcelin-gen\n          uri: lb://sourcelin-gen\n          predicates:\n            - Path=/code/**\n          filters:\n            - StripPrefix=1\n        # 定时任务\n        - id: sourcelin-job\n          uri: lb://sourcelin-job\n          predicates:\n            - Path=/schedule/**\n          filters:\n            - StripPrefix=1\n        # 系统模块\n        - id: sourcelin-system\n          uri: lb://sourcelin-system\n          predicates:\n            - Path=/system/**\n          filters:\n            - StripPrefix=1\n        # 文件服务\n        - id: sourcelin-file\n          uri: lb://sourcelin-file\n          predicates:\n            - Path=/file/**\n          filters:\n            - StripPrefix=1\n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n','57cec5abd0e0a6b77d853750344a9dc0','2023-10-23 17:08:28','2023-10-23 09:08:29','nacos','0:0:0:0:0:0:0:1','U','',NULL),(0,14,'sourcelin-auth-dev.yml','DEFAULT_GROUP','','spring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n','8bd9dada9a94822feeab40de55efced6','2023-10-23 17:09:20','2023-10-23 09:09:22',NULL,'0:0:0:0:0:0:0:1','I','',NULL),(0,15,'sourcelin-monitor-dev.yml','DEFAULT_GROUP','','# spring\nspring:\n  security:\n    user:\n      name: sourcelin\n      password: 123456\n  boot:\n    admin:\n      ui:\n        title: sourcelin服务状态监控\n','6f122fd2bfb8d45f858e7d6529a9cd44','2023-10-23 17:09:21','2023-10-23 09:09:22',NULL,'0:0:0:0:0:0:0:1','I','',NULL),(0,16,'sourcelin-system-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.system\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip','4e4eb1af342cbd58c229e2fd62986137','2023-10-23 17:09:22','2023-10-23 09:09:22',NULL,'0:0:0:0:0:0:0:1','I','',NULL),(0,17,'sourcelin-gen-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.gen.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n\n# 代码生成\ngen:\n  # 作者\n  author: sourcelin\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.sourcelin.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n','4872335e95ffb20796734a9bf55685fd','2023-10-23 17:09:22','2023-10-23 09:09:22',NULL,'0:0:0:0:0:0:0:1','I','',NULL),(0,18,'sourcelin-job-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.job.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n','569b2b00429b1154c3dd52efbd76f4d7','2023-10-23 17:09:22','2023-10-23 09:09:22',NULL,'0:0:0:0:0:0:0:1','I','',NULL),(0,19,'sourcelin-file-dev.yml','DEFAULT_GROUP','','# 本地文件上传    \r\nfile:\r\n    domain: http://127.0.0.1:9300\r\n    path: D:/sourcelin/uploadPath\r\n    prefix: /statics\r\n\r\n# FastDFS配置\r\nfdfs:\r\n  domain: http://8.129.231.12\r\n  soTimeout: 3000\r\n  connectTimeout: 2000\r\n  trackerList: 8.129.231.12:22122\r\n\r\n# Minio配置\r\nminio:\r\n  url: http://8.129.231.12:9000\r\n  accessKey: minioadmin\r\n  secretKey: minioadmin\r\n  bucketName: test','5382b93f3d8059d6068c0501fdd41195','2023-10-23 17:09:23','2023-10-23 09:09:22',NULL,'0:0:0:0:0:0:0:1','I','',NULL),(0,20,'sentinel-sourcelin-gateway','DEFAULT_GROUP','','[\r\n    {\r\n        \"resource\": \"sourcelin-auth\",\r\n        \"count\": 500,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"sourcelin-system\",\r\n        \"count\": 1000,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"sourcelin-gen\",\r\n        \"count\": 200,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"sourcelin-job\",\r\n        \"count\": 300,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    }\r\n]','9f3a3069261598f74220bc47958ec252','2023-10-23 17:09:24','2023-10-23 09:09:22',NULL,'0:0:0:0:0:0:0:1','I','',NULL),(25,21,'sourcelin-system-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.system\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip','4e4eb1af342cbd58c229e2fd62986137','2023-10-23 17:10:26','2023-10-23 09:10:27','nacos','0:0:0:0:0:0:0:1','U','',NULL),(26,22,'sourcelin-gen-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.gen.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n\n# 代码生成\ngen:\n  # 作者\n  author: sourcelin\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.sourcelin.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n','4872335e95ffb20796734a9bf55685fd','2023-10-23 17:11:08','2023-10-23 09:11:09','nacos','0:0:0:0:0:0:0:1','U','',NULL),(27,23,'sourcelin-job-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.job.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n','569b2b00429b1154c3dd52efbd76f4d7','2023-10-23 17:11:29','2023-10-23 09:11:30','nacos','0:0:0:0:0:0:0:1','U','',NULL),(29,24,'sentinel-sourcelin-gateway','DEFAULT_GROUP','','[\r\n    {\r\n        \"resource\": \"sourcelin-auth\",\r\n        \"count\": 500,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"sourcelin-system\",\r\n        \"count\": 1000,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"sourcelin-gen\",\r\n        \"count\": 200,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    },\r\n	{\r\n        \"resource\": \"sourcelin-job\",\r\n        \"count\": 300,\r\n        \"grade\": 1,\r\n        \"limitApp\": \"default\",\r\n        \"strategy\": 0,\r\n        \"controlBehavior\": 0\r\n    }\r\n]','9f3a3069261598f74220bc47958ec252','2023-10-23 17:12:02','2023-10-23 09:12:03','nacos','0:0:0:0:0:0:0:1','U','',NULL),(26,25,'sourcelin-gen-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.gen.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n\n# 代码生成\ngen:\n  # 作者\n  author: sourcelin\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.sourcelin.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n','8c48f21a954b29992f15b68e2b75ffd7','2023-10-24 16:11:30','2023-10-24 08:11:32','nacos','0:0:0:0:0:0:0:1','U','',NULL),(27,26,'sourcelin-job-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.job.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n','1e164cf7b7d390d8054c7ceeaf3d5218','2023-10-24 16:12:16','2023-10-24 08:12:18','nacos','0:0:0:0:0:0:0:1','U','',NULL),(28,27,'sourcelin-file-dev.yml','DEFAULT_GROUP','','# 本地文件上传    \r\nfile:\r\n    domain: http://127.0.0.1:9300\r\n    path: D:/sourcelin/uploadPath\r\n    prefix: /statics\r\n\r\n# FastDFS配置\r\nfdfs:\r\n  domain: http://8.129.231.12\r\n  soTimeout: 3000\r\n  connectTimeout: 2000\r\n  trackerList: 8.129.231.12:22122\r\n\r\n# Minio配置\r\nminio:\r\n  url: http://8.129.231.12:9000\r\n  accessKey: minioadmin\r\n  secretKey: minioadmin\r\n  bucketName: test','5382b93f3d8059d6068c0501fdd41195','2023-10-24 16:13:55','2023-10-24 08:13:58','nacos','0:0:0:0:0:0:0:1','U','',NULL),(24,28,'sourcelin-monitor-dev.yml','DEFAULT_GROUP','','# spring\nspring:\n  security:\n    user:\n      name: sourcelin\n      password: 123456\n  boot:\n    admin:\n      ui:\n        title: sourcelin服务状态监控\n','6f122fd2bfb8d45f858e7d6529a9cd44','2023-10-24 18:38:20','2023-10-24 10:38:19','nacos','0:0:0:0:0:0:0:1','U','',NULL),(25,29,'sourcelin-system-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: 123456\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n            username: root\n            password: 123456\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.system\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip','1f99db1ba575568306db51957a05fec2','2023-10-24 18:38:54','2023-10-24 10:38:55','nacos','0:0:0:0:0:0:0:1','U','',NULL),(26,30,'sourcelin-gen-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.gen.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 代码生成接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n\n# 代码生成\ngen:\n  # 作者\n  author: sourcelin\n  # 默认生成包路径 system 需改成自己的模块名称 如 system monitor tool\n  packageName: com.sourcelin.system\n  # 自动去除表前缀，默认是false\n  autoRemovePre: false\n  # 表前缀（生成类名不会包含表前缀，多个用逗号分隔）\n  tablePrefix: sys_\n','8c48f21a954b29992f15b68e2b75ffd7','2023-10-24 18:39:14','2023-10-24 10:39:15','nacos','0:0:0:0:0:0:0:1','U','',NULL),(27,31,'sourcelin-job-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/ry-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.job.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n','1e164cf7b7d390d8054c7ceeaf3d5218','2023-10-24 18:39:27','2023-10-24 10:39:27','nacos','0:0:0:0:0:0:0:1','U','',NULL),(27,32,'sourcelin-job-dev.yml','DEFAULT_GROUP','','# spring配置\nspring:\n  redis:\n    host: localhost\n    port: 6379\n    password: \n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: jdbc:mysql://127.0.0.1:3306/sourcelin-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8\n    username: root\n    password: 123456\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.job.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n','fde8acd1c3f602255bc920225dac5bcd','2023-10-24 18:39:34','2023-10-24 10:39:34','nacos','0:0:0:0:0:0:0:1','U','',NULL),(28,33,'sourcelin-file-dev.yml','DEFAULT_GROUP','','# 本地文件上传    \nfile:\n    domain: http://127.0.0.1:9300\n    path: D:/sourcelin/uploadPath\n    prefix: /statics\n\n# FastDFS配置\nfdfs:\n  domain: http://8.129.231.12\n  soTimeout: 3000\n  connectTimeout: 2000\n  trackerList: 8.129.231.12:22122\n\n# Minio配置\nminio:\n  url: http://8.129.231.12:9000\n  accessKey: minioadmin\n  secretKey: minioadmin\n  bucketName: test','20b4b16d487cd6386d3426dbc558b435','2023-10-24 18:39:53','2023-10-24 10:39:54','nacos','0:0:0:0:0:0:0:1','U','',NULL);
/*!40000 ALTER TABLE `his_config_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `permissions` (
  `role` varchar(50) NOT NULL,
  `resource` varchar(255) NOT NULL,
  `action` varchar(8) NOT NULL,
  UNIQUE KEY `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='Blob类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_blob_triggers`
--

LOCK TABLES `qrtz_blob_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_blob_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_blob_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='日历信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_calendars`
--

LOCK TABLES `qrtz_calendars` WRITE;
/*!40000 ALTER TABLE `qrtz_calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='Cron类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_cron_triggers`
--

LOCK TABLES `qrtz_cron_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_cron_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_cron_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_fired_triggers`
--

LOCK TABLES `qrtz_fired_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_fired_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_fired_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_job_details`
--

LOCK TABLES `qrtz_job_details` WRITE;
/*!40000 ALTER TABLE `qrtz_job_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='存储的悲观锁信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_locks`
--

LOCK TABLES `qrtz_locks` WRITE;
/*!40000 ALTER TABLE `qrtz_locks` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='暂停的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--

LOCK TABLES `qrtz_paused_trigger_grps` WRITE;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint(13) NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint(13) NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='调度器状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_scheduler_state`
--

LOCK TABLES `qrtz_scheduler_state` WRITE;
/*!40000 ALTER TABLE `qrtz_scheduler_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_scheduler_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simple_triggers`
--

LOCK TABLES `qrtz_simple_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simple_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simple_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simprop_triggers`
--

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simprop_triggers`
--

LOCK TABLES `qrtz_simprop_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_triggers`
--

LOCK TABLES `qrtz_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `username` varchar(50) NOT NULL,
  `role` varchar(50) NOT NULL,
  UNIQUE KEY `idx_user_role` (`username`,`role`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES ('nacos','ROLE_ADMIN');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2023-10-17 14:29:37','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2023-10-17 14:29:37','',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2023-10-17 14:29:37','',NULL,'深色主题theme-dark，浅色主题theme-light'),(4,'账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y','admin','2023-10-17 14:29:37','',NULL,'是否开启注册用户功能（true开启，false关闭）'),(5,'用户登录-黑名单列表','sys.login.blackIPList','','Y','admin','2023-10-17 14:29:37','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),(6,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1','Y','admin','2026-03-30 13:35:46','',NULL,'0：关闭；1：若从未更新过密码(pwd_update_date 为空)则登录后提示修改'),(7,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0','Y','admin','2026-03-30 13:35:47','',NULL,'0：不限制；填写大于0小于365的正整数为有效天数，超期登录后提示修改'),(8,'管理后台UI默认配置','sys.admin.ui.defaults','{"themePreference":"system","layout":"left","sidebarAppearance":"classic","themeColor":"#4B55FA","showTagsView":true,"showAppLogo":true,"pageSwitchingAnimation":"fade-slide","componentSize":"default"}','Y','admin','2026-04-17 00:00:00','',NULL,'管理后台默认 UI 配置'),(9,'管理后台UI策略','sys.admin.ui.policy','{"allowCustomThemeColor":true,"allowUserLayoutSwitch":true,"allowUserSidebarAppearanceSwitch":true}','Y','admin','2026-04-17 00:00:00','',NULL,'管理后台 UI 策略'),(10,'管理后台主题色预设','sys.admin.ui.presets','["#4B55FA","#0F766E","#C2410C","#1D4ED8","#BE185D"]','Y','admin','2026-04-17 00:00:00','',NULL,'管理后台主题色预设');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (100,0,'0','sourcelin科技',0,'sourcelin','15888888888','ry@qq.com','0','0','admin','2023-10-17 14:29:35','',NULL),(101,100,'0,100','深圳总公司',1,'sourcelin','15888888888','ry@qq.com','0','0','admin','2023-10-17 14:29:35','',NULL),(102,100,'0,100','长沙分公司',2,'sourcelin','15888888888','ry@qq.com','0','0','admin','2023-10-17 14:29:35','',NULL),(103,101,'0,100,101','研发部门',1,'sourcelin','15888888888','ry@qq.com','0','0','admin','2023-10-17 14:29:35','',NULL),(104,101,'0,100,101','市场部门',2,'sourcelin','15888888888','ry@qq.com','0','0','admin','2023-10-17 14:29:35','',NULL),(105,101,'0,100,101','测试部门',3,'sourcelin','15888888888','ry@qq.com','0','0','admin','2023-10-17 14:29:35','',NULL),(106,101,'0,100,101','财务部门',4,'sourcelin','15888888888','ry@qq.com','0','0','admin','2023-10-17 14:29:35','',NULL),(107,101,'0,100,101','运维部门',5,'sourcelin','15888888888','ry@qq.com','0','0','admin','2023-10-17 14:29:35','',NULL),(108,102,'0,100,102','市场部门',1,'sourcelin','15888888888','ry@qq.com','0','0','admin','2023-10-17 14:29:35','',NULL),(109,102,'0,100,102','财务部门',2,'sourcelin','15888888888','ry@qq.com','0','0','admin','2023-10-17 14:29:35','',NULL);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,1,'男','0','sys_user_sex','','','Y','0','admin','2023-10-17 14:29:36','',NULL,'性别男'),(2,2,'女','1','sys_user_sex','','','N','0','admin','2023-10-17 14:29:36','',NULL,'性别女'),(3,3,'未知','2','sys_user_sex','','','N','0','admin','2023-10-17 14:29:36','',NULL,'性别未知'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2023-10-17 14:29:36','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2023-10-17 14:29:36','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2023-10-17 14:29:36','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2023-10-17 14:29:36','',NULL,'停用状态'),(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2023-10-17 14:29:36','',NULL,'正常状态'),(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2023-10-17 14:29:36','',NULL,'停用状态'),(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2023-10-17 14:29:36','',NULL,'默认分组'),(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2023-10-17 14:29:36','',NULL,'系统分组'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2023-10-17 14:29:36','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2023-10-17 14:29:36','',NULL,'系统默认否'),(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2023-10-17 14:29:36','',NULL,'通知'),(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2023-10-17 14:29:36','',NULL,'公告'),(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2023-10-17 14:29:36','',NULL,'正常状态'),(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2023-10-17 14:29:36','',NULL,'关闭状态'),(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2023-10-17 14:29:36','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2023-10-17 14:29:36','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2023-10-17 14:29:36','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2023-10-17 14:29:36','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2023-10-17 14:29:36','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2023-10-17 14:29:36','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2023-10-17 14:29:36','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2023-10-17 14:29:36','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2023-10-17 14:29:36','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2023-10-17 14:29:36','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2023-10-17 14:29:36','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2023-10-17 14:29:36','',NULL,'停用状态'),(37,1,'博客文章','article','blog_comment_source','','','N','0','admin','2026-04-05 12:35:29','',NULL,NULL),(38,2,'说说','say','blog_comment_source','','','N','0','admin','2026-04-05 12:35:29','',NULL,NULL),(39,3,'树洞','treehole','blog_comment_source','','','N','0','admin','2026-04-05 12:35:29','',NULL,NULL),(40,1,'推荐','1','blog_friend_category','','primary','N','0','admin','2026-04-05 12:35:36','',NULL,NULL),(41,2,'精选','2','blog_friend_category','','success','N','0','admin','2026-04-05 12:35:36','',NULL,NULL),(42,3,'更多','3','blog_friend_category','','info','N','0','admin','2026-04-05 12:35:36','',NULL,NULL),(43,1,'申请','0','blog_friend_status','','warning','N','0','admin','2026-04-05 12:35:43','',NULL,NULL),(44,2,'上架','1','blog_friend_status','','success','N','0','admin','2026-04-05 12:35:43','',NULL,NULL),(45,3,'下架','2','blog_friend_status','','danger','N','0','admin','2026-04-05 12:35:43','',NULL,NULL),(46,1,'公开','1','blog_read_auth','','success','Y','0','admin','2026-04-05 12:35:52','',NULL,NULL),(47,2,'登录可读','2','blog_read_auth','','primary','N','0','admin','2026-04-05 12:35:52','',NULL,NULL),(48,3,'评论后可读','3','blog_read_auth','','info','N','0','admin','2026-04-05 12:35:52','',NULL,NULL),(49,4,'验证后可读','4','blog_read_auth','','warning','N','0','admin','2026-04-05 12:35:52','',NULL,NULL),(50,5,'VIP可读','5','blog_read_auth','','warning','N','0','admin','2026-04-05 12:35:52','',NULL,NULL),(51,6,'付费后可读','6','blog_read_auth','','danger','N','0','admin','2026-04-05 12:35:52','',NULL,NULL),(52,7,'点赞后可读','7','blog_read_auth','','info','N','0','admin','2026-04-05 12:35:52','',NULL,NULL),(53,8,'收藏后可读','8','blog_read_auth','','info','N','0','admin','2026-04-05 12:35:52','',NULL,NULL),(54,9,'关注后可读','9','blog_read_auth','','info','N','0','admin','2026-04-05 12:35:52','',NULL,NULL),(55,1,'审核中','1','blog_status','','warning','N','0','admin','2026-04-05 12:35:59','',NULL,NULL),(56,2,'已发布','2','blog_status','','success','N','0','admin','2026-04-05 12:35:59','',NULL,NULL),(57,3,'草稿','3','blog_status','','info','N','0','admin','2026-04-05 12:35:59','',NULL,NULL),(58,1,'否','0','blog_comment','','info','N','0','admin','2026-04-05 12:36:08','',NULL,NULL),(59,2,'是','1','blog_comment','','success','N','0','admin','2026-04-05 12:36:08','',NULL,NULL),(60,1,'否','0','blog_original','','info','N','0','admin','2026-04-05 12:36:11','',NULL,NULL),(61,2,'是','1','blog_original','','success','N','0','admin','2026-04-05 12:36:11','',NULL,NULL),(62,1,'否','0','blog_yes_no','','info','N','0','admin','2026-04-05 12:36:19','',NULL,NULL),(63,2,'是','1','blog_yes_no','','success','N','0','admin','2026-04-05 12:36:19','',NULL,NULL),(64,1,'普通用户','1','blog_user_type','','primary','Y','0','admin','2026-04-05 12:36:22','',NULL,NULL),(65,2,'博主','2','blog_user_type','','success','N','0','admin','2026-04-05 12:36:22','',NULL,NULL),(66,1,'正常','0','blog_user_status','','success','Y','0','admin','2026-04-05 12:36:31','',NULL,NULL),(67,2,'停用','1','blog_user_status','','danger','N','0','admin','2026-04-05 12:36:31','',NULL,NULL),(68,1,'账号密码','1','blog_login_type','','primary','Y','0','admin','2026-04-05 12:36:36','',NULL,NULL),(69,2,'码云','2','blog_login_type','','','N','0','admin','2026-04-05 12:36:36','',NULL,NULL),(70,3,'Github','3','blog_login_type','','','N','0','admin','2026-04-05 12:36:36','',NULL,NULL),(71,4,'QQ','4','blog_login_type','','','N','0','admin','2026-04-05 12:36:36','',NULL,NULL),(72,5,'微信','5','blog_login_type','','','N','0','admin','2026-04-05 12:36:36','',NULL,NULL);
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex','0','admin','2023-10-17 14:29:36','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','admin','2023-10-17 14:29:36','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','admin','2023-10-17 14:29:36','',NULL,'系统开关列表'),(4,'任务状态','sys_job_status','0','admin','2023-10-17 14:29:36','',NULL,'任务状态列表'),(5,'任务分组','sys_job_group','0','admin','2023-10-17 14:29:36','',NULL,'任务分组列表'),(6,'系统是否','sys_yes_no','0','admin','2023-10-17 14:29:36','',NULL,'系统是否列表'),(7,'通知类型','sys_notice_type','0','admin','2023-10-17 14:29:36','',NULL,'通知类型列表'),(8,'通知状态','sys_notice_status','0','admin','2023-10-17 14:29:36','',NULL,'通知状态列表'),(9,'操作类型','sys_oper_type','0','admin','2023-10-17 14:29:36','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','admin','2023-10-17 14:29:36','',NULL,'登录状态列表'),(14,'博客评论来源','blog_comment_source','0','admin','2026-04-05 12:35:27','',NULL,'b_comment.source，与 CommentSourceEnum 对齐'),(15,'友链分类','blog_friend_category','0','admin','2026-04-05 12:35:34','',NULL,'FriendLink.category：1推荐 2精选 3更多'),(16,'友链状态','blog_friend_status','0','admin','2026-04-05 12:35:41','',NULL,'FriendLink.status：0申请 1上架 2下架'),(17,'文章阅读权限','blog_read_auth','0','admin','2026-04-05 12:35:48','',NULL,'ArticleConstants 1～9'),(18,'文章状态','blog_status','0','admin','2026-04-05 12:35:57','',NULL,'1审核中 2已发布 3草稿'),(19,'文章评论开关','blog_comment','0','admin','2026-04-05 12:36:06','',NULL,'0否 1是'),(20,'是否原创','blog_original','0','admin','2026-04-05 12:36:10','',NULL,'0否 1是'),(21,'博客是否','blog_yes_no','0','admin','2026-04-05 12:36:17','',NULL,'0否 1是'),(22,'博客用户类型','blog_user_type','0','admin','2026-04-05 12:36:21','',NULL,'UserTypeEnum：1普通 2博主'),(23,'博客用户状态','blog_user_status','0','admin','2026-04-05 12:36:29','',NULL,'0正常 1停用'),(24,'博客登录方式','blog_login_type','0','admin','2026-04-05 12:36:33','',NULL,'login_type_list：1账号密码 2码云 3Github 4QQ 5微信');
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job`
--

LOCK TABLES `sys_job` WRITE;
/*!40000 ALTER TABLE `sys_job` DISABLE KEYS */;
INSERT INTO `sys_job` VALUES (1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','admin','2023-10-17 14:29:37','',NULL,''),(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(\'ry\')','0/15 * * * * ?','3','1','1','admin','2023-10-17 14:29:37','',NULL,''),(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','admin','2023-10-17 14:29:37','',NULL,'');
/*!40000 ALTER TABLE `sys_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job_log`
--

LOCK TABLES `sys_job_log` WRITE;
/*!40000 ALTER TABLE `sys_job_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_logininfor`
--

DROP TABLE IF EXISTS `sys_logininfor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=356 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_logininfor`
--

LOCK TABLES `sys_logininfor` WRITE;
/*!40000 ALTER TABLE `sys_logininfor` DISABLE KEYS */;
INSERT INTO `sys_logininfor` VALUES (100,'admin','127.0.0.1','0','登录成功','2023-10-17 16:20:31'),(101,'admin','127.0.0.1','0','登录成功','2023-10-17 16:26:34'),(102,'admin','127.0.0.1','0','退出成功','2023-10-17 16:26:45'),(103,'admin','127.0.0.1','0','登录成功','2023-10-17 16:27:30'),(104,'admin','127.0.0.1','0','登录成功','2023-10-23 11:49:27'),(105,'admin','127.0.0.1','0','登录成功','2024-07-01 19:26:31'),(106,'admin','127.0.0.1','0','登录成功','2024-07-02 08:47:31'),(107,'admin','127.0.0.1','0','登录成功','2024-07-19 16:57:19'),(108,'admin','127.0.0.1','0','登录成功','2025-03-11 15:14:48'),(109,'admin','127.0.0.1','0','登录成功','2026-02-12 10:48:46'),(110,'admin','127.0.0.1','0','退出成功','2026-02-12 10:50:30'),(111,'admin','127.0.0.1','0','登录成功','2026-02-12 10:51:46'),(112,'admin','127.0.0.1','0','登录成功','2026-03-17 00:35:07'),(113,'admin','127.0.0.1','0','退出成功','2026-03-17 00:37:12'),(114,'admin','127.0.0.1','0','登录成功','2026-03-17 00:37:28'),(115,'admin','127.0.0.1','0','登录成功','2026-03-21 22:29:20'),(116,'admin','127.0.0.1','0','登录成功','2026-03-30 18:42:39'),(117,'admin','127.0.0.1','0','登录成功','2026-03-30 18:43:16'),(118,'admin','127.0.0.1','0','登录成功','2026-03-30 22:19:48'),(119,'admin','127.0.0.1','0','登录成功','2026-03-30 23:34:44'),(120,'admin','127.0.0.1','0','登录成功','2026-03-30 23:50:52'),(121,'admin','127.0.0.1','0','退出成功','2026-03-30 23:55:36'),(122,'admin','127.0.0.1','0','登录成功','2026-03-30 23:56:30'),(123,'admin','127.0.0.1','0','登录成功','2026-03-31 13:29:53'),(124,'admin','127.0.0.1','0','注册成功','2026-03-31 19:36:29'),(125,'admin','127.0.0.1','0','登录成功','2026-03-31 19:56:24'),(126,'admin','127.0.0.1','0','登录成功','2026-03-31 19:57:42'),(127,'admin','127.0.0.1','0','登录成功','2026-03-31 20:06:36'),(128,'admin','127.0.0.1','0','登录成功','2026-03-31 20:14:02'),(129,'admin','127.0.0.1','0','登录成功','2026-04-01 00:54:28'),(130,NULL,'127.0.0.1','0','退出成功','2026-04-01 00:54:30'),(131,'admin','127.0.0.1','0','登录成功','2026-04-01 00:54:54'),(132,NULL,'127.0.0.1','0','退出成功','2026-04-01 00:54:55'),(133,'admin','127.0.0.1','0','登录成功','2026-04-01 00:56:16'),(134,NULL,'127.0.0.1','0','退出成功','2026-04-01 00:56:17'),(135,'admin','127.0.0.1','0','登录成功','2026-04-01 01:01:51'),(136,NULL,'127.0.0.1','0','退出成功','2026-04-01 01:01:52'),(137,'admin','127.0.0.1','0','登录成功','2026-04-01 01:07:45'),(138,'admin','127.0.0.1','0','登录成功','2026-04-01 01:08:07'),(139,NULL,'127.0.0.1','0','退出成功','2026-04-01 01:08:08'),(140,'admin','127.0.0.1','0','登录成功','2026-04-01 01:13:23'),(141,'admin','127.0.0.1','0','登录成功','2026-04-01 01:16:05'),(142,NULL,'127.0.0.1','0','退出成功','2026-04-01 01:16:06'),(143,'admin','127.0.0.1','0','登录成功','2026-04-01 10:22:42'),(144,NULL,'127.0.0.1','0','退出成功','2026-04-01 10:23:39'),(145,'admin','127.0.0.1','0','登录成功','2026-04-01 10:23:43'),(146,'admin','127.0.0.1','0','登录成功','2026-04-01 10:25:13'),(147,'admin','127.0.0.1','0','登录成功','2026-04-01 15:30:54'),(148,'admin','127.0.0.1','0','登录成功','2026-04-01 22:35:53'),(149,'admin','127.0.0.1','0','登录成功','2026-04-01 22:44:04'),(150,'admin','127.0.0.1','0','登录成功','2026-04-01 22:53:55'),(151,'admin','127.0.0.1','0','登录成功','2026-04-01 22:54:45'),(152,'admin','127.0.0.1','0','登录成功','2026-04-01 22:55:00'),(153,'admin','127.0.0.1','0','登录成功','2026-04-01 23:11:46'),(154,'admin','127.0.0.1','0','登录成功','2026-04-01 23:17:17'),(155,'admin','127.0.0.1','0','登录成功','2026-04-01 23:17:41'),(156,'admin','127.0.0.1','0','登录成功','2026-04-01 23:18:33'),(157,'admin','127.0.0.1','0','登录成功','2026-04-01 23:20:00'),(158,'admin','127.0.0.1','0','登录成功','2026-04-01 23:20:27'),(159,'admin','127.0.0.1','0','登录成功','2026-04-01 23:23:29'),(160,'admin','127.0.0.1','0','登录成功','2026-04-01 23:24:21'),(161,'admin','127.0.0.1','0','登录成功','2026-04-01 23:29:52'),(162,'admin','127.0.0.1','0','登录成功','2026-04-01 23:29:59'),(163,'admin','127.0.0.1','0','登录成功','2026-04-01 23:32:03'),(164,'admin','127.0.0.1','0','登录成功','2026-04-01 23:32:11'),(165,'admin','127.0.0.1','0','登录成功','2026-04-01 23:33:45'),(166,'admin','127.0.0.1','0','登录成功','2026-04-01 23:33:51'),(167,'admin','127.0.0.1','0','登录成功','2026-04-01 23:43:51'),(168,'admin','127.0.0.1','0','登录成功','2026-04-01 23:59:33'),(169,'admin','127.0.0.1','0','登录成功','2026-04-01 23:59:45'),(170,'admin','127.0.0.1','0','登录成功','2026-04-02 00:08:09'),(171,'admin','127.0.0.1','0','登录成功','2026-04-02 09:29:19'),(172,'admin','127.0.0.1','0','登录成功','2026-04-02 10:14:17'),(173,'admin','127.0.0.1','0','登录成功','2026-04-02 11:58:31'),(174,'admin','127.0.0.1','0','登录成功','2026-04-02 14:48:14'),(175,'admin','127.0.0.1','0','登录成功','2026-04-03 00:10:31'),(176,'admin','127.0.0.1','0','登录成功','2026-04-03 00:10:51'),(177,'admin','127.0.0.1','0','登录成功','2026-04-03 00:15:51'),(178,'admin','127.0.0.1','0','登录成功','2026-04-03 00:17:30'),(179,'admin','127.0.0.1','0','登录成功','2026-04-03 00:28:07'),(180,'admin','127.0.0.1','0','登录成功','2026-04-03 10:25:23'),(181,'admin','127.0.0.1','1','用户不存在','2026-04-03 10:41:46'),(182,'SourceLin','127.0.0.1','0','登录成功','2026-04-03 10:42:06'),(183,'SourceLin','127.0.0.1','0','登录成功','2026-04-03 12:27:19'),(184,'SourceLin','127.0.0.1','0','登录成功','2026-04-03 12:27:47'),(185,'SourceLin','127.0.0.1','0','登录成功','2026-04-03 13:44:12'),(186,'SourceLin','127.0.0.1','0','登录成功','2026-04-03 13:45:33'),(187,'SourceLin','127.0.0.1','0','登录成功','2026-04-03 14:06:35'),(188,'SourceLin','127.0.0.1','0','登录成功','2026-04-03 14:16:40'),(189,'SourceLin','127.0.0.1','0','登录成功','2026-04-03 14:19:06'),(190,'SourceLin','127.0.0.1','0','登录成功','2026-04-03 15:34:42'),(191,'SourceLin','127.0.0.1','0','登录成功','2026-04-03 16:23:23'),(192,'SourceLin','127.0.0.1','0','登录成功','2026-04-03 16:23:27'),(193,'SourceLin','127.0.0.1','0','登录成功','2026-04-03 16:23:28'),(194,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 02:06:49'),(195,'SourceLin','127.0.0.1','1','用户不存在','2026-04-04 02:06:49'),(196,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 02:07:04'),(197,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 02:22:22'),(198,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 03:42:00'),(199,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 03:42:30'),(200,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 03:47:23'),(201,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 03:47:30'),(202,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 05:09:38'),(203,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 05:09:40'),(204,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 05:09:46'),(205,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 09:22:19'),(206,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 09:22:23'),(207,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 09:22:27'),(208,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 10:19:27'),(209,'SourceLin','127.0.0.1','1','用户不存在','2026-04-04 10:19:33'),(210,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 10:19:48'),(211,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 10:32:52'),(212,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 10:33:08'),(213,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 10:35:05'),(214,'SourceLin','127.0.0.1','1','登录用户不存在','2026-04-04 11:11:31'),(215,'SourceLin','127.0.0.1','1','登录用户不存在','2026-04-04 11:11:52'),(216,'admin','127.0.0.1','0','登录成功','2026-04-04 11:12:14'),(217,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 11:35:15'),(218,'SourceLIn','127.0.0.1','0','登录成功','2026-04-04 11:42:09'),(219,'SourceLIn','127.0.0.1','0','登录成功','2026-04-04 12:06:13'),(220,'SourceLIn','127.0.0.1','0','登录成功','2026-04-04 12:06:32'),(221,'SourceLIn','127.0.0.1','0','登录成功','2026-04-04 12:41:38'),(222,'SourceLIn','127.0.0.1','0','登录成功','2026-04-04 12:41:42'),(223,'SourceLIn','127.0.0.1','0','登录成功','2026-04-04 12:41:47'),(224,'SourceLIn','127.0.0.1','1','用户不存在','2026-04-04 15:40:24'),(225,'SourceLIn','127.0.0.1','0','登录成功','2026-04-04 15:40:32'),(226,'SourceLIn','127.0.0.1','1','用户不存在','2026-04-04 15:40:35'),(227,'SourceLIn','127.0.0.1','0','登录成功','2026-04-04 15:41:08'),(228,'SourceLIn','127.0.0.1','0','登录成功','2026-04-04 15:59:18'),(229,'SourceLIn','127.0.0.1','1','用户不存在','2026-04-04 16:43:51'),(230,'SourceLIn','127.0.0.1','0','登录成功','2026-04-04 16:44:55'),(231,NULL,'127.0.0.1','0','退出成功','2026-04-04 17:16:57'),(232,NULL,'127.0.0.1','0','退出成功','2026-04-04 17:17:04'),(233,'admin','127.0.0.1','1','登录用户不存在','2026-04-04 17:43:14'),(234,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 18:13:30'),(235,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 18:13:35'),(236,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 18:13:36'),(237,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 18:31:55'),(238,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 18:31:57'),(239,'SourceLin','127.0.0.1','0','登录成功','2026-04-04 18:32:00'),(240,'SourceLin','127.0.0.1','0','登录成功','2026-04-05 02:46:51'),(241,'SourceLin','127.0.0.1','0','登录成功','2026-04-05 02:46:56'),(242,'SourceLin','127.0.0.1','0','登录成功','2026-04-05 02:47:08'),(243,'admin','127.0.0.1','1','登录用户不存在','2026-04-05 02:59:56'),(244,'admin','127.0.0.1','1','登录用户不存在','2026-04-05 02:59:59'),(245,'admin','127.0.0.1','0','登录成功','2026-04-05 03:00:11'),(246,'admin','127.0.0.1','0','登录成功','2026-04-05 03:12:33'),(247,NULL,'127.0.0.1','0','退出成功','2026-04-05 03:12:34'),(248,'SourceLIn','127.0.0.1','0','登录成功','2026-04-05 05:59:05'),(249,'SourceLIn','127.0.0.1','1','登录用户不存在','2026-04-05 12:08:09'),(250,'admin','127.0.0.1','0','登录成功','2026-04-05 12:09:00'),(251,NULL,'127.0.0.1','0','退出成功','2026-04-05 12:25:16'),(252,'admin','127.0.0.1','0','登录成功','2026-04-05 12:25:26'),(253,NULL,'127.0.0.1','0','退出成功','2026-04-05 12:37:39'),(254,'admin','127.0.0.1','0','登录成功','2026-04-05 12:37:49'),(255,NULL,'127.0.0.1','0','退出成功','2026-04-05 12:53:06'),(256,'admin','127.0.0.1','0','登录成功','2026-04-05 12:53:12'),(257,NULL,'127.0.0.1','0','退出成功','2026-04-05 12:54:02'),(258,'admin','127.0.0.1','0','登录成功','2026-04-05 12:54:11'),(259,NULL,'127.0.0.1','0','退出成功','2026-04-05 13:08:46'),(260,'admin','127.0.0.1','0','登录成功','2026-04-05 13:08:55'),(261,NULL,'127.0.0.1','0','退出成功','2026-04-05 13:23:01'),(262,'admin','127.0.0.1','0','登录成功','2026-04-05 13:23:02'),(263,NULL,'127.0.0.1','0','退出成功','2026-04-05 13:29:55'),(264,'admin','127.0.0.1','0','登录成功','2026-04-05 13:37:44'),(265,'admin','127.0.0.1','1','登录用户不存在','2026-04-05 13:37:46'),(266,NULL,'127.0.0.1','0','退出成功','2026-04-05 13:41:11'),(267,'SourceLIn','127.0.0.1','0','登录成功','2026-04-05 13:43:29'),(268,'admin','127.0.0.1','1','登录用户不存在','2026-04-05 13:48:35'),(269,'admin','127.0.0.1','0','登录成功','2026-04-05 13:48:39'),(270,NULL,'127.0.0.1','0','退出成功','2026-04-05 14:04:54'),(271,'admin','127.0.0.1','0','登录成功','2026-04-05 14:05:06'),(272,NULL,'127.0.0.1','0','退出成功','2026-04-05 14:21:51'),(273,'admin','127.0.0.1','0','登录成功','2026-04-05 14:24:17'),(274,'SourceLIn','127.0.0.1','0','登录成功','2026-04-05 14:31:38'),(275,'admin','127.0.0.1','0','登录成功','2026-04-05 15:04:12'),(276,'admin','127.0.0.1','1','登录用户不存在','2026-04-05 15:24:46'),(277,'admin','127.0.0.1','0','登录成功','2026-04-05 15:24:57'),(278,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 06:55:51'),(279,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 06:55:57'),(280,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 06:56:05'),(281,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 08:55:41'),(282,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 09:10:24'),(283,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 09:10:48'),(284,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 09:16:36'),(285,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 09:16:37'),(286,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 09:27:02'),(287,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 09:27:05'),(288,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 09:30:28'),(289,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 10:24:50'),(290,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 10:25:07'),(291,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 14:40:19'),(292,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 14:40:20'),(293,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 14:40:31'),(294,'admin','127.0.0.1','0','登录成功','2026-04-06 15:01:04'),(295,'SourceLIn','127.0.0.1','0','登录成功','2026-04-06 15:24:11'),(296,'lyq','127.0.0.1','0','注册成功','2026-04-07 12:06:11'),(297,'lyq','127.0.0.1','0','登录成功','2026-04-07 12:06:40'),(298,'admin','127.0.0.1','1','登录用户不存在','2026-04-07 12:12:31'),(299,'admin','127.0.0.1','1','登录用户不存在','2026-04-07 12:12:31'),(300,'admin','127.0.0.1','0','登录成功','2026-04-07 12:12:50'),(301,NULL,'127.0.0.1','0','退出成功','2026-04-07 12:13:05'),(302,'lyq','127.0.0.1','0','注册成功','2026-04-07 12:37:31'),(303,'lyq','127.0.0.1','0','注册成功','2026-04-07 12:38:02'),(304,'lyq','127.0.0.1','0','登录成功','2026-04-07 12:38:13'),(305,'lyq','127.0.0.1','0','登录成功','2026-04-07 12:38:32'),(306,'1012435726@qq.com','127.0.0.1','1','邮箱未注册','2026-04-07 12:39:28'),(307,'1012435726@qq.com','127.0.0.1','1','邮箱未注册','2026-04-07 12:41:56'),(308,'1012435726@qq.com','127.0.0.1','1','邮箱未注册','2026-04-07 12:42:23'),(309,'1012435726@qq.com','127.0.0.1','1','邮箱未注册','2026-04-07 13:05:37'),(310,'1012435726@qq.com','127.0.0.1','0','登录成功','2026-04-07 13:07:38'),(311,'lyq','127.0.0.1','0','注册成功','2026-04-07 13:09:34'),(312,'admin','127.0.0.1','0','登录成功','2026-04-07 13:12:36'),(313,'lyq','127.0.0.1','0','登录成功','2026-04-07 13:13:20'),(314,'lyq','127.0.0.1','1','密码输入错误1次','2026-04-07 13:13:48'),(315,'lyq','127.0.0.1','0','登录成功','2026-04-07 13:13:59'),(316,'lyq','127.0.0.1','0','登录成功','2026-04-07 13:19:27'),(317,'lyq','127.0.0.1','0','登录成功','2026-04-07 13:19:30'),(318,'lyq','127.0.0.1','1','用户不存在','2026-04-07 13:19:36'),(319,'lyq','127.0.0.1','0','登录成功','2026-04-07 15:47:50'),(320,NULL,'127.0.0.1','0','退出成功','2026-04-07 16:12:10'),(321,'admin','127.0.0.1','1','用户不存在','2026-04-07 16:31:57'),(322,'admin','127.0.0.1','1','用户不存在','2026-04-07 16:32:32'),(323,'admin','127.0.0.1','1','用户不存在','2026-04-07 16:32:39'),(324,'lyq','127.0.0.1','0','登录成功','2026-04-07 16:32:54'),(325,'admin','127.0.0.1','0','登录成功','2026-04-07 16:39:44'),(326,'lyq','127.0.0.1','0','登录成功','2026-04-07 16:40:33'),(327,'lyq','127.0.0.1','0','登录成功','2026-04-07 16:48:30'),(328,'lyq','127.0.0.1','0','登录成功','2026-04-08 09:36:44'),(329,'lyq','127.0.0.1','0','登录成功','2026-04-08 09:36:52'),(330,NULL,'127.0.0.1','0','退出成功','2026-04-08 09:39:42'),(331,'admin','127.0.0.1','0','登录成功','2026-04-08 09:39:51'),(332,'lyq','127.0.0.1','0','登录成功','2026-04-08 11:23:26'),(333,'lyq','127.0.0.1','1','用户不存在','2026-04-08 11:28:37'),(334,'lyq','127.0.0.1','0','登录成功','2026-04-08 11:56:25'),(335,'lyq','127.0.0.1','0','登录成功','2026-04-08 11:56:41'),(336,'lyq','127.0.0.1','0','登录成功','2026-04-08 11:56:53'),(337,'SourceLIn','127.0.0.1','0','登录成功','2026-04-08 12:11:20'),(338,'SourceLIn','127.0.0.1','0','登录成功','2026-04-08 12:17:02'),(339,'SourceLIn','127.0.0.1','0','登录成功','2026-04-08 12:17:12'),(340,'SourceLIn','127.0.0.1','0','登录成功','2026-04-08 12:27:01'),(341,'SourceLIn','127.0.0.1','0','登录成功','2026-04-08 12:27:05'),(342,'lyq','127.0.0.1','0','登录成功','2026-04-08 14:21:44'),(343,'SourceLIn','127.0.0.1','0','登录成功','2026-04-08 14:21:58'),(344,'admin','127.0.0.1','0','登录成功','2026-04-08 15:31:45'),(345,'admin','127.0.0.1','1','登录用户不存在','2026-04-08 15:31:52'),(346,'admin','127.0.0.1','0','登录成功','2026-04-08 15:43:32'),(347,NULL,'127.0.0.1','0','退出成功','2026-04-08 15:44:29'),(348,'admin','127.0.0.1','0','登录成功','2026-04-08 15:44:31'),(349,NULL,'127.0.0.1','0','退出成功','2026-04-08 15:47:18'),(350,'admin','127.0.0.1','0','登录成功','2026-04-08 17:43:28'),(351,'SourceLIn','127.0.0.1','0','登录成功','2026-04-09 19:05:26'),(352,'SourceLIn','127.0.0.1','0','登录成功','2026-04-09 19:05:30'),(353,'SourceLIn','127.0.0.1','0','登录成功','2026-04-09 19:26:49'),(354,'SourceLIn','127.0.0.1','0','登录成功','2026-04-09 20:32:19'),(355,'SourceLIn','127.0.0.1','0','登录成功','2026-04-09 20:32:33');
/*!40000 ALTER TABLE `sys_logininfor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=1156 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,1,'system',NULL,'','',1,0,'M','0','0','','system','admin','2023-10-17 14:29:36','',NULL,'系统管理目录'),(2,'系统监控',0,2,'monitor',NULL,'','',1,0,'M','0','0','','monitor','admin','2023-10-17 14:29:36','',NULL,'系统监控目录'),(3,'系统工具',0,3,'tool',NULL,'','',1,0,'M','0','0','','tool','admin','2023-10-17 14:29:36','',NULL,'系统工具目录'),(100,'用户管理',1,1,'user','system/user/index','','',1,0,'C','0','0','system:user:list','user','admin','2023-10-17 14:29:36','',NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','system/role/index','','',1,0,'C','0','0','system:role:list','peoples','admin','2023-10-17 14:29:36','',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','system/menu/index','','',1,0,'C','0','0','system:menu:list','tree-table','admin','2023-10-17 14:29:36','',NULL,'菜单管理菜单'),(103,'部门管理',1,4,'dept','system/dept/index','','',1,0,'C','0','0','system:dept:list','tree','admin','2023-10-17 14:29:36','',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','system/post/index','','',1,0,'C','0','0','system:post:list','post','admin','2023-10-17 14:29:36','',NULL,'岗位管理菜单'),(105,'字典管理',1,6,'dict','system/dict/index','','',1,0,'C','0','0','system:dict:list','dict','admin','2023-10-17 14:29:36','',NULL,'字典管理菜单'),(106,'参数设置',1,7,'config','system/config/index','','',1,0,'C','0','0','system:config:list','edit','admin','2023-10-17 14:29:36','',NULL,'参数设置菜单'),(107,'通知公告',1,8,'notice','system/notice/index','','',1,0,'C','0','0','system:notice:list','message','admin','2023-10-17 14:29:36','',NULL,'通知公告菜单'),(108,'日志管理',1,9,'log','','','',1,0,'M','0','0','','log','admin','2023-10-17 14:29:36','',NULL,'日志管理菜单'),(109,'在线用户',2,1,'online','monitor/online/index','','',1,0,'C','0','0','monitor:online:list','online','admin','2023-10-17 14:29:36','',NULL,'在线用户菜单'),(110,'定时任务',2,2,'job','monitor/job/index','','',1,0,'C','0','0','monitor:job:list','job','admin','2023-10-17 14:29:36','',NULL,'定时任务菜单'),(111,'Sentinel控制台',2,3,'http://localhost:8718','','','',1,1,'C','0','0','monitor:sentinel:list','sentinel','admin','2023-10-17 14:29:36','',NULL,'流量控制菜单'),(112,'Nacos控制台',2,4,'http://localhost:8848/nacos','','','',1,1,'C','0','0','monitor:nacos:list','nacos','admin','2023-10-17 14:29:36','',NULL,'服务治理菜单'),(113,'Admin控制台',2,5,'http://localhost:9100/login','','','',1,1,'C','0','0','monitor:server:list','server','admin','2023-10-17 14:29:36','',NULL,'服务监控菜单'),(114,'表单构建',3,1,'build','tool/build/index','','',1,0,'C','0','0','tool:build:list','build','admin','2023-10-17 14:29:36','',NULL,'表单构建菜单'),(115,'代码生成',3,2,'gen','tool/gen/index','','',1,0,'C','0','0','tool:gen:list','code','admin','2023-10-17 14:29:36','',NULL,'代码生成菜单'),(116,'系统接口',3,3,'http://localhost:8080/swagger-ui/index.html','','','',1,1,'C','0','0','tool:swagger:list','swagger','admin','2023-10-17 14:29:36','',NULL,'系统接口菜单'),(500,'操作日志',108,1,'operlog','system/operlog/index','','',1,0,'C','0','0','system:operlog:list','form','admin','2023-10-17 14:29:36','',NULL,'操作日志菜单'),(501,'登录日志',108,2,'logininfor','system/logininfor/index','','',1,0,'C','0','0','system:logininfor:list','logininfor','admin','2023-10-17 14:29:36','',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'','','','',1,0,'F','0','0','system:user:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1001,'用户新增',100,2,'','','','',1,0,'F','0','0','system:user:add','#','admin','2023-10-17 14:29:36','',NULL,''),(1002,'用户修改',100,3,'','','','',1,0,'F','0','0','system:user:edit','#','admin','2023-10-17 14:29:36','',NULL,''),(1003,'用户删除',100,4,'','','','',1,0,'F','0','0','system:user:remove','#','admin','2023-10-17 14:29:36','',NULL,''),(1004,'用户导出',100,5,'','','','',1,0,'F','0','0','system:user:export','#','admin','2023-10-17 14:29:36','',NULL,''),(1005,'用户导入',100,6,'','','','',1,0,'F','0','0','system:user:import','#','admin','2023-10-17 14:29:36','',NULL,''),(1006,'重置密码',100,7,'','','','',1,0,'F','0','0','system:user:resetPwd','#','admin','2023-10-17 14:29:36','',NULL,''),(1007,'角色查询',101,1,'','','','',1,0,'F','0','0','system:role:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1008,'角色新增',101,2,'','','','',1,0,'F','0','0','system:role:add','#','admin','2023-10-17 14:29:36','',NULL,''),(1009,'角色修改',101,3,'','','','',1,0,'F','0','0','system:role:edit','#','admin','2023-10-17 14:29:36','',NULL,''),(1010,'角色删除',101,4,'','','','',1,0,'F','0','0','system:role:remove','#','admin','2023-10-17 14:29:36','',NULL,''),(1011,'角色导出',101,5,'','','','',1,0,'F','0','0','system:role:export','#','admin','2023-10-17 14:29:36','',NULL,''),(1012,'菜单查询',102,1,'','','','',1,0,'F','0','0','system:menu:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1013,'菜单新增',102,2,'','','','',1,0,'F','0','0','system:menu:add','#','admin','2023-10-17 14:29:36','',NULL,''),(1014,'菜单修改',102,3,'','','','',1,0,'F','0','0','system:menu:edit','#','admin','2023-10-17 14:29:36','',NULL,''),(1015,'菜单删除',102,4,'','','','',1,0,'F','0','0','system:menu:remove','#','admin','2023-10-17 14:29:36','',NULL,''),(1016,'部门查询',103,1,'','','','',1,0,'F','0','0','system:dept:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1017,'部门新增',103,2,'','','','',1,0,'F','0','0','system:dept:add','#','admin','2023-10-17 14:29:36','',NULL,''),(1018,'部门修改',103,3,'','','','',1,0,'F','0','0','system:dept:edit','#','admin','2023-10-17 14:29:36','',NULL,''),(1019,'部门删除',103,4,'','','','',1,0,'F','0','0','system:dept:remove','#','admin','2023-10-17 14:29:36','',NULL,''),(1020,'岗位查询',104,1,'','','','',1,0,'F','0','0','system:post:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1021,'岗位新增',104,2,'','','','',1,0,'F','0','0','system:post:add','#','admin','2023-10-17 14:29:36','',NULL,''),(1022,'岗位修改',104,3,'','','','',1,0,'F','0','0','system:post:edit','#','admin','2023-10-17 14:29:36','',NULL,''),(1023,'岗位删除',104,4,'','','','',1,0,'F','0','0','system:post:remove','#','admin','2023-10-17 14:29:36','',NULL,''),(1024,'岗位导出',104,5,'','','','',1,0,'F','0','0','system:post:export','#','admin','2023-10-17 14:29:36','',NULL,''),(1025,'字典查询',105,1,'#','','','',1,0,'F','0','0','system:dict:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1026,'字典新增',105,2,'#','','','',1,0,'F','0','0','system:dict:add','#','admin','2023-10-17 14:29:36','',NULL,''),(1027,'字典修改',105,3,'#','','','',1,0,'F','0','0','system:dict:edit','#','admin','2023-10-17 14:29:36','',NULL,''),(1028,'字典删除',105,4,'#','','','',1,0,'F','0','0','system:dict:remove','#','admin','2023-10-17 14:29:36','',NULL,''),(1029,'字典导出',105,5,'#','','','',1,0,'F','0','0','system:dict:export','#','admin','2023-10-17 14:29:36','',NULL,''),(1030,'参数查询',106,1,'#','','','',1,0,'F','0','0','system:config:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1031,'参数新增',106,2,'#','','','',1,0,'F','0','0','system:config:add','#','admin','2023-10-17 14:29:36','',NULL,''),(1032,'参数修改',106,3,'#','','','',1,0,'F','0','0','system:config:edit','#','admin','2023-10-17 14:29:36','',NULL,''),(1033,'参数删除',106,4,'#','','','',1,0,'F','0','0','system:config:remove','#','admin','2023-10-17 14:29:36','',NULL,''),(1034,'参数导出',106,5,'#','','','',1,0,'F','0','0','system:config:export','#','admin','2023-10-17 14:29:36','',NULL,''),(1035,'公告查询',107,1,'#','','','',1,0,'F','0','0','system:notice:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1036,'公告新增',107,2,'#','','','',1,0,'F','0','0','system:notice:add','#','admin','2023-10-17 14:29:36','',NULL,''),(1037,'公告修改',107,3,'#','','','',1,0,'F','0','0','system:notice:edit','#','admin','2023-10-17 14:29:36','',NULL,''),(1038,'公告删除',107,4,'#','','','',1,0,'F','0','0','system:notice:remove','#','admin','2023-10-17 14:29:36','',NULL,''),(1039,'操作查询',500,1,'#','','','',1,0,'F','0','0','system:operlog:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1040,'操作删除',500,2,'#','','','',1,0,'F','0','0','system:operlog:remove','#','admin','2023-10-17 14:29:36','',NULL,''),(1041,'日志导出',500,3,'#','','','',1,0,'F','0','0','system:operlog:export','#','admin','2023-10-17 14:29:36','',NULL,''),(1042,'登录查询',501,1,'#','','','',1,0,'F','0','0','system:logininfor:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1043,'登录删除',501,2,'#','','','',1,0,'F','0','0','system:logininfor:remove','#','admin','2023-10-17 14:29:36','',NULL,''),(1044,'日志导出',501,3,'#','','','',1,0,'F','0','0','system:logininfor:export','#','admin','2023-10-17 14:29:36','',NULL,''),(1045,'账户解锁',501,4,'#','','','',1,0,'F','0','0','system:logininfor:unlock','#','admin','2023-10-17 14:29:36','',NULL,''),(1046,'在线查询',109,1,'#','','','',1,0,'F','0','0','monitor:online:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1047,'批量强退',109,2,'#','','','',1,0,'F','0','0','monitor:online:batchLogout','#','admin','2023-10-17 14:29:36','',NULL,''),(1048,'单条强退',109,3,'#','','','',1,0,'F','0','0','monitor:online:forceLogout','#','admin','2023-10-17 14:29:36','',NULL,''),(1049,'任务查询',110,1,'#','','','',1,0,'F','0','0','monitor:job:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1050,'任务新增',110,2,'#','','','',1,0,'F','0','0','monitor:job:add','#','admin','2023-10-17 14:29:36','',NULL,''),(1051,'任务修改',110,3,'#','','','',1,0,'F','0','0','monitor:job:edit','#','admin','2023-10-17 14:29:36','',NULL,''),(1052,'任务删除',110,4,'#','','','',1,0,'F','0','0','monitor:job:remove','#','admin','2023-10-17 14:29:36','',NULL,''),(1053,'状态修改',110,5,'#','','','',1,0,'F','0','0','monitor:job:changeStatus','#','admin','2023-10-17 14:29:36','',NULL,''),(1054,'任务导出',110,6,'#','','','',1,0,'F','0','0','monitor:job:export','#','admin','2023-10-17 14:29:36','',NULL,''),(1055,'生成查询',115,1,'#','','','',1,0,'F','0','0','tool:gen:query','#','admin','2023-10-17 14:29:36','',NULL,''),(1056,'生成修改',115,2,'#','','','',1,0,'F','0','0','tool:gen:edit','#','admin','2023-10-17 14:29:36','',NULL,''),(1057,'生成删除',115,3,'#','','','',1,0,'F','0','0','tool:gen:remove','#','admin','2023-10-17 14:29:36','',NULL,''),(1058,'导入代码',115,2,'#','','','',1,0,'F','0','0','tool:gen:import','#','admin','2023-10-17 14:29:36','',NULL,''),(1059,'预览代码',115,4,'#','','','',1,0,'F','0','0','tool:gen:preview','#','admin','2023-10-17 14:29:36','',NULL,''),(1060,'生成代码',115,5,'#','','','',1,0,'F','0','0','tool:gen:code','#','admin','2023-10-17 14:29:36','',NULL,''),(1061,'网站管理',0,4,'config',NULL,NULL,'',1,0,'M','0','0','','monitor','admin','2024-07-01 20:24:21','admin','2024-07-19 17:09:59',''),(1062,'用户管理',0,5,'user',NULL,NULL,'',1,0,'M','0','0','','peoples','admin','2024-07-01 20:25:41','admin','2024-07-19 17:09:50',''),(1063,'博客管理',0,6,'blog',NULL,NULL,'',1,0,'M','0','0',NULL,'log','admin','2024-07-01 20:26:15','',NULL,''),(1064,'网站配置',1061,1,'config','blog/config/index',NULL,'',1,0,'C','0','0','blog:config:list','system','admin','2024-07-01 20:28:19','admin','2024-07-19 17:20:46','网站配置菜单'),(1065,'网站配置查询',1064,1,'#','',NULL,'',1,0,'F','0','0','blog:config:query','#','admin','2024-07-01 20:29:18','',NULL,''),(1066,'网站配置新增',1064,2,'#','',NULL,'',1,0,'F','0','0','blog:config:add','#','admin','2024-07-01 20:29:18','',NULL,''),(1067,'网站配置修改',1064,3,'#','',NULL,'',1,0,'F','0','0','blog:config:edit','#','admin','2024-07-01 20:29:18','',NULL,''),(1068,'网站配置删除',1064,4,'#','',NULL,'',1,0,'F','0','0','blog:config:remove','#','admin','2024-07-01 20:29:18','',NULL,''),(1069,'网站配置导出',1064,5,'#','',NULL,'',1,0,'F','0','0','blog:config:export','#','admin','2024-07-01 20:29:18','',NULL,''),(1070,'门户导航栏',1061,1,'navbar','blog/navbar/index',NULL,'',1,0,'C','0','0','blog:navbar:list','category','admin','2024-07-01 20:34:34','admin','2024-07-19 17:21:45','门户导航栏对象菜单'),(1071,'门户导航栏对象查询',1070,1,'#','',NULL,'',1,0,'F','0','0','blog:navbar:query','#','admin','2024-07-01 20:34:34','',NULL,''),(1072,'门户导航栏对象新增',1070,2,'#','',NULL,'',1,0,'F','0','0','blog:navbar:add','#','admin','2024-07-01 20:34:34','',NULL,''),(1073,'门户导航栏对象修改',1070,3,'#','',NULL,'',1,0,'F','0','0','blog:navbar:edit','#','admin','2024-07-01 20:34:34','',NULL,''),(1074,'门户导航栏对象删除',1070,4,'#','',NULL,'',1,0,'F','0','0','blog:navbar:remove','#','admin','2024-07-01 20:34:34','',NULL,''),(1075,'门户导航栏对象导出',1070,5,'#','',NULL,'',1,0,'F','0','0','blog:navbar:export','#','admin','2024-07-01 20:34:34','',NULL,''),(1088,'友链管理',1061,1,'link','blog/link/index',NULL,'',1,0,'C','0','0','blog:link:list','link','admin','2024-07-01 20:42:39','admin','2024-07-19 17:22:16','友链管理菜单'),(1089,'友链管理查询',1088,1,'#','',NULL,'',1,0,'F','0','0','blog:link:query','#','admin','2024-07-01 20:42:39','',NULL,''),(1090,'友链管理新增',1088,2,'#','',NULL,'',1,0,'F','0','0','blog:link:add','#','admin','2024-07-01 20:42:39','',NULL,''),(1091,'友链管理修改',1088,3,'#','',NULL,'',1,0,'F','0','0','blog:link:edit','#','admin','2024-07-01 20:42:39','',NULL,''),(1092,'友链管理删除',1088,4,'#','',NULL,'',1,0,'F','0','0','blog:link:remove','#','admin','2024-07-01 20:42:39','',NULL,''),(1093,'友链管理导出',1088,5,'#','',NULL,'',1,0,'F','0','0','blog:link:export','#','admin','2024-07-01 20:42:39','',NULL,''),(1094,'博客文章',1063,1,'article','blog/article/index',NULL,'',1,0,'C','0','0','blog:article:list','education','admin','2024-07-02 09:04:21','admin','2024-07-19 17:19:41','博客文章菜单'),(1095,'博客文章查询',1094,1,'#','',NULL,'',1,0,'F','0','0','blog:article:query','#','admin','2024-07-02 09:04:23','',NULL,''),(1096,'博客文章新增',1094,2,'#','',NULL,'',1,0,'F','0','0','blog:article:add','#','admin','2024-07-02 09:04:23','',NULL,''),(1097,'博客文章修改',1094,3,'#','',NULL,'',1,0,'F','0','0','blog:article:edit','#','admin','2024-07-02 09:04:23','',NULL,''),(1098,'博客文章删除',1094,4,'#','',NULL,'',1,0,'F','0','0','blog:article:remove','#','admin','2024-07-02 09:04:23','',NULL,''),(1099,'博客文章导出',1094,5,'#','',NULL,'',1,0,'F','0','0','blog:article:export','#','admin','2024-07-02 09:04:23','',NULL,''),(1100,'博客分类',1063,1,'category','blog/category/index',NULL,'',1,0,'C','0','0','blog:category:list','category','admin','2024-07-02 09:05:15','admin','2024-07-19 17:19:49','博客分类菜单'),(1101,'博客分类查询',1100,1,'#','',NULL,'',1,0,'F','0','0','blog:category:query','#','admin','2024-07-02 09:05:15','',NULL,''),(1102,'博客分类新增',1100,2,'#','',NULL,'',1,0,'F','0','0','blog:category:add','#','admin','2024-07-02 09:05:15','',NULL,''),(1103,'博客分类修改',1100,3,'#','',NULL,'',1,0,'F','0','0','blog:category:edit','#','admin','2024-07-02 09:05:15','',NULL,''),(1104,'博客分类删除',1100,4,'#','',NULL,'',1,0,'F','0','0','blog:category:remove','#','admin','2024-07-02 09:05:15','',NULL,''),(1105,'博客分类导出',1100,5,'#','',NULL,'',1,0,'F','0','0','blog:category:export','#','admin','2024-07-02 09:05:15','',NULL,''),(1106,'评论管理',1063,1,'comment','blog/comment/index',NULL,'',1,0,'C','0','0','blog:comment:list','edit','admin','2024-07-02 09:05:46','admin','2024-07-19 17:08:05','评论管理菜单'),(1107,'评论管理查询',1106,1,'#','',NULL,'',1,0,'F','0','0','blog:comment:query','#','admin','2024-07-02 09:05:46','',NULL,''),(1108,'评论管理新增',1106,2,'#','',NULL,'',1,0,'F','0','0','blog:comment:add','#','admin','2024-07-02 09:05:46','',NULL,''),(1109,'评论管理修改',1106,3,'#','',NULL,'',1,0,'F','0','0','blog:comment:edit','#','admin','2024-07-02 09:05:46','',NULL,''),(1110,'评论管理删除',1106,4,'#','',NULL,'',1,0,'F','0','0','blog:comment:remove','#','admin','2024-07-02 09:05:46','',NULL,''),(1111,'评论管理导出',1106,5,'#','',NULL,'',1,0,'F','0','0','blog:comment:export','#','admin','2024-07-02 09:05:46','',NULL,''),(1112,'博客标签',1063,1,'tag','blog/tag/index',NULL,'',1,0,'C','0','0','blog:tag:list','tag','admin','2024-07-02 09:06:21','admin','2024-07-19 17:20:06','博客标签菜单'),(1113,'博客标签查询',1112,1,'#','',NULL,'',1,0,'F','0','0','blog:tag:query','#','admin','2024-07-02 09:06:21','',NULL,''),(1114,'博客标签新增',1112,2,'#','',NULL,'',1,0,'F','0','0','blog:tag:add','#','admin','2024-07-02 09:06:21','',NULL,''),(1115,'博客标签修改',1112,3,'#','',NULL,'',1,0,'F','0','0','blog:tag:edit','#','admin','2024-07-02 09:06:21','',NULL,''),(1116,'博客标签删除',1112,4,'#','',NULL,'',1,0,'F','0','0','blog:tag:remove','#','admin','2024-07-02 09:06:21','',NULL,''),(1117,'博客标签导出',1112,5,'#','',NULL,'',1,0,'F','0','0','blog:tag:export','#','admin','2024-07-02 09:06:21','',NULL,''),(1118,'用户信息',1062,1,'user','blog/user/index',NULL,'',1,0,'C','0','0','blog:user:list','people','admin','2024-07-02 09:06:37','admin','2024-07-19 17:20:20','用户信息菜单'),(1119,'用户信息查询',1118,1,'#','',NULL,'',1,0,'F','0','0','blog:user:query','#','admin','2024-07-02 09:06:37','',NULL,''),(1120,'用户信息新增',1118,2,'#','',NULL,'',1,0,'F','0','0','blog:user:add','#','admin','2024-07-02 09:06:37','',NULL,''),(1121,'用户信息修改',1118,3,'#','',NULL,'',1,0,'F','0','0','blog:user:edit','#','admin','2024-07-02 09:06:37','',NULL,''),(1122,'用户信息删除',1118,4,'#','',NULL,'',1,0,'F','0','0','blog:user:remove','#','admin','2024-07-02 09:06:37','',NULL,''),(1123,'用户信息导出',1118,5,'#','',NULL,'',1,0,'F','0','0','blog:user:export','#','admin','2024-07-02 09:06:37','',NULL,''),(1124,'说说管理',1063,2,'say','blog/say/index','','',1,0,'C','0','0','blog:say:list','message','admin','2026-03-23 01:24:11','',NULL,''),(1125,'说说查询',1124,1,'','','','',1,0,'F','0','0','blog:say:query','#','admin','2026-03-23 01:24:32','',NULL,''),(1126,'说说新增',1124,2,'','','','',1,0,'F','0','0','blog:say:add','#','admin','2026-03-23 01:24:32','',NULL,''),(1127,'说说修改',1124,3,'','','','',1,0,'F','0','0','blog:say:edit','#','admin','2026-03-23 01:24:32','',NULL,''),(1128,'说说删除',1124,4,'','','','',1,0,'F','0','0','blog:say:remove','#','admin','2026-03-23 01:24:32','',NULL,''),(1129,'树洞管理',1063,3,'treehole','blog/treehole/index','','',1,0,'C','0','0','blog:treehole:list','tree','admin','2026-03-23 01:24:33','',NULL,''),(1130,'树洞查询',1129,1,'','','','',1,0,'F','0','0','blog:treehole:query','#','admin','2026-03-23 01:24:47','',NULL,''),(1131,'树洞新增',1129,2,'','','','',1,0,'F','0','0','blog:treehole:add','#','admin','2026-03-23 01:24:47','',NULL,''),(1132,'树洞修改',1129,3,'','','','',1,0,'F','0','0','blog:treehole:edit','#','admin','2026-03-23 01:24:47','',NULL,''),(1133,'树洞删除',1129,4,'','','','',1,0,'F','0','0','blog:treehole:remove','#','admin','2026-03-23 01:24:47','',NULL,''),(1134,'友链审核',1088,5,'','','','',1,0,'F','0','0','blog:link:examine','#','admin','2026-03-23 01:40:41','',NULL,'上下架审核，与 blog:link:edit 二选一即可调用接口'),(1135,'导航目录',1063,8,'navigation','blog/navigation/index','','',1,0,'C','0','0','blog:navigation:list','guide','admin','2026-04-02 09:09:39','',NULL,'博客导航目录管理'),(1136,'导航查询',1135,1,'','','','',1,0,'F','0','0','blog:navigation:query','#','admin','2026-04-02 09:09:39','',NULL,''),(1137,'导航新增',1135,2,'','','','',1,0,'F','0','0','blog:navigation:add','#','admin','2026-04-02 09:09:39','',NULL,''),(1138,'导航修改',1135,3,'','','','',1,0,'F','0','0','blog:navigation:edit','#','admin','2026-04-02 09:09:39','',NULL,''),(1139,'导航删除',1135,4,'','','','',1,0,'F','0','0','blog:navigation:remove','#','admin','2026-04-02 09:09:39','',NULL,''),(1140,'导航导出',1135,5,'','','','',1,0,'F','0','0','blog:navigation:export','#','admin','2026-04-02 09:09:39','',NULL,''),(1141,'收藏管理',1063,9,'collect','blog/collect/index','','',1,0,'C','0','0','blog:collect:list','star','admin','2026-04-02 09:09:39','',NULL,'博客收藏管理'),(1142,'收藏查询',1141,1,'','','','',1,0,'F','0','0','blog:collect:query','#','admin','2026-04-02 09:09:39','',NULL,''),(1143,'收藏删除',1141,2,'','','','',1,0,'F','0','0','blog:collect:remove','#','admin','2026-04-02 09:09:39','',NULL,''),(1145,'关注管理',1063,10,'follow','blog/follow/index','','',1,0,'C','0','0','blog:follow:list','peoples','admin','2026-04-02 09:09:39','',NULL,'博客关注关系管理'),(1146,'关注查询',1145,1,'','','','',1,0,'F','0','0','blog:follow:query','#','admin','2026-04-02 09:09:39','',NULL,''),(1147,'关注删除',1145,2,'','','','',1,0,'F','0','0','blog:follow:remove','#','admin','2026-04-02 09:09:39','',NULL,''),(1149,'评论词库',1063,11,'','','','',1,0,'F','0','0','blog:moderation:query','edit','admin','2026-04-05 01:50:41','',NULL,'查询当前词库与版本（接口）'),(1150,'词库刷新',1063,12,'','','','',1,0,'F','0','0','blog:moderation:edit','#','admin','2026-04-05 01:50:41','',NULL,'从配置重建内存词树'),(1151,'词库保存',1063,13,'','','','',1,0,'F','0','0','blog:moderation:edit','#','admin','2026-04-05 01:50:42','',NULL,'内存词库覆盖（不落 Nacos）'),(1152,'机审词库',1063,14,'moderation','blog/moderation/index','','',1,0,'C','0','0','blog:moderation:query','education','admin','2026-04-05 01:50:43','',NULL,'评论机审词库与内存刷新'),(1154,'待处理评论',1063,15,'commentPending','blog/comment/index','{\"workbench\":\"1\"}','',1,0,'C','0','0','blog:comment:list','message','admin','2026-04-05 12:08:38','',NULL,'按待审核+机审决策筛选，复用评论列表接口'),(1155,'数据统计',1063,0,'stats','blog/stats/index','','',1,0,'C','0','0','blog:stats:view','dashboard','admin','2026-04-05 13:04:05','1','2026-04-05 13:12:10','博客文章/评论/用户趋势与汇总');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (1,'温馨提醒：2018-07-01 sourcelin新版本发布啦','2','新版本内容','0','admin','2023-10-17 14:29:37','',NULL,'管理员',0),(2,'维护通知：2018-07-01 sourcelin系统凌晨维护','1','维护内容','0','admin','2023-10-17 14:29:37','',NULL,'管理员',0),(3,'花中安睡的静谧画面','1',NULL,'0','1','2026-04-02 14:14:09','1','2026-04-04 11:39:18',NULL,0);
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice_read`
--

DROP TABLE IF EXISTS `sys_notice_read`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_notice_read` (
  `read_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '已读主键',
  `notice_id` int(4) NOT NULL COMMENT '公告id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `read_time` datetime NOT NULL COMMENT '阅读时间',
  PRIMARY KEY (`read_id`),
  UNIQUE KEY `uk_user_notice` (`user_id`,`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告已读记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice_read`
--

LOCK TABLES `sys_notice_read` WRITE;
/*!40000 ALTER TABLE `sys_notice_read` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_notice_read` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=172 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
INSERT INTO `sys_oper_log` VALUES (100,'用户头像',2,'com.sourcelin.system.controller.SysProfileController.avatar()','POST',1,'admin',NULL,'/user/profile/avatar','127.0.0.1','','','{\"msg\":\"文件服务异常，请联系管理员\",\"code\":500}',0,NULL,'2023-10-17 16:39:39',273),(101,'用户头像',2,'com.sourcelin.system.controller.SysProfileController.avatar()','POST',1,'admin',NULL,'/user/profile/avatar','127.0.0.1','','','{\"msg\":\"操作成功\",\"imgUrl\":\"http://127.0.0.1:9300/statics/2023/10/17/blob_20231017164259A001.png\",\"code\":200}',0,NULL,'2023-10-17 16:42:58',632),(102,'用户头像',2,'com.sourcelin.system.controller.SysProfileController.avatar()','POST',1,'admin',NULL,'/user/profile/avatar','127.0.0.1','','','{\"msg\":\"操作成功\",\"imgUrl\":\"http://127.0.0.1:9300/statics/2023/10/17/blob_20231017164408A002.png\",\"code\":200}',0,NULL,'2023-10-17 16:44:08',111),(103,'代码生成',6,'com.sourcelin.gen.controller.GenController.importTableSave()','POST',1,'admin',NULL,'/gen/importTable','127.0.0.1','','{\"tables\":\"b_user,b_web_navbar,b_friend_link,b_follow,b_comment,b_collect,b_category,b_article_tag,b_tag,b_article\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:18:36',622),(104,'代码生成',6,'com.sourcelin.gen.controller.GenController.importTableSave()','POST',1,'admin',NULL,'/gen/importTable','127.0.0.1','','{\"tables\":\"b_web_config\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:21:17',46),(105,'代码生成',3,'com.sourcelin.gen.controller.GenController.remove()','DELETE',1,'admin',NULL,'/gen/2','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:22:07',11),(106,'菜单管理',1,'com.sourcelin.system.controller.SysMenuController.add()','POST',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"monitor\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"网站管理\",\"menuType\":\"M\",\"orderNum\":4,\"params\":{},\"parentId\":0,\"path\":\"blog\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:24:23',156),(107,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"createTime\":\"2023-10-17 14:29:36\",\"icon\":\"guide\",\"isCache\":\"0\",\"isFrame\":\"0\",\"menuId\":4,\"menuName\":\"sourcelin官网\",\"menuType\":\"M\",\"orderNum\":99,\"params\":{},\"parentId\":0,\"path\":\"http://sourcelin.vip\",\"perms\":\"\",\"query\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:24:44',11),(108,'菜单管理',1,'com.sourcelin.system.controller.SysMenuController.add()','POST',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"github\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"用户管理\",\"menuType\":\"M\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"blog\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:25:41',8),(109,'菜单管理',1,'com.sourcelin.system.controller.SysMenuController.add()','POST',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"log\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"博客管理\",\"menuType\":\"M\",\"orderNum\":6,\"params\":{},\"parentId\":0,\"path\":\"blog\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:26:15',6),(110,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"createTime\":\"2024-07-01 20:25:41\",\"icon\":\"peoples\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1062,\"menuName\":\"用户管理\",\"menuType\":\"M\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"blog\",\"perms\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:26:26',6),(111,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"component\":\"blog/navbar/index\",\"createTime\":\"2024-07-01 20:34:34\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1070,\"menuName\":\"门户导航栏\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":1061,\"path\":\"navbar\",\"perms\":\"blog:navbar:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:35:31',7),(112,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1082','127.0.0.1','','{}','{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}',0,NULL,'2024-07-01 20:38:53',4),(113,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1087','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:39:05',15),(114,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1086','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:39:09',26),(115,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1085','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:39:17',90),(116,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1084','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:39:23',19),(117,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1083','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:39:26',14),(118,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1082','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:39:29',8),(119,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1081','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:39:34',8),(120,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1080','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:39:37',7),(121,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1079','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:39:39',11),(122,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1078','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:39:42',7),(123,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1077','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:39:45',8),(124,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'admin',NULL,'/menu/1076','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-01 20:39:47',7),(125,'用户头像',2,'com.sourcelin.system.controller.SysProfileController.avatar()','POST',1,'admin',NULL,'/user/profile/avatar','127.0.0.1','','','{\"msg\":\"文件服务异常，请联系管理员\",\"code\":500}',0,NULL,'2024-07-02 09:14:41',199),(126,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"component\":\"blog/comment/index\",\"createTime\":\"2024-07-02 09:05:46\",\"icon\":\"edit\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1106,\"menuName\":\"评论管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":1063,\"path\":\"comment\",\"perms\":\"blog:comment:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-19 17:08:05',23),(127,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"createTime\":\"2024-07-01 20:25:41\",\"icon\":\"peoples\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1062,\"menuName\":\"用户管理\",\"menuType\":\"M\",\"orderNum\":5,\"params\":{},\"parentId\":0,\"path\":\"user\",\"perms\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-19 17:09:50',9),(128,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"createTime\":\"2024-07-01 20:24:21\",\"icon\":\"monitor\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1061,\"menuName\":\"网站管理\",\"menuType\":\"M\",\"orderNum\":4,\"params\":{},\"parentId\":0,\"path\":\"config\",\"perms\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-19 17:09:59',7),(129,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"component\":\"blog/article/index\",\"createTime\":\"2024-07-02 09:04:21\",\"icon\":\"education\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1094,\"menuName\":\"博客文章\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":1063,\"path\":\"article\",\"perms\":\"blog:article:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-19 17:19:41',6),(130,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"component\":\"blog/category/index\",\"createTime\":\"2024-07-02 09:05:15\",\"icon\":\"category\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1100,\"menuName\":\"博客分类\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":1063,\"path\":\"category\",\"perms\":\"blog:category:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-19 17:19:49',5),(131,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"component\":\"blog/tag/index\",\"createTime\":\"2024-07-02 09:06:21\",\"icon\":\"tag\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1112,\"menuName\":\"博客标签\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":1063,\"path\":\"tag\",\"perms\":\"blog:tag:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-19 17:20:06',7),(132,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"component\":\"blog/user/index\",\"createTime\":\"2024-07-02 09:06:37\",\"icon\":\"people\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1118,\"menuName\":\"用户信息\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":1062,\"path\":\"user\",\"perms\":\"blog:user:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-19 17:20:20',6),(133,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"component\":\"blog/config/index\",\"createTime\":\"2024-07-01 20:28:19\",\"icon\":\"system\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1064,\"menuName\":\"网站配置\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":1061,\"path\":\"config\",\"perms\":\"blog:config:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-19 17:20:46',8),(134,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"component\":\"blog/navbar/index\",\"createTime\":\"2024-07-01 20:34:34\",\"icon\":\"category\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1070,\"menuName\":\"门户导航栏\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":1061,\"path\":\"navbar\",\"perms\":\"blog:navbar:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-19 17:21:45',7),(135,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'admin',NULL,'/menu','127.0.0.1','','{\"children\":[],\"component\":\"blog/link/index\",\"createTime\":\"2024-07-01 20:42:39\",\"icon\":\"link\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1088,\"menuName\":\"友链管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":1061,\"path\":\"link\",\"perms\":\"blog:link:list\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2024-07-19 17:22:16',7),(136,'用户信息',1,'com.sourcelin.blog.controller.admin.UserController.add()','POST',1,'admin',NULL,'/admin/user','127.0.0.1','','{\"createTime\":\"2026-03-30 23:51:13\",\"nickname\":\"SourceLin\",\"params\":{},\"sex\":2,\"userStatus\":0,\"userType\":1,\"username\":\"SourceLin\"}',NULL,1,'\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'create_time\' in \'field list\'\r\n### The error may exist in file [D:\\Projects\\sourcelin\\sourcelin-cloud-blog\\sourcelin-modules\\sourcelin-blog\\target\\classes\\mapper\\blog\\UserMapper.xml]\r\n### The error may involve com.sourcelin.blog.mapper.UserMapper.insertUser-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into b_user          ( username,             nickname,             user_type,                                                    sex,                                                                                                                                  create_time,                                       user_status )           values ( ?,             ?,             ?,                                                    ?,                                                                                                                                  ?,                                       ? )\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'create_time\' in \'field list\'\n; bad SQL grammar []; nested exception is java.sql.SQLSyntaxErrorException: Unknown column \'create_time\' in \'field list\'','2026-03-30 23:51:14',181),(137,'用户信息',1,'com.sourcelin.blog.controller.admin.UserController.add()','POST',1,'admin',NULL,'/admin/user','127.0.0.1','','{\"createTime\":\"2026-03-30 23:51:17\",\"nickname\":\"SourceLin\",\"params\":{},\"sex\":2,\"userStatus\":0,\"userType\":1,\"username\":\"SourceLin\"}',NULL,1,'\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'create_time\' in \'field list\'\r\n### The error may exist in file [D:\\Projects\\sourcelin\\sourcelin-cloud-blog\\sourcelin-modules\\sourcelin-blog\\target\\classes\\mapper\\blog\\UserMapper.xml]\r\n### The error may involve com.sourcelin.blog.mapper.UserMapper.insertUser-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into b_user          ( username,             nickname,             user_type,                                                    sex,                                                                                                                                  create_time,                                       user_status )           values ( ?,             ?,             ?,                                                    ?,                                                                                                                                  ?,                                       ? )\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'create_time\' in \'field list\'\n; bad SQL grammar []; nested exception is java.sql.SQLSyntaxErrorException: Unknown column \'create_time\' in \'field list\'','2026-03-30 23:51:17',27),(138,'通知公告',1,'com.sourcelin.system.controller.SysNoticeController.add()','POST',1,'1',NULL,'/notice','127.0.0.1','','{\"createBy\":\"1\",\"noticeTitle\":\"花中安睡的静谧画面\",\"noticeType\":\"1\",\"params\":{},\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-02 14:14:10',683),(139,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"avatar\":\"https://picsum.photos/1200/600?random=1\",\"categoryId\":86101,\"content\":\"清晨的界面不需要喧闹。把间距、色彩和文字节奏放缓，读者会更愿意停留。首页首先要传达的是情绪和秩序，而不是堆叠信息。\",\"id\":86301,\"isComment\":1,\"isOriginal\":1,\"isRecommend\":0,\"isTop\":1,\"readAuth\":1,\"status\":2,\"summary\":\"把光线、留白与文字的呼吸感留在同一张首页里。\",\"tagIds\":[86202,86207,86251],\"title\":\"在晨雾里慢慢醒来的页面\",\"userId\":86001}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-02 14:15:03',3149),(140,'通知公告',2,'com.sourcelin.system.controller.SysNoticeController.edit()','PUT',1,'1',NULL,'/notice','127.0.0.1','','{\"createBy\":\"1\",\"createTime\":\"2026-04-02 14:14:09\",\"noticeId\":3,\"noticeTitle\":\"花中安睡的静谧画面\",\"noticeType\":\"1\",\"params\":{},\"status\":\"0\",\"updateBy\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-02 14:20:58',61),(141,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"avatar\":\"https://picsum.photos/1200/600?random=1\",\"categoryId\":86101,\"content\":\"清晨的界面不需要喧闹。把间距、色彩和文字节奏放缓，读者会更愿意停留。首页首先要传达的是情绪和秩序，而不是堆叠信息。\",\"id\":86301,\"isComment\":1,\"isOriginal\":1,\"isRecommend\":0,\"isTop\":1,\"readAuth\":1,\"status\":2,\"summary\":\"把光线、留白与文字的呼吸感留在同一张首页里。\",\"tagIds\":[86202,86207,86251],\"title\":\"在晨雾里慢慢醒来的页面\",\"userId\":86001}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-02 14:22:40',45),(142,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"avatar\":\"https://picsum.photos/1200/600?random=1\",\"categoryId\":86101,\"content\":\"清晨的界面不需要喧闹。把间距、色彩和文字节奏放缓，读者会更愿意停留。首页首先要传达的是情绪和秩序，而不是堆叠信息。\",\"id\":86301,\"isComment\":1,\"isOriginal\":1,\"isRecommend\":0,\"isTop\":1,\"readAuth\":1,\"status\":2,\"summary\":\"把光线、留白与文字的呼吸感留在同一张首页里。\",\"tagIds\":[86202,86207,86251],\"title\":\"在晨雾里慢慢醒来的页面\",\"userId\":86001}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-02 14:23:20',36),(143,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"id\":86312,\"status\":2}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-02 14:52:06',504),(144,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"id\":86312,\"status\":2}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-02 14:56:56',13),(145,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"id\":86313,\"status\":2}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-03 00:16:19',545),(146,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"id\":86313,\"isTop\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-03 00:16:22',52),(147,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"id\":86313,\"status\":0}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-03 00:16:49',44),(148,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"id\":86313,\"status\":2}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-03 00:17:04',33),(149,'用户信息',2,'com.sourcelin.blog.controller.admin.UserController.edit()','PUT',1,'1',NULL,'/admin/user','127.0.0.1','','{\"createBy\":\"\",\"createTime\":\"2026-03-31 19:36:28\",\"delFlag\":\"0\",\"email\":\"1012435726@qq.com\",\"follow\":0,\"id\":1,\"loginDate\":\"2026-04-02 16:28:07\",\"loginIp\":\"127.0.0.1\",\"nickname\":\"SourceLin\",\"params\":{},\"praise\":0,\"updateBy\":\"\",\"updateTime\":\"2026-03-31 19:36:28\",\"userStatus\":0,\"userType\":2,\"username\":\"SourceLin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-03 00:29:52',206),(150,'用户信息',2,'com.sourcelin.blog.controller.admin.UserController.edit()','PUT',1,'1',NULL,'/admin/user','127.0.0.1','','{\"avatar\":\"http://127.0.0.1:9300/statics/2026/04/03/QQ图片20220304170905_20260403003010A004.jpg\",\"createBy\":\"\",\"createTime\":\"2026-03-31 19:36:28\",\"delFlag\":\"0\",\"email\":\"1012435726@qq.com\",\"follow\":0,\"id\":1,\"loginDate\":\"2026-04-02 16:28:07\",\"loginIp\":\"127.0.0.1\",\"nickname\":\"SourceLin\",\"params\":{},\"praise\":0,\"updateBy\":\"\",\"updateTime\":\"2026-03-31 19:36:28\",\"userStatus\":0,\"userType\":2,\"username\":\"SourceLin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-03 00:30:12',66),(151,'门户导航栏',3,'com.sourcelin.blog.controller.admin.WebNavbarController.remove()','DELETE',1,'1',NULL,'/admin/navbar/62005','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-04 11:35:14',557),(152,'通知公告',2,'com.sourcelin.system.controller.SysNoticeController.edit()','PUT',1,'1',NULL,'/notice','127.0.0.1','','{\"createBy\":\"1\",\"createTime\":\"2026-04-02 14:14:09\",\"noticeId\":3,\"noticeTitle\":\"花中安睡的静谧画面\",\"noticeType\":\"1\",\"params\":{},\"status\":\"0\",\"updateBy\":\"1\",\"updateTime\":\"2026-04-02 14:20:58\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-04 11:39:19',197),(153,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'1',NULL,'/menu/4','127.0.0.1','','{}','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2026-04-04 11:39:30',163),(154,'角色管理',2,'com.sourcelin.system.controller.SysRoleController.edit()','PUT',1,'1',NULL,'/role','127.0.0.1','','{\"admin\":false,\"createTime\":\"2023-10-17 14:29:36\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,3,114,115,1055,1056,1058,1057,1059,1060,116],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-04 11:39:44',567),(155,'菜单管理',3,'com.sourcelin.system.controller.SysMenuController.remove()','DELETE',1,'1',NULL,'/menu/4','127.0.0.1','','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-04 11:39:54',175),(156,'文章评论',2,'com.sourcelin.blog.controller.admin.CommentController.edit()','PUT',1,'1',NULL,'/admin/comment','127.0.0.1','','{\"id\":33,\"params\":{},\"status\":1,\"updateTime\":\"2026-04-05 11:16:22\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-05 03:16:22',268),(157,'文章评论',2,'com.sourcelin.blog.controller.admin.CommentController.edit()','PUT',1,'1',NULL,'/admin/comment','127.0.0.1','','{\"id\":39,\"params\":{},\"status\":2,\"updateTime\":\"2026-04-05 20:13:24\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-05 12:14:05',2686),(158,'字典数据',2,'com.sourcelin.system.controller.SysDictDataController.edit()','PUT',1,'1',NULL,'/dict/data','127.0.0.1','','{\"createBy\":\"admin\",\"createTime\":\"2026-04-05 12:25:17\",\"cssClass\":\"\",\"default\":false,\"dictCode\":34,\"dictLabel\":\"文章\",\"dictSort\":1,\"dictType\":\"blog_comment_source\",\"dictValue\":\"article\",\"isDefault\":\"N\",\"listClass\":\"\",\"params\":{},\"status\":\"0\",\"updateBy\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-05 12:26:40',238),(159,'个人信息',2,'com.sourcelin.system.controller.SysProfileController.updatePwd()','PUT',1,'1',NULL,'/user/profile/updatePwd','127.0.0.1','','{}',NULL,1,'','2026-04-05 13:01:35',74),(160,'个人信息',2,'com.sourcelin.system.controller.SysProfileController.updatePwd()','PUT',1,'1',NULL,'/user/profile/updatePwd','127.0.0.1','','{}',NULL,1,'','2026-04-05 13:01:40',56),(161,'个人信息',2,'com.sourcelin.system.controller.SysProfileController.updatePwd()','PUT',1,'1',NULL,'/user/profile/updatePwd','127.0.0.1','','{}',NULL,1,'','2026-04-05 13:01:47',57),(162,'菜单管理',2,'com.sourcelin.system.controller.SysMenuController.edit()','PUT',1,'1',NULL,'/menu','127.0.0.1','','{\"children\":[],\"component\":\"blog/stats/index\",\"createTime\":\"2026-04-05 13:04:05\",\"icon\":\"dashboard\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1155,\"menuName\":\"数据统计\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":1063,\"path\":\"stats\",\"perms\":\"blog:stats:view\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"1\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-05 13:12:10',314),(163,'个人信息',2,'com.sourcelin.system.controller.SysProfileController.updatePwd()','PUT',1,'1',NULL,'/user/profile/updatePwd','127.0.0.1','','{}',NULL,1,'','2026-04-05 14:02:05',68),(164,'个人信息',2,'com.sourcelin.system.controller.SysProfileController.updatePwd()','PUT',1,'1',NULL,'/user/profile/updatePwd','127.0.0.1','','{}',NULL,1,'','2026-04-05 14:03:09',114),(165,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"id\":86312,\"isTop\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-05 14:28:19',2848),(166,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"id\":86303,\"isTop\":0}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-05 14:28:59',251),(167,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"id\":86302,\"isTop\":0}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-05 14:29:05',204),(168,'博客文章',2,'com.sourcelin.blog.controller.admin.ArticleController.edit()','PUT',1,'1',NULL,'/admin/article','127.0.0.1','','{\"id\":86301,\"isTop\":0}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-04-05 14:29:08',229),(169,'用户头像',2,'com.sourcelin.system.controller.SysProfileController.avatar()','POST',1,'1',NULL,'/user/profile/avatar','127.0.0.1','','','{\"msg\":\"上传图片异常，请联系管理员\",\"code\":500}',0,NULL,'2026-04-06 15:02:04',1715),(170,'用户头像',2,'com.sourcelin.system.controller.SysProfileController.avatar()','POST',1,'1',NULL,'/user/profile/avatar','127.0.0.1','','','{\"msg\":\"上传图片异常，请联系管理员\",\"code\":500}',0,NULL,'2026-04-06 15:02:07',550),(171,'个人信息',2,'com.sourcelin.system.controller.SysProfileController.updatePwd()','PUT',1,'1',NULL,'/user/profile/updatePwd','127.0.0.1','','{}',NULL,1,'','2026-04-06 15:02:23',155);
/*!40000 ALTER TABLE `sys_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_post`
--

LOCK TABLES `sys_post` WRITE;
/*!40000 ALTER TABLE `sys_post` DISABLE KEYS */;
INSERT INTO `sys_post` VALUES (1,'ceo','董事长',1,'0','admin','2023-10-17 14:29:36','',NULL,''),(2,'se','项目经理',2,'0','admin','2023-10-17 14:29:36','',NULL,''),(3,'hr','人力资源',3,'0','admin','2023-10-17 14:29:36','',NULL,''),(4,'user','普通员工',4,'0','admin','2023-10-17 14:29:36','',NULL,'');
/*!40000 ALTER TABLE `sys_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1',1,1,'0','0','admin','2023-10-17 14:29:36','',NULL,'超级管理员'),(2,'普通角色','common',2,'2',1,1,'0','0','admin','2023-10-17 14:29:36','1','2026-04-04 11:39:43','普通角色');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_dept`
--

LOCK TABLES `sys_role_dept` WRITE;
/*!40000 ALTER TABLE `sys_role_dept` DISABLE KEYS */;
INSERT INTO `sys_role_dept` VALUES (2,100),(2,101),(2,105);
/*!40000 ALTER TABLE `sys_role_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (2,1),(2,2),(2,3),(2,100),(2,101),(2,102),(2,103),(2,104),(2,105),(2,106),(2,107),(2,108),(2,109),(2,110),(2,111),(2,112),(2,113),(2,114),(2,115),(2,116),(2,500),(2,501),(2,1000),(2,1001),(2,1002),(2,1003),(2,1004),(2,1005),(2,1006),(2,1007),(2,1008),(2,1009),(2,1010),(2,1011),(2,1012),(2,1013),(2,1014),(2,1015),(2,1016),(2,1017),(2,1018),(2,1019),(2,1020),(2,1021),(2,1022),(2,1023),(2,1024),(2,1025),(2,1026),(2,1027),(2,1028),(2,1029),(2,1030),(2,1031),(2,1032),(2,1033),(2,1034),(2,1035),(2,1036),(2,1037),(2,1038),(2,1039),(2,1040),(2,1041),(2,1042),(2,1043),(2,1044),(2,1045),(2,1046),(2,1047),(2,1048),(2,1049),(2,1050),(2,1051),(2,1052),(2,1053),(2,1054),(2,1055),(2,1056),(2,1057),(2,1058),(2,1059),(2,1060);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,103,'admin','sourcelin','http://127.0.0.1:9300/statics/2023/10/17/blob_20231017164408A002.png','00','ry@163.com','15888888888','1',NULL,'$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-01-01 00:00:00',NULL,'admin','2026-01-01 00:00:00','',NULL,'管理员'),(2,105,'ry','sourcelin','','00','ry@qq.com','15666666666','1',NULL,'$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-01-01 00:00:00',NULL,'admin','2026-01-01 00:00:00','',NULL,'测试员');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_blog_relation`
--

DROP TABLE IF EXISTS `sys_user_blog_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_blog_relation`
--

LOCK TABLES `sys_user_blog_relation` WRITE;
/*!40000 ALTER TABLE `sys_user_blog_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_user_blog_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_post`
--

LOCK TABLES `sys_user_post` WRITE;
/*!40000 ALTER TABLE `sys_user_post` DISABLE KEYS */;
INSERT INTO `sys_user_post` VALUES (1,1),(2,2);
/*!40000 ALTER TABLE `sys_user_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(2,2);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tenant_capacity`
--

DROP TABLE IF EXISTS `tenant_capacity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tenant_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='租户容量信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tenant_capacity`
--

LOCK TABLES `tenant_capacity` WRITE;
/*!40000 ALTER TABLE `tenant_capacity` DISABLE KEYS */;
/*!40000 ALTER TABLE `tenant_capacity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tenant_info`
--

DROP TABLE IF EXISTS `tenant_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tenant_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) COLLATE utf8_bin DEFAULT '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='tenant_info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tenant_info`
--

LOCK TABLES `tenant_info` WRITE;
/*!40000 ALTER TABLE `tenant_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `tenant_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(500) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('nacos','$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'sourcelin-cloud'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-09 21:16:45
