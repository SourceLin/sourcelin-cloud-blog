package com.sourcelin.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 博客 AI 整合相关配置（prefix: blog.ai）
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "blog.ai")
public class BlogAiProperties {
    /** 全局 AI 功能总开关 */
    private boolean enabled;
    /** 知识库同步需要单独启用，避免未安装可选 SQL 时访问同步表。 */
    private boolean knowledgeSyncEnabled;
}
