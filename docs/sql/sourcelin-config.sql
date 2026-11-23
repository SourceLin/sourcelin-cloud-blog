-- Nacos Server 3.1.1 初始化，仅在独立空库执行。
CREATE DATABASE IF NOT EXISTS `sourcelin-config` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
USE `sourcelin-config`;

/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/******************************************/
/*   表名称 = config_info                  */
/******************************************/
CREATE TABLE `config_info` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `data_id` varchar(255) NOT NULL COMMENT 'data_id',
                               `group_id` varchar(128) DEFAULT NULL COMMENT 'group_id',
                               `content` longtext NOT NULL COMMENT 'content',
                               `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
                               `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                               `src_user` text COMMENT 'source user',
                               `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
                               `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
                               `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
                               `c_desc` varchar(256) DEFAULT NULL COMMENT 'configuration description',
                               `c_use` varchar(64) DEFAULT NULL COMMENT 'configuration usage',
                               `effect` varchar(64) DEFAULT NULL COMMENT '配置生效的描述',
                               `type` varchar(64) DEFAULT NULL COMMENT '配置的类型',
                               `c_schema` text COMMENT '配置的模式',
                               `encrypted_data_key` varchar(1024) NOT NULL DEFAULT '' COMMENT '密钥',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

/******************************************/
/*   表名称 = config_info  since 2.5.0                */
/******************************************/
CREATE TABLE `config_info_gray` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
                                    `data_id` varchar(255) NOT NULL COMMENT 'data_id',
                                    `group_id` varchar(128) NOT NULL COMMENT 'group_id',
                                    `content` longtext NOT NULL COMMENT 'content',
                                    `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
                                    `src_user` text COMMENT 'src_user',
                                    `src_ip` varchar(100) DEFAULT NULL COMMENT 'src_ip',
                                    `gmt_create` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'gmt_create',
                                    `gmt_modified` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'gmt_modified',
                                    `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
                                    `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
                                    `gray_name` varchar(128) NOT NULL COMMENT 'gray_name',
                                    `gray_rule` text NOT NULL COMMENT 'gray_rule',
                                    `encrypted_data_key` varchar(256) NOT NULL DEFAULT '' COMMENT 'encrypted_data_key',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_configinfogray_datagrouptenantgray` (`data_id`,`group_id`,`tenant_id`,`gray_name`),
                                    KEY `idx_dataid_gmt_modified` (`data_id`,`gmt_modified`),
                                    KEY `idx_gmt_modified` (`gmt_modified`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='config_info_gray';

/******************************************/
/*   表名称 = config_tags_relation         */
/******************************************/
CREATE TABLE `config_tags_relation` (
                                        `id` bigint(20) NOT NULL COMMENT 'id',
                                        `tag_name` varchar(128) NOT NULL COMMENT 'tag_name',
                                        `tag_type` varchar(64) DEFAULT NULL COMMENT 'tag_type',
                                        `data_id` varchar(255) NOT NULL COMMENT 'data_id',
                                        `group_id` varchar(128) NOT NULL COMMENT 'group_id',
                                        `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
                                        `nid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增长标识',
                                        PRIMARY KEY (`nid`),
                                        UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
                                        KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

/******************************************/
/*   表名称 = group_capacity               */
/******************************************/
CREATE TABLE `group_capacity` (
                                  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `group_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
                                  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
                                  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
                                  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
                                  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
                                  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
                                  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
                                  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='集群、各Group容量信息表';

