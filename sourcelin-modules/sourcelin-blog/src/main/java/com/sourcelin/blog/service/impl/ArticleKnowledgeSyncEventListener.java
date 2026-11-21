package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.service.IArticleKnowledgeSyncService;
import com.sourcelin.blog.service.event.ArticleKnowledgeSyncEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 文章事务提交后同步 Dify 知识库。
 */
@Component
public class ArticleKnowledgeSyncEventListener {
    private static final Logger log = LoggerFactory.getLogger(ArticleKnowledgeSyncEventListener.class);

    private final IArticleKnowledgeSyncService articleKnowledgeSyncService;

    public ArticleKnowledgeSyncEventListener(IArticleKnowledgeSyncService articleKnowledgeSyncService) {
        this.articleKnowledgeSyncService = articleKnowledgeSyncService;
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.sourcelin.blog.config.BlogAiProperties properties;

    @Async
    @TransactionalEventListener
    public void onArticleChanged(ArticleKnowledgeSyncEvent event) {
        if (!properties.isEnabled() || !properties.isKnowledgeSyncEnabled()) return;
        if (event == null || event.getArticleId() == null) {
            return;
        }
        try {
            articleKnowledgeSyncService.syncArticle(event.getArticleId());
        } catch (Exception e) {
            log.error("文章知识库同步事件处理失败，articleId={}", event.getArticleId(), e);
        }
    }
}
