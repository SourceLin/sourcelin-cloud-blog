package com.sourcelin.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论机审 Nacos 配置（prefix: moderation）
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "moderation")
public class ModerationProperties
{
    private Article article = new Article();
    private Keyword keyword = new Keyword();
    /** 一期占位 */
    private boolean aiEnabled;

    @Data
    public static class Article
    {
        /** true：干净内容自动通过；false：非 BLOCK 一律人工待审 */
        private boolean autoPassOnClean = true;
    }

    @Data
    public static class Keyword
    {
        private int version = 1;
        private List<String> allow = new ArrayList<>();
        private List<String> block = new ArrayList<>();
        private List<String> suspect = new ArrayList<>();
    }
}