/******************************************/
/*   表名称 = his_config_info              */
/******************************************/
CREATE TABLE `his_config_info` (
                                   `id` bigint(20) unsigned NOT NULL COMMENT 'id',
                                   `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增标识',
                                   `data_id` varchar(255) NOT NULL COMMENT 'data_id',
                                   `group_id` varchar(128) NOT NULL COMMENT 'group_id',
                                   `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
                                   `content` longtext NOT NULL COMMENT 'content',
                                   `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
                                   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `src_user` text COMMENT 'source user',
                                   `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
                                   `op_type` char(10) DEFAULT NULL COMMENT 'operation type',
                                   `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
                                   `encrypted_data_key` varchar(1024) NOT NULL DEFAULT '' COMMENT '密钥',
                                   `publish_type` varchar(50)  DEFAULT 'formal' COMMENT 'publish type gray or formal',
                                   `gray_name` varchar(50)  DEFAULT NULL COMMENT 'gray name',
                                   `ext_info`  longtext DEFAULT NULL COMMENT 'ext info',
                                   PRIMARY KEY (`nid`),
                                   KEY `idx_gmt_create` (`gmt_create`),
                                   KEY `idx_gmt_modified` (`gmt_modified`),
                                   KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='多租户改造';


/******************************************/
/*   表名称 = tenant_capacity              */
/******************************************/
CREATE TABLE `tenant_capacity` (
                                   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `tenant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
                                   `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
                                   `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
                                   `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
                                   `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
                                   `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
                                   `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
                                   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='租户容量信息表';


CREATE TABLE `tenant_info` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `kp` varchar(128) NOT NULL COMMENT 'kp',
                               `tenant_id` varchar(128) default '' COMMENT 'tenant_id',
                               `tenant_name` varchar(128) default '' COMMENT 'tenant_name',
                               `tenant_desc` varchar(256) DEFAULT NULL COMMENT 'tenant_desc',
                               `create_source` varchar(32) DEFAULT NULL COMMENT 'create_source',
                               `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
                               `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
                               KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

CREATE TABLE `users` (
                         `username` varchar(50) NOT NULL PRIMARY KEY COMMENT 'username',
                         `password` varchar(500) NOT NULL COMMENT 'password',
                         `enabled` boolean NOT NULL COMMENT 'enabled'
);

CREATE TABLE `roles` (
                         `username` varchar(50) NOT NULL COMMENT 'username',
                         `role` varchar(50) NOT NULL COMMENT 'role',
                         UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
);

CREATE TABLE `permissions` (
                               `role` varchar(50) NOT NULL COMMENT 'role',
                               `resource` varchar(128) NOT NULL COMMENT 'resource',
                               `action` varchar(8) NOT NULL COMMENT 'action',
                               UNIQUE INDEX `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
);

INSERT INTO `config_info` VALUES (1, 'application-dev.yml', 'BLOG_GROUP', 'spring:\n  data:\n    redis:\n      host: ${REDIS_HOST:127.0.0.1}\n      port: ${REDIS_PORT:6379}\n      password: ${REDIS_PASSWORD:}\n      database: ${REDIS_DATABASE:0}\n  autoconfigure:\n    exclude: com.alibaba.druid.spring.boot4.autoconfigure.DruidDataSourceAutoConfigure\n\n  cloud:\n    openfeign:\n      okhttp:\n        enabled: true\n      httpclient:\n        hc5:\n          enabled: false\n      client:\n        config:\n          default:\n            connectTimeout: 10000\n            readTimeout: 10000\n      compression:\n        request:\n          enabled: true\n        response:\n          enabled: true\n\n# feign 配置\nfeign:\n  sentinel:\n    enabled: true\n# 暴露监控端点\nmanagement:\n  endpoints:\n    web:\n      exposure:\n        include: \'*\'\n', '746e467adfd67bb173daf5f8dca5d841', '2020-05-20 12:00:00', '2022-04-24 10:26:34', 'nacos', '0:0:0:0:0:0:0:1', '', '', '通用配置', 'null', 'null', 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (21, 'sourcelin-gateway-dev.yml', 'BLOG_GROUP', 'spring:\n  cloud:\n    gateway:\n      server:\n        webflux:\n          discovery:\n            locator:\n              lowerCaseServiceId: true\n              enabled: true\n          routes:\n            - id: sourcelin-blog\n              uri: lb://sourcelin-blog\n              predicates:\n                - Path=/blog/**\n              filters:\n                - StripPrefix=1\n            - id: sourcelin-ai\n              uri: lb://sourcelin-ai\n              predicates:\n                - Path=/ai/**\n              filters:\n                - StripPrefix=1\n            - id: sourcelin-message\n              uri: lb://sourcelin-message\n              predicates:\n                - Path=/message/**\n              filters:\n                - StripPrefix=1\n            # 认证中心\n            - id: sourcelin-auth\n              uri: lb://sourcelin-auth\n              predicates:\n                - Path=/auth/**\n              filters:\n                - CacheRequestFilter\n                - StripPrefix=1\n            # 定时任务\n            - id: sourcelin-job\n              uri: lb://sourcelin-job\n              predicates:\n                - Path=/schedule/**\n              filters:\n                - StripPrefix=1\n            # 系统模块\n            - id: sourcelin-system\n              uri: lb://sourcelin-system\n              predicates:\n                - Path=/system/**\n              filters:\n                - StripPrefix=1\n            # 文件服务\n            - id: sourcelin-file\n              uri: lb://sourcelin-file\n              predicates:\n                - Path=/file/**\n              filters:\n                - StripPrefix=1\n\n# 安全配置\nsecurity:\n  # 验证码\n  captcha:\n    enabled: true\n    type: math\n  # 防止XSS攻击\n  xss:\n    enabled: true\n    excludeUrls:\n      - /system/notice\n  # 不校验白名单\n  ignore:\n    whites:\n      - /message/stream\n      - /blog/front/ai/status\n      - /blog/front/messages/realtime-status\n      - /blog/front/ai/abilities/ai_agent_chat/invoke/stream\n      - /auth/logout\n      - /auth/login\n      - /auth/register\n      - /*/v2/api-docs\n      - /csrf\n', '3eb98433d15afe3105a6f44b7317ef8a', '2023-10-23 09:07:51', '2023-10-23 09:08:29', 'nacos', '0:0:0:0:0:0:0:1', '', '', '网关模块', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (23, 'sourcelin-auth-dev.yml', 'BLOG_GROUP', '{}\n', '8a80554c91d9fca8acb82f023de02f11', '2023-10-23 09:09:22', '2023-10-23 09:09:22', NULL, '0:0:0:0:0:0:0:1', '', '', '认证中心', NULL, NULL, 'yaml', NULL, '');
INSERT INTO `config_info` VALUES (30, 'sourcelin-blog-dev.yml', 'BLOG_GROUP', '# spring配置\nspring:\n  datasource:\n    dynamic:\n      primary: master\n      datasource:\n        master:\n          driver-class-name: com.mysql.cj.jdbc.Driver\n          url: ${BLOG_DB_URL:${DB_URL:jdbc:mysql://127.0.0.1:3306/sourcelin-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8}}\n          username: ${BLOG_DB_USERNAME:${DB_USERNAME:root}}\n          password: ${BLOG_DB_PASSWORD:${DB_PASSWORD:}}\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        connectTimeout: 30000\n        socketTimeout: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n\n# mybatis配置\nmybatis:\n  type-aliases-package: com.sourcelin.blog\n  mapper-locations: classpath:mapper/**/*.xml\n\n# 博客 AI 整合相关配置\nblog:\n  ai:\n    enabled: ${BLOG_AI_ENABLED:false}\n', 'c2206bd17cf9879e6e404449c7f6995e', '2026-06-04 00:00:00', '2026-06-04 00:00:00', 'nacos', '0:0:0:0:0:0:0:1', '', '', '博客模块', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (24, 'sourcelin-monitor-dev.yml', 'BLOG_GROUP', '# spring\nspring:\n  security:\n    user:\n      name: sourcelin\n      password: change_me\n  boot:\n    admin:\n      ui:\n        title: sourcelin服务状态监控\n', '884b8979104c9103888fe2b92af6059b', '2023-10-23 09:09:22', '2023-10-24 10:38:19', 'nacos', '0:0:0:0:0:0:0:1', '', '', '监控中心', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (25, 'sourcelin-system-dev.yml', 'BLOG_GROUP', '# spring配置\nspring:\n  datasource:\n    druid:\n      stat-view-servlet:\n        enabled: true\n        loginUsername: admin\n        loginPassword: change_me\n    dynamic:\n      druid:\n        initial-size: 5\n        min-idle: 5\n        maxActive: 20\n        maxWait: 60000\n        timeBetweenEvictionRunsMillis: 60000\n        minEvictableIdleTimeMillis: 300000\n        validationQuery: SELECT 1 FROM DUAL\n        testWhileIdle: true\n        testOnBorrow: false\n        testOnReturn: false\n        poolPreparedStatements: true\n        maxPoolPreparedStatementPerConnectionSize: 20\n        filters: stat,slf4j\n        connectionProperties: druid.stat.mergeSql\\=true;druid.stat.slowSqlMillis\\=5000\n      datasource:\n          # 主库数据源\n          master:\n            driver-class-name: com.mysql.cj.jdbc.Driver\n            url: ${SYSTEM_DB_URL:${DB_URL:jdbc:mysql://127.0.0.1:3306/sourcelin-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8}}\n            username: ${SYSTEM_DB_USERNAME:${DB_USERNAME:root}}\n            password: ${SYSTEM_DB_PASSWORD:${DB_PASSWORD:}}\n          # 从库数据源\n          # slave:\n            # username: \n            # password: \n            # url: \n            # driver-class-name: \n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.system\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 系统模块接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip', '59fe2e4e3c4648388b5b43e193315cf6', '2023-10-23 09:09:22', '2023-10-24 10:38:55', 'nacos', '0:0:0:0:0:0:0:1', '', '', '系统模块', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (27, 'sourcelin-job-dev.yml', 'BLOG_GROUP', '# spring配置\nspring:\n  datasource:\n    driver-class-name: com.mysql.cj.jdbc.Driver\n    url: ${JOB_DB_URL:${DB_URL:jdbc:mysql://127.0.0.1:3306/sourcelin-cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8}}\n    username: ${JOB_DB_USERNAME:${DB_USERNAME:root}}\n    password: ${JOB_DB_PASSWORD:${DB_PASSWORD:}}\n\n# mybatis配置\nmybatis:\n    # 搜索指定包别名\n    typeAliasesPackage: com.sourcelin.job.domain\n    # 配置mapper的扫描，找到所有的mapper.xml映射文件\n    mapperLocations: classpath:mapper/**/*.xml\n\n# swagger配置\nswagger:\n  title: 定时任务接口文档\n  license: Powered By sourcelin\n  licenseUrl: https://sourcelin.vip\n', 'e3b8fd798d87bb252b2ae8c59112cc23', '2023-10-23 09:09:22', '2023-10-24 10:39:34', 'nacos', '0:0:0:0:0:0:0:1', '', '', '定时任务', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (28, 'sourcelin-file-dev.yml', 'BLOG_GROUP', '# 本地文件上传    \nfile:\n    domain: http://127.0.0.1:9300\n    path: D:/sourcelin/uploadPath\n    prefix: /statics\n\n# FastDFS配置\nfdfs:\n  domain: http://127.0.0.1\n  soTimeout: 3000\n  connectTimeout: 2000\n  trackerList: 127.0.0.1:22122\n\n# Minio配置\nminio:\n  url: http://127.0.0.1:9000\n  accessKey: change_me\n  secretKey: change_me\n  bucketName: test', 'a829f491b7057e2026cbed6ee3247b43', '2023-10-23 09:09:22', '2023-10-24 10:39:54', 'nacos', '0:0:0:0:0:0:0:1', '', '', '文件服务', '', '', 'yaml', '', '');
INSERT INTO `config_info` VALUES (29, 'sentinel-sourcelin-gateway', 'BLOG_GROUP', '[\n    {\n        \"resource\": \"sourcelin-auth\",\n        \"count\": 500,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"sourcelin-system\",\n        \"count\": 1000,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    },\n	{\n        \"resource\": \"sourcelin-job\",\n        \"count\": 300,\n        \"grade\": 1,\n        \"limitApp\": \"default\",\n        \"strategy\": 0,\n        \"controlBehavior\": 0\n    }\n]', '9edfca354a291fb8c5b93aa690ddbe01', '2023-10-23 09:09:22', '2023-10-23 09:12:03', 'nacos', '0:0:0:0:0:0:0:1', '', '', '限流策略', '', '', 'json', '', '');
UPDATE config_info SET md5 = MD5(content) WHERE group_id = 'BLOG_GROUP';
